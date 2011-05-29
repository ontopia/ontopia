
package net.ontopia.topicmaps.webed.impl.utils;

import java.util.Collection;

/**
 * INTERNAL: Helper class for storing a collection of TopicIF objects
 * for scope specification as well as a flag which indicates that the
 * unconstrained scope is allowed.
 */
public class ScopeStorage {

  protected Collection scope;
  protected boolean unconstrainedIncluded;
  
  public ScopeStorage(Collection scope, boolean unconstrainedIncluded) {
    this.scope = scope;
    this.unconstrainedIncluded = unconstrainedIncluded;
  }

  public Collection getScope() {
    return scope;
  }

  public void setScope(Collection scope) {
    this.scope = scope;
  }

  public boolean isUnconstrainedIncluded() {
    return unconstrainedIncluded;
  }

  public void setUnconstrainedIncluded(boolean unconstrainedIncluded) {
    this.unconstrainedIncluded = unconstrainedIncluded;
  }
  
}
