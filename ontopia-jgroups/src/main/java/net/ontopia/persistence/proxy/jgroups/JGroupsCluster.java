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

package net.ontopia.persistence.proxy.jgroups;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import net.ontopia.persistence.proxy.ClusterIF;
import net.ontopia.persistence.proxy.ClusterNodeListenerIF;
import net.ontopia.persistence.proxy.EvictableIF;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.InstrumentedClusterIF;
import net.ontopia.persistence.proxy.StorageIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ObjectMessage;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.protocols.MsgStats;
import org.jgroups.protocols.TP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Class that represents a jgroups cluster of Ontopia instances.
 */
// Sample cluster properties: UDP(mcast_addr=228.10.9.8;mcast_port=5678):PING:FD
public class JGroupsCluster implements InstrumentedClusterIF, Receiver {
  private static final Logger log = LoggerFactory.getLogger(JGroupsCluster.class);

  protected JChannel channel;
  protected String clusterId;
  protected StorageIF storage;
  protected ConcurrentLinkedQueue<JGroupsEvent> queue = new ConcurrentLinkedQueue<JGroupsEvent>();
  protected final Set<ClusterNodeListenerIF> listeners = new HashSet<>();

  protected JGroupsCluster(String clusterId, StorageIF storage) {
    this.clusterId = clusterId;
    this.storage = storage;
  }
  
  public JGroupsCluster(String clusterId, JChannel channel) {
    this.clusterId = clusterId;
    this.channel = channel;
  }

  @Override
  public synchronized void join() {
    try {
      if (channel == null) {
        channel = createChannel();
        channel.setReceiver(this);
        channel.setName(System.getProperty("net.ontopia.persistence.proxy.nodeName"));
        channel.connect(clusterId);
      } else {

        // wrap the receiver
        channel.setReceiver(new WrappedReceiver(channel.getReceiver()));

        if (channel.isConnected()) {
          clusterId = channel.clusterName();
        } else {
          channel.connect(clusterId);
        }
        log.info("Joining JGroups cluster: {} as {}, using shared channel", clusterId, channel.getAddress());
      }
    } catch (Exception e) {
      throw new OntopiaRuntimeException("Could not connect to cluster '" + clusterId + "'.", e);
    }
  }

  protected JChannel createChannel() {
    String joinMessage = "Joining JGroups cluster: '" + clusterId + "'";
	  String clusterProps = storage.getProperty("net.ontopia.topicmaps.impl.rdbms.Cluster.properties");

	  try {
      URL url = (clusterProps != null ? StreamUtils.getResource(clusterProps) : null);
      if (url == null) {
        if (clusterProps == null) {
        log.info(joinMessage + ", using default cluster properties.");
        return new JChannel();
        } else {
        log.info(joinMessage + ", using cluster properties as given: '" + clusterProps + "'");
        return new JChannel(clusterProps);
        }
      } else {
        log.info(joinMessage + ", using cluster properties in: '" + url + "'");
        return new JChannel(url.openStream());
      }
	  } catch (Exception e) {
      throw new OntopiaRuntimeException("Problems occurred while loading " +
        "JGroups properties from " + clusterProps + ", trying to join cluster '" +
        clusterId + "'", e);
	  }
  }  

  public JChannel getChannel() {
    return channel;
  }

  @Override
  public synchronized void leave() {
    log.info("Leaving cluster: '" + clusterId + "'");
    flush();
    if (channel != null) {
      channel.close();
      channel = null;
    }
  }

  @Override
  public void evictIdentity(IdentityIF identity) {
    queue.add(JGroupsEvent.evictIdentity(identity));
  }

  @Override
  public void evictFields(IdentityIF identity) {
    queue.add(JGroupsEvent.evictFields(identity));
  }

  @Override
  public void evictField(IdentityIF identity, int field) {
    queue.add(JGroupsEvent.evictField(identity, field));
  }

  @Override
  public void clearDatacache() {
    queue.add(JGroupsEvent.clear());
  }

  @Override
  public void evictCache(IdentityIF namespace, int cacheType, Object key) {
    queue.add(JGroupsEvent.evictCache(namespace, cacheType, key));
  }

  @Override
  public void evictCache(IdentityIF namespace, int cacheType, Collection keys) {
    queue.add(JGroupsEvent.evictCache(namespace, cacheType, keys));
  }

  @Override
  public void clearCache(IdentityIF namespace, int cacheType) {
    queue.add(JGroupsEvent.clearCache(namespace, cacheType));
  }

  // -----------------------------------------------------------------------------
  // Event I/O
  // -----------------------------------------------------------------------------

