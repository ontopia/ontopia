package net.ontopia.topicmaps.viz;

import com.touchgraph.graphlayout.TGException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.OntopiaRuntimeException;

public class CreateTMClassInstanceAssociation implements EdgeRecoveryObjectIF {
  private TopicIF instance;
  private TopicIF type;

  // Use with care and keep private. Only for temporary internal storage!
  private TMClassInstanceAssociation lastEdge;

  public CreateTMClassInstanceAssociation(TopicIF instance, TopicIF type) {
    this.instance = instance;
    this.type = type;
  }

  public void execute(TopicMapView view) {
    lastEdge = view.getEdge(type, instance);
    if (lastEdge != null)
      return;
    
    TMTopicNode typeNode = view.assertNode(type, true);
    TMTopicNode instanceNode = view.assertNode(instance, true); 

    try {
      // The following lines were necessary to make the far topic visible.
      view.getTGPanel().addNode(typeNode);
      view.getTGPanel().addNode(instanceNode);
    } catch (TGException e) {
      throw new OntopiaRuntimeException(e);
    }
    
    view.makeTypeInstanceEdge(instanceNode, typeNode);
  }

  public TMAbstractEdge recoverEdge(TopicMapView view) {
    execute(view);
    return lastEdge;
  }
}
