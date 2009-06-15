package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.TopicIF;

public class DoSetInASFilterState implements RecoveryObjectIF {
  private TopicIF scope;
  private boolean useInFilter;

  public DoSetInASFilterState(TopicIF scope, boolean useInFilter) {
    this.scope = scope;
    this.useInFilter = useInFilter;
  }

  public void execute(TopicMapView view) {
    view.controller.getConfigurationManager()
        .setInAssociationScopeFilter(scope, useInFilter);
    view.controller.getVizPanel().getAssociationScopeFilterMenu().
        setInAssociationScopeFilter(scope, useInFilter);
  }
}
