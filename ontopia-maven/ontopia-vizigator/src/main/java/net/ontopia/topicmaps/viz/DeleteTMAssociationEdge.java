package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.AssociationIF;

public class DeleteTMAssociationEdge implements RecoveryObjectIF {
  private AssociationIF association;

  public DeleteTMAssociationEdge(AssociationIF association) {
    this.association = association;
  }

  public void execute(TopicMapView view) {
    TMAssociationEdge edge = view.getEdge(association);
    if (edge != null) {
      view.getTGPanel().deleteEdge(edge);
      view.deleteEdge(edge);
    }
  }
}
