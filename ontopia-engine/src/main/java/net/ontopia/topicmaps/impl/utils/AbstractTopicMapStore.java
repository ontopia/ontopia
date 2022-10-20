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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.NotRemovableException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: An abstract TopicMapStoreIF implementation.
 */

public abstract class AbstractTopicMapStore implements TopicMapStoreIF {

  protected LocatorIF base_address;
  
  protected boolean open;
  protected boolean closed;
  protected boolean deleted;
  protected boolean readonly;
  protected boolean readonlySet;
  
  protected TopicMapReferenceIF reference;

  public TopicMapListenerIF[] topic_listeners;

  @Override
  public boolean isOpen() {
    return open;
  }
  
  @Override
  public void open() {
    if (deleted) {
      throw new OntopiaRuntimeException("A deleted store cannot be reopened.");
    }
        
    // Set open flag to true
    open = true;
  }
  
  public abstract TopicMapTransactionIF getTransaction();

  @Override
  public TopicMapIF getTopicMap() {
    return getTransaction().getTopicMap();
  }

  @Override
  public LocatorIF getBaseAddress() {
    return base_address;
  }
  
  @Override
  public void commit() {
    getTransaction().commit();
  }

  @Override
  public void abort() {
    getTransaction().abort();
  }

  //! public void clear() {
  //!   // remove all the objects from the topic map
  //!   TopicMapIF tm = getTopicMap();
  //!   tm.clear();
  //!   //! close();
  //! }

  @Override
  public void delete(boolean force) throws NotRemovableException {
    // Do nothing except closing the store, since we do not know how
    // to delete the topic map here. Implementations have to implement
    // deletion themselves.

    TopicMapIF tm = getTopicMap();

    if (!force) {
      // If we're not forcing, complain if the topic map contains any data.
      if (!tm.getTopics().isEmpty()) {
        throw new NotRemovableException("Cannot delete topic map when it contains topics.");
      }
      if (!tm.getAssociations().isEmpty()) {
        throw new NotRemovableException("Cannot delete topic map when it contains associations.");
      }
    }
    
    // Remove all the objects from the topic map
    tm.clear();

    close();
    deleted = true;
  }
  
  @Override
  public boolean isReadOnly() {
    return readonly;
  }

  public void setReadOnly(boolean readonly) {
    if (readonlySet) {
      throw new OntopiaRuntimeException("Readonly flag has already been set.");
    }
    this.readonly = readonly;
    this.readonlySet = true;
  }

  /* -- topic map reference -- */

  @Override
  public TopicMapReferenceIF getReference() {
    return reference;
  }

  @Override
  public void setReference(TopicMapReferenceIF reference) {
    this.reference = reference;
  }

  /* -- store pool -- */

  public boolean validate() {
    // store is valid unless closed
    return !closed;
  }

  public abstract void close(boolean returnToPool);

  // -----------------------------------------------------------------------------
  // TopicMapListenerIF implementation
  // -----------------------------------------------------------------------------
  
  public void setTopicListeners(TopicMapListenerIF[] listeners) {
    this.topic_listeners = listeners;
  }

  public abstract EventManagerIF getEventManager();

}
