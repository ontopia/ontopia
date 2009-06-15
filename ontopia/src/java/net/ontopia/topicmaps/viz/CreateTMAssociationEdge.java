package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;

public class CreateTMAssociationEdge implements EdgeRecoveryObjectIF {
  private AssociationIF association;

  // Use with care and keep private. Only for temporary internal storage!
  private TMAssociationEdge lastEdge;

  public CreateTMAssociationEdge(AssociationIF association,
                                 TopicIF scopingTopic) {
    this.association = association;
  }

  public void execute(TopicMapView view) {
    // One can rely on view.makeAssociation creating a TMAssociationEdge, since
    // the associationargument must necessarily be binary. Otherwise, an error
    // has been made in constructing this CreateTMassociationEdge.
    lastEdge =  view.getEdge(association);
    if (lastEdge == null)
      lastEdge = (TMAssociationEdge)view
          .makeAssociation(association, null, true);  
  }

  public TMAbstractEdge recoverEdge(TopicMapView view) {
    execute(view);
    return lastEdge;
  }
}
