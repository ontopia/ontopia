
// $Id: ApplicableInContextDecider.java,v 1.7 2008/01/10 11:08:49 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Decider that decides whether the ScopedIF's scope is
 * applicable in the user context. This is implies that the ScopedIF's
 * scope must be either the unconstrained scope (empty) or a superset
 * of the user context.</p>
 *
 * See {@link net.ontopia.topicmaps.utils.ScopeUtils} for
 * more information.
 */
public class ApplicableInContextDecider implements DeciderIF {
  
  protected Collection context;

  public ApplicableInContextDecider(Collection context) {
    this.context = context;
  }
  
  public boolean ok(Object scoped) {
    return ScopeUtils.isApplicableInContext((ScopedIF)scoped, context);
  }

}
