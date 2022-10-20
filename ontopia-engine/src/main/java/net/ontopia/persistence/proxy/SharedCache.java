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

package net.ontopia.persistence.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A shared storage cache implementation. The cache uses a
 * lookup index to store its cache entries. This cache is transaction
 * independent.
 */

public class SharedCache implements StorageCacheIF, AccessRegistrarIF {
  private static final String NULL = "null";

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(SharedCache.class.getName());
  
  protected StorageIF storage;
  protected Map<IdentityIF, CacheEntry> datacache;
  protected Map<Object, Integer> field_counts = new HashMap<Object, Integer>();

  protected long current_ticket_value;
  protected TicketIF current_ticket;
  protected int eviction;
  
  private final boolean debug;
  
  protected ClusterIF cluster;
  protected long timestamp;
  
  SharedCache(StorageIF storage, Map<IdentityIF, CacheEntry> datacache) {
    this.storage = storage;
    this.datacache = datacache;
    this.debug = log.isDebugEnabled();
    this.timestamp = System.currentTimeMillis();
    this.current_ticket = new Ticket(current_ticket_value);
  }

  // -----------------------------------------------------------------------------
  // Clustering
  // -----------------------------------------------------------------------------

  public void setCluster(ClusterIF cluster) {
    this.cluster = cluster;
  }
  
  // -----------------------------------------------------------------------------
  // StorageCacheIF implementation
  // -----------------------------------------------------------------------------
  
  @Override
  public AccessRegistrarIF getRegistrar() {
    return this;
  }
  
  @Override
  public void close() {
    // TODO: clear cache? and if persistent maybe delete physical
    // cache. this might also be done on startup.
  }
  
  @Override
  public boolean exists(StorageAccessIF access, IdentityIF identity) {
    // Check to see if identity is registered here
    if (datacache.get(identity) == null) {
      // Need to check database for existence and load object. An
      // exception will be thrown if the object does not exist in the
      // database.
      
      // This call will lead to a registerIdentity callback if the
      // object exists in the data repository.
      return access.loadObject(this, identity);
    }
    // Object exists
    return true;
  }
  
  @Override
  public Object getValue(StorageAccessIF access, IdentityIF identity, int field) {
    CacheEntry fields = datacache.get(identity);
    
    // Check to see if field is in the local cache
    if (fields != null) {
      synchronized (fields) {
        if (fields.contains(field)) {
          if (debug) {
            log.debug("Getting " + identity + " field from cache: " + field);
          }
          // Get field value from fields cache
          return fields.getValue(field);
        }
      }
    }
      
    // Load field value(s) from database
    if (debug) {
      log.debug("Getting " + identity + " field from store: " + field);
    }
    
    return access.loadField(this, identity, field);
  }
  
  @Override
  public boolean isObjectLoaded(IdentityIF identity) {
    // TODO: flag argument for also checking parent caches too?
    if (datacache.get(identity) != null) {
      return true;
    } else {
      return false;
    }      
  }
  
  @Override
  public boolean isFieldLoaded(IdentityIF identity, int field) {
    // TODO: flag argument for also checking parent caches too?
    // If identity does not exist, nor does field
    CacheEntry fields = datacache.get(identity);
    if (fields != null && fields.contains(field)) {
      return true;
    } else {
      return false;
    }
  }
  
  @Override
  public void evictIdentity(IdentityIF identity, boolean notifyCluster) {
    if (debug) {
      log.debug("SharedCache: evicting identity " + identity);
    }
    // unregister identity
    datacache.remove(identity);
    // notify cluster
    if (cluster != null && notifyCluster) {
      cluster.evictIdentity(identity);
    }
  }
  
  @Override
  public void evictFields(IdentityIF identity, boolean notifyCluster) {
    if (debug) {
      log.debug("SharedCache: evicting fields " + identity);
    }
    CacheEntry fields = datacache.get(identity);
    if (fields != null) {
      // Drop all field values from the cache
      fields.clear();
    }
    // notify cluster
    if (cluster != null && notifyCluster) {
      cluster.evictFields(identity);
    }
  }
  
