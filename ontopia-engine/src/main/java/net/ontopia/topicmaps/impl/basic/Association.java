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

package net.ontopia.topicmaps.impl.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.UniqueSet;

/**
 * INTERNAL: The basic association implementation.
 */

public class Association extends TMObject implements AssociationIF {

  private static final long serialVersionUID = -8986947932370957132L;
  
  protected TopicIF reifier;
  protected TopicIF type;
  protected UniqueSet<TopicIF> scope;
  protected Set<AssociationRoleIF> roles;
  
  protected Association(TopicMap tm) {
    super(tm);
    roles = topicmap.cfactory.makeSmallSet();
  }
  
  // -----------------------------------------------------------------------------
  // AssociationIF implementation
  // -----------------------------------------------------------------------------
  
  /**
   * INTERNAL: Sets the topic map that the object belongs to. [parent]
   */
  protected void setTopicMap(TopicMap parent) {
    // (De)reference pooled sets
    if (scope != null) {
      if (parent == null) {
        topicmap.setpool.dereference(scope);
      } else {
        scope = topicmap.setpool.get(scope);
      }
    }

    // Set parent
    this.parent = parent;
  }

  @Override
  public Collection<TopicIF> getRoleTypes() {
    Collection<TopicIF> result = topicmap.cfactory.makeSmallSet();
    synchronized (roles) {    
      Iterator<AssociationRoleIF> iter = roles.iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = iter.next();
        TopicIF type = role.getType();
        if (type != null) {
          result.add(role.getType());
        }
      }
    }
    return result;
  }
  
  @Override
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    Objects.requireNonNull(roletype, "Role type must not be null.");
    CrossTopicMapException.check(roletype, this);
    Collection<AssociationRoleIF> result = topicmap.cfactory.makeSmallSet();
    synchronized (roles) {
      Iterator<AssociationRoleIF> iter = roles.iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = iter.next();
        if (role.getType() == roletype) {
          result.add(role);
        }
      }
    }
    return result;
  }

  @Override
  public Collection<AssociationRoleIF> getRoles() {
    return Collections.unmodifiableSet(roles);
  }

  protected void addRole(AssociationRoleIF _assoc_role) {
    AssociationRole assoc_role = (AssociationRole)_assoc_role;
    Objects.requireNonNull(assoc_role, MSG_NULL_ARGUMENT);
    // Check to see if association role is already a member of this association
    if (assoc_role.parent == this) {
      return;
    }
    // Check if used elsewhere.
    if (assoc_role.parent != null) {
      throw new ConstraintViolationException("Moving objects is not allowed.");
    }
    // Notify listeners
    fireEvent(AssociationIF.EVENT_ADD_ROLE, assoc_role, null);    
    // Set association property
    assoc_role.setAssociation(this);
    // Add association role to list of association roles
    roles.add(assoc_role);
    // Make sure role is added to player's list
    Topic player = (Topic) assoc_role.getPlayer();
    if (player != null && parent != null) {
      player.addRole(assoc_role);
    }
  }

  protected void removeRole(AssociationRoleIF _assoc_role) {
    AssociationRole assoc_role = (AssociationRole)_assoc_role;
    Objects.requireNonNull(assoc_role, MSG_NULL_ARGUMENT);
    // Check to see if association role is not a member of this association
    if (assoc_role.parent != this) {
      return;
    }
    // Notify listeners
    fireEvent(AssociationIF.EVENT_REMOVE_ROLE, null, assoc_role);
    // Remove role from list of roles
    roles.remove(assoc_role);
    // Removing role from player's list of roles
    Topic player = (Topic) assoc_role.getPlayer();
    if (player != null && parent != null) {
      player.removeRole(assoc_role);
    }
    // Unset association property
    assoc_role.setAssociation(null);
  }

  @Override
  public void remove() {
    if (topicmap != null) {
      DeletionUtils.removeDependencies(this);
      topicmap.removeAssociation(this);
    }
  }

  // -----------------------------------------------------------------------------
  // ScopedIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public Collection<TopicIF> getScope() {
    // Return scope defined on this object
    return (scope == null ? Collections.<TopicIF>emptyList() : scope);
  }

  @Override
  public void addTheme(TopicIF theme) {
    Objects.requireNonNull(theme, MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(AssociationIF.EVENT_ADD_THEME, theme, null);
    // Add theme to scope
    if (scope == null) {
      scope = topicmap.setpool.get(Collections.<TopicIF>emptySet());
    }
    scope = topicmap.setpool.add(scope, theme, true);
  }

  @Override
  public void removeTheme(TopicIF theme) {
    Objects.requireNonNull(theme, MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent(AssociationIF.EVENT_REMOVE_THEME, null, theme);
    // Remove theme from scope
    if (scope == null) {
      return;
    }
    scope = topicmap.setpool.remove(scope, theme, true);
  }

  // -----------------------------------------------------------------------------
  // TypedIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public TopicIF getType() {
    return type;
  }

  @Override
  public void setType(TopicIF type) {
    Objects.requireNonNull(type, "Association type must not be null.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent(AssociationIF.EVENT_SET_TYPE, type, getType());
    this.type = type;
  }
  
  // -----------------------------------------------------------------------------
  // ReifiableIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public TopicIF getReifier() {
    return reifier;
  }
  
  @Override
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null) {
      CrossTopicMapException.check(_reifier, this);
    }
    if (DuplicateReificationException.check(this, _reifier)) { return; }
    // Notify listeners
    Topic reifier = (Topic)_reifier;
    Topic oldReifier = (Topic)getReifier();
    fireEvent(ReifiableIF.EVENT_SET_REIFIER, reifier, oldReifier);
    this.reifier = reifier;
    if (oldReifier != null) {
      oldReifier.setReified(null);
    }
    if (reifier != null) {
      reifier.setReified(this);
    }
  }

  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------
  
  @Override
  public String toString() {
    return ObjectStrings.toString("basic.Association", (AssociationIF)this);
  }
  
}
