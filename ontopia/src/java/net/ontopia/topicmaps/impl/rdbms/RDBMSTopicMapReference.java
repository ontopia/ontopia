
// $Id: RDBMSTopicMapReference.java,v 1.42 2007/08/30 09:12:31 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.StorageIF;
import net.ontopia.topicmaps.core.StoreDeletedException;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.AbstractTopicMapReference;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.topicmaps.impl.utils.StorePoolableObjectFactory;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: RDBMS database topic map reference.
 */

public class RDBMSTopicMapReference extends AbstractTopicMapReference {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(RDBMSTopicMapReference.class.getName());

  protected StorageIF storage;

  protected long topicmap_id;

  protected LocatorIF base_address;

  // store pool
  protected StorePoolableObjectFactory ofactory;
  protected GenericObjectPool pool;
  protected RDBMSTopicMapStore rostore;

  public RDBMSTopicMapReference(String _id, String _title, StorageIF _storage,
      long _topicmap_id, LocatorIF _base_address) {
    super(_id, _title);
    this.storage = _storage;
    this.topicmap_id = _topicmap_id;
    this.base_address = _base_address;
  }

  protected RDBMSTopicMapStore _createStore(boolean readonly) {
    RDBMSTopicMapStore store = new RDBMSTopicMapStore(storage, topicmap_id);
    store.setReadOnly(readonly);
    store.setReference(RDBMSTopicMapReference.this);
    if (base_address != null)
      store.setBaseAddressOverride(base_address);
    return store;    
  }

