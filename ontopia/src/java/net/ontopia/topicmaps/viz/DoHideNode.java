package net.ontopia.topicmaps.viz;

public class DoHideNode implements RecoveryObjectIF {
  private VizController controller;
  private NodeRecoveryObjectIF recreator;

  public DoHideNode(VizController controller, NodeRecoveryObjectIF recreator) {
    this.controller = controller;
    this.recreator = recreator;
  }

  public void execute(TopicMapView view) {
    TMAbstractNode node = recreator.recoverNode(view);
    controller.hideNode(node);
  }
}
