package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.TopicIF;

public class DoSetATVisibility implements RecoveryObjectIF {
  private TopicIF type;
  private int visibility;

  public DoSetATVisibility(TopicIF type, int visibility) {
    this.type = type;
    this.visibility = visibility;
  }


  public void execute(TopicMapView view) {
    VizController controller = view.controller;
    controller.setAssociationTypeVisibility(type, visibility);
    TypesConfigFrame assocFrame = controller.getAssocFrame();
    assocFrame.updateSelectedFilter();
  }

}
