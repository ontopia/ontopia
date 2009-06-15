
// $Id: StoreRegistry.java,v 1.11 2007/08/30 09:44:01 geir.gronmo Exp $

package net.ontopia.topicmaps.entry;

import java.io.*;
import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Class used to manage topic map stores for multiple
 * application users.<p>
 *
 * The class uses transaction user objects and topic map reference
 * keys to group topic map stores. Topic map stores are created using
 * the containing topic map repository instance.<p>
 *
 * Note that only a single topic map store can be managed per
 * transaction user and reference key combination.
 *
 * @since 1.3.2
 * @deprecated
 */
public class StoreRegistry {

  protected TopicMapRepositoryIF repository;
  protected Map txnusers = Collections.synchronizedMap(new HashMap());

  public StoreRegistry(TopicMapRepositoryIF repository) {
    this.repository = repository;
  }

  /**
   * INTERNAL: Returns the topic map repository used.
   */
  public TopicMapRepositoryIF getRepository() {
    return repository;
  }

  /**
   * INTERNAL: Looks up a topic map store for a given transaction user
   * and topic map reference key. A transaction user is the object
   * that is used to make sure that you get the right transaction
   * among many. The transaction user object can be any object. It is
   * just used to look up the right store.
   */
  public TopicMapStoreIF getStore(Object txnuser, String refkey) {
    synchronized (txnuser) {
      if (txnusers.containsKey(txnuser)) {
        Map stores = (Map)txnusers.get(txnuser);
        if (stores.containsKey(refkey))
          return (TopicMapStoreIF)stores.get(refkey);
      }
    }
    return null;
  }

  /**
   * INTERNAL: Returns the reference key for the given transaction
   * user's topic map store. An exception is thrown if the store is
   * not registered with the registry.
   */
  public synchronized String getReferenceKey(Object txnuser, TopicMapStoreIF store) {
    synchronized (txnuser) {
      if (txnusers.containsKey(txnuser)) {
        Map stores = (Map)txnusers.get(txnuser);
        Iterator iter = stores.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry entry = (Map.Entry)iter.next();
          if (store.equals(entry.getValue()))
            return (String)entry.getKey();
        }
      }
    }
    throw new OntopiaRuntimeException(store + " is not opened for transaction user: " + txnuser);
  }

  /**
   * INTERNAL: Returns a collection contains all reference keys for
   * the given transaction user.
   */
  public synchronized Collection getReferenceKeys(Object txnuser) {
    Map refmap = (Map)txnusers.get(txnuser);
    if (refmap != null)
      return refmap.keySet();
    else
      return Collections.EMPTY_SET;
  }

  /**
   * INTERNAL: Returns a collection contains all stores that is
   * registered with the given transaction user.
   */
  public synchronized Collection getStores(Object txnuser) {
    Map refmap = (Map)txnusers.get(txnuser);
    if (refmap != null)
      return refmap.values();
    else
      return Collections.EMPTY_SET;
  }

  /**
   * INTERNAL: Returns true if the topic map store is already opend for
   * the given transaction user and reference key.<p>
   *
   * @return true if the store is open, false otherwise
   *
   * @since 1.3.4
   */
  public boolean isStoreOpen(Object txnuser, String refkey) {
    synchronized (txnuser) {
      return (getStore(txnuser, refkey) != null);
    }
  }

  /**
   * INTERNAL: Open a new topic store for the given transaction user
   * and reference key. The store is created from a topic map
   * reference that is looked up in the topic map repository using the
   * reference key.
   */
  public TopicMapStoreIF openStore(Object txnuser, String refkey, boolean readonly) throws IOException {
    TopicMapStoreIF store;
    synchronized (txnuser) {
      store = getStore(txnuser, refkey);
      if (store != null)
        throw new OntopiaRuntimeException("Store already open for reference key: " + refkey);
      // Create new store
      TopicMapReferenceIF ref = repository.getReferenceByKey(refkey);
      if (ref == null)
        throw new OntopiaRuntimeException("Could not find reference with key: " + refkey);

      store = ref.createStore(readonly);
      putStore(txnuser, refkey, store);
    }
    return store;
  }

  protected void putStore(Object txnuser, String refkey, TopicMapStoreIF store) {
    if (!txnusers.containsKey(txnuser))
      txnusers.put(txnuser, new HashMap());
    Map stores = (Map)txnusers.get(txnuser);
    stores.put(refkey, store);
  }

  /**
   * INTERNAL: Closes and dereferences the topic map store for the
   * given transaction user and reference key.
   */
  public void closeStore(Object txnuser, String refkey) {
    synchronized (txnuser) {
      TopicMapStoreIF store = getStore(txnuser, refkey);
      if (store == null)
        throw new OntopiaRuntimeException("No store open for reference key: " + refkey);

      // Close store and dereference
      store.close();
      removeStore(txnuser, refkey);
    }
  }

  protected void removeStore(Object txnuser, String refkey) {
    Map stores = (Map)txnusers.get(txnuser);
    stores.remove(refkey);
    if (stores.isEmpty())
      txnusers.remove(txnuser);
  }

}
