package net.ontopia.topicmaps.viz;

public class DoCollapseNode implements RecoveryObjectIF {
  private VizController controller;
  private NodeRecoveryObjectIF recreator;

  public DoCollapseNode(VizController controller,
                        NodeRecoveryObjectIF recreator) {
    this.controller = controller;
    this.recreator = recreator;
  }

  public void execute(TopicMapView view) {
    TMAbstractNode node = recreator.recoverNode(view);
    controller.collapseNode(node);
  }
}
