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
  
  @Override
  public String getObjectId() {
    return objectId;
  }
  
  @Override
  public boolean isReadOnly() {
    return true;
  }
  
  @Override
  public TopicMapIF getTopicMap() {
    return null;
  }

  @Override
  public Collection<LocatorIF> getItemIdentifiers() {
    return (srclocs == null ? Collections.<LocatorIF>emptyList() : srclocs);
  }

  @Override
  public void addItemIdentifier(LocatorIF locator) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  @Override
  public void removeItemIdentifier(LocatorIF locator) {
    throw new ReadOnlyException();
  }
  
  @Override
  public void remove() {
    throw new ReadOnlyException();
  }

}
