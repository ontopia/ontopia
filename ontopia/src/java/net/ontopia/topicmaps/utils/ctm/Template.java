
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
import net.ontopia.infoset.core.LocatorIF;
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
  private Map<String, ParameterGenerator> generators;  
  
  public Template(String name, List<String> parameters) {
    this.name = name;
    this.parameters = parameters;
    this.events = new ArrayList();
    this.generators = new HashMap<String, ParameterGenerator>();
    this.named_wilcards = new CompactHashSet();

    for (String param : parameters)
      generators.put(param, new ParameterGenerator());
  }

  public String getName() {
    return name;
  }

  public int getParameterCount() {
    return parameters.size();
  }

  public void addEvent(ParseEventIF event) {
    events.add(event);
  }

  public void registerWildcard(String name, ValueGeneratorIF gen) {
    named_wilcards.add(gen);
  }

  public ValueGeneratorIF getGenerator(String name) {
    ValueGeneratorIF gen = generators.get(name);
    if (gen == null)
      throw new InvalidTopicMapException("No such parameter: " + name);
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
      ValueGeneratorIF value = (ValueGeneratorIF) arguments.get(ix);
      // generator producing variable value inside invoked template
      ParameterGenerator generator = generators.get(name);
      // connecting parameter with its value
      generator.setGenerator(value);
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

  // --- Parameter generator

  static class ParameterGenerator implements ValueGeneratorIF {
    private ValueGeneratorIF gen;

    public ParameterGenerator() {
    }

    public void setGenerator(ValueGeneratorIF gen) {
      this.gen = gen;
    }
    
    public boolean isTopic() {
      return gen.isTopic();
    }
  
    public String getLiteral() {
      return gen.getLiteral();
    }
  
    public LocatorIF getDatatype() {
      return gen.getDatatype();
    }

    public LocatorIF getLocator() {
      return gen.getLocator();
    }
  
    public ValueGeneratorIF copy() {
      return this;
    }

    public TopicIF getTopic() {
      return gen.getTopic();
    }

    public String toString() {
      return "[ParameterGenerator: " + gen + "]";
    }
  }
}
