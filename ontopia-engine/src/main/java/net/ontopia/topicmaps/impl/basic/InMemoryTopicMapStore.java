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

package net.ontopia.topicmaps.impl.basic;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.StoreNotOpenException;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: The in-memory TopicMapStoreIF implementation.
 */
public class InMemoryTopicMapStore extends AbstractTopicMapStore {

  protected TopicMapTransactionIF transaction;

  @Override
  public int getImplementation() {
    return TopicMapStoreIF.IN_MEMORY_IMPLEMENTATION;
  }

  @Override
  public boolean isTransactional() {
    return false;
  }

  @Override
  public TopicMapTransactionIF getTransaction() {
    // Open store automagically if store is not open at this point.
    if (!isOpen()) {
      open();
    }
    
    // Create a new transaction if it doesn't exist or it has been
    // deactivated.
    if (transaction == null || !transaction.isActive()) {
      transaction = new InMemoryTopicMapTransaction(this);
    }
    return transaction;
  }

  @Override
  public void setBaseAddress(LocatorIF base_address) {
    this.base_address = base_address;
  }

  /* -- store pool -- */
  
  @Override
  public void close() {
    // return to reference or close
    close((reference != null));
  }
  
  @Override
  public void close(boolean returnStore) {
    
    if (returnStore) {
      // return store
      if (reference != null) {
        
        // notify topic map reference that store has been closed.
        reference.storeClosed(this);
      } else {
        throw new OntopiaRuntimeException("Cannot return store when not attached to topic map reference.");
      }
      
    } else {
      // physically close store
      if (!isOpen()) {
        throw new StoreNotOpenException("Store is not open.");
      }
      
      // reset reference
      reference = null;
      
      // set open flag to false and closed to true
      open = false;
      closed = true;
    }
  }

  @Override
  public String getProperty(String propertyName) {
    return null; // TODO: add property support
  }
  
  // ---------------------------------------------------------------------------
  // EventManagerIF: for testing purposes only
  // ---------------------------------------------------------------------------

  @Override
  public EventManagerIF getEventManager() {    
    return (EventManagerIF)getTopicMap();
  }
  
}
