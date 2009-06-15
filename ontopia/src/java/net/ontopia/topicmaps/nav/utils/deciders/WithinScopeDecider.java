// $Id: WithinScopeDecider.java,v 1.11 2008/01/10 11:08:48 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.deciders;

import java.util.Collection;

import net.ontopia.utils.DeciderIF;
import net.ontopia.topicmaps.core.ScopedIF;

/**
 * INTERNAL: Decider that decides whether the scoped object is broader
 * than the context. The context must be identical or a subset of the
 * scopes. If there is no context, there can be no "ok".
 */
public class WithinScopeDecider implements DeciderIF {
  
  protected Collection context;

  public WithinScopeDecider(Collection context) {
    this.context = context;
  }

  public boolean ok(Object scoped) { 
    if (context == null || context.isEmpty())
      return false;
    Collection objscope = ((ScopedIF) scoped).getScope();
    if (objscope.containsAll(context))
      return true;
    return false;
  }
  
}





