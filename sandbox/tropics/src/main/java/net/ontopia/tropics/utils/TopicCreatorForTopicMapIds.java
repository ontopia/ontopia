package net.ontopia.tropics.utils;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;

public class TopicCreatorForTopicMapIds implements Predicate {
  private final String hostRef;
  
  private final TopicMapIF tm;       
  private final TopicIF topicMapType;
  
  public TopicCreatorForTopicMapIds(TopicMapIF tm, String hostRef) {
    this.tm = tm;
    this.hostRef = hostRef;

    TopicMapBuilderIF builder = tm.getBuilder();
    this.topicMapType = builder.makeTopic();
  }

  public void init() throws ConstraintViolationException {
    TopicMapBuilderIF builder = tm.getBuilder();

    builder.makeTopicName(this.topicMapType, "Topic Map");
    this.topicMapType.addItemIdentifier(URILocator.create(hostRef + "/api/v1/topics/topicmap"));
  }

  public void apply(String value) {
    TopicMapBuilderIF builder = tm.getBuilder();
    
    TopicIF topic = builder.makeTopic(topicMapType);
    builder.makeTopicName(topic, value + " Topic Map");
    try {
      topic.addItemIdentifier(URILocator.create(hostRef + "/api/v1/topicmaps/" + value));
    } catch (ConstraintViolationException e) {
      e.printStackTrace();
    }
  }    
}
