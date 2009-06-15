
// $Id: AbstractTopicMapReference.java,v 1.32 2008/05/29 10:54:58 geir.gronmo Exp $

package net.ontopia.topicmaps.entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.ontopia.topicmaps.core.StoreDeletedException;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.events.AbstractTopicMapListener;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
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

  public AbstractTopicMapReference(String id, String title) {
    this.id = id;
    this.title = title;
  }
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public TopicMapSourceIF getSource() {
    return source;
  }

  public void setSource(TopicMapSourceIF source) {
    this.source = source;
  }

  public boolean isOpen() {
    return isopen;
  }

  public synchronized void open() {
    if (isDeleted()) 
      throw new StoreDeletedException("Topic map has been deleted through this reference.");
    this.isopen = true;
  }

  public synchronized void close() {
    this.isopen = false;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public synchronized void delete() {
    if (source == null)
      throw new UnsupportedOperationException("This reference cannot be deleted as it does not belong to a source.");
    if (!source.supportsDelete())
      throw new UnsupportedOperationException("This reference cannot be deleted as the source does not allow deleting.");

    // ignore if store already deleted
    if (isDeleted()) return;

    // close the reference
    close();
    this.deleted = true;
  }

  public synchronized void clear() throws IOException {
    // naive implementation that gets a store and clears it
    TopicMapStoreIF store = null;
    try {
      store = (TopicMapStoreIF)createStore(false);
      store.getTopicMap().clear();    
    } finally {
      if (store != null && store.isOpen()) store.close();
    }
  }

  public abstract TopicMapStoreIF createStore(boolean readonly)
    throws IOException;

  // -- store pooling

  public void storeClosed(TopicMapStoreIF store) {
  }

  // ---------------------------------------------------------------------------
  // TopicMapListenerIF implementation
  // ---------------------------------------------------------------------------

  protected List listeners;
  protected TopicMapListenerIF[] topic_listeners;  

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
    if (topic_listeners == null)
      listeners = new ArrayList(3);
    listeners.add(listener);
    makeListenerArray();
    // register self with listener
    if (listener instanceof AbstractTopicMapListener)
      ((AbstractTopicMapListener)listener).setReference(this);
  }

  /**
   * INTERNAL: Remove topic listener from reference.
   */
  public synchronized void removeTopicListener(TopicMapListenerIF listener) {
    if (topic_listeners == null) return;
    listeners.remove(listener);
    makeListenerArray();
    // unregister self with listener
    if (listener instanceof AbstractTopicMapListener)
      ((AbstractTopicMapListener)listener).setReference(null);
  }
  
  /**
   * INTERNAL: Register topic listener from list of listener implementations.
   */
  public synchronized void registerTopicListeners(String listenerList) {
    StringTokenizer st = new StringTokenizer(listenerList, ", ");
    while (st.hasMoreTokens()) {
      Object object = ObjectUtils.newInstance(st.nextToken());
      if (object instanceof TopicMapListenerIF)
        addTopicListener((TopicMapListenerIF)object);
      else
        throw new OntopiaRuntimeException("Listener " + object + " is not a TopicMapListenerIF and cannot be added to reference with id " + getId());
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
