package net.ontopia.topicmaps.viz;

public class SetFocusNode implements RecoveryObjectIF {

  private NodeRecoveryObjectIF nodeCreator;

  public SetFocusNode(NodeRecoveryObjectIF nodeCreator) {
    this.nodeCreator = nodeCreator;
  }

  public void execute(TopicMapView view) {
    view.getTGPanel().setSelect(nodeCreator.recoverNode(view));    
  }
}
