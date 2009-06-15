package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.TopicIF;

public class DoSetTTVisibility implements RecoveryObjectIF {
  private TopicIF type;
  private int visibility;

  public DoSetTTVisibility(TopicIF type, int visibility) {
    this.type = type;
    this.visibility = visibility;
  }


  public void execute(TopicMapView view) {
    VizController controller = view.controller;
    controller.setTopicTypeVisibility(type, visibility);
    TypesConfigFrame topicFrame = controller.getTopicFrame();
    topicFrame.updateSelectedFilter();
  }
}
