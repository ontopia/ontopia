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

package net.ontopia.topicmaps.core.events;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
  
public abstract class TopicModificationTests extends AbstractTopicMapTest {

  protected TopicIF bart;
  protected TesterListener listener;
  
  @Override
  public void setUp() throws Exception {
    // get a new topic map object from the factory.
    factory = getFactory();
    topicmapRef = factory.makeTopicMapReference();
    listener = new TesterListener();
    TopicMapEvents.addTopicListener(topicmapRef, listener);
    // load topic map
    topicmap = topicmapRef.createStore(false).getTopicMap();
    ImportExportUtils.getReader(TestFileUtils.getTestInputFile("various", "bart.ltm")).importInto(topicmap);
    topicmap.getStore().commit();
    
    // get the builder of that topic map.
    builder = topicmap.getBuilder();

    // get test topic
    bart = topicmap.getTopicBySubjectIdentifier(URILocator.create("test:bart"));
  }

  @Override
  public void tearDown() {
    TopicMapEvents.removeTopicListener(topicmapRef, listener);
    super.tearDown();
  }
  
  // --- Test cases

  protected class TesterListener implements TopicMapListenerIF {
    private Collection snapshots = new HashSet();
    
    @Override
    public void objectAdded(TMObjectIF o) {
      // no-op
    }

    @Override
    public void objectModified(TMObjectIF snapshot) {      
      this.snapshots.add(((TopicIF)snapshot).getObjectId());
    }
    
    @Override
    public void objectRemoved(TMObjectIF o) {
      // no-op
    }

    public void reset() {
      snapshots.clear();
    }
  }

  protected void beforeTest() {
    listener.reset();
    Assert.assertTrue("listener not properly reset", listener.snapshots.isEmpty());
  }
  
  protected void afterTest() {
    topicmap.getStore().commit();
    Assert.assertTrue("topic was not registered as modified", listener.snapshots.contains(bart.getObjectId()));
  }
  
