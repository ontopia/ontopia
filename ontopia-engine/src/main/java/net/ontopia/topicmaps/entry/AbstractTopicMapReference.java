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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.ontopia.topicmaps.core.StoreDeletedException;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.events.AbstractTopicMapListener;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: An abstract topic map reference class that contains
 * methods that handles the id and title properties. Subclasses should
 * implement the createStore method.<p>
 */
public abstract class AbstractTopicMapReference
  implements TopicMapReferenceIF {

  protected String id;
  protected String title;
  protected boolean isopen;
  protected boolean deleted;

  protected TopicMapSourceIF source;

  protected List<TopicMapListenerIF> listeners;
  protected TopicMapListenerIF[] topic_listeners;  

  public AbstractTopicMapReference(String id, String title) {
    this.id = id;
    this.title = title;
  }
  
  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public TopicMapSourceIF getSource() {
    return source;
  }

  @Override
  public void setSource(TopicMapSourceIF source) {
    this.source = source;
  }

  @Override
  public boolean isOpen() {
    return isopen;
  }

  @Override
  public synchronized void open() {
    if (isDeleted()) {
      throw new StoreDeletedException("Topic map has been deleted through this reference.");
    }
    this.isopen = true;
  }

  @Override
  public synchronized void close() {
    this.isopen = false;
  }

  @Override
  public boolean isDeleted() {
    return deleted;
  }

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

    // close the reference
    close();
    this.deleted = true;
  }

  @Override
  public synchronized void clear() throws IOException {
    // naive implementation that gets a store and clears it
    TopicMapStoreIF store = null;
    try {
      store = createStore(false);
      store.getTopicMap().clear();    
    } finally {
      if (store != null && store.isOpen()) {
        store.close();
      }
    }
  }

  @Override
  public abstract TopicMapStoreIF createStore(boolean readonly)
    throws IOException;

  // -- store pooling

  @Override
  public void storeClosed(TopicMapStoreIF store) {
    // no-op
  }

  // ---------------------------------------------------------------------------
  // TopicMapListenerIF implementation
  // ---------------------------------------------------------------------------

  protected TopicMapListenerIF[] getTopicListeners() {
    return topic_listeners;
  }

  // NOTE: called every time listener changes
  protected void setTopicListeners(TopicMapListenerIF[] topic_listeners) {
    this.topic_listeners = topic_listeners;
  }

  /**
   * INTERNAL: Add topic listener to reference.
   */
  public synchronized void addTopicListener(TopicMapListenerIF listener) {
    if (topic_listeners == null) {
      listeners = new ArrayList<TopicMapListenerIF>(3);
    }
    listeners.add(listener);
    makeListenerArray();
    // register self with listener
    if (listener instanceof AbstractTopicMapListener) {
      ((AbstractTopicMapListener)listener).setReference(this);
    }
  }

  /**
   * INTERNAL: Remove topic listener from reference.
   */
  public synchronized void removeTopicListener(TopicMapListenerIF listener) {
    if (topic_listeners == null) {
      return;
    }
    listeners.remove(listener);
    makeListenerArray();
    // unregister self with listener
    if (listener instanceof AbstractTopicMapListener) {
      ((AbstractTopicMapListener)listener).setReference(null);
    }
  }
  
  /**
   * INTERNAL: Register topic listener from list of listener implementations.
   */
  public synchronized void registerTopicListeners(String listenerList) {
    StringTokenizer st = new StringTokenizer(listenerList, ", ");
    while (st.hasMoreTokens()) {
      Object object = ObjectUtils.newInstance(st.nextToken());
      if (object instanceof TopicMapListenerIF) {
        addTopicListener((TopicMapListenerIF)object);
      } else {
        throw new OntopiaRuntimeException("Listener " + object + " is not a TopicMapListenerIF and cannot be added to reference with id " + getId());
      }
    }
  }
  
  protected void makeListenerArray() {
    if (listeners == null || listeners.isEmpty()) {
      this.listeners = null;
      setTopicListeners(null);
    } else {
      TopicMapListenerIF[] tls = new TopicMapListenerIF[listeners.size()];
      listeners.toArray(tls);
      setTopicListeners(tls);
    }
  }
  
}
