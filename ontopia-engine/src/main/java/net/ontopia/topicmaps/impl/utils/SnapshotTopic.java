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

package net.ontopia.topicmaps.impl.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public class SnapshotTopic extends SnapshotTMObject implements TopicIF {

	protected ReifiableIF reified;

  protected Collection<LocatorIF> sublocs;
  protected Collection<LocatorIF> subinds;

  protected Collection<TopicNameIF> basenames;
  protected Collection<OccurrenceIF> occurrences;
  protected Collection<TopicIF> types;
  
  SnapshotTopic(TopicIF original, int snapshotType, Map<TMObjectIF, SnapshotTMObject> processed) {
    this.snapshotType = snapshotType;

    switch (snapshotType) {
    case SNAPSHOT_REFERENCE:
      this.objectId = original.getObjectId();
      break;
    case SNAPSHOT_COMPLETE:
      this.objectId = original.getObjectId();
      this.sublocs = new ArrayList<LocatorIF>(original.getSubjectLocators());
      this.subinds = new ArrayList<LocatorIF>(original.getSubjectIdentifiers());
      this.srclocs = new ArrayList<LocatorIF>(original.getItemIdentifiers());
      this.basenames = new ArrayList<TopicNameIF>();
      Iterator<TopicNameIF> biter = original.getTopicNames().iterator();
      while (biter.hasNext()) {
        this.basenames.add(SnapshotTopicName.makeSnapshot(biter.next(), snapshotType, processed));
      }
      this.occurrences = new ArrayList<OccurrenceIF>();
      Iterator<OccurrenceIF> oiter = original.getOccurrences().iterator();
      while (oiter.hasNext()) {
        this.occurrences.add(SnapshotOccurrence.makeSnapshot(oiter.next(), snapshotType, processed));
      }
      this.types = new ArrayList<TopicIF>();
      Iterator<TopicIF> titer = original.getTypes().iterator();
      while (titer.hasNext()) {
        this.types.add(SnapshotTopic.makeSnapshot(titer.next(), snapshotType, processed));
      }
			// TODO: add support for this when needed
      //! this.reified = SnapshotTopic.makeSnapshot(original.getReified(),
      //!                                        SnapshotTopic.SNAPSHOT_REFERENCE);      
      break;
    default:
      throw new OntopiaRuntimeException("Unknown snapshot type: " + snapshotType);
    }
  }

  public static TopicIF makeSnapshot(TopicIF original, int snapshotType, Map<TMObjectIF, SnapshotTMObject> processed) {
    if (original == null) {
      return null; // this avoids a thousand ifs elsewhere
    } else if (processed.containsKey(original)) {
      return (TopicIF)processed.get(original);
    }
		
		SnapshotTopic st = new SnapshotTopic(original, snapshotType, processed);
		processed.put(original, st);
		return st;
  }

  // ---------------------------------------------------------------------------
  // TopicIF implementation
  // ---------------------------------------------------------------------------

  @Override
  public Collection<LocatorIF> getSubjectLocators() {
    return (sublocs == null ? Collections.<LocatorIF>emptyList() : sublocs);
	}

  @Override
  public void addSubjectLocator(LocatorIF subject_locator) throws ConstraintViolationException {
    throw new UnsupportedOperationException();
    // should this be throw new ReadOnlyException(); ?
	}

  @Override
	public void removeSubjectLocator(LocatorIF subject_locator) {
    throw new UnsupportedOperationException();
    // should this be throw new ReadOnlyException(); ?
	}

  @Override
  public Collection<LocatorIF> getSubjectIdentifiers() {
    return (subinds == null ? Collections.<LocatorIF>emptyList() : subinds);
  }

  @Override
  public void addSubjectIdentifier(LocatorIF locator) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  @Override
  public void removeSubjectIdentifier(LocatorIF loc) {
    throw new ReadOnlyException();
  }

  @Override
  public Collection<TopicIF> getTypes() {
    return (types == null ? Collections.<TopicIF>emptyList() : types);
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
  public Collection<TopicNameIF> getTopicNames() {
    return (basenames == null ? Collections.<TopicNameIF>emptyList() : basenames);
  }
  
  @Override
  public Collection<TopicNameIF> getTopicNamesByType(TopicIF type) {
    return Collections.emptyList();
  }

  @Override
  public Collection<OccurrenceIF> getOccurrences() {
    return (occurrences == null ? Collections.<OccurrenceIF>emptyList() : occurrences);
  }

  @Override
  public Collection<OccurrenceIF> getOccurrencesByType(TopicIF type) {
    return Collections.emptyList();
  }
  
  @Override
  public Collection<AssociationRoleIF> getRoles() {
    return Collections.emptyList();
  }
  
  @Override
  public Collection<AssociationRoleIF> getRolesByType(TopicIF rtype) {
    return Collections.emptyList();
  }
  
  @Override
  public Collection<AssociationRoleIF> getRolesByType(TopicIF rtype, TopicIF atype) {
    return Collections.emptyList();
  }
  
  @Override
  public Collection<AssociationIF> getAssociations() {
    return Collections.emptyList();
  }

  @Override
  public Collection<AssociationIF> getAssociationsByType(TopicIF type) {
    return Collections.emptyList();
  }

  @Override
  public void merge(TopicIF topic) {
    throw new ReadOnlyException();
  }

  @Override
  public String toString() {
    return "[SnapshotTopic, " + getObjectId() + "]";
  }
  
  @Override
	public ReifiableIF getReified() {
		return reified;
	}

}
