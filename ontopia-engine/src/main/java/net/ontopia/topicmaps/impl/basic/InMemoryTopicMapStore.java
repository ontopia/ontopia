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

import java.io.File;
import java.io.IOException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.StoreNotOpenException;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.FulltextIndexManager;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: The in-memory TopicMapStoreIF implementation.
 */
public class InMemoryTopicMapStore extends AbstractTopicMapStore {

  protected TopicMapTransactionIF transaction;

  protected boolean maintainFulltextIndex = false;
  protected File indexDirectory = null;
  protected FulltextIndexManager ftmanager = null;
  
  public InMemoryTopicMapStore(boolean maintainFulltextIndex, File indexDirectory) {
    this.maintainFulltextIndex = maintainFulltextIndex;
    this.indexDirectory = indexDirectory;

    // register fulltext index manager at this point, so that we can track
    // all events that occur at loading
    if (maintainFulltextIndex) {
      this.ftmanager = new FulltextIndexManager(this);
      try {
        this.ftmanager.synchronizeIndex(true);
      } catch (IOException ioe) {
        throw new OntopiaRuntimeException(ioe);
      }
    }
  }
 
  public InMemoryTopicMapStore() {
    this(false, null);
  }

  public int getImplementation() {
    return TopicMapStoreIF.IN_MEMORY_IMPLEMENTATION;
  }

  public boolean isTransactional() {
    return false;
  }

  public TopicMapTransactionIF getTransaction() {
    // Open store automagically if store is not open at this point.
    if (!isOpen()) open();
    
    // Create a new transaction if it doesn't exist or it has been
    // deactivated.
    if (transaction == null || !transaction.isActive())
      transaction = new InMemoryTopicMapTransaction(this);
    return transaction;
  }

  public void setBaseAddress(LocatorIF base_address) {
    this.base_address = base_address;
  }

  /* -- store pool -- */
  
  public void close() {
    // return to reference or close
    close((reference != null));
  }
  
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
      if (!isOpen()) throw new StoreNotOpenException("Store is not open.");
      
      // reset reference
      reference = null;
      
      // set open flag to false and closed to true
      open = false;
      closed = true;
    }
    
    // close and dereference ftmanager
    try {
      if (ftmanager != null) {
        ftmanager.close();
        ftmanager = null;
      }
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public String getProperty(String propertyName) {
    return null; // TODO: add property support
  }
  
  /**
   * INTERNAL: Synchronizes the underlying fulltext index with the latest
   * changes in the topic map.
   * 
   * @return True if index was modified.
   */
  public boolean synchronizeFulltextIndex() throws IOException {
    if (maintainFulltextIndex)
      return synchronizeFulltextIndex(false);
    else
      return false;
  }

  // INTERNAL
  public synchronized boolean synchronizeFulltextIndex(boolean replaceIndex)
      throws IOException {
    return ftmanager.synchronizeIndex(replaceIndex);
  }  

  public void deleteFullTextIndex() throws IOException {
    if (ftmanager != null) {
      ftmanager.close();
      maintainFulltextIndex = false;
      ftmanager = null;
    }
    if (indexDirectory.exists())
      FileUtils.deleteDirectory(indexDirectory, true);
  }

  public File getIndexDirectory() {
    return indexDirectory;
  }

  // ---------------------------------------------------------------------------
  // EventManagerIF: for testing purposes only
  // ---------------------------------------------------------------------------

  @Override
  public EventManagerIF getEventManager() {    
    return (EventManagerIF)getTopicMap();
  }
  
}