  @Override
  public synchronized void flush() {
    if (queue.isEmpty()) { return; }
    // retrieve all pending events from event queue and send event list to cluster
    ArrayList<JGroupsEvent> data = new ArrayList<JGroupsEvent>(queue);
    log.debug("Sending " + data.size() + " events.");
    sendEvent(data);
    queue.clear();
  }

  private void sendEvent(java.io.Serializable e) {
    log.debug("Sending: " + e);
    try {
      channel.send(new ObjectMessage(null, e).setFlag(Message.TransientFlag.DONT_LOOPBACK));
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
        log.debug("evict identity {}", e.value);
        storage.getStorageCache().evictIdentity((IdentityIF)e.value, false);
        break;
      case ClusterIF.DATA_CACHE_FIELDS_EVICT:
        log.debug("evict fields {}", e.value);
        storage.getStorageCache().evictFields((IdentityIF)e.value, false);
        break;
      case ClusterIF.DATA_CACHE_FIELD_EVICT:
        log.debug("evict field {} for {}", e.field, e.value);
        storage.getStorageCache().evictField((IdentityIF)e.value, e.field, false);
        break;
      case ClusterIF.DATA_CACHE_CLEAR:
        log.debug("clear ALL cache");
        storage.getStorageCache().clear(false);
        break;
      case ClusterIF.QUERY_CACHE_SRCLOC_EVICT:
      case ClusterIF.QUERY_CACHE_SUBIND_EVICT:
      case ClusterIF.QUERY_CACHE_SUBLOC_EVICT:
      case ClusterIF.QUERY_CACHE_RT1_EVICT:
      case ClusterIF.QUERY_CACHE_RT2_EVICT: {
        log.debug("evict query in {}: {}", e.eventType, e.value);
        EvictableIF evictable = storage.getHelperObject(e.eventType, e.namespace);
        if (e.value instanceof Collection) {
          evictable.removeAll((Collection)e.value, false);
        } else {
          evictable.remove(e.value, false);
        }
        break;
      }
      case ClusterIF.QUERY_CACHE_SRCLOC_CLEAR:
      case ClusterIF.QUERY_CACHE_SUBIND_CLEAR:
      case ClusterIF.QUERY_CACHE_SUBLOC_CLEAR:
      case ClusterIF.QUERY_CACHE_RT1_CLEAR:
      case ClusterIF.QUERY_CACHE_RT2_CLEAR: {
        log.debug("clear query cache {}: {}", e.eventType, e.value);
        storage.getHelperObject(e.eventType - 1 , e.namespace).clear(false);
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
      List<JGroupsEvent> data = msg.getObject();
      log.debug("Received " + data.size() + " events.");
      data.forEach(this::processEvent);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @Override
  public void viewAccepted(View view) {
    log.info("Cluster members changes: {}", view.getMembers());
    listeners.forEach(ClusterNodeListenerIF::notifyNodeChange);
  }

  @Override
  public String getClusterName() {
    return clusterId;
  }

  @Override
  public String getClusterState() {
    return channel.getState();
  }

  protected long getStat(ToLongFunction<MsgStats> statMethod) {
    return channel.getProtocolStack().getProtocols().stream()
      .filter(TP.class::isInstance)
      .map(TP.class::cast)
      .map(TP::getMessageStats)
      .collect(Collectors.summingLong(statMethod));
  }

  @Override
  public long getClusterReceivedBytes() {
    return getStat(MsgStats::getNumBytesReceived);
  }

  @Override
  public long getClusterReceivedMessages() {
    return getStat(MsgStats::getNumMsgsReceived);
  }

  @Override
  public long getClusterSentBytes() {
    return getStat(MsgStats::getNumBytesSent);
  }

  @Override
  public long getClusterSentMessages() {
    return getStat(MsgStats::getNumMsgsSent);
  }

  @Override
  public long getClusterNodeCount() {
    return channel.getView().getMembers().size();
  }

  @Override
  public Set<String> getClusterNodes() {
    return channel.getView().getMembers().stream().map(Address::toString).collect(Collectors.toSet());
  }

  @Override
  public String getClusterNode() {
    return channel.getAddressAsString();
  }

  @Override
  public void addClusterNodeListener(ClusterNodeListenerIF listener) {
    listeners.add(listener);
  }

  @Override
  public void removeClusterNodeListener(ClusterNodeListenerIF listener) {
    listeners.remove(listener);
  }

  protected class WrappedReceiver implements Receiver {

    private final Receiver wrapped;

    protected WrappedReceiver(Receiver wrapped) {
      this.wrapped = wrapped;
    }

    @Override
    public void receive(Message msg) {
      Object object = msg.getObject();
      if ((object instanceof List list) && list.stream().allMatch(JGroupsEvent.class::isInstance)) {
        receive(msg);
      } else if (wrapped != null) {
        wrapped.receive(msg);
      }
    }
  }
}
