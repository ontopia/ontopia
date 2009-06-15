package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.AssociationRoleIF;

public class DeleteTMRoleEdge implements RecoveryObjectIF {
  private AssociationRoleIF role;

  public DeleteTMRoleEdge(AssociationRoleIF role) {
    this.role = role;
  }
  
  public void execute(TopicMapView view) {
    TMRoleEdge edge = view.getEdge(role);
    if (edge != null) {
      view.getTGPanel().deleteEdge(edge);
      view.deleteEdge(edge);
    }
  }
}
