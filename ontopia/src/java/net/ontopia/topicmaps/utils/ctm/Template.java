
// $Id: Template.java,v 1.3 2009/02/27 12:02:29 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

/**
 * INTERNAL: Represents a CTM template. Contains a recorded list of
 * parse events which is replayed when the template is invoked.
 */
public class Template {
  private String name;
  private List<String> parameters; // just the names, in declared order
  private List events;
  private Set named_wilcards;
  /**
   * Each variable used in the template has a corresponding generator
   * stored here. On invocation, the invoke() method sets the passed
   * arguments in the generators stored here.
   */
  private Map generators;  
  
  public Template(String name, List<String> parameters) {
    this.name = name;
    this.parameters = parameters;
    this.events = new ArrayList();
    this.generators = new HashMap();
    this.named_wilcards = new CompactHashSet();
  }

  public String getName() {
    return name;
  }

  public void addEvent(ParseEventIF event) {
    events.add(event);
  }

  public void registerWildcard(String name, TopicGeneratorIF gen) {
    named_wilcards.add(gen);
  }

  public VariableLiteralGenerator getLiteralVariable(String name) {
    // FIXME: does variable actually exist in this template?
    // FIXME: what if variable actually of another type?
    VariableLiteralGenerator gen = (VariableLiteralGenerator)
      generators.get(name);
    if (gen == null) {
      gen = new VariableLiteralGenerator(this, name);
      generators.put(name, gen);
    }
    return gen;
  }

  public VariableTopicGenerator getTopicVariable(String name) {
    // FIXME: does variable actually exist in this template?
    // FIXME: what if variable actually of another type?
    VariableTopicGenerator gen = (VariableTopicGenerator)
      generators.get(name);
    if (gen == null) {
      gen = new VariableTopicGenerator(this, name);
      generators.put(name, gen);
    }
    return gen;
  }

  /**
   * Used for the special case:
   *
   * <pre>
   * def foo($a)
   *   $a.
   * end;
   * foo(topic); # we're passing a topic
   * foo(<http://psi.example.com/topic>); # we're passing an IRI
   * </pre>
   */
  public TopicIdentityVariableGenerator getTopicIdentityVariable(String name,
                                                         TopicMapIF topicmap) {
    // FIXME: does variable actually exist in this template?
    // FIXME: what if variable actually of another type?
    // FIXME: can fuck this up by first making topic ref, then topic id ref...
    TopicIdentityVariableGenerator gen = (TopicIdentityVariableGenerator)
      generators.get(name);
    if (gen == null) {
      gen = new TopicIdentityVariableGenerator(this, name, topicmap);
      generators.put(name, gen);
    }
    return gen;
  }

  /**
   * Invokes the template.
   * @param arguments a list of generator objects producing the values
   *                  for the arguments, in the same order as the
   *                  parameters list
   */
  public void invoke(List arguments, ParseEventHandlerIF handler) {    
    if (parameters.size() != arguments.size())
      throw new InvalidTopicMapException("Incorrect number of arguments to " +
                                         "template " + name + ", got " +
                                         arguments.size() + ", expected " +
                                         parameters.size());

    for (int ix = 0; ix < parameters.size(); ix++) {
      // name of parameter
      String name = parameters.get(ix);
      // generator producing passed argument value
      Object value = arguments.get(ix);
      // generator producing variable value inside invoked template
      Object generator = generators.get(name);

      if (generator instanceof VariableTopicGenerator) {
        TopicGeneratorIF gen = (TopicGeneratorIF) value;
        ((VariableTopicGenerator) generator).setTopic(gen.getTopic());
      } else if (generator instanceof VariableLiteralGenerator)
        ((VariableLiteralGenerator) generator).setValue(value);
      else if (generator instanceof TopicIdentityVariableGenerator)
        ((TopicIdentityVariableGenerator) generator).setValue(value);
      else
        throw new OntopiaRuntimeException("No generator for parameter " + name +
                                          " to template " + this.name + ": " +
                                          generator);
    }

    for (int ix = 0; ix < events.size(); ix++) {
      ParseEventIF event = (ParseEventIF) events.get(ix);
      event.replay(handler);
    }

    // release all topics created by named wildcards so that on next
    // invocation we create new ones
    Iterator it = named_wilcards.iterator();
    while (it.hasNext()) {
      NamedWildcardTopicGenerator gen = (NamedWildcardTopicGenerator) it.next();
      gen.contextEnd();
    }
  }
}
