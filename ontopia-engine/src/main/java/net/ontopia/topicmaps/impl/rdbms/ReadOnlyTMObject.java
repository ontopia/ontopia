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
import net.ontopia.persistence.proxy.AbstractROPersistent;
import net.ontopia.persistence.proxy.IdentityNotFoundException;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;

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
  
  protected long getLongId() {
    return (Long) id.getKey(0);
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
      return this.<TopicMapIF>loadField(TMObject.LF_topicmap);
    } catch (IdentityNotFoundException e) {
      // object has been deleted by somebody else, so return null
      return null;
    }
  }

  public Collection<LocatorIF> getItemIdentifiers() {
    return this.<LocatorIF>loadCollectionField(TMObject.LF_sources);
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
