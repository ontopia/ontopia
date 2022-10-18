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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Class that represents a cluster of OKS instances.
 */

public class JGroupsCluster extends ReceiverAdapter implements ClusterIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(JGroupsCluster.class.getName());

  protected JChannel dchannel;
  
  protected String clusterId;
  protected String clusterProps;
  
  protected StorageIF storage;
  protected ConcurrentLinkedQueue<JGroupsEvent> queue;

  // Sample cluster properties: UDP(mcast_addr=228.10.9.8;mcast_port=5678):PING:FD
  
  protected JGroupsCluster(String clusterId, String clusterProps, StorageIF storage) {
    this.clusterId = clusterId;
    this.clusterProps = clusterProps;
    this.storage = storage;
    this.queue = new ConcurrentLinkedQueue<JGroupsEvent>();
  }
  
  @Override
  public synchronized void join() {   
    try {
      String joinMessage = "Joining JGroups cluster: '" + clusterId + "'";

      try {
        URL url = (clusterProps != null ? StreamUtils.getResource(clusterProps) : null);
        if (url == null) {
          if (clusterProps == null) {
            log.info(joinMessage + ", using default cluster properties.");
            this.dchannel = new JChannel();
          } else {
            log.info(joinMessage + ", using cluster properties as given: '" + clusterProps + "'");
            this.dchannel = new JChannel(clusterProps);
          }
        } else {
          log.info(joinMessage + ", using cluster properties in: '" + url + "'");
          this.dchannel = new JChannel(url);
        }
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Problems occurred while loading " + 
          "JGroups properties from " + clusterProps + ", trying to join cluster '" +
          clusterId + "'", e);
      }
      
      dchannel.setReceiver(this);

      this.dchannel.connect(clusterId);
      
    } catch (Exception e) {
      throw new OntopiaRuntimeException("Could not connect to cluster '" + clusterId + "'.", e);
    }
  }
  
  @Override
  public synchronized void leave() {
    log.info("Leaving cluster: '" + clusterId + "'");
    flush();
    if (dchannel != null) {
      dchannel.close();
      dchannel = null;
    }
  }
  
  @Override
  public void evictIdentity(IdentityIF identity) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = ClusterIF.DATA_CACHE_IDENTITY_EVICT;
    e.value = identity;
    queue(e);
  }
  
  @Override
  public void evictFields(IdentityIF identity) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = ClusterIF.DATA_CACHE_FIELDS_EVICT;
    e.value = identity;
    queue(e);
  }
  
  @Override
  public void evictField(IdentityIF identity, int field) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = ClusterIF.DATA_CACHE_FIELD_EVICT;
    e.value = identity;
    e.field = field;
    queue(e);
  }

  @Override
  public void clearDatacache() {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = ClusterIF.DATA_CACHE_CLEAR;
    queue(e);
  }
  
  @Override
  public void evictCache(IdentityIF namespace, int cacheType, Object key) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = cacheType; // event type is same as cache type
    e.namespace = namespace;
    e.value = key;
    queue(e);
  }
  
  @Override
  public void evictCache(IdentityIF namespace, int cacheType, Collection keys) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = cacheType; // event type is same as cache type
    e.namespace = namespace;
    e.value = keys;
    queue(e);
  }

  @Override
  public void clearCache(IdentityIF namespace, int cacheType) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = cacheType + 1; // event type is same as cache type + 1
    e.namespace = namespace;
    queue(e);
  }

  private void queue(JGroupsEvent e) {
    queue.add(e);
  }
  
  // -----------------------------------------------------------------------------
  // Event I/O
  // -----------------------------------------------------------------------------

  @Override
  public synchronized void flush() {
    // retrieve all pending events from event queue
    JGroupsEvent o = queue.poll();
    if (o != null) {
      ArrayList<JGroupsEvent> data = new ArrayList<JGroupsEvent>();
      do {
        data.add(o);
        o = queue.poll();
      } while (o != null);
      // send event list to cluster
      log.debug("Sending " + data.size() + " events.");
      sendEvent(data);
    }    
  }
    
  private void sendEvent(java.io.Serializable e) {
    log.debug("Sending: " + e);
    try {
      Message msg = new Message(null, e);
      dchannel.send(msg);
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  protected void processEvent(JGroupsEvent e) {
    //! log.debug("Processing event: " + e);
    if (storage.getStorageCache() == null) {
      log.warn("Cannot process cluster event without shared storage cache. Ignoring event: " + e);
      return;
    }
    switch (e.eventType) {
    case ClusterIF.DATA_CACHE_IDENTITY_EVICT:
      log.debug("  IE: " + e.value);
      storage.getStorageCache().evictIdentity((IdentityIF)e.value, false);
      break;
    case ClusterIF.DATA_CACHE_FIELDS_EVICT:
      log.debug("  FE: " + e.value);
      storage.getStorageCache().evictFields((IdentityIF)e.value, false);
      break;
    case ClusterIF.DATA_CACHE_FIELD_EVICT:
      log.debug("  FI: " + e.field + " " + e.value);
      storage.getStorageCache().evictField((IdentityIF)e.value, e.field, false);
      break;
    case ClusterIF.DATA_CACHE_CLEAR:
      log.debug("  DC!");
      storage.getStorageCache().clear(false);
      break;
    case ClusterIF.QUERY_CACHE_SRCLOC_EVICT:
    case ClusterIF.QUERY_CACHE_SUBIND_EVICT:
    case ClusterIF.QUERY_CACHE_SUBLOC_EVICT:
    case ClusterIF.QUERY_CACHE_RT1_EVICT:
    case ClusterIF.QUERY_CACHE_RT2_EVICT: {
      log.debug("  QE " + e.eventType + ": " + e.value);
      EvictableIF evictable = storage.getHelperObject(e.eventType, e.namespace);
      if (e.value instanceof Collection)
        evictable.removeAll((Collection)e.value, false);
      else
        evictable.remove(e.value, false);
      break;
    }
    case ClusterIF.QUERY_CACHE_SRCLOC_CLEAR:
    case ClusterIF.QUERY_CACHE_SUBIND_CLEAR:
    case ClusterIF.QUERY_CACHE_SUBLOC_CLEAR:
    case ClusterIF.QUERY_CACHE_RT1_CLEAR:
    case ClusterIF.QUERY_CACHE_RT2_CLEAR: {
      log.debug("  QC " + e.eventType + ": " + e.value);
      EvictableIF evictable = storage.getHelperObject(e.eventType-1 , e.namespace);
      evictable.clear(false);
      break;
    }
    default:
      log.debug("Ignored event: " + e);
    }
  }

  // -----------------------------------------------------------------------------
  // JGroups ReceiverAdapter implementation
  // -----------------------------------------------------------------------------

  @Override
  public void receive(Message msg) {
    try {
      List data = (List)msg.getObject();
      log.debug("Received " + data.size() + " events.");
      for (int i=0; i < data.size(); i++) {
        JGroupsEvent e = (JGroupsEvent)data.get(i);
        processEvent(e);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
