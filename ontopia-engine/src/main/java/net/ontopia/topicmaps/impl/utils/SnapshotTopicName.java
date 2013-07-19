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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public class SnapshotTopicName extends SnapshotTMObject implements TopicNameIF {

	protected TopicIF reifier;
  protected TopicIF topic;
  protected String value;
  protected Collection<VariantNameIF> variants;
  protected Collection<TopicIF> scope;
  protected TopicIF type;
  
  SnapshotTopicName(TopicNameIF original, int snapshotType, Map<TMObjectIF, SnapshotTMObject> processed) {
    this.snapshotType = snapshotType;

    switch (snapshotType) {
    case SNAPSHOT_REFERENCE:
      this.objectId = original.getObjectId();
      break;
    case SNAPSHOT_COMPLETE:
      this.objectId = original.getObjectId();
      this.srclocs = new ArrayList<LocatorIF>(original.getItemIdentifiers());
      this.topic = SnapshotTopic.makeSnapshot(original.getTopic(), SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      this.value = original.getValue();
      this.variants = new ArrayList<VariantNameIF>();
      Iterator<VariantNameIF> viter = original.getVariants().iterator();
      while (viter.hasNext()) {
        this.variants.add(SnapshotVariantName.makeSnapshot(viter.next(), snapshotType, processed));
      }
      this.scope = new ArrayList<TopicIF>();
      Iterator<TopicIF> siter = original.getScope().iterator();
      while (siter.hasNext()) {
        this.scope.add(SnapshotTopic.makeSnapshot(siter.next(), snapshotType, processed));
      }
      this.type = SnapshotTopic.makeSnapshot(original.getType(),
                                             SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      this.reifier = SnapshotTopic.makeSnapshot(original.getReifier(),
																								SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      break;
    default:
      throw new OntopiaRuntimeException("Unknown snapshot type: " +
                                        snapshotType);
    }
  }

  public static TopicNameIF makeSnapshot(TopicNameIF original, int snapshotType, Map<TMObjectIF, SnapshotTMObject> processed) {
		return new SnapshotTopicName(original, snapshotType, processed);
  }

  // ---------------------------------------------------------------------------
  // TopicNameIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getTopic() {
    return topic;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    throw new ReadOnlyException();
  }
  
  public Collection<VariantNameIF> getVariants() {
    Collection<VariantNameIF> empty = Collections.emptyList();
    return (variants == null ? empty : variants);
  }
  
  public Collection<TopicIF> getScope() {
    Collection<TopicIF> empty = Collections.emptyList();
    return (scope == null ? empty : scope);
  }
  
  public void addTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }
  
  public void removeTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }
  
  public TopicIF getType() {
    return type;
  }
  
  public void setType(TopicIF type) {
    throw new ReadOnlyException();
  }

  public TopicIF getReifier() {
    return reifier;
	}
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
	}

  public String toString() {
    return "[SnapshotTopicName, " + getObjectId() + "]";
  }
  

}
