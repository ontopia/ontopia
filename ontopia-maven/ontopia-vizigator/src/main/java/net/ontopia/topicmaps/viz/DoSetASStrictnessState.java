package net.ontopia.topicmaps.viz;

public class DoSetASStrictnessState implements RecoveryObjectIF {
  private int strictness;

  public DoSetASStrictnessState(int strictness) {
    this.strictness = strictness;
  }

  public void execute(TopicMapView view) {
    view.controller.getConfigurationManager()
        .setAssociationScopeFilterStrictness(strictness);
    view.controller.getVizPanel().getAssociationScopeFilterMenu()
        .setStrictnessSelection(strictness);
  }
}