  @Override
  public void evictField(IdentityIF identity, int field, boolean notifyCluster) {
    CacheEntry fields = datacache.get(identity);
    if (fields != null) {
      // Drop field value from fields cache
      fields.unsetValue(field, null); // NOTE: value is now null when unset
    }
    // notify cluster
    if (cluster != null && notifyCluster) {
      cluster.evictField(identity, field);
    }
  }  
  
  // -----------------------------------------------------------------------------
  // prefetch
  // -----------------------------------------------------------------------------
  
  @Override
  public int prefetch(StorageAccessIF access, Class<?> type, int field, int nextField, boolean traverse, Collection<IdentityIF> identities) {
    long start = System.currentTimeMillis();
    int num = identities.size();
    if (debug) {
      log.debug("--LFM-P: s" + field + " " + num + " " + traverse + " " + type + " " + nextField);
    }
    if (traverse) {
      // filter out identities that have their fields loaded, but
      // not their next field loaded
      Collection<IdentityIF> filtered = new ArrayList<IdentityIF>(num);
      for (IdentityIF identity : identities) {
        CacheEntry fields = datacache.get(identity);
          
        if (fields == null || !fields.contains(field)) {
          // prefetch if field not loaded
          filtered.add(identity);
        } else if (nextField >= 0) {
          // prefetch if 1:1 field loaded, but next field not loaded
          Object value = fields.getValue(field);
          if (value != null && value instanceof IdentityIF) {
            CacheEntry nfields = datacache.get((IdentityIF)value);
              
            if (nfields == null || !nfields.contains(nextField)) {
              filtered.add(identity);
            }
          }
        }          
      }
      num = filtered.size();
      if (num > 1) {
        access.loadFieldMultiple(this, filtered, null, type, field);
      }
    } else {
      // filter out identities that already have their *fields* loaded
      Collection<IdentityIF> filtered = new ArrayList<IdentityIF>(num);
      for (IdentityIF identity : identities) {
        if (!isFieldLoaded(identity, field)) {
          filtered.add(identity);
        }
      }
      num = filtered.size();
      if (num > 1) {
        access.loadFieldMultiple(this, filtered, null, type, field);
      }
    }
    if (debug) {
      log.debug("--LFM-P: e" + field + " " + num + " (" + (System.currentTimeMillis() - start) + " ms)");
    }
    return num;
  }
  
  // -----------------------------------------------------------------------------
  // AccessRegistrarIF implementation
  // -----------------------------------------------------------------------------
  
  @Override
  public IdentityIF createIdentity(Class<?> type, long key) {
    // do identity interning
    IdentityIF identity = new LongIdentity(type, key);
    CacheEntry entry = datacache.get(identity);
    return (entry == null ? identity : entry.getIdentity());
  }
  
  @Override
  public IdentityIF createIdentity(Class<?> type, Object key) {
    IdentityIF identity = new AtomicIdentity(type, key);
    CacheEntry entry = datacache.get(identity);
    return (entry == null ? identity : entry.getIdentity());
  }
  
  @Override
  public IdentityIF createIdentity(Class<?> type, Object[] keys) {
    IdentityIF identity = new Identity(type, keys);
    CacheEntry entry = datacache.get(identity);
    return (entry == null ? identity : entry.getIdentity());
  }
  
  @Override
  public void registerIdentity(TicketIF ticket, IdentityIF identity) {
    // validate ticket
    if (!ticket.isValid()) {
      return;
    }

    if (debug) {
      log.debug("Registering identity " + identity);
    }
    
    // register identity if we don't already have it.
    if (datacache.get(identity) == null) {
      
      //! System.out.println("+I  " + identity);      
      
      // register with cache
      datacache.put(WrappedIdentity.wrap(identity), new CacheEntry(identity, getFieldsCount(identity.getType())));
    }
  }
  
  @Override
  public void registerField(TicketIF ticket, IdentityIF identity, int field, Object value) {
    // validate ticket
    if (!ticket.isValid()) {
      return;
    }
                                          
    // Note: object identity should be registered already.
    if (debug) {
      log.debug("Registering " + identity + " field " + field + "=" + value);
    }
    CacheEntry fields = datacache.get(identity);
    if (fields == null) {
      IdentityIF wrappedIdentity = WrappedIdentity.wrap(identity);
      // create new cache entry
      fields = new CacheEntry(wrappedIdentity, getFieldsCount(wrappedIdentity.getType()));
      fields.setValue(field, value);
      // register with cache
      datacache.put(wrappedIdentity, fields);
    } else {
      fields.setValue(field, value);
    }
  }

