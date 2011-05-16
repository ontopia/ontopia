package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.AssociationIF;

public class DeleteTMAssociationNode implements RecoveryObjectIF {
  private AssociationIF association;

  public DeleteTMAssociationNode(AssociationIF association) {
    this.association = association;
  }
  
  public void execute(TopicMapView view) {
    TMAssociationNode node = view.getNode(association);
    if (node != null) {
      view.getTGPanel().deleteNode(node);
      view.deleteNode(node);
    }
  }
}
