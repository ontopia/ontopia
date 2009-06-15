package net.ontopia.topicmaps.viz;

public class DoSetLocality implements RecoveryObjectIF {
  private VizController controller;
  private int locality;

  public DoSetLocality(VizController controller, int locality) {
    this.controller = controller;
    this.locality = locality;
  }

  public void execute(TopicMapView view) {
    controller.setLocality(locality);
  }
}
