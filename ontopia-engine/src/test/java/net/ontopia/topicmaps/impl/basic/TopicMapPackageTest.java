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

package net.ontopia.topicmaps.impl.basic;

import java.util.Collections;
import java.util.Iterator;
import junit.framework.TestCase;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.utils.IntersectionOfContextDecider;
import net.ontopia.utils.DeciderIterator;

public abstract class TopicMapPackageTest extends TestCase {
  public static TopicMapIF tm;
  public static LocatorIF base;

  protected boolean session = true;
  
  public TopicMapPackageTest(String name) {
    super(name);
  }

  protected abstract void setUp();

  protected abstract void tearDown();

  public void testTopicMap() {
    LocatorIF locat1 = base.resolveAbsolute("#post-added-theme-1"); 
    LocatorIF locat2 = base.resolveAbsolute("#post-added-theme-2");
    LocatorIF locrt1 = base.resolveAbsolute("#removed-topic-1");

    // System.out.println("tm:" + tm);
    tmobjectTest(tm, false, false);

    // Topics
    assertTrue("getTopics (not null)", tm.getTopics() != null);
    assertTrue("getTopics (type check)", tm.getTopics() instanceof java.util.Collection);
    int topic_count = tm.getTopics().size();
    TopicIF removed_topic = (TopicIF)tm.getObjectByItemIdentifier(locrt1);
    removed_topic.remove();
    assertTrue("removeTopic", tm.getTopics().size() == topic_count -1);

    if (session) {

      // Associations
      assertTrue("getAssociations (not null)", tm.getAssociations() != null);
      assertTrue("getAssociations (type check)", tm.getAssociations() instanceof java.util.Collection);
      int association_count = tm.getAssociations().size();

      // FIXME: Removing associations means clearing the player of its roles! [see bug #74]
      LocatorIF loct3 = base.resolveAbsolute("#topic-3");
      AssociationIF removed_association = ((AssociationRoleIF)((TopicIF)tm.getObjectByItemIdentifier(loct3)).getRoles().iterator().next()).getAssociation();
      // AssociationIF removed_association = (AssociationIF)(new ArrayList(tm.getAssociations()).get(0));
      removed_association.remove();
      assertTrue("removeAssociation", tm.getAssociations().size() == association_count -1);
    }
    
    // Topic lookup
    LocatorIF loc1 = base.resolveAbsolute("#topic-1");
    LocatorIF locno = base.resolveAbsolute("#unknown-topic");
    assertTrue("getTopic (known topic)", (TopicIF)tm.getObjectByItemIdentifier(loc1) instanceof TopicIF);
    assertTrue("getTopic (unknown topic)", (TopicIF)tm.getObjectByItemIdentifier(locno) == null);

    if (session) {
      assertTrue("getTopicsByIndicator (known identity)" + base.resolveAbsolute("#topic-identity-1"),
                 tm.getTopicBySubjectIdentifier(base.resolveAbsolute("#topic-identity-1"))
                 instanceof TopicIF);
      assertTrue("getTopicsByIndicator (unknown identity)",
                 tm.getTopicBySubjectIdentifier(base.resolveAbsolute("#unknown-identity"))
                 == null);
    }

    //      try {
    //        // SGML ids
    //        ((TopicIF)tm.getObjectById("topic-2")).setSGMLId("topic-2-with-new-id");
    //        assertTrue("getTopic not null (before)", (TopicIF)tm.getObjectById("topic-2-with-new-id") != null);
    //        assertTrue("getSGMLId equal set value (before)", ((TopicIF)tm.getObjectById("topic-2-with-new-id")).getSGMLId().equals("topic-2-with-new-id"));
      
    //        ((TopicIF)tm.getObjectById("topic-2-with-new-id")).setSGMLId("topic-2");
    //        assertTrue("getTopic not null (after)", (TopicIF)tm.getObjectById("topic-2") != null);
    //        assertTrue("getSGMLId equal set value (after)", ((TopicIF)tm.getObjectById("topic-2")).getSGMLId().equals("topic-2"));
      
    //        // Identities
    //        assertTrue("getIdentities (type check)", tm.getIdentities() instanceof java.util.Collection);
    //        int identity_count = tm.getIdentities().size();
    //        ((TopicIF)tm.getObjectById("topic-2")).setIdentity("topic-identity-2");
    //        assertTrue("getIdentities not null", ((TopicIF)tm.getObjectById("topic-2")).getIdentity() != null);
    //        assertTrue("getIdentities (size check after change)", tm.getIdentities().size() == identity_count + 1);
    //        ((TopicIF)tm.getObjectById("topic-2")).setIdentity(null);
    //        assertTrue("getIdentities null", ((TopicIF)tm.getObjectById("topic-2")).getIdentity() == null);
    //        assertTrue("getIdentities (size check after reset)" + tm.getIdentities().size() +" "+ identity_count, tm.getIdentities().size() == identity_count);
    //      } catch(UniquenessViolationException e) {
    //        assertTrue("Error: " + e.toString(), false);
    //      }
  }

