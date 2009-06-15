package net.ontopia.topicmaps.viz;

import net.ontopia.topicmaps.core.TopicIF;

public class DoSetInASFilter implements RecoveryObjectIF {
  private TopicIF scope;
  private boolean useInFilter;

  public DoSetInASFilter(TopicIF scope, boolean useInFilter) {
    this.scope = scope;
    this.useInFilter = useInFilter;
  }

  public void execute(TopicMapView view) {
    view.controller.setInAssociationScopeFilter(scope, useInFilter);
    view.controller.getVizPanel().getAssociationScopeFilterMenu().
        setInAssociationScopeFilter(scope, useInFilter);
  }

}
