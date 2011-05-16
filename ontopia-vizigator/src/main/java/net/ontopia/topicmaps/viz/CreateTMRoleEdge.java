package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;

import com.touchgraph.graphlayout.TGException;

public class CreateTMRoleEdge implements EdgeRecoveryObjectIF {
  private AssociationRoleIF role;

  // Use with care and keep private. Only for temporary internal storage!
  private TMRoleEdge lastEdge;

  public CreateTMRoleEdge(AssociationRoleIF role) {
    this.role = role;
  }
  
  public void execute(TopicMapView view) {
    lastEdge = view.getEdge(role);
    if (lastEdge != null)
      return;
    
    AssociationIF association = role.getAssociation();
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
    
    lastEdge = view.makeRole(assocNode, role, true);

    while (view.newNodes.isEmpty() == false) {
      TMAbstractNode newNode = (TMAbstractNode) view.newNodes.remove(0);
      view.nodesUpdateCount.add(newNode);
      try {
        view.getTGPanel().addNode(newNode);
      } catch (TGException e) {
        view.lenientAddNode(newNode);
      }
    }
  }

  public TMAbstractEdge recoverEdge(TopicMapView view) {
    execute(view);
    return lastEdge;
  }
}