  public void testTopic() {
    LocatorIF loc1 = base.resolveAbsolute("#topic-1");
    LocatorIF loc1i = base.resolveAbsolute("#topic-identity-1");
    LocatorIF loc2 = base.resolveAbsolute("#topic-2");
    TopicIF topic1 = (TopicIF)tm.getObjectByItemIdentifier(loc1);
    TopicIF topic2 = (TopicIF)tm.getObjectByItemIdentifier(loc2);
    tmobjectTest(topic1, true, false);
    tmobjectTest(topic2, true, false);

    // SGML id (see testTopicMap for a complete test)
    assertTrue("getResId topic-1", topic1.getItemIdentifiers().contains(loc1));
    assertTrue("getResId topic-2", topic2.getItemIdentifiers().contains(loc2));

    // Identity (see testTopicMap for a complete test)
    assertTrue("getSubjInd topic-1", topic1.getSubjectIdentifiers().contains(loc1i));
    assertTrue("getSubjInd topic-2", topic2.getSubjectIdentifiers().size() == 0);

    // Linktype
    // String linktype = topic1.getLinkType();
    // topic1.setLinkType("linktype1");
    // assertTrue(topic1.getLinkType() != null);
    // assertTrue(topic1.getLinkType().equals("linktype1"));
    // topic1.setLinkType(linktype);

    // Names
    int org_count = topic2.getTopicNames().size();
    assertTrue("getNames (size check before add)", org_count == 3);
    TopicNameIF name = tm.getBuilder().makeTopicName(topic2, "");

    // Note: the builder adds it for us.
    assertTrue("getTopicNames (size check after add)",
               topic2.getTopicNames().size() == org_count + 1);
    name.remove();
    assertTrue("getTopicNames (size check after remove)",
               topic2.getTopicNames().size() == org_count);

    // Occurrences
    assertTrue("getOccurrences (type check)",
               topic2.getOccurrences() instanceof java.util.Collection);
    int occurs_count = topic2.getOccurrences().size();
    assertTrue("getOccurrences (size check before add)",
               topic2.getOccurrences().size() == 2);
    OccurrenceIF occurs =
      tm.getBuilder().makeOccurrence(topic2, tm.getBuilder().makeTopic(), "");

    // Note: the factory adds it for us.
    assertTrue("getOccurrences (size check after add)", topic2.getOccurrences().size() == occurs_count + 1);
    occurs.remove();
    assertTrue("getOccurrences (size check after remove)", topic2.getOccurrences().size() == occurs_count);

    // Association roles
    assertTrue("getRoles (type check)",
               topic2.getRoles() instanceof java.util.Collection);
    int assocrl_count = topic2.getRoles().size();
    assertTrue("getRoles (size check before add)", topic2.getRoles().size() == 1);
    AssociationIF assoc = tm.getBuilder().makeAssociation(tm.getBuilder().makeTopic());
    AssociationRoleIF assocrl = tm.getBuilder().makeAssociationRole(assoc, tm.getBuilder().makeTopic(), tm.getBuilder().makeTopic());
    
    // the topic should not have this role yet, because the
    // association is not part of the TM yet
    assertTrue("getRoles (size check after role add)",
               topic2.getRoles().size() == assocrl_count);

    assocrl.setPlayer(topic2);

    // the role should now have been added
    assertTrue("getRoles (size check after player set)",
               topic2.getRoles().size() == assocrl_count + 1);

		TopicIF otopic = tm.getBuilder().makeTopic();
    assocrl.setPlayer(otopic);
    
    assertTrue("getRoles (size check after player remove)",
               topic2.getRoles().size() == assocrl_count);

    assertTrue("getRoles (size check after player add)",
               otopic.getRoles().size() == 1);

    // Types
    assertTrue("getTypes (size check before add)", topic1.getTypes().size() == 2);
  }