  // -----------------------------------------------------------------------------
  // Tickets
  // -----------------------------------------------------------------------------

  @Override
  public TicketIF getTicket() {
    return current_ticket;
  }

  @Override
  public synchronized void registerEviction() {
    eviction++;
    this.current_ticket = new Ticket(++current_ticket_value);
  }
  
  @Override
  public synchronized void releaseEviction() {
    eviction--;
  }

  private synchronized int getEvictionCount() {
    return eviction;
  }

  private synchronized long getCurrentTicket() {
    return current_ticket_value;
  }

  private synchronized boolean isRunningEviction() {
    return (eviction > 0);
  }
  
  private class Ticket implements TicketIF {
    private long value;
    private Ticket(long value) {
      this.value = value;
    }
    @Override
    public boolean isValid() {
      return value == current_ticket_value && !isRunningEviction();
    }
  }
  
  // -----------------------------------------------------------------------------
  // CacheEntry initialization
  // -----------------------------------------------------------------------------
  
  protected int getFieldsCount(Class<?> type) {
    synchronized (field_counts) {
      Integer count = field_counts.get(type);
      if (count != null) {
        return count.intValue();
      }
      
      // Otherwise compute the fields count
      ClassInfoIF cinfo = storage.getMapping().getClassInfo(type);
      int field_count = cinfo.getValueFieldInfos().length;
      
      // The field counts is a map between Class and the number of
      // fields that they have. This number is used to allocate room for
      // field metata and field values in the data cache entries.
      field_counts.put(type, field_count);
      return field_count;
    }
  }
  
  // -----------------------------------------------------------------------------
  // Cache reset + statistics
  // -----------------------------------------------------------------------------
  
  @Override
  public void clear(boolean notifyCluster) {
    this.datacache.clear();
    if (cluster != null && notifyCluster) {
      cluster.clearDatacache();
    }
  }
  
  public void writeReport(java.io.Writer out, boolean dumpCache) throws java.io.IOException {
    try {
      // lock data cache while generating report
      Map<Object, Integer> stats = new HashMap<Object, Integer>();
      // generate statistics
      int size = 0;
      synchronized (datacache) {
        for (IdentityIF key : datacache.keySet()) {
          if (key == null) {
            continue;
          }
          size++;
          if (!stats.containsKey(key.getType())) {
            stats.put(key.getType(), 1);
          } else {
            Integer cnt = stats.get(key.getType());
            stats.put(key.getType(), cnt.intValue() + 1);
          }
        }
      }
      
      out.write("<p>Cache size: " + size + "<br>\n");
      out.write("Eviction: " + getEvictionCount() + " Ticket: " + getCurrentTicket() + "<br>\n");
      out.write("Created: " + new Date(timestamp) + " (" + (System.currentTimeMillis()-timestamp) + " ms)</p>\n");
          
      // output statistics
      out.write("<table>\n");
      for (Object key : stats.keySet()) {
        Integer val = stats.get(key);
          out.write("<tr><td>");
          out.write((key == null ? NULL : StringUtils.escapeHTMLEntities(key.toString())));
          out.write("</td><td>");
          out.write((val == null ? NULL : StringUtils.escapeHTMLEntities(val.toString())));
          out.write("</td></tr>\n");
      }
      out.write("</table><br>\n");
      
      if (dumpCache) {
        out.write("<table>\n");
        synchronized (datacache) {
          for (Object key : datacache.keySet()) {
            if (key == null) {
              continue;
            }
            CacheEntry val = datacache.get(key);
            out.write("<tr><td>");
            out.write((key == null ? NULL : StringUtils.escapeHTMLEntities(key.toString())));
            out.write("</td><td>");
            out.write((val == null ? NULL : StringUtils.escapeHTMLEntities(val.toString())));
            out.write("</td></tr>\n");
          }
          out.write("</table><br>\n");
        }
      }
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  // -----------------------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------------------
  
  @Override
  public String toString() {
    return new StringBuilder("proxy.SharedCache@")
        .append(System.identityHashCode(this))
        .toString();
  }
  
}