  @Test
  public void testTopicLifecycle() {
    Assert.assertTrue("Could not find topic.", bart != null);

    // TopicIF.addSubjectLocator
    beforeTest();
    bart.addSubjectLocator(URILocator.create("x:subject-locator"));
    afterTest();

    // TopicIF.removeSubjectLocator
    beforeTest();
    bart.removeSubjectLocator(URILocator.create("x:subject-locator"));
    afterTest();

    // TopicIF.addSubjectIdentifier
    beforeTest();
    bart.addSubjectIdentifier(URILocator.create("x:subject-indicator"));
    afterTest();

    // TopicIF.removeSubjectIdentifier
    beforeTest();
    bart.removeSubjectIdentifier(URILocator.create("x:subject-indicator"));
    afterTest();

    // TopicIF.addTopicName
    beforeTest();
    TopicNameIF bn = builder.makeTopicName(bart, "");
    afterTest();

    // TopicNameIF.remove
    beforeTest();
    bn.remove();
    afterTest();

    // TopicIF.addOccurrence
    beforeTest();
    OccurrenceIF oc = builder.makeOccurrence(bart, bart, "");
    afterTest();

    // OccurrenceIF.remove
    beforeTest();
    oc.remove();
    afterTest();

    // TopicIF.addType
    beforeTest();
    TopicIF other = topicmap.getTopicBySubjectIdentifier(URILocator.create("test:other"));
    bart.addType(other);
    afterTest();

    // TopicIF.removeType
    beforeTest();
    bart.removeType(other);
    afterTest();

    // TopicIF.addItemIdentifier
    beforeTest();
    bart.addItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();
    
    // TopicIF.removeItemIdentifier
    beforeTest();
    bart.removeItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();

    // -----------------------------------------------------------------------------
    
    // TopicNameIF.setValue
    beforeTest();
    bn = bart.getTopicNames().iterator().next();
    bn.setValue("New name");
    afterTest();

    // TopicNameIF.addItemIdentifier
    beforeTest();
    bn.addItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();
    
    // TopicNameIF.removeItemIdentifier
    beforeTest();
    bn.removeItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();

    // TopicNameIF.addTheme
    beforeTest();
    bn.addTheme(other);
    afterTest();

    // TopicNameIF.removeTheme
    beforeTest();
    bn.removeTheme(other);
    afterTest();

    // TopicNameIF.setType
    beforeTest();
    bn.setType(other);
    afterTest();

    // TopicNameIF.addVariant
    beforeTest();
    VariantNameIF vn = builder.makeVariantName(bn, "", Collections.emptySet());
    afterTest();

    // VariantNameIF.setValue
    beforeTest();
    vn.setValue("New variant");
    afterTest();

    // VariantNameIF.setLocator
    beforeTest();
    vn.setLocator(URILocator.create("x:variant-locator"));
    afterTest();

    // VariantNameIF.addItemIdentifier
    beforeTest();
    vn.addItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();
    
    // VariantNameIF.removeItemIdentifier
    beforeTest();
    vn.removeItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();

    // VariantNameIF.addTheme
    beforeTest();
    vn.addTheme(other);
    afterTest();

    // VariantNameIF.removeTheme
    beforeTest();
    vn.removeTheme(other);
    afterTest();

    // VariantNameIF.remove
    beforeTest();
    vn.remove();
    afterTest();

    // -----------------------------------------------------------------------------

    // OccurrenceIF.setValue
    beforeTest();
    oc = bart.getOccurrences().iterator().next();
    oc.setValue("New value");
    afterTest();
    
    // OccurrenceIF.setLocator
    beforeTest();
    oc.setLocator(URILocator.create("x:occurrence-locator"));
    afterTest();

    // OccurrenceIF.addItemIdentifier
    beforeTest();
    oc.addItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();
    
    // OccurrenceIF.removeItemIdentifier
    beforeTest();
    oc.removeItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();

    // OccurrenceIF.addTheme
    beforeTest();
    oc.addTheme(other);
    afterTest();

    // OccurrenceIF.removeTheme
    beforeTest();
    oc.removeTheme(other);
    afterTest();

    // OccurrenceIF.setType
    beforeTest();
    oc.setType(other);
    afterTest();

    // -----------------------------------------------------------------------------

    // AssociationRoleIF.setPlayer
    beforeTest();
    AssociationRoleIF ar = bart.getRoles().iterator().next();
    AssociationIF as = ar.getAssociation();
    AssociationRoleIF or = null;
    Iterator<AssociationRoleIF> iter = as.getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role = iter.next();
      if (!role.equals(ar)) {
        or = role;
        break;
      }
    }
    TopicIF springfield = or.getPlayer();
    or.setPlayer(or.getType());
    topicmap.getStore().commit();
    Assert.assertTrue("bart was not registered as modified", listener.snapshots.contains(bart.getObjectId()));
    Assert.assertTrue("springfield was not registered as modified", listener.snapshots.contains(springfield.getObjectId()));

    // AssociationIF.addItemIdentifier
    beforeTest();
    as.addItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();
    
    // AssociationIF.removeItemIdentifier
    beforeTest();
    as.removeItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();

    // AssociationRoleIF.addItemIdentifier
    beforeTest();
    ar.addItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();
    
    // AssociationRoleIF.removeItemIdentifier
    beforeTest();
    ar.removeItemIdentifier(URILocator.create("x:source-locator"));
    afterTest();
    
    // AssociationIF.addTheme
    beforeTest();
    as.addTheme(other);
    afterTest();
    
    // AssociationIF.removeTheme
    beforeTest();
    as.removeTheme(other);
    afterTest();

    // AssociationIF.setType
    beforeTest();
    as.setType(other);
    afterTest();

    // AssociationRoleIF.setType
    beforeTest();
    ar.setType(other);
    afterTest();

    // AssociationIF.addRole
    beforeTest();
    AssociationRoleIF xr = builder.makeAssociationRole(as, builder.makeTopic(), builder.makeTopic());
    afterTest();

    // AssociationRoleIF.remove
    beforeTest();
    xr.remove();
    afterTest();

    // AssociationIF.addRole
    beforeTest();
    xr = builder.makeAssociationRole(as, builder.makeTopic(), builder.makeTopic());
    afterTest();

    // AssociationIF.remove
    beforeTest();
    xr.getAssociation().remove();
    afterTest();
    
  }

}
