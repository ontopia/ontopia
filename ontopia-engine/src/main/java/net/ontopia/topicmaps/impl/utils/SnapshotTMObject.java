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

import java.util.Collection;
import java.util.Collections;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ConstraintViolationException;

/**
 * INTERNAL: 
 */
public abstract class SnapshotTMObject implements TMObjectIF {

  public static final int SNAPSHOT_REFERENCE = 1;
  public static final int SNAPSHOT_COMPLETE = 2;

  protected int snapshotType;

  protected String objectId;
  protected Collection<LocatorIF> srclocs;
  
  // -----------------------------------------------------------------------------
  // TMObjectIF implementation
  // -----------------------------------------------------------------------------
  
  public String getObjectId() {
    return objectId;
  }
  
  public boolean isReadOnly() {
    return true;
  }
  
  public TopicMapIF getTopicMap() {
    return null;
  }

  public Collection<LocatorIF> getItemIdentifiers() {
    Collection<LocatorIF> empty = Collections.emptyList();
    return (srclocs == null ? empty : srclocs);
  }

  public void addItemIdentifier(LocatorIF locator) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  public void removeItemIdentifier(LocatorIF locator) {
    throw new ReadOnlyException();
  }
  
  public void remove() {
    throw new ReadOnlyException();
  }

}
