
// $Id: TopicIdentityVariableGenerator.java,v 1.2 2009/02/27 12:03:41 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

public class TopicIdentityVariableGenerator implements TopicGeneratorIF {
  private Template template;
  private String variable;
  private TopicMapIF topicmap;
  private TopicIF topic;    // set if we were passed a topic (subjid then null)
  private LocatorIF subjid; // if set we were passed the subjid of a topic
  
  public TopicIdentityVariableGenerator(Template template, String variable,
                                        TopicMapIF topicmap) {
    this.template = template;
    this.variable = variable;
    this.topicmap = topicmap;
  }
  
  public TopicIF getTopic() {
    if (topic == null) {
      topic = topicmap.getTopicBySubjectIdentifier(subjid);
      if (topic == null) {
        topic = topicmap.getBuilder().makeTopic();
        topic.addSubjectIdentifier(subjid);
      }
    }
    return topic;
  }

  public TopicGeneratorIF copyTopic() {
    return this;
  }

  public void setValue(Object value) {
    if (value instanceof TopicGeneratorIF) {
      TopicGeneratorIF gen = (TopicGeneratorIF) value;
      topic = gen.getTopic();
    } else {
      BasicLiteralGenerator gen = (BasicLiteralGenerator) value;
      if (!gen.getDatatype().equals(PSI.getXSDURI()))
        throw new InvalidTopicMapException("Wrong argument type for $" +
                                           variable + ": " + gen.getLiteral());
      
      try {
        subjid = new URILocator(gen.getLiteral());
      } catch (java.net.MalformedURLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }
}