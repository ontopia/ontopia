
// $Id: AbstractTopicMapStore.java,v 1.25 2008/06/11 16:55:59 geir.gronmo Exp $

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

  public AbstractTopicMapStore() {
  }
  
  public boolean isOpen() {
    return open;
  }
  
  public void open() {
    if (deleted)
      throw new OntopiaRuntimeException("A deleted store cannot be reopened.");
        
    // Set open flag to true
    open = true;
  }
  
  public abstract TopicMapTransactionIF getTransaction();

  public TopicMapIF getTopicMap() {
    return getTransaction().getTopicMap();
  }

  public LocatorIF getBaseAddress() {
    return base_address;
  }
  
  public void commit() {
    getTransaction().commit();
  }

  public void abort() {
    getTransaction().abort();
  }

  //! public void clear() {
  //!   // remove all the objects from the topic map
  //!   TopicMapIF tm = getTopicMap();
  //!   tm.clear();
  //!   //! close();
  //! }

  public void delete(boolean force) throws NotRemovableException {
    // Do nothing except closing the store, since we do not know how
    // to delete the topic map here. Implementations have to implement
    // deletion themselves.

    TopicMapIF tm = getTopicMap();

    if (!force) {
      // If we're not forcing, complain if the topic map contains any data.
      if (!tm.getTopics().isEmpty())
        throw new NotRemovableException("Cannot delete topic map when it contains topics.");
      if (!tm.getAssociations().isEmpty())
        throw new NotRemovableException("Cannot delete topic map when it contains associations.");
    }
    
    // Remove all the objects from the topic map
    tm.clear();

    close();
    deleted = true;
  }
  
  public boolean isReadOnly() {
    return readonly;
  }

  public void setReadOnly(boolean readonly) {
    if (readonlySet) throw new OntopiaRuntimeException("Readonly flag has already been set.");
    this.readonly = readonly;
    this.readonlySet = true;
  }

  /* -- topic map reference -- */

  public TopicMapReferenceIF getReference() {
    return reference;
  }

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
  
  public TopicMapListenerIF[] topic_listeners;

  public void setTopicListeners(TopicMapListenerIF[] listeners) {
    this.topic_listeners = listeners;
  }

  public abstract EventManagerIF getEventManager();

}
