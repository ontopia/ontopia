
// $Id: VariableTopicGenerator.java,v 1.2 2009/02/27 12:04:00 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.topicmaps.core.TopicIF;

public class VariableTopicGenerator implements TopicGeneratorIF {
  private Template template;
  private String variable;
  private TopicIF topic;
  
  public VariableTopicGenerator(Template template, String variable) {
    this.template = template;
    this.variable = variable;
  }
  
  public TopicIF getTopic() {
    return topic;
  }

  public TopicGeneratorIF copyTopic() {
    return this;
  }

  public void setTopic(TopicIF topic) {
    this.topic = topic;
  }
}
