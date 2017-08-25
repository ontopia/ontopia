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
import net.ontopia.persistence.proxy.IdentityNotFoundException;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.CompactHashSet;

/**
 * INTERNAL: The read-only rdbms association implementation.
 */
public class ReadOnlyAssociation extends ReadOnlyTMObject implements AssociationIF {

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return Association.fields.length;
  }

  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public String getClassIndicator() {
    return Association.CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : Association.CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // AssociationIF implementation
  // ---------------------------------------------------------------------------

  public Collection<TopicIF> getRoleTypes() {
    Collection<TopicIF> result = new CompactHashSet<TopicIF>();
    for (AssociationRoleIF role : this.<AssociationRoleIF>loadCollectionField(Association.LF_roles)) {
      TopicIF type = role.getType();
      if (type != null)
        result.add(role.getType());
    }
    return result;
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    Collection<AssociationRoleIF> result = new CompactHashSet<AssociationRoleIF>();
    for (AssociationRoleIF role : this.<AssociationRoleIF>loadCollectionField(Association.LF_roles)) {
      if (role.getType() == roletype)
        result.add(role);
    }
    return result;
  }

  public Collection<AssociationRoleIF> getRoles() {
    try {
      return this.<AssociationRoleIF>loadCollectionField(Association.LF_roles);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return empty set
      return Collections.EMPTY_SET;
    }
  }

  protected void addRole(AssociationRoleIF assoc_role) {
    throw new ReadOnlyException();
  }

  protected void removeRole(AssociationRoleIF assoc_role) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  public Collection<TopicIF> getScope() {
    return this.<TopicIF>loadCollectionField(Association.LF_scope);
  }

  public void addTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  public void removeTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getType() {
    try {
      return this.<TopicIF>loadField(Association.LF_type);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return null
      return null;
    }
  }

  public void setType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getReifier() {
    try {
      return this.<TopicIF>loadField(Association.LF_reifier);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return null
      return null;
    }
  }
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------
  
  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyAssociation", (AssociationIF) this);
  }
  
}
