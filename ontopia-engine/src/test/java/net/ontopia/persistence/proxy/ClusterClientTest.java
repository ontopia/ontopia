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

import java.util.HashMap;
import java.util.Map;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import org.jgroups.Message;
import org.junit.Ignore;

/**
 * INTERNAL: Client test class that receives events from a master. The
 * tests each check an individual aspect of the object model api.
 */
@Ignore //not to be ran by Maven's JUnit
public class ClusterClientTest extends AbstractClusterTest {

  private Map tests;
  private TopicMapIF topicmap;
  private boolean testInitialProperties;

  private int testsRun;
  private int testsFailed;
  
  public ClusterClientTest(String clusterId, String clusterProps) {
    super(clusterId, clusterProps);
  }

  @Override
  public void setUp() {
    
    // get topic map
    TopicMapStoreIF store = TopicMaps.createStore("cluster-test", false);
    topicmap = store.getTopicMap();
    
    // set up tests
    tests = new HashMap();
      
    tests.put("test:start", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          done = false;
        }
      });
    
    // -----------------------------------------------------------------------------    

    // TopicMapIF.addItemIdentifier
    tests.put("TopicMapIF.addItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicMapIF m = (TopicMapIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Topic map source locator is not set", m.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Topic map not found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")).equals(m));
        }
      });
    tests.put("TopicMapIF.removeItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicMapIF m = (TopicMapIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Topic map source locator is set", !m.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Topic map found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")) == null);
        }
      });

    // -----------------------------------------------------------------------------
    
    // TopicMapIF.addTopic
    tests.put("TopicMapIF.addTopic", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicIF t = (TopicIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Added topic not found", t != null);
          if (testInitialProperties) {
            assertTrue("Subject locator is set", t.getSubjectLocators().isEmpty());
            assertTrue("Source locators is set", t.getItemIdentifiers().isEmpty());
            assertTrue("Subject identifiers is set", t.getSubjectIdentifiers().isEmpty());
            assertTrue("Types is set", t.getTypes().isEmpty());
            assertTrue("Base names is set", t.getTopicNames().isEmpty());
            assertTrue("Occurrences is set", t.getOccurrences().isEmpty());
          }
        }
      });
    
    // TopicIF.setSubject
    tests.put("TopicIF.setSubject", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicIF t = (TopicIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Subject locator is not set", t.getSubjectLocators().contains(URILocator.create("x:subject")));
          assertTrue("Topic not found by subject locator", topicmap.getTopicBySubjectLocator(URILocator.create("x:subject")).equals(t));
        }
      });
    tests.put("TopicIF.setSubject:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicIF t = (TopicIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Subject locator is not null", t.getSubjectLocators().isEmpty());
          assertTrue("Topic found by subject locator", topicmap.getTopicBySubjectLocator(URILocator.create("x:subject")) == null);
        }
      });

    // TopicIF.addSubjectIdentifier
    tests.put("TopicIF.addSubjectIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicIF t = (TopicIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Subject identifier is not set", t.getSubjectIdentifiers().contains(URILocator.create("x:subject-indicator")));
          assertTrue("Topic not found by subject identifier", topicmap.getTopicBySubjectIdentifier(URILocator.create("x:subject-indicator")).equals(t));
        }
      });
    tests.put("TopicIF.removeSubjectIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicIF t = (TopicIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Subject identifier is set", !t.getSubjectIdentifiers().contains(URILocator.create("x:subject-indicator")));
          assertTrue("Topic found by subject identifier", topicmap.getTopicBySubjectIdentifier(URILocator.create("x:subject-indicator")) == null);
        }
      });

    // TopicIF.addItemIdentifier
    tests.put("TopicIF.addItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicIF t = (TopicIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Topic source locator is not set", t.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Topic not found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")).equals(t));
        }
      });
    tests.put("TopicIF.removeItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicIF t = (TopicIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Topic source locator is set", !t.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Topic found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")) == null);
        }
      });

    // TopicIF.addType
    tests.put("TopicIF.addType", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicIF t = (TopicIF)topicmap.getObjectById(mt.objectId);
          TopicIF type = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Topic type is not set", t.getTypes().contains(type));
        }
      });
    // TopicIF.removeType
    tests.put("TopicIF.removeType", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicIF t = (TopicIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Topic type is set", t.getTypes().isEmpty());
        }
      });

    // -----------------------------------------------------------------------------
    
    // TopicIF.addTopicName
    tests.put("TopicIF.addTopicName", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicNameIF bn = (TopicNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Added base name not found", bn != null);
          if (testInitialProperties) {
            assertTrue("Source locators is set", bn.getItemIdentifiers().isEmpty());
            assertTrue("Scope is set", bn.getScope().isEmpty());
            assertTrue("Type is set", bn.getType() == null);
            assertTrue("Value is set", "".equals(bn.getValue()));
            assertTrue("Variants is set", bn.getVariants().isEmpty());
          }
        }
      });

    // TopicNameIF.addItemIdentifier
    tests.put("TopicNameIF.addItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicNameIF bn = (TopicNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Base name source locator is not set", bn.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Base name not found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")).equals(bn));
        }
      });
    tests.put("TopicNameIF.removeItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicNameIF bn = (TopicNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Base name source locator is set", !bn.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Base name found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")) == null);
        }
      });

    // TopicNameIF.addTheme
    tests.put("TopicNameIF.addTheme", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicNameIF bn = (TopicNameIF)topicmap.getObjectById(mt.objectId);
          TopicIF theme = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Base name theme is not set", bn.getScope().contains(theme));
        }
      });
    // TopicNameIF.removeTheme
    tests.put("TopicNameIF.removeTheme", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicNameIF bn = (TopicNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Base name theme is set", bn.getScope().isEmpty());
        }
      });

    // TopicNameIF.setType
    tests.put("TopicNameIF.setType", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicNameIF bn = (TopicNameIF)topicmap.getObjectById(mt.objectId);
          TopicIF type = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Base name theme is not set", type.equals(bn.getType()));
        }
      });
    tests.put("TopicNameIF.setType:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicNameIF bn = (TopicNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Base name theme is set", bn.getType() == null);
        }
      });
    
    // TopicNameIF.setValue
    tests.put("TopicNameIF.setValue", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicNameIF bn = (TopicNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Base name value is not set", "New name".equals(bn.getValue()));
        }
      });
    tests.put("TopicNameIF.setValue:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicNameIF bn = (TopicNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Base name value is not null", "".equals(bn.getValue()));
        }
      });
    
    // -----------------------------------------------------------------------------    
    
    // TopicNameIF.addVariant
    tests.put("TopicNameIF.addVariant", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          VariantNameIF vn = (VariantNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Added variant name not found", vn != null);
          if (testInitialProperties) {
            assertTrue("Source locators is set", vn.getItemIdentifiers().isEmpty());
            assertTrue("Scope is set", vn.getScope().isEmpty());
            assertTrue("Value is set", vn.getValue() == null);
            assertTrue("Locator is set", vn.getLocator() == null);
          }
        }
      });

    // VariantNameIF.addItemIdentifier
    tests.put("VariantNameIF.addItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          VariantNameIF vn = (VariantNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Variant name source locator is not set", vn.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Variant name not found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")).equals(vn));
        }
      });
    tests.put("VariantNameIF.removeItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          VariantNameIF vn = (VariantNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Variant name source locator is set", !vn.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Variant name found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")) == null);
        }
      });

    // VariantNameIF.addTheme
    tests.put("VariantNameIF.addTheme", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          VariantNameIF vn = (VariantNameIF)topicmap.getObjectById(mt.objectId);
          TopicIF theme = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Variant name theme is not set", vn.getScope().contains(theme));
        }
      });
    // VariantNameIF.removeTheme
    tests.put("VariantNameIF.removeTheme", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          VariantNameIF vn = (VariantNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Variant name theme is set", vn.getScope().isEmpty());
        }
      });
    
    // VariantNameIF.setValue
    tests.put("VariantNameIF.setValue", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          VariantNameIF vn = (VariantNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Variant name value is not set", "New variant".equals(vn.getValue()));
        }
      });
    tests.put("VariantNameIF.setValue:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          VariantNameIF vn = (VariantNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Variant name value is not null", "".equals(vn.getValue()));
        }
      });
    
    // VariantNameIF.setLocator
    tests.put("VariantNameIF.setLocator", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          VariantNameIF vn = (VariantNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Variant name locator is not set", URILocator.create("x:variant-locator").equals(vn.getLocator()));
        }
      });
    tests.put("VariantNameIF.setLocator:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          VariantNameIF vn = (VariantNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Variant name locator is not null", URILocator.create("x:variant-locator:clear").equals(vn.getLocator()));
        }
      });
    
    // TopicNameIF.removeVariant
    tests.put("TopicNameIF.removeVariant", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          VariantNameIF vn = (VariantNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Removed variant name found", vn == null);
        }
      });
    
    // TopicIF.removeTopicName
    tests.put("TopicIF.removeTopicName", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicNameIF bn = (TopicNameIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Removed base name found", bn == null);
        }
      });
    
    // -----------------------------------------------------------------------------    
    
    // TopicIF.addOccurrence
    tests.put("TopicIF.addOccurrence", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Added occurrence not found", o != null);
          if (testInitialProperties) {
            assertTrue("Source locators is set", o.getItemIdentifiers().isEmpty());
            assertTrue("Scope is set", o.getScope().isEmpty());
            assertTrue("Type is set", o.getType() == null);
            assertTrue("Value is set", o.getValue() == null);
            assertTrue("Locator is set", o.getLocator() == null);
          }
        }
      });

    // OccurrenceIF.addItemIdentifier
    tests.put("OccurrenceIF.addItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Occurrence source locator is not set", o.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Occurrence not found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")).equals(o));
        }
      });
    tests.put("OccurrenceIF.removeItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Occurrence source locator is set", !o.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Occurrence found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")) == null);
        }
      });

    // OccurrenceIF.addTheme
    tests.put("OccurrenceIF.addTheme", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          TopicIF theme = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Occurrence theme is not set", o.getScope().contains(theme));
        }
      });
    // OccurrenceIF.removeTheme
    tests.put("OccurrenceIF.removeTheme", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Occurrence theme is set", o.getScope().isEmpty());
        }
      });

    // OccurrenceIF.setType
    tests.put("OccurrenceIF.setType", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          TopicIF type = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Occurrence type is not set", type.equals(o.getType()));
        }
      });
    tests.put("OccurrenceIF.setType:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Occurrence type is set", o.getType().getSubjectIdentifiers().contains(URILocator.create("type:cleared")));
        }
      });
    
    // OccurrenceIF.setValue
    tests.put("OccurrenceIF.setValue", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Occurrence value is not set", "New occurrence".equals(o.getValue()));
        }
      });
    tests.put("OccurrenceIF.setValue:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Occurrence value is not null", "".equals(o.getValue()));
        }
      });
    
    // OccurrenceIF.setLocator
    tests.put("OccurrenceIF.setLocator", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Occurrence locator is not set", URILocator.create("x:occurrence-locator").equals(o.getLocator()));
        }
      });
    tests.put("OccurrenceIF.setLocator:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Occurrence locator is not null", URILocator.create("x:occurrence-locator:clear").equals(o.getLocator()));
        }
      });
    
    // TopicIF.removeOccurrence
    tests.put("TopicIF.removeOccurrence", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          OccurrenceIF o = (OccurrenceIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Removed occurrence found", o == null);
        }
      });
    
    // -----------------------------------------------------------------------------    
    
    // TopicMapIF.addAssociation
    tests.put("TopicMapIF.addAssociation", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationIF a = (AssociationIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Added association not found", a != null);
          if (testInitialProperties) {
            assertTrue("Source locators is set", a.getItemIdentifiers().isEmpty());
            assertTrue("Scope is set", a.getScope().isEmpty());
            assertTrue("Type is set", a.getType() == null);
            assertTrue("Roles is set", a.getRoles().isEmpty());
          }
        }
      });

    // AssociationIF.addItemIdentifier
    tests.put("AssociationIF.addItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationIF a = (AssociationIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Association source locator is not set", a.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Association not found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")).equals(a));
        }
      });
    tests.put("AssociationIF.removeItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationIF a = (AssociationIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Association source locator is set", !a.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Association found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")) == null);
        }
      });

    // AssociationIF.addTheme
    tests.put("AssociationIF.addTheme", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationIF a = (AssociationIF)topicmap.getObjectById(mt.objectId);
          TopicIF theme = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Association theme is not set", a.getScope().contains(theme));
        }
      });
    // AssociationIF.removeTheme
    tests.put("AssociationIF.removeTheme", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationIF a = (AssociationIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Association theme is set", a.getScope().isEmpty());
        }
      });

    // AssociationIF.setType
    tests.put("AssociationIF.setType", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationIF a = (AssociationIF)topicmap.getObjectById(mt.objectId);
          TopicIF type = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Association type is not set", type.equals(a.getType()));
        }
      });
    tests.put("AssociationIF.setType:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationIF a = (AssociationIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Association type is set", a.getType().getSubjectIdentifiers().contains(URILocator.create("type:cleared")));
        }
      });

    // -----------------------------------------------------------------------------    
    
    // AssociationIF.addRole
    tests.put("AssociationIF.addRole", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationRoleIF r = (AssociationRoleIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Added role not found", r != null);
          if (testInitialProperties) {
            assertTrue("Source locators is set", r.getItemIdentifiers().isEmpty());
            assertTrue("Type is set", r.getType() == null);
            assertTrue("Player is set", r.getPlayer() == null);
          }
        }
      });

    // AssociationRoleIF.addItemIdentifier
    tests.put("AssociationRoleIF.addItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationRoleIF r = (AssociationRoleIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Role source locator is not set", r.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Role not found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")).equals(r));
        }
      });
    tests.put("AssociationRoleIF.removeItemIdentifier", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationRoleIF r = (AssociationRoleIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Role source locator is set", !r.getItemIdentifiers().contains(URILocator.create("x:source-locator")));
          assertTrue("Role found by source locator", topicmap.getObjectByItemIdentifier(URILocator.create("x:source-locator")) == null);
        }
      });

    // AssociationRoleIF.setType
    tests.put("AssociationRoleIF.setType", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationRoleIF r = (AssociationRoleIF)topicmap.getObjectById(mt.objectId);
          TopicIF type = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Role type is not set", type.equals(r.getType()));
        }
      });
    tests.put("AssociationRoleIF.setType:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationRoleIF r = (AssociationRoleIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Role type is set", r.getType().getSubjectIdentifiers().contains(URILocator.create("type:cleared")));
        }
      });

    // AssociationRoleIF.setPlayer
    tests.put("AssociationRoleIF.setPlayer", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationRoleIF r = (AssociationRoleIF)topicmap.getObjectById(mt.objectId);
          TopicIF player = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Role player is not set" + player, player.equals(r.getPlayer()));
          assertTrue("Player role is not set", player.getRoles().contains(r));
        }
      });
    tests.put("AssociationRoleIF.setPlayer:clear", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationRoleIF r = (AssociationRoleIF)topicmap.getObjectById(mt.objectId);
          TopicIF player = (TopicIF)topicmap.getObjectById(mt.value);
          assertTrue("Role player is set", r.getPlayer().getSubjectIdentifiers().contains(URILocator.create("player:cleared")));
          assertTrue("Player roles is set", player.getRoles().isEmpty());
        }
      });

    // AssociationIF.removeRole
    tests.put("AssociationIF.removeRole", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationRoleIF r = (AssociationRoleIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Removed role found", r == null);
        }
      });

    // TopicMapIF.removeAssociation
    tests.put("TopicMapIF.removeAssociation", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          AssociationIF a = (AssociationIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Removed association found", a == null);
        }
      });
    
    // -----------------------------------------------------------------------------    

    // TopicMapIF.removeTopic
    tests.put("TopicMapIF.removeTopic", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          TopicIF t = (TopicIF)topicmap.getObjectById(mt.objectId);
          assertTrue("Removed topic found", t == null);
        }
      });

    tests.put("test:end", new ClientTest() {
      @Override
        public void run(MasterTest mt) {
          done = true;
        }
      });

    // join test cluster
    joinCluster();    
  }

  @Override
  public void tearDown() {
    // leave cluster
    leaveCluster();

    if (topicmap != null) {
      topicmap.getStore().close();
    }
  }

  @Override
  public void run() throws InterruptedException {
    System.out.println("Client is ready.");
    while (true) {
      Thread.sleep(100);
      if (done) {
        break;
      }
    }
  }
  
  // -----------------------------------------------------------------------------
  // JGroups MessageListener implementation
  // -----------------------------------------------------------------------------

  @Override
  public void receive(Message msg) {
  
    try {
      MasterTest mt = (MasterTest)msg.getObject();
      testsRun++;
      System.out.println("Received test: " + mt.testname);
      ClientTest ct = (ClientTest)tests.get(mt.testname);
      if (ct == null) {
        throw new OntopiaRuntimeException("Could not find test: " + mt.testname);
      }
      ct.run(mt);
      topicmap.getStore().commit();
      
    } catch (Exception e) {
      testsFailed++;
      e.printStackTrace();
    }
  }
  
  // -----------------------------------------------------------------------------
  // Main
  // -----------------------------------------------------------------------------

  public static void main(String[] args) throws Exception {

    // initialize logging
    CmdlineUtils.initializeLogging();
      
    // register logging options
    CmdlineOptions options = new CmdlineOptions("ClusterClientTest", args);
    CmdlineUtils.registerLoggingOptions(options);

    String clusterId = "cluster-test"; // args[0];
    String clusterProps = null; // (args.length >= 2 ? args[1] : null);

    ClusterClientTest tester = new ClusterClientTest(clusterId, clusterProps);
    try {
      tester.setUp();
      tester.run();    
    } finally {
      tester.tearDown();
    }
    System.out.println("Tests: " + tester.testsRun + " failed: " + tester.testsFailed);
  }
  
}
