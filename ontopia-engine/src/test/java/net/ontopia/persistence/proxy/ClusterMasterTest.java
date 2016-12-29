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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import org.jgroups.Message;
import org.junit.Ignore;

/**
 * INTERNAL: Server test class that sends events to clients. The tests
 * each modify an individual aspect of the object model api.
 *
 * Instructions:<br>
 *
 * build: ant dist.jar.oks.enterprise.test
 * distribute: ant -Dhostname=oks01 distribute.cluster; ant -Dhostname=oks02 distribute.cluster
 * run master: java -Djava.net.preferIPv4Stack=true net.ontopia.persistence.proxy.test.ClusterMasterTest | grep -v UDP
 * run client: java -Djava.net.preferIPv4Stack=true net.ontopia.persistence.proxy.test.ClusterClientTest | grep -v UDP
 * 
 */
@Ignore //not to be ran by Maven's JUnit
public class ClusterMasterTest extends AbstractClusterTest implements java.io.Serializable {

  transient List tests;

  transient TopicMapStoreIF store;
  transient TopicMapIF topicmap;
  transient TopicMapBuilderIF builder;

  public ClusterMasterTest() {
    // for serialization
  }
  
  public ClusterMasterTest(String clusterId, String clusterProps) {
    super(clusterId, clusterProps);
  }