  protected void tmobjectTest(TMObjectIF tmobject, boolean typed,
                              boolean scoped) {
    if (tmobject != tm)
      assertTrue("getTopicMap" + tmobject + tmobject.getClass() +
                 tmobject.getTopicMap(),
                 tmobject.getTopicMap() == tm);

    assertTrue("isTyped", (tmobject instanceof TypedIF ||
                           tmobject instanceof TopicIF) == typed);
    assertTrue("isScoped", (tmobject instanceof ScopedIF) == scoped);
    if (tmobject instanceof TypedIF || tmobject instanceof TopicIF)
      typedTest(tmobject);
    if (tmobject instanceof ScopedIF) scopedTest((ScopedIF) tmobject);
  }
  
  protected void scopedTest(ScopedIF scoped) {
    LocatorIF loc = base.resolveAbsolute("#theme-1");
    TopicIF theme = (TopicIF)tm.getObjectByItemIdentifier(loc);
    assertTrue("getScope (type check)",
               scoped.getScope() instanceof java.util.Collection);
    int scope_count = scoped.getScope().size();

    scoped.addTheme(theme);    
    assertTrue("getScope (size check after add)",
               scoped.getScope().size() == scope_count + 1);
    scoped.removeTheme(theme);
    assertTrue("getScope (size check after remove)",
               scoped.getScope().size() == scope_count);    
  }

  protected void typedTest(TMObjectIF tmobject) {
    // Check if this really is a typed object
    if (!(tmobject instanceof TypedIF || tmobject instanceof TopicIF))
      fail("Object " + tmobject + " isnt't typed.");
    
    // Topic
    if (tmobject instanceof TopicIF) {
      TopicIF topic = (TopicIF) tmobject;
      assertTrue("getTypes (not null)", topic.getTypes() != null);
      assertTrue("getTypes (type check)",
                 topic.getTypes() instanceof java.util.Collection);

      int types_size = topic.getTypes().size();

      LocatorIF loc1 = base.resolveAbsolute("#post-added-type-1");
      LocatorIF loc2 = base.resolveAbsolute("#post-added-type-2");
      topic.addType((TopicIF)tm.getObjectByItemIdentifier(loc1));
      topic.addType((TopicIF)tm.getObjectByItemIdentifier(loc2));
      assertTrue("getTypes (size check after add)", topic.getTypes().size() == types_size + 2);
      topic.removeType((TopicIF)tm.getObjectByItemIdentifier(loc1));
      topic.removeType((TopicIF)tm.getObjectByItemIdentifier(loc2));
      assertTrue("getTypes (size check after remove)", topic.getTypes().size() == types_size);
    }
    // Single typed
    else {
      TypedIF typed = (TypedIF)tmobject;
      if (!(typed instanceof TopicNameIF)) {
	assertTrue("getType (not null)", typed.getType() != null);
	assertTrue("getType (type check)", typed.getType() instanceof TopicIF);
      }
    }
  }
  