  protected void init() {
    // store factory
    TopicMapStoreFactoryIF sfactory = new TopicMapStoreFactoryIF() {
      public TopicMapStoreIF createStore() {
       return _createStore(false);
      }
    };

    // initialize pool
    this.ofactory = new StorePoolableObjectFactory(sfactory);
    this.pool = new GenericObjectPool(ofactory);
    this.pool.setTestOnBorrow(true);

    Map properties = storage.getProperties();
    if (properties != null) {
      // Set minimum pool size (default: 0)
      String _minsize = PropertyUtils.getProperty(properties,
          "net.ontopia.topicmaps.impl.rdbms.StorePool.MinimumSize", false);
      int minsize = (_minsize == null ? 0 : Integer.parseInt(_minsize));
      log.debug("Setting StorePool.MinimumSize '" + minsize + "'");
      pool.setMinIdle(minsize); // 0 = no limit

      // Set maximum pool size (default: Integer.MAX_VALUE)
      String _maxsize = PropertyUtils.getProperty(properties,
          "net.ontopia.topicmaps.impl.rdbms.StorePool.MaximumSize", false);
      int maxsize = (_maxsize == null ? 8 : Integer.parseInt(_maxsize));
      log.debug("Setting StorePool.MaximumSize '" + maxsize + "'");
      pool.setMaxActive(maxsize); // 0 = no limit

      // Set soft maximum - emergency objects (default: false)
      boolean softmax = PropertyUtils.isTrue(properties,
          "net.ontopia.topicmaps.impl.rdbms.StorePool.SoftMaximum", false);
      log.debug("Setting StorePool.SoftMaximum '" + softmax + "'");
      if (softmax)
        pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);
      else
        pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
    }
  }

  public synchronized void open() {
    // ignore if already open
    if (isOpen())
      return;
    if (isDeleted())
      throw new StoreDeletedException(
          "Topic map has been deleted through this reference.");
    // initialize reference
    init();
    this.isopen = true;
  }

  public synchronized TopicMapStoreIF createStore(boolean readonly) {
    if (!isOpen())
      open();
    log.debug("RTR: borrow " + getId() + " i: " + pool.getNumIdle() + " a: "
        + pool.getNumActive());
    try {

      if (readonly) {
       if (rostore == null) 
         rostore = _createStore(true);
       else {
         boolean valid = rostore.validate();
         if (!valid) {
           try { rostore.close(); } catch (Exception e) { }
           rostore = _createStore(true);
         }
       }
       return rostore;

      } else {
       // borrow store from pool and set managed members
       AbstractTopicMapStore store = (AbstractTopicMapStore) pool.borrowObject();
       // register listeners
       store.setTopicListeners(getTopicListeners());
       return store;
      }
    } catch (Exception e) {
      // NOTE: NoSuchElementException will be thrown if pool times out or is
      // full
      throw new OntopiaRuntimeException("Could not get topic map store '"
          + getId() + "' from pool.", e);
    }
  }

  public synchronized void close() {
    // ISSUE: should block until all stores are returned to pool?
    this.isopen = false;
    if (pool != null) {
      try {
        // WARNING: it is important to note that pool does not close
        // active stores.
        pool.close();
        pool = null;

       if (rostore != null) {
         rostore.close(false);
         rostore = null;
       }
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }

  public synchronized void clear() {
    if (isDeleted())
      throw new StoreDeletedException(
          "Topic map has been deleted through this reference.");

    // close reference
    close();

    // create new topic map store which is to be used when clearing.
    RDBMSTopicMapStore store = new RDBMSTopicMapStore(storage, topicmap_id);
    // clear topic map. The store is closed automatically.
    try {
      store.clear();
      store.commit();
    } finally {
      if (store.isOpen())
        store.close();
    }
  }

  public synchronized void delete() {
    if (source == null)
      throw new UnsupportedOperationException("This reference cannot be deleted as it does not belong to a source.");
    if (!source.supportsDelete())
      throw new UnsupportedOperationException("This reference cannot be deleted as the source does not allow deleting.");
    // ignore if store already deleted
    if (isDeleted())
      return;

    // close reference
    close();

    // create new topic map store which is to be used when deleting.
    RDBMSTopicMapStore store = new RDBMSTopicMapStore(storage, topicmap_id);
    // delete topic map from data repository by delegating to
    // TopicMapStoreIF.close(). The store is closed automatically.
    this.deleted = store.delete(this);
  }

  public String toString() {
    return super.toString() + " [" + topicmap_id + "]";
  }

  // --- Extension properties

  public long getTopicMapId() {
    return topicmap_id;
  }
  
  // --- store pooling
  
  public synchronized void storeClosed(TopicMapStoreIF store) {
    if (!store.isReadOnly()) {
      // dereference listeners
      ((AbstractTopicMapStore)store).setTopicListeners(null);
    }
    if (pool != null) {
      log.debug("RTR: return " + getId() + " i: " + pool.getNumIdle() + " a: " + pool.getNumActive());
      try { 
        // return rw store to pool
        if (!store.isReadOnly())
          pool.returnObject(store);
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Could not return topic map store '" + getId() + "' to pool.", e);
      }
    } else {
      // pool has been closed before store, so we should close store ourselves
      synchronized (store) {
        if (store.isOpen())
          ((RDBMSTopicMapStore)store).close(false);
      }
    }
  }
  
  public void writeReport(java.io.Writer out, boolean dumpCaches) throws java.io.IOException {
    out.write("<table>\n");
    out.write("  <tr><td>");
    out.write("Topic Map:");
    out.write("</td><td>");
    out.write(getId());
    out.write("  </td></tr>\n");
    out.write("  <tr><td>");
    out.write("Active:");
    out.write("</td><td>");
    out.write(Integer.toString(pool == null ? 0 : pool.getNumActive()));
    out.write("  </td></tr>\n");
    out.write("  <tr><td>");
    out.write("Idle:");
    out.write("</td><td>");
    out.write(Integer.toString(pool == null ? 0 : pool.getNumIdle()));
    out.write("  </td><tr>\n");
    out.write("</table>\n");
    
    Object[] stores = ofactory.stores.toArray();
    for (int i = 0; i < stores.length; i++) {
      RDBMSTopicMapStore store = (RDBMSTopicMapStore)stores[i];
      out.write("<h3>Identity Map - ReadWriteStore #");
      out.write(Integer.toString(i+1));
      out.write("</h3>\n"); 
      store.writeIdentityMap(out, dumpCaches);
    }
    if (rostore != null) {
      out.write("<h3>Identity Map - ReadOnlyStore</h3>\n"); 
      rostore.writeIdentityMap(out, dumpCaches);
    }
  }
}
  
