package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.TopicIF;

public class DeleteTMTopicNode implements RecoveryObjectIF {
  private TopicIF topic;

  public DeleteTMTopicNode(TopicIF topic) {
    this.topic = topic;
  }

  public void execute(TopicMapView view) {
    TMTopicNode node = view.assertNode(topic, false);
    if (node != null) {
      view.getTGPanel().deleteNode(node);
      view.deleteNode(node);
    }
  }

}
