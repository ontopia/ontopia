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
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;

/**
 * INTERNAL: The read-only rdbms topic implementation.
 */
public class ReadOnlyTopic extends ReadOnlyTMObject implements TopicIF {
  
  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------
  
  @Override
  public int _p_getFieldCount() {
    return Topic.fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------
  
  @Override
  public String getClassIndicator() {
    return Topic.CLASS_INDICATOR;
  }
  
  @Override
  public String getObjectId() {
    return (id == null ? null : Topic.CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // TopicIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public Collection<LocatorIF> getSubjectLocators() {
    return this.<LocatorIF>loadCollectionField(Topic.LF_subjects);
  }

  @Override
  public void addSubjectLocator(LocatorIF subject_locator) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  @Override
  public void removeSubjectLocator(LocatorIF subject_locator) {
    throw new ReadOnlyException();
  }

  @Override
  public Collection<LocatorIF> getSubjectIdentifiers() {
    return this.<LocatorIF>loadCollectionField(Topic.LF_indicators);
  }

  @Override
  public void addSubjectIdentifier(LocatorIF subject_identifier) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  @Override
  public void removeSubjectIdentifier(LocatorIF subject_identifier) {
    throw new ReadOnlyException();
  }
  
  @Override
  public Collection<TopicNameIF> getTopicNames() {
    return this.<TopicNameIF>loadCollectionField(Topic.LF_names);
  }
  
  @Override
  public Collection<TopicNameIF> getTopicNamesByType(TopicIF type) {
    return ((ReadOnlyTopicMap)getTopicMap()).getTopicNamesByType(this, type);
  }
  
  protected void addTopicName(TopicNameIF name) {
    throw new ReadOnlyException();
  }
  
  protected void removeTopicName(TopicNameIF name) {
    throw new ReadOnlyException();
  }
  
  @Override
  public Collection<OccurrenceIF> getOccurrences() {
    return this.<OccurrenceIF>loadCollectionField(Topic.LF_occurrences);
  }
  
  @Override
  public Collection<OccurrenceIF> getOccurrencesByType(TopicIF type) {
    return ((ReadOnlyTopicMap)getTopicMap()).getOccurrencesByType(this, type);
  }

  protected void addOccurrence(OccurrenceIF occurrence) {
    throw new ReadOnlyException();
  }
  
  protected void removeOccurrence(OccurrenceIF occurrence) {
    throw new ReadOnlyException();
  }
  
  @Override
  public Collection<AssociationRoleIF> getRoles() {
    return this.<AssociationRoleIF>loadCollectionField(Topic.LF_roles);
  }
  
  @Override
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    // lookup roles by type
    if (roletype == null) {
      ReadOnlyTopicMap tm = (ReadOnlyTopicMap)getTopicMap();
      if (tm == null) {
        throw new ConstraintViolationException("Cannot retrieve roles by type when topic isn't attached to a topic map.");
      }
      return tm.getRolesByType(this, roletype);
      
    } else {
      ReadOnlyTopicMap tm = (ReadOnlyTopicMap)roletype.getTopicMap();
      return tm.getRolesByType(this, roletype);
    }
  }
  
  @Override
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype, TopicIF assoc_type) {
    // lookup roles by type
    if (roletype == null) {
      ReadOnlyTopicMap tm = (ReadOnlyTopicMap)getTopicMap();
      if (tm == null) {
        throw new ConstraintViolationException("Cannot retrieve roles by type when topic isn't attached to a topic map.");
      }
      return tm.getRolesByType(this, roletype, assoc_type);
      
    } else {
      ReadOnlyTopicMap tm = (ReadOnlyTopicMap)roletype.getTopicMap();
      return tm.getRolesByType(this, roletype, assoc_type);
    }
  }
  
  @Override
  public Collection<AssociationIF> getAssociations() {
    return ((ReadOnlyTopicMap)getTopicMap()).getAssocations(this);
  }

  @Override
  public Collection<AssociationIF> getAssociationsByType(TopicIF type) {
    return ((ReadOnlyTopicMap)getTopicMap()).getAssociationsByType(this, type);
  }
  
  @Override
  public void merge(TopicIF topic) {
    throw new ReadOnlyException();
  } 
  
  @Override
  public Collection<TopicIF> getTypes() {
    return this.<TopicIF>loadCollectionField(Topic.LF_types);
  }
  
  @Override
  public void addType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  @Override
  public void removeType(TopicIF type) {
    throw new ReadOnlyException();
  }

  @Override
	public ReifiableIF getReified() {
		String reifiedId = this.<String>loadField(Topic.LF_reified);
		if (reifiedId == null) {
      return null;
    }
		return (ReifiableIF)getTopicMap().getObjectById(reifiedId);
	}
  
  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------
  
  @Override
  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyTopic", (TopicIF)this);
  }
  
}
