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

package net.ontopia.topicmaps.entry;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.StoreDeletedException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: An abstract topic map reference class that retrieves
 * topic maps referenced through URLs. Subclasses should implement the
 * loadTopicMap method.<p>
 *
 * @since 1.3.2
 */
public abstract class AbstractURLTopicMapReference
  extends AbstractTopicMapReference {

  protected URL url;
  protected LocatorIF base_address;
  protected boolean duplicate_suppression;

  protected boolean reuse_store = true;
  protected TopicMapStoreIF store;

  protected boolean maintainFulltextIndexes;
  protected String indexDirectory;

  public AbstractURLTopicMapReference(String id, String title, URL url,
                                      LocatorIF base_address) {
    super(id, title);
    this.url = url;
    this.base_address = base_address;
    if (base_address == null) {
      this.base_address = new URILocator(url);
    }
  }

  /**
   * INTERNAL: Returns the URL of the topic map pointed at.
   */
  public URL getURL() {
    return url;
  }

  /**
   * INTERNAL: Returns the base address locator to be used when loading
   * the topic map.
   */
  public LocatorIF getBaseAddress() {
    return base_address;
  }

  /**
   * INTERNAL: Sets the base address locator to be used when loading
   * the topic map.
   */
  public void setBaseAddress(LocatorIF base_address) {
    this.base_address = base_address;
  }

  /**
   * INTERNAL: Gets the duplicate suppression flag. If the flag is
   * true duplicate suppression is to be performed when loading the
   * topic maps.
   *
   * @since 1.4.2
   */
  public boolean getDuplicateSuppression() {
    return duplicate_suppression;
  }

  /**
   * INTERNAL: Sets the duplicate suppression flag. If the flag is
   * true duplicate suppression is to be performed when loading the
   * topic maps.
   *
   * @since 1.4.2
   */
  public void setDuplicateSuppression(boolean duplicate_suppression) {
    this.duplicate_suppression = duplicate_suppression;
  }

  /**
   * INTERNAL: Flag that indicates whether the same store should be
   * returned by the createStore(boolean) method on every. If the flag
   * is false then a new store will be returned every time. Returning
   * a new store every time effectively means that the referenced
   * topic map will be loaded on every method call.
   *
   * @since 2.1
   */
  public boolean getReuseStore() {
    return reuse_store;
  }

  /**
   * INTERNAL: Sets the reuse_store flag.
   *
   * @since 2.1
   */
  public void setReuseStore(boolean reuse_store) {
    this.reuse_store = reuse_store;
  }

  @Override
  public synchronized void open() {
    // ignore if already open
    if (isOpen()) {
      return;
    }
    if (isDeleted()) {
      throw new StoreDeletedException("Topic map has been deleted through this reference.");
    }
    // make sure store is loaded
    if (reuse_store && store == null) {

      // load topic map store
      boolean readonly = false; // WARNING: store always read-write!!!
      TopicMapStoreIF store = null;
      try {
        store = loadTopicMap(readonly).getStore();
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
      // register store
      store.setReference(this);
      // register listeners
      ((AbstractTopicMapStore)store).setTopicListeners(getTopicListeners());
    
      this.store = store;
    }
    this.isopen = true;
  }

  @Override
  public synchronized void close() {    
    // close and dereference store
    if (store != null) {
      if (store.isOpen()) {
        store.close();
      }
      store = null;
    }
    this.isopen = false;
  }

  /**
   * INTERNAL: Deletes the topic map pointed to. The reference is closed
   * before the topic map is deleted. Note that only URIs pointing to
   * through files can actually be deleted, i.e. "file:" URLs.
   *
   * @since 1.3.2
   */
  @Override
  public synchronized void delete() {
    if (source == null) {
      throw new UnsupportedOperationException("This reference cannot be deleted as it does not belong to a source.");
    }
    if (!source.supportsDelete()) {
      throw new UnsupportedOperationException("This reference cannot be deleted as the source does not allow deleting.");
    }
    
    // ignore if store already deleted
    if (isDeleted()) {
      return;
    }

    // close reference
    close();
    // delete file.
    if ("file".equals(url.getProtocol())) {
      File file = new File(url.getFile());
      // FIXME: complain if file does not exist?
      file.delete();
    }
    this.deleted = true;
  }
  
  @Override
  public synchronized TopicMapStoreIF createStore(boolean readonly) throws IOException {
    if (!isOpen()) {
      open();
    }

    if (reuse_store && store != null) {
      return store;
    }

    // load topic map store
    TopicMapStoreIF store = loadTopicMap(readonly).getStore();
    // register store
    store.setReference(this);
    // register listeners
    ((AbstractTopicMapStore)store).setTopicListeners(getTopicListeners());
    
    if (reuse_store) {
      this.store = store;
    }
    return store;
  }

  protected abstract TopicMapIF loadTopicMap(boolean readonly) throws IOException;
  
  @Override
  public String toString() {
    return super.toString() + " [" + url.toString() + "]";
  }

  /**
   * INTERNAL: Returns true if stores will keep underlying fulltext
   * indexes up-to-date.
   * @return True if fulltext indexes are maintained.
   * @since 3.0
   */
  public boolean getMaintainFulltextIndexes() {
    return maintainFulltextIndexes;
  }

  /**
   * INTERNAL: Specifies whether underlying fulltext indexes are to be
   * kept up-to-date or not.
   * @param maintainFulltextIndexes True if fulltext indexes are maintained.
   * @since 3.0
   */
  public void setMaintainFulltextIndexes(boolean maintainFulltextIndexes) {
    this.maintainFulltextIndexes = maintainFulltextIndexes;
  }

  public String getIndexDirectory() {
    return indexDirectory;
  }

  public void setIndexDirectory(String indexDirectory) {
    this.indexDirectory = indexDirectory;
  }

  @Override
  protected void setTopicListeners(TopicMapListenerIF[] topic_listeners) {
    super.setTopicListeners(topic_listeners);
    if (reuse_store && store != null) {
      // register listeners
      ((AbstractTopicMapStore)store).setTopicListeners(getTopicListeners());
    }
  }  
}
