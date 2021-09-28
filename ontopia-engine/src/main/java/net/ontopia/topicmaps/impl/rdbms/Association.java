/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.impl.rdbms;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.ontopia.persistence.proxy.IdentityNotFoundException;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.CompactHashSet;

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
    fireEvent(AssociationIF.EVENT_ADD_ROLE, assoc_role, null);    
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
    fireEvent(AssociationIF.EVENT_REMOVE_ROLE, null, assoc_role);    
    // Unset association property
    ((AssociationRole)assoc_role).setAssociation(null);
    // Remove role from list of roles
    valueRemoved(LF_roles, assoc_role, false);

    // Removing role from player's list of roles
    Topic player = (Topic) assoc_role.getPlayer();
    if (player != null && getTopicMap() != null) {
      // Notify listeners
      ((AssociationRole)assoc_role).fireEvent(AssociationRoleIF.EVENT_SET_PLAYER, player, null);
      player.removeRole(assoc_role);
    }
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
    fireEvent(AssociationIF.EVENT_ADD_THEME, theme, null);
    // Notify transaction
    valueAdded(LF_scope, theme, true);
  }

  public void removeTheme(TopicIF theme) {
    if (theme == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(AssociationIF.EVENT_REMOVE_THEME, null, theme);
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
    fireEvent(AssociationIF.EVENT_SET_TYPE, type, getType());
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
    fireEvent(ReifiableIF.EVENT_SET_REIFIER, reifier, oldReifier);
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
