package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.TopicIF;

public class DoSetATVisibilityState implements RecoveryObjectIF {
  private TopicIF type;
  private int visibility;

  public DoSetATVisibilityState(TopicIF type, int visibility) {
    this.type = type;
    this.visibility = visibility;
  }

  public void execute(TopicMapView view) {
    VizController controller = view.controller;
    controller.getConfigurationManager().setTypeVisible(type, visibility);    
    TypesConfigFrame assocFrame = controller.getAssocFrame();
    assocFrame.updateSelectedFilter();
  }
}
