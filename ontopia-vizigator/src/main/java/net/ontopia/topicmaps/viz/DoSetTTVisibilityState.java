package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.TopicIF;

public class DoSetTTVisibilityState implements RecoveryObjectIF {
  private TopicIF type;
  private int visibility;

  public DoSetTTVisibilityState(TopicIF type, int visibility) {
    this.type = type;
    this.visibility = visibility;
  }

  public void execute(TopicMapView view) {
    VizController controller = view.controller;
    controller.getConfigurationManager().setTypeVisible(type, visibility);    
    TypesConfigFrame topicFrame = controller.getTopicFrame();
    topicFrame.updateSelectedFilter();
  }
}
