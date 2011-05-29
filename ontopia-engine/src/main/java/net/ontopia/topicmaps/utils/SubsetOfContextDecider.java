
package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Decider that decides whether the ScopedIF's scope is a
 * subset of the user context or not.</p>
 *
 * See {@link net.ontopia.topicmaps.utils.ScopeUtils} for
 * more information.
 */

public class SubsetOfContextDecider implements DeciderIF {
  
  protected Collection context;
  
  public SubsetOfContextDecider(Collection context) {
    this.context = context;
  }
  
  public boolean ok(Object scoped) {
    return ScopeUtils.isSubsetOfContext((ScopedIF)scoped, context);
  }

}





