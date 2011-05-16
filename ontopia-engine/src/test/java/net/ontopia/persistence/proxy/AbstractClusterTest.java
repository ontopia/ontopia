
// $Id: AbstractClusterTest.java,v 1.1 2007/10/29 13:57:44 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.util.*;
import java.net.URL;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import net.ontopia.infoset.core.*;
import net.ontopia.topicmaps.core.*;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.MessageListener;
import org.jgroups.blocks.PullPushAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
public abstract class AbstractClusterTest implements MessageListener {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(AbstractClusterTest.class.getName());
  
  transient protected JChannel channel;
  transient protected PullPushAdapter adapter;

  transient protected String clusterId;
  transient protected String clusterProps;

  transient protected boolean done;

  public AbstractClusterTest() {
    // for serialization
  }
  
  public AbstractClusterTest(String clusterId, String clusterProps) {
    this.clusterId = clusterId;
    this.clusterProps = clusterProps;    
  }

  public abstract void setUp();

  public abstract void tearDown();
  
  public abstract void run() throws InterruptedException;
  
  // -----------------------------------------------------------------------------
  // Cluster membership
  // -----------------------------------------------------------------------------
  
  public synchronized void joinCluster() {   
    try {
      System.out.println("Joining cluster: '" + clusterId + "'");

      try {
        URL url = (clusterProps != null ? StreamUtils.getResource(clusterProps) : null);
        if (url == null) {
          if (clusterProps == null) {
            log.debug("Using default cluster properties.");
            this.channel = new JChannel();
          } else {
            log.debug("Using cluster properties as given: '" + clusterProps + "'");
            this.channel = new JChannel(clusterProps);
          }
        } else {
          log.debug("Using cluster properties in: '" + url + "'");
          this.channel = new JChannel(url);
        }
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Problems occurred while loading JGroups properties from " + clusterProps, e);
      }
      
      this.channel.setOpt(Channel.LOCAL, Boolean.FALSE);
      this.channel.setOpt(Channel.AUTO_GETSTATE, Boolean.TRUE);      
      this.channel.connect(clusterId);
      this.adapter = new PullPushAdapter(this.channel);
      this.adapter.setListener(this);
    } catch (ChannelException e) {
      throw new OntopiaRuntimeException("Could not connect to cluster '" + clusterId + "'.", e);
    }
  }
  
  public synchronized void leaveCluster() {
    System.out.println("Leaving cluster: '" + clusterId + "'");
    if (adapter != null) {
      adapter.stop();
      adapter = null;
    }
    if (channel != null) {
      channel.close();
      channel = null;
    }
  }

  // -----------------------------------------------------------------------------
  // JGroups MessageListener implementation
  // -----------------------------------------------------------------------------

  public abstract void receive(Message msg);

  public byte[] getState() {
    return null;
  }

  public void setState(byte[] jgstate) {
  }
  
  // -----------------------------------------------------------------------------
  // Test methods
  // -----------------------------------------------------------------------------

  public void assertTrue(String message, boolean fact) {
    if (!fact)
      throw new OntopiaRuntimeException(message);
  }
  
}
