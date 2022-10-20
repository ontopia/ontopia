/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.utils.ctm;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
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
  private ValueGeneratorIF generator;
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
  
  @Override
  public void startTopicItemIdentifier(ValueGeneratorIF locator) {
    topic = context.makeTopicByItemIdentifier(locator.getLocator());
  }
  
  @Override
  public void startTopicSubjectIdentifier(ValueGeneratorIF locator) {
    topic = context.makeTopicBySubjectIdentifier(locator.getLocator());
  }
  
  @Override
  public void startTopicSubjectLocator(ValueGeneratorIF locator) {
    topic = context.makeTopicBySubjectLocator(locator.getLocator());
  }

  @Override
  public void startTopic(ValueGeneratorIF topicgen) {
    // this is a special situation, because while we might be passed a
    // topic, we might also be passed just an IRI to be interpreted as
    // a subject identifier
    if (topicgen.isTopic()) {
      topic = topicgen.getTopic();
    } else if (topicgen.getLocator() != null) {
      startTopicSubjectIdentifier(topicgen);
    } else {
      throw new InvalidTopicMapException("Wrong type passed as topic identifier: " + topicgen.getLiteral());
    }
  }
  
  @Override
  public void addItemIdentifier(ValueGeneratorIF locator) {
    topic.addItemIdentifier(locator.getLocator());
  }
  
  @Override
  public void addSubjectIdentifier(ValueGeneratorIF locator) {
    TopicMapIF tm = builder.getTopicMap();
    TopicIF other = tm.getTopicBySubjectIdentifier(locator.getLocator());
    if (other != null) {
      merge(topic, other);
    } else {
      topic.addSubjectIdentifier(locator.getLocator());
    }
  }
  
  @Override
  public void addSubjectLocator(ValueGeneratorIF locator) {
    topic.addSubjectLocator(locator.getLocator());
  }

  @Override
  public void addTopicType(ValueGeneratorIF type) {
    topic.addType(type.getTopic());
  }

  @Override
  public void addSubtype(ValueGeneratorIF thesubtype) {
    // get typing topics
    if (assoctype == null) {
      assoctype = getTopicByPSI(PSI.getSAMSupertypeSubtype());
    }
    if (subtype == null) {
      subtype = getTopicByPSI(PSI.getSAMSubtype());
    }
    if (supertype == null) {
      supertype = getTopicByPSI(PSI.getSAMSupertype());
    }
    
    // make the assertion
    AssociationIF assoc = builder.makeAssociation(assoctype); 
    builder.makeAssociationRole(assoc, subtype, topic);
    builder.makeAssociationRole(assoc, supertype, thesubtype.getTopic());
  }
  
  @Override
  public void startName(ValueGeneratorIF type, ValueGeneratorIF value) {
    name = builder.makeTopicName(topic, type.getTopic(), value.getLiteral());
    scoped = name;
    reifiable = name;    
  }
  
  @Override
  public void addScopingTopic(ValueGeneratorIF topic) {
    scoped.addTheme(topic.getTopic());
  }
  
  @Override
  public void addReifier(ValueGeneratorIF topic) {
    TopicIF reifier = topic.getTopic();
    if (reifier.getReified() != null) {
      throw new InvalidTopicMapException("Cannot reify " + reifiable + " because "+
                                         reifier + " already reifies " +
                                         reifier.getReified());
    }
    reifiable.setReifier(reifier);
  }

  @Override
  public void startVariant(ValueGeneratorIF value) {
    // FIXME: no support for datatypes here yet...
    VariantNameIF variant = builder.makeVariantName(name, value.getLiteral(), Collections.emptySet());
    scoped = variant;
    reifiable = variant;
  }
  
  @Override
  public void endName() {
    // no-op
  }

  @Override
  public void startOccurrence(ValueGeneratorIF type, ValueGeneratorIF value) {
    OccurrenceIF occurrence = 
      builder.makeOccurrence(topic,
                             type.getTopic(),
                             value.getLiteral(),
                             value.getDatatype());
    scoped = occurrence;
    reifiable = occurrence;
  }

  @Override
  public void endOccurrence() {
    // no-op
  }
  
  @Override
  public void endTopic() {
    topic = null; // so we can tell if we are in a block or not
  }

  @Override
  public void startAssociation(ValueGeneratorIF type) {
    association = builder.makeAssociation(type.getTopic()); 
    scoped = association;    
  }
  
  @Override
  public void addRole(ValueGeneratorIF type, ValueGeneratorIF player) {
    reifiable = builder.makeAssociationRole(association,
                                            type.getTopic(),
                                            player.getTopic());
  }

  @Override
  public void endRoles() {
    reifiable = association;
  }
  
  @Override
  public void endAssociation() {
  }

  @Override
  public void startEmbeddedTopic() {
    framestack.push(new ParseFrame(topic, name, scoped, reifiable,
                                   association)); 
    topic = context.makeAnonymousTopic();
  }

  @Override
  public ValueGeneratorIF endEmbeddedTopic() {
    previous_embedded = topic;
    ParseFrame frame = (ParseFrame) framestack.pop(); 
    topic = frame.topic;
    name = frame.name;
    scoped = frame.scoped;
    reifiable = frame.reifiable;
    association = frame.association;
    return generator;
  }

  @Override
  public void templateInvocation(String name, List arguments) {    
    if (topic != null) {
      // invocations inside topic blocks need to have the current topic prepended
      // to the list of parameters. note that this needs to be as a generator.
      // however, argument lists may be stored in GenericParseEvent objects, so
      // to modify the argument list we must first copy it.
      arguments = new ArrayList(arguments);
      arguments.add(0, new ValueGenerator(topic, null, null, null));
    }

    Template template = context.getTemplate(name, arguments.size());
    if (template == null) {
      throw new InvalidTopicMapException("Template '" + name + "' not declared"+
                                         " with " + arguments.size() +
                                         " parameters");
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
    if (Objects.equals(topic, other)) {
      return;
    }

    // make sure hard-wired references to "ako" topics are not lost
    if (Objects.equals(assoctype, other)) {
      assoctype = topic;
    } else if (Objects.equals(subtype, other)) {
      subtype = topic;
    } else if (Objects.equals(supertype, other)) {
      supertype = topic;
    }
    
    MergeUtils.mergeInto(topic, other);
  }
  
  // --- PreviousEmbeddedTopicGenerator

  class PreviousEmbeddedTopicGenerator extends AbstractTopicGenerator {
    
    @Override
    public TopicIF getTopic() {
      return previous_embedded;
    }

    @Override
    public ValueGeneratorIF copy() {
      return new ValueGenerator(previous_embedded, null, null, null);
    }
  }
}