  public void setUp() {
    // join test cluster
    joinCluster();
    
    // get topic map
    this.store = TopicMaps.createStore("cluster-test", false);
    System.out.println("Store: " + store);
    this.topicmap = store.getTopicMap();
    this.builder = topicmap.getBuilder();

    // clear topic map
    topicmap.clear();
    // clear TopicMapIF.source_locators
    Iterator iter = new ArrayList(topicmap.getItemIdentifiers()).iterator();
    while (iter.hasNext()) {
      LocatorIF loc = (LocatorIF)iter.next();
      topicmap.removeItemIdentifier(loc);
    }
    this.store.commit();
    
    // set up tests
    tests = new ArrayList();

    Map shared = new HashMap();
    
    tests.add(new MasterTest(shared, "test:start") {
        public void run() {
          done = false;
        }
      });

    // -----------------------------------------------------------------------------
    
    // TopicMapIF.addItemIdentifier
    tests.add(new MasterTest(shared, "TopicMapIF.addItemIdentifier") {
        public void run() {
          topicmap.addItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = topicmap.getObjectId();
        }
      });
    
    // TopicMapIF.removeItemIdentifier
    tests.add(new MasterTest(shared, "TopicMapIF.removeItemIdentifier") {
        public void run() {
          topicmap.removeItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = topicmap.getObjectId();
        }
      });

    // -----------------------------------------------------------------------------

    // TopicMapIF.addTopic
    tests.add(new MasterTest(shared, "TopicMapIF.addTopic") {
        public void run() {
          TopicIF t = builder.makeTopic();
          this.data.put("TopicIF", t);
          this.objectId = t.getObjectId();
        }
      });

    // TopicIF.setSubject
    tests.add(new MasterTest(shared, "TopicIF.setSubject") {
        public void run() {
          TopicIF t = getTopic();
          t.addSubjectLocator(URILocator.create("x:subject"));
          this.objectId = t.getObjectId();
        }
      });
    tests.add(new MasterTest(shared, "TopicIF.setSubject:clear") {
        public void run() {
          TopicIF t = getTopic();
          t.removeSubjectLocator(URILocator.create("x:subject"));
          this.objectId = t.getObjectId();
        }
      });

    // TopicIF.addSubjectIdentifier
    tests.add(new MasterTest(shared, "TopicIF.addSubjectIdentifier") {
        public void run() {
          TopicIF t = getTopic();
          t.addSubjectIdentifier(URILocator.create("x:subject-indicator"));
          this.objectId = t.getObjectId();
        }
      });
    
    // TopicIF.removeSubjectIdentifier
    tests.add(new MasterTest(shared, "TopicIF.removeSubjectIdentifier") {
        public void run() {
          TopicIF t = getTopic();
          t.removeSubjectIdentifier(URILocator.create("x:subject-indicator"));
          this.objectId = t.getObjectId();
        }
      });
    
    // TopicIF.addType
    tests.add(new MasterTest(shared, "TopicIF.addType") {
        public void run() {
          TopicIF t = getTopic();
          TopicIF type = builder.makeTopic();
          this.data.put("type", type);
          t.addType(type);
          this.objectId = t.getObjectId();
          this.value = type.getObjectId();
        }
      });
    
    // TopicIF.removeType
    tests.add(new MasterTest(shared, "TopicIF.removeType") {
        public void run() {
          TopicIF t = getTopic();
          TopicIF type = (TopicIF)this.data.remove("type");
          t.removeType(type);
          this.objectId = t.getObjectId();
          this.value = type.getObjectId();
        }
      });
    
    // TopicIF.addItemIdentifier
    tests.add(new MasterTest(shared, "TopicIF.addItemIdentifier") {
        public void run() {
          TopicIF t = getTopic();
          t.addItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = t.getObjectId();
        }
      });
    
    // TopicIF.removeItemIdentifier
    tests.add(new MasterTest(shared, "TopicIF.removeItemIdentifier") {
        public void run() {
          TopicIF t = getTopic();
          t.removeItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = t.getObjectId();
        }
      });

    // -----------------------------------------------------------------------------
    
    // TopicIF.addTopicName
    tests.add(new MasterTest(shared, "TopicIF.addTopicName") {
        public void run() {
          TopicNameIF bn = builder.makeTopicName(getTopic(), "");
          this.data.put("TopicNameIF", bn);
          this.objectId = bn.getObjectId();
        }
      });
    
    // TopicNameIF.addItemIdentifier
    tests.add(new MasterTest(shared, "TopicNameIF.addItemIdentifier") {
        public void run() {
          TopicNameIF bn = getTopicName();
          bn.addItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = bn.getObjectId();
        }
      });
    
    // TopicNameIF.removeItemIdentifier
    tests.add(new MasterTest(shared, "TopicNameIF.removeItemIdentifier") {
        public void run() {
          TopicNameIF bn = getTopicName();
          bn.removeItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = bn.getObjectId();
        }
      });
    
    // TopicNameIF.addTheme
    tests.add(new MasterTest(shared, "TopicNameIF.addTheme") {
        public void run() {
          TopicNameIF bn = getTopicName();
          TopicIF theme = builder.makeTopic();
          this.data.put("scope", theme);
          bn.addTheme(theme);
          this.objectId = bn.getObjectId();
          this.value = theme.getObjectId();
        }
      });
    
    // TopicNameIF.removeTheme
    tests.add(new MasterTest(shared, "TopicNameIF.removeTheme") {
        public void run() {
          TopicNameIF bn = getTopicName();
          TopicIF theme = (TopicIF)this.data.remove("scope");
          bn.removeTheme(theme);
          this.objectId = bn.getObjectId();
          this.value = theme.getObjectId();
        }
      });
    
    // TopicNameIF.setType
    tests.add(new MasterTest(shared, "TopicNameIF.setType") {
        public void run() {
          TopicNameIF bn = getTopicName();
          TopicIF type = builder.makeTopic(); // create type
          this.data.put("type", type);
          bn.setType(type);
          this.objectId = bn.getObjectId();
          this.value = type.getObjectId();
        }
      });
    tests.add(new MasterTest(shared, "TopicNameIF.setType:clear") {
        public void run() {
          TopicNameIF bn = getTopicName();
          TopicIF type = builder.makeTopic(); // create type
          bn.setType(null);
          TopicIF xtype = (TopicIF)this.data.remove("type"); // remove type
          xtype.remove();          
          this.objectId = bn.getObjectId();
        }
      });

    // TopicNameIF.setValue
    tests.add(new MasterTest(shared, "TopicNameIF.setValue") {
        public void run() {
          TopicNameIF bn = getTopicName();
          bn.setValue("New name");
          this.objectId = bn.getObjectId();
        }
      });
    tests.add(new MasterTest(shared, "TopicNameIF.setValue:clear") {
        public void run() {
          TopicNameIF bn = getTopicName();
          bn.setValue("");
          this.objectId = bn.getObjectId();
        }
      });

    // -----------------------------------------------------------------------------

    // TopicNameIF.addVariant
    tests.add(new MasterTest(shared, "TopicNameIF.addVariant") {
        public void run() {
          TopicNameIF bn = getTopicName();
          VariantNameIF vn = builder.makeVariantName(bn, "");
          this.data.put("VariantNameIF", vn);
          this.objectId = vn.getObjectId();
        }
      });
    
    // VariantNameIF.addItemIdentifier
    tests.add(new MasterTest(shared, "VariantNameIF.addItemIdentifier") {
        public void run() {
          VariantNameIF vn = getVariantName();
          vn.addItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = vn.getObjectId();
        }
      });
    
    // VariantNameIF.removeItemIdentifier
    tests.add(new MasterTest(shared, "VariantNameIF.removeItemIdentifier") {
        public void run() {
          VariantNameIF vn = getVariantName();
          vn.removeItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = vn.getObjectId();
        }
      });
    
    // VariantNameIF.addTheme
    tests.add(new MasterTest(shared, "VariantNameIF.addTheme") {
        public void run() {
          VariantNameIF vn = getVariantName();
          TopicIF theme = builder.makeTopic();
          this.data.put("scope", theme);
          vn.addTheme(theme);
          this.objectId = vn.getObjectId();
          this.value = theme.getObjectId();
        }
      });
    
    // VariantNameIF.removeTheme
    tests.add(new MasterTest(shared, "VariantNameIF.removeTheme") {
        public void run() {
          VariantNameIF vn = getVariantName();
          TopicIF theme = (TopicIF)this.data.remove("scope");
          vn.removeTheme(theme);
          this.objectId = vn.getObjectId();
          this.value = theme.getObjectId();
        }
      });

    // VariantNameIF.setValue
    tests.add(new MasterTest(shared, "VariantNameIF.setValue") {
        public void run() {
          VariantNameIF vn = getVariantName();
          vn.setValue("New variant");
          this.objectId = vn.getObjectId();
        }
      });
    tests.add(new MasterTest(shared, "VariantNameIF.setValue:clear") {
        public void run() {
          VariantNameIF vn = getVariantName();
          vn.setValue("");
          this.objectId = vn.getObjectId();
        }
      });

    // VariantNameIF.setLocator
    tests.add(new MasterTest(shared, "VariantNameIF.setLocator") {
        public void run() {
          VariantNameIF vn = getVariantName();
          vn.setLocator(URILocator.create("x:variant-locator"));
          this.objectId = vn.getObjectId();
        }
      });
    tests.add(new MasterTest(shared, "VariantNameIF.setLocator:clear") {
        public void run() {
          VariantNameIF vn = getVariantName();
          vn.setLocator(URILocator.create("x:variant-locator:clear"));
          this.objectId = vn.getObjectId();
        }
      });
    
    // TopicNameIF.removeVariant
    tests.add(new MasterTest(shared, "TopicNameIF.removeVariant") {
        public void run() {
          TopicNameIF bn = getTopicName();
          VariantNameIF vn = (VariantNameIF)this.data.remove("VariantNameIF");
          this.objectId = vn.getObjectId();
          vn.remove();
        }
      });
         
    // TopicIF.removeTopicName
    tests.add(new MasterTest(shared, "TopicIF.removeTopicName") {
        public void run() {
          TopicIF t = getTopic();
          TopicNameIF bn = (TopicNameIF)this.data.remove("TopicNameIF");
          this.objectId = bn.getObjectId();
          bn.remove();
        }
      });

    // -----------------------------------------------------------------------------
    
    // TopicIF.addOccurrence
    tests.add(new MasterTest(shared, "TopicIF.addOccurrence") {
        public void run() {
          OccurrenceIF o = builder.makeOccurrence(getTopic(), getTopic(), "");
          this.data.put("OccurrenceIF", o);
          this.objectId = o.getObjectId();
        }
      });
    
    // OccurrenceIF.addItemIdentifier
    tests.add(new MasterTest(shared, "OccurrenceIF.addItemIdentifier") {
        public void run() {
          OccurrenceIF o = getOccurrence();
          o.addItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = o.getObjectId();
        }
      });
    
    // OccurrenceIF.removeItemIdentifier
    tests.add(new MasterTest(shared, "OccurrenceIF.removeItemIdentifier") {
        public void run() {
          OccurrenceIF o = getOccurrence();
          o.removeItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = o.getObjectId();
        }
      });
    
    // OccurrenceIF.addTheme
    tests.add(new MasterTest(shared, "OccurrenceIF.addTheme") {
        public void run() {
          OccurrenceIF o = getOccurrence();
          TopicIF theme = builder.makeTopic();
          this.data.put("scope", theme);
          o.addTheme(theme);
          this.objectId = o.getObjectId();
          this.value = theme.getObjectId();
        }
      });
    
    // OccurrenceIF.removeTheme
    tests.add(new MasterTest(shared, "OccurrenceIF.removeTheme") {
        public void run() {
          OccurrenceIF o = getOccurrence();
          TopicIF theme = (TopicIF)this.data.remove("scope");
          o.removeTheme(theme);
          this.objectId = o.getObjectId();
          this.value = theme.getObjectId();
        }
      });
    
    // OccurrenceIF.setType
    tests.add(new MasterTest(shared, "OccurrenceIF.setType") {
        public void run() {
          OccurrenceIF o = getOccurrence();
          TopicIF type = builder.makeTopic(); // create type
          this.data.put("type", type);
          o.setType(type);
          this.objectId = o.getObjectId();
          this.value = type.getObjectId();
        }
      });
    tests.add(new MasterTest(shared, "OccurrenceIF.setType:clear") {
        public void run() {
          OccurrenceIF o = getOccurrence();
          TopicIF type = builder.makeTopic(); // create type
					type.addSubjectIdentifier(URILocator.create("type:cleared"));
          o.setType(type);
          TopicIF xtype = (TopicIF)this.data.remove("type"); // remove type
          xtype.remove();          
          this.objectId = o.getObjectId();
        }
      });

    // OccurrenceIF.setValue
    tests.add(new MasterTest(shared, "OccurrenceIF.setValue") {
        public void run() {
          OccurrenceIF o = getOccurrence();
          o.setValue("New occurrence");
          this.objectId = o.getObjectId();
        }
      });
    tests.add(new MasterTest(shared, "OccurrenceIF.setValue:clear") {
        public void run() {
          OccurrenceIF o = getOccurrence();
          o.setValue("");
          this.objectId = o.getObjectId();
        }
      });

    // OccurrenceIF.setLocator
    tests.add(new MasterTest(shared, "OccurrenceIF.setLocator") {
        public void run() {
          OccurrenceIF o = getOccurrence();
          o.setLocator(URILocator.create("x:occurrence-locator"));
          this.objectId = o.getObjectId();
        }
      });
         
    // TopicIF.removeOccurrence
    tests.add(new MasterTest(shared, "TopicIF.removeOccurrence") {
        public void run() {
          TopicIF t = getTopic();
          OccurrenceIF o = (OccurrenceIF)this.data.remove("OccurrenceIF");
          this.objectId = o.getObjectId();
          o.remove();
        }
      });
    
    // -----------------------------------------------------------------------------    

    // TopicMapIF.addAssocation
    tests.add(new MasterTest(shared, "TopicMapIF.addAssociation") {
        public void run() {
          AssociationIF a = builder.makeAssociation(builder.makeTopic());
          this.data.put("AssociationIF", a);
          this.objectId = a.getObjectId();
        }
      });
    
    // AssociationIF.addItemIdentifier
    tests.add(new MasterTest(shared, "AssociationIF.addItemIdentifier") {
        public void run() {
          AssociationIF a = getAssociation();
          a.addItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = a.getObjectId();
        }
      });
    
    // AssociationIF.removeItemIdentifier
    tests.add(new MasterTest(shared, "AssociationIF.removeItemIdentifier") {
        public void run() {
          AssociationIF a = getAssociation();
          a.removeItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = a.getObjectId();
        }
      });
    
    // AssociationIF.addTheme
    tests.add(new MasterTest(shared, "AssociationIF.addTheme") {
        public void run() {
          AssociationIF a = getAssociation();
          TopicIF theme = builder.makeTopic();
          this.data.put("scope", theme);
          a.addTheme(theme);
          this.objectId = a.getObjectId();
          this.value = theme.getObjectId();
        }
      });
    
    // AssociationIF.removeTheme
    tests.add(new MasterTest(shared, "AssociationIF.removeTheme") {
        public void run() {
          AssociationIF a = getAssociation();
          TopicIF theme = (TopicIF)this.data.remove("scope");
          a.removeTheme(theme);
          this.objectId = a.getObjectId();
          this.value = theme.getObjectId();
        }
      });
    
    // AssociationIF.setType
    tests.add(new MasterTest(shared, "AssociationIF.setType") {
        public void run() {
          AssociationIF a = getAssociation();
          TopicIF type = builder.makeTopic(); // create type
          this.data.put("type", type);
          a.setType(type);
          this.objectId = a.getObjectId();
          this.value = type.getObjectId();
        }
      });
    tests.add(new MasterTest(shared, "AssociationIF.setType:clear") {
        public void run() {
          AssociationIF a = getAssociation();
          TopicIF type = builder.makeTopic(); // create type
					type.addSubjectIdentifier(URILocator.create("type:cleared"));
          a.setType(type);
          TopicIF xtype = (TopicIF)this.data.remove("type"); // remove type
          xtype.remove();          
          this.objectId = a.getObjectId();
        }
      });
    
    // -----------------------------------------------------------------------------    

    // AssociationIF.addRole
    tests.add(new MasterTest(shared, "AssociationIF.addRole") {
        public void run() {
          AssociationIF a = getAssociation();
          AssociationRoleIF r = builder.makeAssociationRole(a, builder.makeTopic(), builder.makeTopic());
          this.data.put("AssociationRoleIF", r);
          this.objectId = r.getObjectId();
        }
      });
    
    // AssociationRoleIF.addItemIdentifier
    tests.add(new MasterTest(shared, "AssociationRoleIF.addItemIdentifier") {
        public void run() {
          AssociationRoleIF r = getAssociationRole();
          r.addItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = r.getObjectId();
        }
      });
    
    // AssociationRoleIF.removeItemIdentifier
    tests.add(new MasterTest(shared, "AssociationRoleIF.removeItemIdentifier") {
        public void run() {
          AssociationRoleIF r = getAssociationRole();
          r.removeItemIdentifier(URILocator.create("x:source-locator"));
          this.objectId = r.getObjectId();
        }
      });
    
    // AssociationRoleIF.setType
    tests.add(new MasterTest(shared, "AssociationRoleIF.setType") {
        public void run() {
          AssociationRoleIF r = getAssociationRole();
          TopicIF type = builder.makeTopic(); // create type
          this.data.put("type", type);
          r.setType(type);
          this.objectId = r.getObjectId();
          this.value = type.getObjectId();
        }
      });
    tests.add(new MasterTest(shared, "AssociationRoleIF.setType:clear") {
        public void run() {
          AssociationRoleIF r = getAssociationRole();
          TopicIF type = builder.makeTopic(); // create type
					type.addSubjectIdentifier(URILocator.create("type:cleared"));
          r.setType(type);
          TopicIF xtype = (TopicIF)this.data.remove("type"); // remove type
          xtype.remove();          
          this.objectId = r.getObjectId();
        }
      });
    
    // AssociationRoleIF.setPlayer
    tests.add(new MasterTest(shared, "AssociationRoleIF.setPlayer") {
        public void run() {
          AssociationRoleIF r = getAssociationRole();
          TopicIF player = getTopic();
          r.setPlayer(player);
          this.objectId = r.getObjectId();
          this.value = player.getObjectId();
        }
      });
    tests.add(new MasterTest(shared, "AssociationRoleIF.setPlayer:clear") {
        public void run() {          
          AssociationRoleIF r = getAssociationRole();
					TopicIF oldPlayer = r.getPlayer();
          TopicIF player = builder.makeTopic(); // create player
					player.addSubjectIdentifier(URILocator.create("player:cleared"));
          r.setPlayer(player);
          this.objectId = r.getObjectId();
          this.value = oldPlayer.getObjectId();
        }
      });
    
    // AssociationIF.removeRole
    tests.add(new MasterTest(shared, "TopicMapIF.removeAssociation") {
        public void run() {
          AssociationIF a = getAssociation();
          AssociationRoleIF r = (AssociationRoleIF)this.data.remove("AssociationRoleIF");
          this.objectId = r.getObjectId();    
          r.remove();
        }
      });
    
    // TopicMapIF.removeAssociation
    tests.add(new MasterTest(shared, "TopicMapIF.removeAssociation") {
        public void run() {
          AssociationIF a = (AssociationIF)this.data.remove("AssociationIF");
          this.objectId = a.getObjectId();
          a.remove();
        }
      });
    
    // TopicMapIF.removeTopic
    tests.add(new MasterTest(shared, "TopicMapIF.removeTopic") {
        public void run() {
          TopicIF t = (TopicIF)this.data.remove("TopicIF");
          this.objectId = t.getObjectId();
          t.remove();
        }
      });

    tests.add(new MasterTest(shared, "test:end") {
        public void run() {
          done = true;
        }
      });
    
  }

