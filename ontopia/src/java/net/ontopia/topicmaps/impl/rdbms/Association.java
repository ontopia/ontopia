
// $Id: Association.java,v 1.58 2008/06/02 10:50:13 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.*;

/**
 * INTERNAL: The rdbms association implementation.
 */

public class Association extends TMObject implements AssociationIF {
  
  // ---------------------------------------------------------------------------
  // Persistent property declarations
  // ---------------------------------------------------------------------------

  protected static final int LF_scope = 2;
  protected static final int LF_type = 3;
  protected static final int LF_roles = 4;
  protected static final int LF_reifier = 5;
  protected static final String[] fields = {"sources", "topicmap", "scope", "type", "roles", "reifier"};

  public void detach() {
    detachCollectionField(LF_sources);
    detachField(LF_topicmap);
    detachField(LF_reifier);
    detachCollectionField(LF_scope);
    detachField(LF_type);
    detachCollectionField(LF_roles);
  }

  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  static final String CLASS_INDICATOR = "A";

  public Association() {  
  }

  public Association(TransactionIF txn) {
    super(txn);
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return fields.length;
  }

  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public String getClassIndicator() {
    return CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // AssociationIF implementation
  // ---------------------------------------------------------------------------
  
  /**
   * INTERNAL: Sets the topic map that the object belongs to. [parent]
   */
  void setTopicMap(TopicMap topicmap) {
    // Notify transaction
    transactionChanged(topicmap);
    valueChanged(LF_topicmap, topicmap, true);

    // Inform association roles
    Collection roles = loadCollectionField(LF_roles);
    Iterator iter = roles.iterator();
    while (iter.hasNext()) {
      ((AssociationRole)iter.next()).setTopicMap(topicmap);
    }
  }

  public Collection<TopicIF> getRoleTypes() {
    Collection<TopicIF> result = new CompactHashSet<TopicIF>();
    for (AssociationRoleIF role : (Collection<AssociationRoleIF>) loadCollectionField(LF_roles)) {
      TopicIF type = role.getType();
      if (type != null)
        result.add(role.getType());
    }
    return result;
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    if (roletype == null)
      throw new NullPointerException("Role type must not be null.");
    CrossTopicMapException.check(roletype, this);
    Collection<AssociationRoleIF> result = new CompactHashSet<AssociationRoleIF>();
    for (AssociationRoleIF role : (Collection<AssociationRoleIF>) loadCollectionField(LF_roles))
      if (role.getType() == roletype)
        result.add(role);
    return result;
  }

  public Collection<AssociationRoleIF> getRoles() {
    try {
      return (Collection<AssociationRoleIF>) loadCollectionField(LF_roles);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return empty set
      return (Collection<AssociationRoleIF>) Collections.EMPTY_SET;
    }
  }

  void addRole(AssociationRoleIF assoc_role) {
    if (assoc_role == null)
      throw new NullPointerException("null is not a valid argument.");
    // Check to see if association role is already a member of this association
    if (assoc_role.getAssociation() == this)
      return;
    // Check if used elsewhere.
    if (assoc_role.getAssociation() != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");

    // Notify listeners
    fireEvent("AssociationIF.addRole", assoc_role, null);    
    // Set association property
    ((AssociationRole)assoc_role).setAssociation(this);
    // Add association role to list of association roles
    valueAdded(LF_roles, assoc_role, false);

    // Make sure role is added to player's list
    Topic player = (Topic) assoc_role.getPlayer();
    if (player != null && getTopicMap() != null)
      player.addRole(assoc_role);
  }

  void removeRole(AssociationRoleIF assoc_role) {
    if (assoc_role == null)
      throw new NullPointerException("null is not a valid argument.");
    // Check to see if association role is not a member of this association
    if (assoc_role.getAssociation() != this)
      return;

    // Notify listeners
    fireEvent("AssociationIF.removeRole", null, assoc_role);    
    // Unset association property
    ((AssociationRole)assoc_role).setAssociation(null);
    // Remove role from list of roles
    valueRemoved(LF_roles, assoc_role, false);

    // Removing role from player's list of roles
    Topic player = (Topic) assoc_role.getPlayer();
    if (player != null && getTopicMap() != null)
      player.removeRole(assoc_role);
  }

  public void remove() {
    TopicMap topicmap = (TopicMap)getTopicMap();
    if (topicmap != null) {
      DeletionUtils.removeDependencies(this);
      topicmap.removeAssociation(this);
    }
  }

  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  public Collection<TopicIF> getScope() {
    return (Collection<TopicIF>) loadCollectionField(LF_scope);
  }

  public void addTheme(TopicIF theme) {
    if (theme == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent("AssociationIF.addTheme", theme, null);
    // Notify transaction
    valueAdded(LF_scope, theme, true);
  }

  public void removeTheme(TopicIF theme) {
    if (theme == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent("AssociationIF.removeTheme", null, theme);
    // Notify transaction
    valueRemoved(LF_scope, theme, true);
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getType() {
    try {
      return (TopicIF)loadField(LF_type);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return null
      return null;
    }
  }

  public void setType(TopicIF type) {
    if (type == null)
      throw new NullPointerException("Association type must not be null.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent("AssociationIF.setType", type, getType());
    // Notify transaction
    valueChanged(LF_type, type, true);
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getReifier() {
    try {
      return (TopicIF)loadField(LF_reifier);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return null
      return null;
    }
  }
  
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null)
      CrossTopicMapException.check(_reifier, this);
    // Notify listeners
    Topic reifier = (Topic)_reifier;
    Topic oldReifier = (Topic)getReifier();
    fireEvent("ReifiableIF.setReifier", reifier, oldReifier);
    valueChanged(LF_reifier, reifier, true);
    if (oldReifier != null) oldReifier.setReified(null);
    if (reifier != null) reifier.setReified(this);
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------
  
  public String toString() {
    return ObjectStrings.toString("rdbms.Association", (AssociationIF) this);
  }
}
