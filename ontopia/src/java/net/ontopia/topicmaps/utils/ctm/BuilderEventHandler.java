
// $Id: BuilderEventHandler.java,v 1.4 2009/04/27 11:03:48 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import java.util.List;
import java.util.Stack;
import java.net.MalformedURLException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

/**
 * INTERNAL: The event handler which actually builds the topic map.
 * This handler assumes that all generators actually resolve to the
 * correct values.
 */
public class BuilderEventHandler implements ParseEventHandlerIF {
  private TopicMapBuilderIF builder;
  private ParseContextIF context;
  private Stack framestack; // for embedded topics
  private TopicGeneratorIF generator;
  private TopicIF previous_embedded;

  // <framed>
  // the topic of the current topic block, *or* the current embedded topic
  private TopicIF topic;
  // the topic name currently being parsed (needed for variant names)  
  private TopicNameIF name;
  // the statement currently being parsed (used by the scope parsing code)
  private ScopedIF scoped;
  // the statement currently being parsed (used by the reifier parsing code)
  private ReifiableIF reifiable;
  // the association currently being parsed (needed for roles)
  private AssociationIF association;
  // </framed>

  private TopicIF assoctype;  // supertype-subtype
  private TopicIF subtype;    // subtype
  private TopicIF supertype;  // supertype
  
  public BuilderEventHandler(TopicMapBuilderIF builder, ParseContextIF context) {
    this.builder = builder;
    this.context = context;
    this.framestack = new Stack();
    this.reifiable = builder.getTopicMap();
    this.generator = new PreviousEmbeddedTopicGenerator();
  }
  
  public void startTopicItemIdentifier(LiteralGeneratorIF locator) {
    topic = context.makeTopicByItemIdentifier(locator.getLocator());
  }
  
  public void startTopicSubjectIdentifier(LiteralGeneratorIF locator) {
    topic = context.makeTopicBySubjectIdentifier(locator.getLocator());
  }
  
  public void startTopicSubjectLocator(LiteralGeneratorIF locator) {
    topic = context.makeTopicBySubjectLocator(locator.getLocator());
  }

  public void startTopic(TopicGeneratorIF topicgen) {
    topic = topicgen.getTopic();
  }
  
  public void addItemIdentifier(LiteralGeneratorIF locator) {
    topic.addItemIdentifier(locator.getLocator());
  }
  
  public void addSubjectIdentifier(LiteralGeneratorIF locator) {
    TopicMapIF tm = builder.getTopicMap();
    TopicIF other = tm.getTopicBySubjectIdentifier(locator.getLocator());
    if (other != null)
      merge(topic, other);
    else
      topic.addSubjectIdentifier(locator.getLocator());
  }
  
  public void addSubjectLocator(LiteralGeneratorIF locator) {
    topic.addSubjectLocator(locator.getLocator());
  }

  public void addTopicType(TopicGeneratorIF type) {
    topic.addType(type.getTopic());
  }

  public void addSubtype(TopicGeneratorIF thesubtype) {
    // get typing topics
    if (assoctype == null)
      assoctype = getTopicByPSI(PSI.getSAMSupertypeSubtype());
    if (subtype == null)
      subtype = getTopicByPSI(PSI.getSAMSubtype());
    if (supertype == null)
      supertype = getTopicByPSI(PSI.getSAMSupertype());
    
    // make the assertion
    AssociationIF assoc = builder.makeAssociation(assoctype); 
    builder.makeAssociationRole(assoc, subtype, topic);
    builder.makeAssociationRole(assoc, supertype, thesubtype.getTopic());
  }
  
  public void startName(TopicGeneratorIF type, LiteralGeneratorIF value) {
    name = builder.makeTopicName(topic, type.getTopic(), value.getLiteral());
    scoped = name;
    reifiable = name;    
  }
  
  public void addScopingTopic(TopicGeneratorIF topic) {
    scoped.addTheme(topic.getTopic());
  }
  
  public void addReifier(TopicGeneratorIF topic) {
    TopicIF reifier = topic.getTopic();
    if (reifier.getReified() != null)
      throw new InvalidTopicMapException("Cannot reify " + reifiable + " because "+
                                         reifier + " already reifies " +
                                         reifier.getReified());
    reifiable.setReifier(reifier);
  }

  public void startVariant(LiteralGeneratorIF value) {
    // FIXME: no support for datatypes here yet...
    VariantNameIF variant = builder.makeVariantName(name, value.getLiteral());
    scoped = variant;
    reifiable = variant;
  }
  
  public void endName() {
  }

  public void startOccurrence(TopicGeneratorIF type, LiteralGeneratorIF value) {
    OccurrenceIF occurrence = 
      builder.makeOccurrence(topic,
                             type.getTopic(),
                             value.getLiteral(),
                             value.getDatatype());
    scoped = occurrence;
    reifiable = occurrence;
  }

  public void endOccurrence() {
  }
  
  public void endTopic() {
    topic = null; // so we can tell if we are in a block or not
  }

  public void startAssociation(TopicGeneratorIF type) {
    association = builder.makeAssociation(type.getTopic()); 
    scoped = association;    
  }
  
  public void addRole(TopicGeneratorIF type, TopicGeneratorIF player) {
    reifiable = builder.makeAssociationRole(association,
                                            type.getTopic(),
                                            player.getTopic());
  }

  public void endRoles() {
    reifiable = association;
  }
  
  public void endAssociation() {
  }

  public void startEmbeddedTopic() {
    framestack.push(new ParseFrame(topic, name, scoped, reifiable,
                                   association)); 
    topic = context.makeAnonymousTopic();
  }

  public TopicGeneratorIF endEmbeddedTopic() {
    previous_embedded = topic;
    ParseFrame frame = (ParseFrame) framestack.pop(); 
    topic = frame.topic;
    name = frame.name;
    scoped = frame.scoped;
    reifiable = frame.reifiable;
    association = frame.association;
    return generator;
  }

  public void templateInvocation(String name, List arguments) {
    Template template = context.getTemplate(name);
    if (template == null) // FIXME: change exception class
      throw new InvalidTopicMapException("Template '" + name + "' not declared");
    
    if (topic != null) {
      // invocations inside topic blocks need to have the current topic prepended
      // to the list of parameters. note that this needs to be as a generator.
      VariableTopicGenerator gen = new VariableTopicGenerator(template, null);
      gen.setTopic(topic);
      arguments.add(0, gen);
    }

    TopicIF tmp = topic; // template may end current topic block
    template.invoke(arguments, this);
    topic = tmp; // preserves current topic, if any
  }

  // --- Internal methods

  private TopicIF getTopicByPSI(LocatorIF psi) {
    return context.makeTopicBySubjectIdentifier(psi);
  }

  private void merge(TopicIF topic, TopicIF other) {
    if (topic == other)
      return;

    // make sure hard-wired references to "ako" topics are not lost
    if (assoctype == other)
      assoctype = topic;
    else if (subtype == other)
      subtype = topic;
    else if (supertype == other)
      supertype = topic;
    
    MergeUtils.mergeInto(topic, other);
  }
  
  // --- PreviousEmbeddedTopicGenerator

  class PreviousEmbeddedTopicGenerator implements TopicGeneratorIF {
    
    public TopicIF getTopic() {
      return previous_embedded;
    }

    public TopicGeneratorIF copyTopic() {
      return new BasicTopicGenerator(previous_embedded);
    }
  }
}
