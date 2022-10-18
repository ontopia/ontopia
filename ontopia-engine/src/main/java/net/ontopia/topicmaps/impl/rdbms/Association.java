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
import java.util.Objects;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.IdentityNotFoundException;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.CompactHashSet;

/**
 * INTERNAL: The rdbms association implementation.
 */

public class Association extends TMObject implements AssociationIF {
  
  public static final String CLASS_INDICATOR = "A";

  // ---------------------------------------------------------------------------
  // Persistent property declarations
  // ---------------------------------------------------------------------------

  protected static final int LF_scope = 2;
  protected static final int LF_type = 3;
  protected static final int LF_roles = 4;
  protected static final int LF_reifier = 5;
  protected static final String[] fields = {"sources", "topicmap", "scope", "type", "roles", "reifier"};

  @Override
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

  public Association() {  
  }

  public Association(TransactionIF txn) {
    super(txn);
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public int _p_getFieldCount() {
    return fields.length;
  }

  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public String getClassIndicator() {
    return CLASS_INDICATOR;
  }

  @Override
  public String getObjectId() {
    return (id == null ? null : CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // AssociationIF implementation
  // ---------------------------------------------------------------------------
  
  /**
   * INTERNAL: Sets the topic map that the object belongs to. [parent]
   */
  protected void setTopicMap(TopicMap topicmap) {
    // Notify transaction
    transactionChanged(topicmap);
    valueChanged(LF_topicmap, topicmap, true);

    // Inform association roles
    for (AssociationRole role : this.<AssociationRole>loadCollectionField(LF_roles)) {
      role.setTopicMap(topicmap);
    }
  }

  @Override
  public Collection<TopicIF> getRoleTypes() {
    Collection<TopicIF> result = new CompactHashSet<TopicIF>();
    for (AssociationRoleIF role : this.<AssociationRoleIF>loadCollectionField(LF_roles)) {
      TopicIF type = role.getType();
      if (type != null)
        result.add(role.getType());
    }
    return result;
  }
  
  @Override
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    Objects.requireNonNull(roletype, "Role type must not be null.");
    CrossTopicMapException.check(roletype, this);
    Collection<AssociationRoleIF> result = new CompactHashSet<AssociationRoleIF>();
    for (AssociationRoleIF role : this.<AssociationRoleIF>loadCollectionField(LF_roles))
      if (role.getType() == roletype)
        result.add(role);
    return result;
  }

  @Override
  public Collection<AssociationRoleIF> getRoles() {
    try {
      return this.<AssociationRoleIF>loadCollectionField(LF_roles);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return empty set
      return Collections.EMPTY_SET;
    }
  }

  protected void addRole(AssociationRoleIF assoc_role) {
    Objects.requireNonNull(assoc_role, MSG_NULL_ARGUMENT);
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

  protected void removeRole(AssociationRoleIF assoc_role) {
    Objects.requireNonNull(assoc_role, MSG_NULL_ARGUMENT);
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

  @Override
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

  @Override
  public Collection<TopicIF> getScope() {
    return this.<TopicIF>loadCollectionField(LF_scope);
  }

  @Override
  public void addTheme(TopicIF theme) {
    Objects.requireNonNull(theme, MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(AssociationIF.EVENT_ADD_THEME, theme, null);
    // Notify transaction
    valueAdded(LF_scope, theme, true);
  }

  @Override
  public void removeTheme(TopicIF theme) {
    Objects.requireNonNull(theme, MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(AssociationIF.EVENT_REMOVE_THEME, null, theme);
    // Notify transaction
    valueRemoved(LF_scope, theme, true);
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getType() {
    try {
      return this.<TopicIF>loadField(LF_type);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return null
      return null;
    }
  }

  @Override
  public void setType(TopicIF type) {
    Objects.requireNonNull(type, "Association type must not be null.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent(AssociationIF.EVENT_SET_TYPE, type, getType());
    // Notify transaction
    valueChanged(LF_type, type, true);
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public TopicIF getReifier() {
    try {
      return this.<TopicIF>loadField(LF_reifier);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return null
      return null;
    }
  }
  
  @Override
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null)
      CrossTopicMapException.check(_reifier, this);
    if (DuplicateReificationException.check(this, _reifier)) { return; }
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
  
  @Override
  public String toString() {
    return ObjectStrings.toString("rdbms.Association", (AssociationIF) this);
  }

  @Override
  public void syncAfterMerge(IdentityIF source, IdentityIF target) {
    super.syncFieldsAfterMerge(source, target, LF_type, LF_reifier, LF_scope);
  }
}