  public void testTopicName() {
    LocatorIF loc1 = base.resolveAbsolute("#topic-2");
    LocatorIF loc2 = base.resolveAbsolute("#double");
    TopicIF topic = (TopicIF)tm.getObjectByItemIdentifier(loc1);
    TopicIF dtheme = (TopicIF)tm.getObjectByItemIdentifier(loc2);

    assertTrue("TopicIF.getTopicNames() size == 3", topic.getTopicNames().size() == 3);

    // Get appropriate base name by scope filtering
    Iterator deciter = new DeciderIterator(new IntersectionOfContextDecider(Collections.singleton(dtheme)),
                                           topic.getTopicNames().iterator());
    TopicNameIF topic_name = (TopicNameIF)deciter.next();
    
    //TopicNameIF topic_name = (TopicNameIF)ScopeUtils.getInBroadScope(topic.getTopicNames(), dtheme).iterator().next();
    tmobjectTest(topic_name, true, true);
    assertTrue("getTopic", topic_name.getTopic() == topic);
    //      assertTrue("getTopicNames (size check before add)", topic_name.getTopicNames().size() == 2);  
    //      assertTrue("getDisplayNames (size check before add)", topic_name.getDisplayNames().size() == 2);  
    //      assertTrue("getSortNames (size check before add)", topic_name.getSortNames().size() == 2);

    int org_count = topic.getTopicNames().size();
    
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicNameIF basename = builder.makeTopicName(topic, "");
    assertTrue("getTopicNames (size check after add)"+topic.getTopicNames().size(), topic.getTopicNames().size() == org_count + 1);  

    basename.remove();
    assertTrue("getTopicNames (size check after remove)", topic.getTopicNames().size() == org_count);  
  }
  
  public void testName() {
    LocatorIF loc = base.resolveAbsolute("#topic-2");
    TopicIF topic = (TopicIF)tm.getObjectByItemIdentifier(loc);
    TopicNameIF topic_name = (TopicNameIF)topic.getTopicNames().iterator().next();
    tmobjectTest(topic_name, true, true);
    assertTrue("getTopicName", topic_name.getTopic() == topic);
  }

  public void testOccurrence() {
    LocatorIF loc = base.resolveAbsolute("#topic-2");
    TopicIF topic = (TopicIF)tm.getObjectByItemIdentifier(loc);
    Iterator iter = topic.getOccurrences().iterator();
    while (iter.hasNext()) {
      OccurrenceIF occurs = (OccurrenceIF)iter.next();
      tmobjectTest(occurs, true, true);
    }
  }
  
  public void testAssociation() {
    LocatorIF loc = base.resolveAbsolute("#topic-1");
    TopicIF topic = (TopicIF)tm.getObjectByItemIdentifier(loc);
    AssociationRoleIF assocrl = (AssociationRoleIF)topic.getRoles().iterator().next();
    AssociationIF association = assocrl.getAssociation();
    tmobjectTest(association, true, true);
  }

  public void testAssociationRole() {
    LocatorIF loc = base.resolveAbsolute("#topic-1");
    LocatorIF loc2 = base.resolveAbsolute("#topic-2");
    TopicIF topic = (TopicIF)tm.getObjectByItemIdentifier(loc);
    AssociationRoleIF assocrl = (AssociationRoleIF)topic.getRoles().iterator().next();
    tmobjectTest(assocrl, true, false);
    // Properties
    assertTrue("getPlayer (equal topic-1 before set)", assocrl.getPlayer() == tm.getObjectByItemIdentifier(loc));
    assocrl.setPlayer((TopicIF)tm.getObjectByItemIdentifier(loc2));
    assertTrue("getPlayer (equal topic-2 after set)", assocrl.getPlayer() == tm.getObjectByItemIdentifier(loc2));
    assocrl.setPlayer((TopicIF)tm.getObjectByItemIdentifier(loc));
    assertTrue("getPlayer (equal topic-1 after set)", assocrl.getPlayer() == tm.getObjectByItemIdentifier(loc));
  }

  public void testTopicMapBuilder() throws UniquenessViolationException {
    TopicMapBuilderIF builder = tm.getBuilder();
    assertTrue("getBuilder (not null)", builder != null);

    // makeTopic(topicmap, sgmlid)
    try {
      TopicIF topic1 = builder.makeTopic();
      topic1.addItemIdentifier(base.resolveAbsolute("#grove"));
      assertTrue("makeTopic1 (resid not empty)",
                 topic1.getItemIdentifiers().size() > 0);
      assertTrue("makeTopic1 (sgmlid is set)",
                 topic1.getItemIdentifiers().contains(base.resolveAbsolute("#grove")));
    }
    catch (ConstraintViolationException e) {
      fail("Source locator was duplicated");
    }
  }
  
}
