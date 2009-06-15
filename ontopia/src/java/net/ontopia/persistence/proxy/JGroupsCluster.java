
// $Id: JGroupsCluster.java,v 1.18 2008/01/07 12:48:52 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.io.StringWriter;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.LookupIndexIF;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.StringUtils;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.MessageListener;
import org.jgroups.blocks.PullPushAdapter;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import org.apache.log4j.Logger;

/**
 * INTERNAL: Class that represents a cluster of OKS instances.
 */

public class JGroupsCluster implements ClusterIF, MessageListener {

  // Define a logging category.
  static Logger log = Logger.getLogger(JGroupsCluster.class.getName());

  final static Integer DATA = new Integer(1); 
  
  protected JChannel dchannel;
  protected PullPushAdapter ppadapter;
  
  protected String clusterId;
  protected String clusterProps;
  
  protected StorageIF storage;
  protected LinkedQueue queue;

  // Sample cluster properties: UDP(mcast_addr=228.10.9.8;mcast_port=5678):PING:FD
  
  JGroupsCluster(String clusterId, String clusterProps, StorageIF storage) {
    this.clusterId = clusterId;
    this.clusterProps = clusterProps;
    this.storage = storage;
    this.queue = new LinkedQueue();
  }
  
  public synchronized void join() {   
    try {
      log.debug("Joining JGroups cluster: '" + clusterId + "'");

      try {
        URL url = (clusterProps != null ? StreamUtils.getResource(clusterProps) : null);
        if (url == null) {
          if (clusterProps == null) {
            log.debug("Using default cluster properties.");
            this.dchannel = new JChannel();
          } else {
            log.debug("Using cluster properties as given: '" + clusterProps + "'");
            this.dchannel = new JChannel(clusterProps);
          }
        } else {
          log.debug("Using cluster properties in: '" + url + "'");
          this.dchannel = new JChannel(url);
        }
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Problems occurred while loading JGroups properties from " + clusterProps, e);
      }
      
      this.dchannel.setOpt(Channel.LOCAL, Boolean.FALSE);
      this.dchannel.setOpt(Channel.AUTO_GETSTATE, Boolean.TRUE);

      this.dchannel.connect(clusterId);
      
      this.ppadapter = new PullPushAdapter(this.dchannel);
      this.ppadapter.registerListener(DATA, this);
      
    } catch (ChannelException e) {
      throw new OntopiaRuntimeException("Could not connect to cluster '" + clusterId + "'.", e);
    }
  }
  
  public synchronized void leave() {
    log.debug("Leaving cluster: '" + clusterId + "'");
    flush();
    if (ppadapter != null) {
      ppadapter.stop();
      ppadapter = null;
    }
    if (dchannel != null) {
      dchannel.close();
      dchannel = null;
    }
  }
  
  public void evictIdentity(IdentityIF identity) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = ClusterIF.DATA_CACHE_IDENTITY_EVICT;
    e.value = identity;
    queue(e);
  }
  
  public void evictFields(IdentityIF identity) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = ClusterIF.DATA_CACHE_FIELDS_EVICT;
    e.value = identity;
    queue(e);
  }
  
  public void evictField(IdentityIF identity, int field) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = ClusterIF.DATA_CACHE_FIELD_EVICT;
    e.value = identity;
    e.field = field;
    queue(e);
  }

  public void clearDatacache() {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = ClusterIF.DATA_CACHE_CLEAR;
    queue(e);
  }
  
  public void evictCache(IdentityIF namespace, int cacheType, Object key) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = cacheType; // event type is same as cache type
    e.namespace = namespace;
    e.value = key;
    queue(e);
  }
  
  public void evictCache(IdentityIF namespace, int cacheType, Collection keys) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = cacheType; // event type is same as cache type
    e.namespace = namespace;
    e.value = keys;
    queue(e);
  }

  public void clearCache(IdentityIF namespace, int cacheType) {
    JGroupsEvent e = new JGroupsEvent();
    e.eventType = cacheType + 1; // event type is same as cache type + 1
    e.namespace = namespace;
    queue(e);
  }

  private void queue(JGroupsEvent e) {
    try {
      queue.put(e);
    } catch (InterruptedException x) {
      throw new OntopiaRuntimeException(x);
    }
  }
  
  // -----------------------------------------------------------------------------
  // Event I/O
  // -----------------------------------------------------------------------------

  public synchronized void flush() {
    // retrieve all pending events from event queue
    try {
      Object o = queue.poll(0);
      if (o != null) {
        ArrayList data = new ArrayList();
        do {
          data.add(o);
          o = queue.poll(0);
        } while (o != null);
        // send event list to cluster
        log.debug("Sending " + data.size() + " events.");
        sendEvent(data);
      }    
    } catch (InterruptedException x) {
      throw new OntopiaRuntimeException(x);
    }
  }
    
  private void sendEvent(java.io.Serializable e) {
    log.debug("Sending: " + e);
    try {
      Message msg = new Message(null, null, e);
      ppadapter.send(DATA, msg);
    } catch (java.lang.Exception ex0) {
      ex0.printStackTrace();
    //! } catch (org.jgroups.ChannelClosedException ex1) {
    //!   ex1.printStackTrace();
    //! } catch (org.jgroups.ChannelNotConnectedException ex2) {
    //!   ex2.printStackTrace();
    }
  }

  protected void processEvent(JGroupsEvent e) {
    //! log.debug("Processing event: " + e);
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
      EvictableIF evictable = (EvictableIF)storage.getHelperObject(e.eventType, e.namespace);
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
      EvictableIF evictable = (EvictableIF)storage.getHelperObject(e.eventType-1 , e.namespace);
      evictable.clear(false);
      break;
    }
    default:
      log.debug("Ignored event: " + e);
    }
  }

  // -----------------------------------------------------------------------------
  // JGroups MessageListener implementation
  // -----------------------------------------------------------------------------

  public void receive(Message msg) {
    try {
      List data = (List)msg.getObject();
      log.debug("Received " + data.size() + " events.");
      for (int i=0; i < data.size(); i++) {
        JGroupsEvent e = (JGroupsEvent)data.get(i);
        processEvent(e);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public byte[] getState() {
    return null;
  }

  public void setState(byte[] jgstate) {
  }
  
}
