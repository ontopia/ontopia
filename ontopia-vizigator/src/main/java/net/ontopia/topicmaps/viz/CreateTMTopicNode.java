package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.TopicIF;

public class CreateTMTopicNode implements NodeRecoveryObjectIF {
  private TopicIF topic;
  
  // Use with care and keep private. Only for temporary internal storage!
  private TMTopicNode lastNode;

  public CreateTMTopicNode(TopicIF topic) {
    this.topic = topic;
  }

  public void execute(TopicMapView view) {
    lastNode = view.assertNode(topic, true);
  }
  
  public TMAbstractNode recoverNode(TopicMapView view) {
    execute(view);
    return lastNode;
  }
}
