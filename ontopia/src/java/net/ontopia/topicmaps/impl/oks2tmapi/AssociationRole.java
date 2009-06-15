
// $Id: AssociationRole.java,v 1.8 2008/01/11 12:22:18 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class AssociationRole extends TopicMapObject implements org.tmapi.core.AssociationRole {

  AssociationRoleIF other;

  AssociationRole(TopicMap tm, AssociationRoleIF other) {
    super(tm);
    this.other = other;
  }

  public TMObjectIF getWrapped() {
    return other;
  }

  public void remove() throws org.tmapi.core.TMAPIException {
    other.remove();
  }

  public org.tmapi.core.Association getAssociation() {
    return tm.wrapAssociation(other.getAssociation());
  }
  
  public void setPlayer(org.tmapi.core.Topic player) {
    other.setPlayer(tm.unwrapTopic(player));
  }
  
  public org.tmapi.core.Topic getPlayer() {
    return tm.wrapTopic(other.getPlayer());
  }
  
  public void setType(org.tmapi.core.Topic type) {
    other.setType(tm.unwrapTopic(type));
  }
  
  public org.tmapi.core.Topic getType() {
    return tm.wrapTopic(other.getType());
  }

  public org.tmapi.core.Topic getReifier() {
    return tm._getReifier(this, tm);
  }
    
}
