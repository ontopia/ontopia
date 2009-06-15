package net.ontopia.topicmaps.viz;

public class DoHideEdge implements RecoveryObjectIF {
  private VizController controller;
  private EdgeRecoveryObjectIF recreator;

  public DoHideEdge(VizController controller, EdgeRecoveryObjectIF recreator) {
    this.controller = controller;
    this.recreator = recreator;
  }

  public void execute(TopicMapView view) {
    TMAbstractEdge node = recreator.recoverEdge(view);
    controller.hideEdge(node);
  }
}
