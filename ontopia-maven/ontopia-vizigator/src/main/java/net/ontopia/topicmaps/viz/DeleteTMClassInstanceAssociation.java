package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.viz.TMClassInstanceAssociation.Key;

public class DeleteTMClassInstanceAssociation implements RecoveryObjectIF {
  private TopicIF type;
  private TopicIF instance;

  public DeleteTMClassInstanceAssociation(TopicIF instance,
                                          TopicIF type) {
    this.instance = instance;
    this.type = type;
  }

  public void execute(TopicMapView view) {
    TMClassInstanceAssociation edge = null;
    Key key = new Key(type, instance);
    edge = (TMClassInstanceAssociation)view
        .findObject(key, view.configman.getTypeInstanceType());
    if (edge != null)
    view.deleteEdge(edge);
  }
}
