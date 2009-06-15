package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.AssociationIF;

public class CreateTMAssociationNode implements NodeRecoveryObjectIF {
  private AssociationIF association;

  // Use with care and keep private. Only for temporary internal storage!
  private TMAssociationNode lastNode;

  public CreateTMAssociationNode(AssociationIF association) {
    this.association = association;
  }
  
  public void execute(TopicMapView view) {
    TMAssociationNode assocNode = (TMAssociationNode)view
        .findObject(association, 
                    association.getType());
      
    if (assocNode == null) {
      assocNode = new TMAssociationNode(association, 
                                        view.currentScopingTopic,
                                        view);
      view.initializeAssociation(assocNode);
      view.addAssociation(assocNode);
    }
    
    lastNode = assocNode;
  }

  public TMAbstractNode recoverNode(TopicMapView view) {
    execute(view);
    return lastNode;
  }
}