  public void tearDown() {
    // leave cluster
    leaveCluster();

    if (store != null)
      store.close();
  }

  public void run() throws InterruptedException {
    System.out.println("Master is ready. Waiting 5000 ms.");
    Thread.sleep(5000);
    System.out.println("Tests: " + tests.size());
    
    Iterator iter = tests.iterator();
    while (iter.hasNext()) {
      MasterTest mt = (MasterTest)iter.next();
      mt.run();
      store.commit();
      sendTest(mt);
      System.out.println("Sleeping 1000ms.");
      Thread.sleep(1000);
    }
  }
  
  // -----------------------------------------------------------------------------
  // JGroups MessageListener implementation
  // -----------------------------------------------------------------------------
    
  protected void sendTest(MasterTest mt) {
    System.out.println("Sending: " + mt.testname);
    try {
      Message msg = new Message(null, null, mt);
      channel.send(msg);
    } catch (Exception ex1) {
      ex1.printStackTrace();
    }
  }

  public void receive(Message msg) {
    System.out.println("Received: " + msg);
  }
  
  // -----------------------------------------------------------------------------
  // Main
  // -----------------------------------------------------------------------------

  public static void main(String[] args) throws Exception {

    // initialize logging
    CmdlineUtils.initializeLogging();
      
    // register logging options
    CmdlineOptions options = new CmdlineOptions("ClusterMasterTest", args);
    CmdlineUtils.registerLoggingOptions(options);

    String clusterId = "cluster-test"; // args[0];
    String clusterProps = null; // (args.length >= 2 ? args[1] : null);

    ClusterMasterTest tester = new ClusterMasterTest(clusterId, clusterProps);
    try {
      tester.setUp();
      tester.run();    
    } finally {
      tester.tearDown();
    }
  }
  
}
