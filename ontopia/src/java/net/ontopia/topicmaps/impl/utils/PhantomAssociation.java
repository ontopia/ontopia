
// $Id: PhantomAssociation.java,v 1.8 2008/06/13 08:17:52 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.utils;

import java.util.Collection;
import java.util.Collections;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Class that represents the association object to which a
 * deleted role might have belonged to. Note that this class exists
 * just to make the API behave gracefully in the cases where
 * association roles are deleted, so that applications do not fail
 * that easily.
 */
public class PhantomAssociation implements AssociationIF {

  // -- AssociationIF

  public Collection getRoleTypes() {
    throw new PhantomAccessException("Cannot access phantom association");
  }

  public Collection getRolesByType(TopicIF roletype) {
    throw new PhantomAccessException("Cannot access phantom association");
  }

  public Collection getRoles() {
    return Collections.EMPTY_SET;
  }

  // -- ScopedIF

  public Collection getScope() {
    throw new PhantomAccessException("Cannot access phantom association");
  }

  public void addTheme(TopicIF theme) {
    throw new PhantomAccessException("Cannot modify phantom association");
  }

  public void removeTheme(TopicIF theme) {
    throw new PhantomAccessException("Cannot modify phantom association");
  }

  // -- TypedIF

  public TopicIF getType() {
    return null;
  }

  public void setType(TopicIF type) {
    throw new PhantomAccessException("Cannot modify phantom association");
  }
  
  public String getObjectId() {
    throw new PhantomAccessException("Cannot access phantom association");
  }

  public boolean isReadOnly() {
    throw new PhantomAccessException("Cannot access phantom association");
  }

  public TopicMapIF getTopicMap() {
    throw new PhantomAccessException("Cannot access phantom association");
  }

  public Collection getItemIdentifiers() {
    throw new PhantomAccessException("Cannot access phantom association");
  }

  public void addItemIdentifier(LocatorIF srcloc) {
    throw new PhantomAccessException("Cannot modify phantom association");
  }

  public void removeItemIdentifier(LocatorIF srcloc) {
    throw new PhantomAccessException("Cannot modify phantom association");
  }
  
  public void remove() {
    throw new PhantomAccessException("Cannot modify phantom association");
  }

  public TopicIF getReifier() {
    throw new PhantomAccessException("Cannot access phantom association");
	}
  
  public void setReifier(TopicIF reifier) {
    throw new PhantomAccessException("Cannot modify phantom association");
	}

}
