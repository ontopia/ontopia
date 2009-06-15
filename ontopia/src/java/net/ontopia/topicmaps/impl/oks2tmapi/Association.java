
// $Id: Association.java,v 1.9 2008/01/11 12:22:18 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class Association extends ScopedObject implements org.tmapi.core.Association {

  AssociationIF other;

  Association(TopicMap tm, AssociationIF other) {
    super(tm);
    this.other = other;
  }

  public TMObjectIF getWrapped() {
    return other;
  }

  public void remove() throws org.tmapi.core.TMAPIException {
    other.remove();
  }
  
  public void setType(org.tmapi.core.Topic type) {
    other.setType(tm.unwrapTopic(type));
  }
  
  public org.tmapi.core.Topic getType() {
    return tm.wrapTopic(other.getType());
  }
  
  public org.tmapi.core.AssociationRole createAssociationRole(org.tmapi.core.Topic player, org.tmapi.core.Topic type) {
    TopicMapIF otm = other.getTopicMap();
    AssociationRoleIF role = otm.getBuilder().makeAssociationRole(other, tm.unwrapTopic(type), tm.unwrapTopic(player));
    return tm.wrapAssociationRole(role);
  }

  public Set getAssociationRoles() {
    return tm.wrapSet(other.getRoles());
  }

  public org.tmapi.core.Topic getReifier() {
    return tm._getReifier(this, tm);
  }
    
}
