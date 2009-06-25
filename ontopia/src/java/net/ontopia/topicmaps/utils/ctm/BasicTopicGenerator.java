
// $Id: BasicTopicGenerator.java,v 1.2 2009/02/27 11:59:57 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.topicmaps.core.TopicIF;

public class BasicTopicGenerator implements TopicGeneratorIF {
  private TopicIF topic;

  public BasicTopicGenerator() {
  }
  
  public BasicTopicGenerator(TopicIF topic) {
    this.topic = topic;
  }
  
  public TopicIF getTopic() {
    return topic;
  }

  public TopicGeneratorIF copyTopic() {
    return new BasicTopicGenerator(topic);
  }

  public void setTopic(TopicIF topic) {
    this.topic = topic;
  }
}
