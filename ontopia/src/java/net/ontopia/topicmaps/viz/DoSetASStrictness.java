package net.ontopia.topicmaps.viz;

public class DoSetASStrictness implements RecoveryObjectIF {
  private int strictness;

  public DoSetASStrictness(int strictness) {
    this.strictness = strictness;
  }

  public void execute(TopicMapView view) {
    view.controller.setAssociationScopeFilterStrictness(strictness);
    view.controller.getVizPanel().getAssociationScopeFilterMenu()
        .setStrictnessSelection(strictness);
  }

}
