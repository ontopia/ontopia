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
  // Data members
  // ---------------------------------------------------------------------------
  
  public ReadOnlyTopic() {
  }
  
  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------
  
  public int _p_getFieldCount() {
    return Topic.fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------
  
  public String getClassIndicator() {
    return Topic.CLASS_INDICATOR;
  }
  
  public String getObjectId() {
    return (id == null ? null : Topic.CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // TopicIF implementation
  // ---------------------------------------------------------------------------

  public Collection<LocatorIF> getSubjectLocators() {
    return (Collection<LocatorIF>) loadCollectionField(Topic.LF_subjects);
  }

  public void addSubjectLocator(LocatorIF subject_locator) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  public void removeSubjectLocator(LocatorIF subject_locator) {
    throw new ReadOnlyException();
  }

  public Collection<LocatorIF> getSubjectIdentifiers() {
    return (Collection<LocatorIF>) loadCollectionField(Topic.LF_indicators);
  }

  public void addSubjectIdentifier(LocatorIF subject_identifier) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  public void removeSubjectIdentifier(LocatorIF subject_identifier) {
    throw new ReadOnlyException();
  }
  
  public Collection<TopicNameIF> getTopicNames() {
    return (Collection<TopicNameIF>) loadCollectionField(Topic.LF_names);
  }
  
  public Collection<TopicNameIF> getTopicNamesByType(TopicIF type) {
    return ((ReadOnlyTopicMap)getTopicMap()).getTopicNamesByType(this, type);
  }
  
  void addTopicName(TopicNameIF name) {
    throw new ReadOnlyException();
  }
  
  void removeTopicName(TopicNameIF name) {
    throw new ReadOnlyException();
  }
  
  public Collection<OccurrenceIF> getOccurrences() {
    return (Collection<OccurrenceIF>) loadCollectionField(Topic.LF_occurrences);
  }
  
  public Collection<OccurrenceIF> getOccurrencesByType(TopicIF type) {
    return ((ReadOnlyTopicMap)getTopicMap()).getOccurrencesByType(this, type);
  }

  void addOccurrence(OccurrenceIF occurrence) {
    throw new ReadOnlyException();
  }
  
  void removeOccurrence(OccurrenceIF occurrence) {
    throw new ReadOnlyException();
  }
  
  public Collection<AssociationRoleIF> getRoles() {
    return (Collection<AssociationRoleIF>) loadCollectionField(Topic.LF_roles);
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    // lookup roles by type
    if (roletype == null) {
      ReadOnlyTopicMap tm = (ReadOnlyTopicMap)getTopicMap();
      if (tm == null)
        throw new ConstraintViolationException("Cannot retrieve roles by type when topic isn't attached to a topic map.");
      return tm.getRolesByType(this, roletype);
      
    } else {
      ReadOnlyTopicMap tm = (ReadOnlyTopicMap)roletype.getTopicMap();
      return tm.getRolesByType(this, roletype);
    }
  }
  
  public Collection getRolesByType(TopicIF roletype, TopicIF assoc_type) {
    // lookup roles by type
    if (roletype == null) {
      ReadOnlyTopicMap tm = (ReadOnlyTopicMap)getTopicMap();
      if (tm == null)
        throw new ConstraintViolationException("Cannot retrieve roles by type when topic isn't attached to a topic map.");
      return tm.getRolesByType(this, roletype, assoc_type);
      
    } else {
      ReadOnlyTopicMap tm = (ReadOnlyTopicMap)roletype.getTopicMap();
      return tm.getRolesByType(this, roletype, assoc_type);
    }
  }
  
  public Collection<AssociationIF> getAssociations() {
    return ((ReadOnlyTopicMap)getTopicMap()).getAssocations(this);
  }

  public Collection<AssociationIF> getAssociationsByType(TopicIF type) {
    return ((ReadOnlyTopicMap)getTopicMap()).getAssociationsByType(this, type);
  }
  
  public void merge(TopicIF topic) {
    throw new ReadOnlyException();
  } 
  
  public Collection getTypes() {
    return loadCollectionField(Topic.LF_types);
  }
  
  public void addType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  public void removeType(TopicIF type) {
    throw new ReadOnlyException();
  }

	public ReifiableIF getReified() {
		String reifiedId = (String)loadField(Topic.LF_reified);
		if (reifiedId == null) return null;
		return (ReifiableIF)getTopicMap().getObjectById(reifiedId);
	}
  
  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------
  
  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyTopic", (TopicIF)this);
  }
  
}
