
package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: This decider accepts all objects in the unconstrained
 * scope, letting a sub-decider rule for object not in the unconstrained
 * scope. Useful for making other deciders accept objects in the
 * unconstrained scope.
 * @since 1.1
 */

public class UnconstrainedScopeDecider implements DeciderIF {
  protected DeciderIF subdecider;

  public UnconstrainedScopeDecider(DeciderIF subdecider) {
    this.subdecider = subdecider;
  }
  
  public boolean ok(Object object) {
    ScopedIF scoped = (ScopedIF) object;
    return scoped.getScope().isEmpty() ||
           subdecider.ok(scoped);
  }

}





