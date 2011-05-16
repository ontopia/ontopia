// $Id: SupersetOfContextDecider.java,v 1.8 2008/01/10 11:08:49 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Decider that decides whether the ScopedIF's scope is a
 * superset of the user context or not.</p>
 *
 * See {@link net.ontopia.topicmaps.utils.ScopeUtils} for
 * more information.
 */

public class SupersetOfContextDecider implements DeciderIF {
  
  protected TopicIF[] context;
  
  public SupersetOfContextDecider(Collection context) {
    this.context = new TopicIF[context.size()];
    context.toArray(this.context);
  }
  
  public boolean ok(Object scoped) {
    return ScopeUtils.isSupersetOfContext((ScopedIF)scoped, context);
  }

}





