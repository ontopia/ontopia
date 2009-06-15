
// $Id: AssociationRolesIndex.java,v 1.2 2005/07/11 09:58:45 grove Exp $

package net.ontopia.topicmaps.impl.oks2tmapi.index;

import java.util.*;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.oks2tmapi.TopicMap;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class AssociationRolesIndex 
  extends AbstractTMAPIIndex 
  implements org.tmapi.index.core.AssociationRolesIndex {

  protected TopicMap tm;
  protected ClassInstanceIndexIF ci;

  public AssociationRolesIndex(TopicMap tm, ClassInstanceIndexIF ci) {
    this.tm = tm;
    this.ci = ci;
  }

  protected IndexIF getOther() {
    return ci;
  }

  public void configure(org.tmapi.core.TopicMap tm) {
    // no-op
  }

  public Collection getAssociationRoleTypes() {
    return tm.wrapSet(ci.getAssociationRoleTypes());
  }

  public Collection getAssociationRolesByType(org.tmapi.core.Topic type) {
    return tm.wrapSet(ci.getAssociationRoles(tm.unwrapTopic(type)));
  }

}
