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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: RDBMS database topic map reference.
 */

public class RDBMSTopicMapReference extends AbstractTopicMapReference {

  public static final String EXHAUSED_BLOCK = "block";
  public static final String EXHAUSED_GROW = "grow";
  public static final String EXHAUSED_FAIL = "fail";

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(RDBMSTopicMapReference.class.getName());

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
      @Override
      public TopicMapStoreIF createStore() {
       return _createStore(false);
      }
    };

    // initialize pool
    this.ofactory = new StorePoolableObjectFactory(sfactory);
    this.pool = new GenericObjectPool(ofactory);
    this.pool.setTestOnBorrow(true);

    Map<String, String> properties = storage.getProperties();
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
      boolean softmax = MapUtils.getBoolean(properties, "net.ontopia.topicmaps.impl.rdbms.StorePool.SoftMaximum", false);
      log.debug("Setting StorePool.SoftMaximum '" + softmax + "'");
      if (softmax)
        pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);
      else
        pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
      
      // EXPERIMENTAL!
      String _etime = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.StorePool.IdleTimeout", false);
      int etime = (_etime == null ? -1 : Integer.parseInt(_etime));
      pool.setTimeBetweenEvictionRunsMillis(etime);
      pool.setSoftMinEvictableIdleTimeMillis(etime);
    }

    // allow the user to fully overwrite exhausted options
    String _whenExhaustedAction = PropertyUtils.getProperty(properties, "net.ontopia.topicmaps.impl.rdbms.StorePool.WhenExhaustedAction", false);
    if (EXHAUSED_BLOCK.equals(_whenExhaustedAction))
      pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
    if (EXHAUSED_GROW.equals(_whenExhaustedAction))
      pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);
    if (EXHAUSED_FAIL.equals(_whenExhaustedAction))
      pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);

    if (pool.getWhenExhaustedAction() == GenericObjectPool.WHEN_EXHAUSTED_BLOCK)
      log.debug("Pool is set to block on exhaused");
    if (pool.getWhenExhaustedAction() == GenericObjectPool.WHEN_EXHAUSTED_GROW)
      log.debug("Pool is set to grow on exhaused");
    if (pool.getWhenExhaustedAction() == GenericObjectPool.WHEN_EXHAUSTED_FAIL)
      log.debug("Pool is set to fail on exhaused");

  }

  @Override
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

  @Override
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

  @Override
  public void setTitle(String title) {
    super.setTitle(title);
    TopicMapStoreIF store = null;
    try {
      store = createStore(false);
      TopicMap topicmap = (TopicMap) store.getTopicMap();
      topicmap.setTitle(title);
      store.commit();
    } finally {
      if (store != null) {
        store.close();
      }
    }
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
   * the topic map and persists it in the database.
   */
  public void setBaseAddress(LocatorIF base_address) {
    this.base_address = base_address;
    TopicMapStoreIF store = null;
    try {
      store = createStore(false);
      TopicMap topicmap = (TopicMap) store.getTopicMap();
      topicmap.setBaseAddress(base_address);
      store.commit();
    } finally {
      if (store != null) {
        store.close();
      }
    }
  }

  @Override
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

  @Override
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

  @Override
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

  @Override
  public String toString() {
    return super.toString() + " [" + topicmap_id + "]";
  }

  // --- Extension properties

  public long getTopicMapId() {
    return topicmap_id;
  }
  
  // --- store pooling
  
  @Override
  public synchronized void storeClosed(TopicMapStoreIF store) {
    if (!store.isReadOnly()) {
      // dereference listeners
      ((AbstractTopicMapStore)store).setTopicListeners(null);
    }
    if (pool != null) {
      log.debug("RTR: return " + getId() + " i: " + pool.getNumIdle() + " a: " + pool.getNumActive());
      try { 
        // return rw store to pool
        if (!store.isReadOnly()) {
          pool.returnObject(store);
        }
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
  
