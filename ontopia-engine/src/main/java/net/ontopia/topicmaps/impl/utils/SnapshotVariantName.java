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

import java.io.Reader;

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
public class SnapshotVariantName extends SnapshotTMObject implements VariantNameIF {

	protected TopicIF reifier;
  protected TopicNameIF basename;
  protected TopicIF topic;
  protected LocatorIF datatype;
  protected String value;
  protected Collection<TopicIF> scope;
  
  SnapshotVariantName(VariantNameIF original, int snapshotType, Map<TMObjectIF, SnapshotTMObject> processed) {
    this.snapshotType = snapshotType;

    switch (snapshotType) {
    case SNAPSHOT_REFERENCE:
      this.objectId = original.getObjectId();
      break;
    case SNAPSHOT_COMPLETE:
      this.objectId = original.getObjectId();
      this.srclocs = new ArrayList<LocatorIF>(original.getItemIdentifiers());
      this.basename = SnapshotTopicName.makeSnapshot(original.getTopicName(), SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      this.topic = SnapshotTopic.makeSnapshot(original.getTopic(), SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      this.datatype = original.getDataType();
      this.value = original.getValue();
      this.scope = new ArrayList<TopicIF>();
      Iterator<TopicIF> siter = original.getScope().iterator();
      while (siter.hasNext()) {
        this.scope.add(SnapshotTopic.makeSnapshot(siter.next(), snapshotType, processed));
      }
      this.reifier = SnapshotTopic.makeSnapshot(original.getReifier(),
																								SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      break;
    default:
      throw new OntopiaRuntimeException("Unknown snapshot type: " + snapshotType);
    }
  }

  public static VariantNameIF makeSnapshot(VariantNameIF original, int snapshotType, Map<TMObjectIF, SnapshotTMObject> processed) {
    return new SnapshotVariantName(original, snapshotType, processed);
  }

  // -----------------------------------------------------------------------------
  // VariantNameIF implementation
  // -----------------------------------------------------------------------------

  @Override
  public TopicNameIF getTopicName() {
    return basename;
  }
  
  @Override
  public TopicIF getTopic() {
    return topic;
  }

  @Override
  public LocatorIF getDataType() {
    return datatype;
  }
  
  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String value, LocatorIF datatype) {
    throw new ReadOnlyException();
  }

  @Override
  public Reader getReader() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setReader(Reader value, long length, LocatorIF datatype) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void setLocator(LocatorIF locator) {
    throw new ReadOnlyException();
  }

  @Override
  public long getLength() {
    return (value == null ? 0 : value.length());
  }
  
  @Override
  public Collection<TopicIF> getScope() {
    return (scope == null ? Collections.<TopicIF>emptyList() : scope);
  }
  
  @Override
  public void addTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }
  
  @Override
  public void removeTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  @Override
  public TopicIF getReifier() {
    return reifier;
	}
  
  @Override
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
	}

  @Override
  public String toString() {
    return "[SnapshotVariantName, " + getObjectId() + "]";
  }
  

}
