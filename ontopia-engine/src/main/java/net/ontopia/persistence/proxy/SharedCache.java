
package net.ontopia.persistence.proxy;

import gnu.trove.THashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ontopia.utils.LookupIndexIF;
import net.ontopia.utils.ClearableIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.SoftValueHashMapIndex;
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.SynchronizedLookupIndex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A shared storage cache implementation. The cache uses a
 * lookup index to store its cache entries. This cache is transaction
 * independent.
 */

public class SharedCache implements StorageCacheIF, AccessRegistrarIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(SharedCache.class.getName());
  
  protected StorageIF storage;
  protected Map datacache;

  protected long current_ticket_value;
  protected TicketIF current_ticket;
  protected int eviction;
  
  private final boolean debug;
  
  protected ClusterIF cluster;
  protected long timestamp;
  
  SharedCache(StorageIF storage, Map datacache) {
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
  
  public AccessRegistrarIF getRegistrar() {
    return this;
  }
  
  public void close() {
    // TODO: clear cache? and if persistent maybe delete physical
    // cache. this might also be done on startup.
  }
  
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
  
  public Object getValue(StorageAccessIF access, IdentityIF identity, int field) {
    CacheEntry fields = (CacheEntry)datacache.get(identity);
    
    // Check to see if field is in the local cache
    if (fields != null) {
      synchronized (fields) {
        if (fields.contains(field)) {
          if (debug)
            log.debug("Getting " + identity + " field from cache: " + field);
          // Get field value from fields cache
          return fields.getValue(field);
        }
      }
    }
      
    // Load field value(s) from database
    if (debug)
      log.debug("Getting " + identity + " field from store: " + field);
    
    return access.loadField(this, identity, field);
  }
  
  public boolean isObjectLoaded(IdentityIF identity) {
    // TODO: flag argument for also checking parent caches too?
    if (datacache.get(identity) != null)
      return true;
    else
      return false;      
  }
  
  public boolean isFieldLoaded(IdentityIF identity, int field) {
    // TODO: flag argument for also checking parent caches too?
    // If identity does not exist, nor does field
    CacheEntry fields = (CacheEntry)datacache.get(identity);
    if (fields != null && fields.contains(field))
      return true;
    else
      return false;
  }
  
  public void evictIdentity(IdentityIF identity, boolean notifyCluster) {
    if (debug)
      log.debug("SharedCache: evicting identity " + identity);
    // unregister identity
    datacache.remove(identity);
    // notify cluster
    if (cluster != null && notifyCluster) cluster.evictIdentity(identity);
  }
  
  public void evictFields(IdentityIF identity, boolean notifyCluster) {
    if (debug)
      log.debug("SharedCache: evicting fields " + identity);
    CacheEntry fields = (CacheEntry)datacache.get(identity);
    if (fields != null) {
      // Drop all field values from the cache
      fields.clear();
    }
    // notify cluster
    if (cluster != null && notifyCluster) cluster.evictFields(identity);
  }
  
  public void evictField(IdentityIF identity, int field, boolean notifyCluster) {
    CacheEntry fields = (CacheEntry)datacache.get(identity);
    if (fields != null) {
      // Drop field value from fields cache
      fields.unsetValue(field, null); // NOTE: value is now null when unset
    }
    // notify cluster
    if (cluster != null && notifyCluster) cluster.evictField(identity, field);
  }  
  
  // -----------------------------------------------------------------------------
  // prefetch
  // -----------------------------------------------------------------------------
  
  public int prefetch(StorageAccessIF access, Object type, int field, int nextField, boolean traverse, Collection identities) {
    long start = System.currentTimeMillis();
    int num = identities.size();
    if (debug)
      log.debug("--LFM-P: s" + field + " " + num + " " + traverse + " " + type + " " + nextField);
    if (traverse) {
      // filter out identities that have their fields loaded, but
      // not their next field loaded
      Collection filtered = new ArrayList(num);
      Iterator iter = identities.iterator();
      for (int i=0; i < num; i++) {
        IdentityIF identity = (IdentityIF)iter.next();
          
        CacheEntry fields = (CacheEntry)datacache.get(identity);
          
        if (fields == null || !fields.contains(field)) {
          // prefetch if field not loaded
          filtered.add(identity);
        } else if (nextField >= 0) {
          // prefetch if 1:1 field loaded, but next field not loaded
          Object value = fields.getValue(field);
          if (value != null && value instanceof IdentityIF) {
            CacheEntry nfields = (CacheEntry)datacache.get((IdentityIF)value);
              
            if (nfields == null || !nfields.contains(nextField)) {
              filtered.add(identity);
            }
          }
        }          
      }
      num = filtered.size();
      if (num > 1)
        access.loadFieldMultiple(this, filtered, null, type, field);
    } else {
      // filter out identities that already have their *fields* loaded
      Collection filtered = new ArrayList(num);
      Iterator iter = identities.iterator();
      for (int i=0; i < num; i++) {
        IdentityIF identity = (IdentityIF)iter.next();
        if (!isFieldLoaded(identity, field))
          filtered.add(identity);
      }
      num = filtered.size();
      if (num > 1)
        access.loadFieldMultiple(this, filtered, null, type, field);
    }
    if (debug)
      log.debug("--LFM-P: e" + field + " " + num + " (" + (System.currentTimeMillis() - start) + " ms)");
    return num;
  }
  
  // -----------------------------------------------------------------------------
  // AccessRegistrarIF implementation
  // -----------------------------------------------------------------------------
  
  public IdentityIF createIdentity(Object type, long key) {
    // do identity interning
    IdentityIF identity = new LongIdentity(type, key);
    CacheEntry entry = (CacheEntry)datacache.get(identity);
    return (entry == null ? identity : entry.getIdentity());
  }
  
  public IdentityIF createIdentity(Object type, Object key) {
    IdentityIF identity = new AtomicIdentity(type, key);
    CacheEntry entry = (CacheEntry)datacache.get(identity);
    return (entry == null ? identity : entry.getIdentity());
  }
  
  public IdentityIF createIdentity(Object type, Object[] keys) {
    IdentityIF identity = new Identity(type, keys);
    CacheEntry entry = (CacheEntry)datacache.get(identity);
    return (entry == null ? identity : entry.getIdentity());
  }
  
  public void registerIdentity(TicketIF ticket, IdentityIF identity) {
    // validate ticket
    if (!ticket.isValid()) return;

    if (debug) log.debug("Registering identity " + identity);
    
    // register identity if we don't already have it.
    if (datacache.get(identity) == null) {
      
      //! System.out.println("+I  " + identity);      
      
      // register with cache
      datacache.put(WrappedIdentity.wrap(identity), new CacheEntry(identity, getFieldsCount(identity.getType())));
    }
  }
  
  public void registerField(TicketIF ticket, IdentityIF identity, int field, Object value) {
    // validate ticket
    if (!ticket.isValid()) return;
                                          
    // Note: object identity should be registered already.
    if (debug)
      log.debug("Registering " + identity + " field " + field + "=" + value);
    CacheEntry fields = (CacheEntry)datacache.get(identity);
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

  public TicketIF getTicket() {
    return current_ticket;
  }

  public synchronized void registerEviction() {
    eviction++;
    this.current_ticket = new Ticket(++current_ticket_value);
  }
  
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
    public boolean isValid() {
      return value == current_ticket_value && !isRunningEviction();
    }
  }
  
  // -----------------------------------------------------------------------------
  // CacheEntry initialization
  // -----------------------------------------------------------------------------
  
  protected Map field_counts = new HashMap();
  
  protected int getFieldsCount(Object type) {
    synchronized (field_counts) {
      Integer count = (Integer)field_counts.get(type);
      if (count != null)
        return count.intValue();
      
      // Otherwise compute the fields count
      ClassInfoIF cinfo = storage.getMapping().getClassInfo(type);
      int field_count = cinfo.getValueFieldInfos().length;
      
      // The field counts is a map between Class and the number of
      // fields that they have. This number is used to allocate room for
      // field metata and field values in the data cache entries.
      field_counts.put(type, new Integer(field_count));
      return field_count;
    }
  }
  
  // -----------------------------------------------------------------------------
  // Cache reset + statistics
  // -----------------------------------------------------------------------------
  
  public void clear(boolean notifyCluster) {
    this.datacache.clear();
    if (cluster != null && notifyCluster) {
      cluster.clearDatacache();
    }
  }
  
  public void writeReport(java.io.Writer out, boolean dumpCache) throws java.io.IOException {
    try {
      // lock data cache while generating report
      Map stats = new HashMap();
      // generate statistics
      int size = 0;
      synchronized (datacache) {
        Iterator iter = datacache.keySet().iterator();
        while (iter.hasNext()) {
          IdentityIF key = (IdentityIF)iter.next();
          if (key == null) continue;
          size++;
          if (!stats.containsKey(key.getType())) {
            stats.put(key.getType(), new Integer(1));
          } else {
            Integer cnt = (Integer)stats.get(key.getType());
            stats.put(key.getType(), new Integer(cnt.intValue() + 1));
          }
        }
      }
      
      out.write("<p>Cache size: " + size + "<br>\n");
      out.write("Eviction: " + getEvictionCount() + " Ticket: " + getCurrentTicket() + "<br>\n");
      out.write("Created: " + new Date(timestamp) + " (" + (System.currentTimeMillis()-timestamp) + " ms)</p>\n");
          
      // output statistics
      out.write("<table>\n");
      Iterator iter = stats.keySet().iterator();
      while (iter.hasNext()) {
        Object key = iter.next();
        Object val = stats.get(key);
          out.write("<tr><td>");
          out.write((key == null ? "null" : StringUtils.escapeHTMLEntities(key.toString())));
          out.write("</td><td>");
          out.write((val == null ? "null" : StringUtils.escapeHTMLEntities(val.toString())));
          out.write("</td></tr>\n");
      }
      out.write("</table><br>\n");
      
      if (dumpCache) {
        out.write("<table>\n");
        synchronized (datacache) {
          iter = datacache.keySet().iterator();
          while (iter.hasNext()) {
            Object key = iter.next();
            if (key == null) continue;
            Object val = datacache.get(key);
            out.write("<tr><td>");
            out.write((key == null ? "null" : StringUtils.escapeHTMLEntities(key.toString())));
            out.write("</td><td>");
            out.write((val == null ? "null" : StringUtils.escapeHTMLEntities(val.toString())));
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
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("proxy.SharedCache@");
    sb.append(System.identityHashCode(this));
    return sb.toString();
  }
  
}
