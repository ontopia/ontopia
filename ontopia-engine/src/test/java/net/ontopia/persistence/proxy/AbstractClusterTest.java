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
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
public abstract class AbstractClusterTest extends ReceiverAdapter {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(AbstractClusterTest.class.getName());
  
  transient protected JChannel channel;

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
      
      this.channel.setReceiver(this);
      this.channel.connect(clusterId);
    } catch (Exception e) {
      throw new OntopiaRuntimeException("Could not connect to cluster '" + clusterId + "'.", e);
    }
  }
  
  public synchronized void leaveCluster() {
    System.out.println("Leaving cluster: '" + clusterId + "'");
    if (channel != null) {
      channel.close();
      channel = null;
    }
  }

  // -----------------------------------------------------------------------------
  // JGroups MessageListener implementation
  // -----------------------------------------------------------------------------

  @Override
  public abstract void receive(Message msg);

  // -----------------------------------------------------------------------------
  // Test methods
  // -----------------------------------------------------------------------------

  public void assertTrue(String message, boolean fact) {
    if (!fact) {
      throw new OntopiaRuntimeException(message);
    }
  }
  
}
