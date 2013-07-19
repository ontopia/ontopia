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

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.persistence.proxy.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 */
public abstract class ReadOnlyTMObject extends AbstractROPersistent implements TMObjectIF {

  public ReadOnlyTMObject() {
  }
  
  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  /**
   * INTERNAL: Returns the token that can be used to indicate the
   * class of this instance. This indicator is currently only used by
   * item identifiers.
   */
  public abstract String getClassIndicator();
  
  long getLongId() {
    return ((Long)id.getKey(0)).longValue();
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public abstract String getObjectId();

  public boolean isReadOnly() {
    return true;
  }

  public TopicMapIF getTopicMap() {
    try {
      return (TopicMapIF)loadField(TMObject.LF_topicmap);
    } catch (IdentityNotFoundException e) {
      // object has been deleted by somebody else, so return null
      return null;
    }
  }

  public Collection<LocatorIF> getItemIdentifiers() {
    return (Collection<LocatorIF>) loadCollectionField(TMObject.LF_sources);
  }

  public void addItemIdentifier(LocatorIF source_locator) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  public void removeItemIdentifier(LocatorIF source_locator) {
    throw new ReadOnlyException();
  }

  public void remove() {
    throw new ReadOnlyException();
  }
  
}
