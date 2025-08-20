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

package net.ontopia.topicmaps.utils;

import java.net.MalformedURLException;
import java.util.Collections;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DuplicateSuppressionUtilsTest {
  protected TopicMapIF        topicmap; 
  protected TopicMapBuilderIF builder;

  @Before
  public void setUp() {
    topicmap = makeTopicMap();
  }
    
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    return store.getTopicMap();
  }
 
  // --- Test cases

  @Test
  public void testVariantRemoval() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "");
    builder.makeVariantName(bn, "duplicate", Collections.emptySet());
    builder.makeVariantName(bn, "duplicate", Collections.emptySet());

    DuplicateSuppressionUtils.removeDuplicates(bn);

    Assert.assertTrue("duplicate variant names were not removed",
           bn.getVariants().size() == 1);
  }

  @Test
  public void testVariantRemovalWithScope() {
    TopicIF theme1 = builder.makeTopic();
    TopicIF theme2 = builder.makeTopic();
    
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "");
    VariantNameIF vn = builder.makeVariantName(bn, "duplicate", Collections.emptySet());
    vn.addTheme(theme1);
    vn.addTheme(theme2);
    vn = builder.makeVariantName(bn, "duplicate", Collections.emptySet());
    vn.addTheme(theme1);
    vn.addTheme(theme2);

    DuplicateSuppressionUtils.removeDuplicates(bn);

    Assert.assertTrue("duplicate variant names were not removed",
           bn.getVariants().size() == 1);
  }

  @Test
  public void testTopicNameRemovalWithScope() {
    TopicIF theme1 = builder.makeTopic();
    TopicIF theme2 = builder.makeTopic();
    
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "test");
    bn.addTheme(theme1);
    bn.addTheme(theme2);
    builder.makeVariantName(bn, "not duplicate", Collections.emptySet());
    
    TopicNameIF bn2 = builder.makeTopicName(topic, "test");
    bn2.addTheme(theme1);
    bn2.addTheme(theme2);
    builder.makeVariantName(bn, "not duplicate, either", Collections.emptySet());

    DuplicateSuppressionUtils.removeDuplicates(topic);

    Assert.assertTrue("duplicate base names were not removed",
           topic.getTopicNames().size() == 1);
    bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    Assert.assertTrue("variant names were not merged",
           bn.getVariants().size() == 2);
  }

  @Test
  public void testTopicNameAndVariantNameRemovalWithScope() {
    TopicIF theme1 = builder.makeTopic();
    TopicIF theme2 = builder.makeTopic();
    
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "test");
    bn.addTheme(theme1);
    bn.addTheme(theme2);
    builder.makeVariantName(bn, "duplicate", Collections.emptySet());
    
    TopicNameIF bn2 = builder.makeTopicName(topic, "test");
    bn2.addTheme(theme1);
    bn2.addTheme(theme2);
    builder.makeVariantName(bn, "duplicate", Collections.emptySet());

    DuplicateSuppressionUtils.removeDuplicates(topic);

    Assert.assertTrue("duplicate base names were not removed",
           topic.getTopicNames().size() == 1);
    bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    Assert.assertTrue("duplicate variant names were not removed",
           bn.getVariants().size() == 1);
  }
  
  @Test
  public void testOccurrenceRemoval() {
    TopicIF type = builder.makeTopic();
    
    TopicIF topic = builder.makeTopic();
    builder.makeOccurrence(topic, type, "duplicate");
    
    builder.makeOccurrence(topic, type, "duplicate");

    DuplicateSuppressionUtils.removeDuplicates(topic);

    Assert.assertTrue("duplicate occurrence were not removed",
           topic.getOccurrences().size() == 1);
  }

  @Test
  public void testAssociationRemoval() {
    TopicIF type = builder.makeTopic();
    TopicIF role1 = builder.makeTopic();
    TopicIF role2 = builder.makeTopic();
    TopicIF player1 = builder.makeTopic();
    TopicIF player2 = builder.makeTopic();
    
    AssociationIF assoc = builder.makeAssociation(type);
    builder.makeAssociationRole(assoc, role1, player1);
    builder.makeAssociationRole(assoc, role2, player2);

    assoc = builder.makeAssociation(type);
    builder.makeAssociationRole(assoc, role1, player1);
    builder.makeAssociationRole(assoc, role2, player2);

    DuplicateSuppressionUtils.removeDuplicates(topicmap);

    Assert.assertTrue("duplicate association was not removed",
           topicmap.getAssociations().size() == 1);
  }

  @Test
  public void testAssociationRoleRemoval() {
    TopicIF type = builder.makeTopic();
    TopicIF role1 = builder.makeTopic();
    TopicIF role2 = builder.makeTopic();
    TopicIF player1 = builder.makeTopic();
    TopicIF player2 = builder.makeTopic();
    
    AssociationIF assoc = builder.makeAssociation(type);
    builder.makeAssociationRole(assoc, role1, player1);
    builder.makeAssociationRole(assoc, role2, player2);
    builder.makeAssociationRole(assoc, role2, player2);

    DuplicateSuppressionUtils.removeDuplicates(topicmap);

    Assert.assertTrue("duplicate association role was not removed",
               assoc.getRoles().size() == 2);
  }

  @Test
  public void testTopicNameItemIds() throws MalformedURLException {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "test");
    bn.addItemIdentifier(URILocator.create("http://psi.example.org"));
    TopicNameIF bn2 = builder.makeTopicName(topic, "test");
    bn2.addItemIdentifier(URILocator.create("http://psi.example.com"));

    DuplicateSuppressionUtils.removeDuplicates(topic);

    Assert.assertTrue("duplicate base names were not removed",
               topic.getTopicNames().size() == 1);
    bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    Assert.assertTrue("item IDs were not merged",
               bn.getItemIdentifiers().size() == 2);
  }

  @Test
  public void testOccurrenceItemIds() throws MalformedURLException {
    TopicIF type = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, type, "duplicate");
    occ.addItemIdentifier(URILocator.create("http://psi.example.org"));
    occ = builder.makeOccurrence(topic, type, "duplicate");
    occ.addItemIdentifier(URILocator.create("http://psi.example.com"));

    DuplicateSuppressionUtils.removeDuplicates(topic);

    Assert.assertTrue("duplicate occurrence were not removed",
           topic.getOccurrences().size() == 1);
    occ = (OccurrenceIF) topic.getOccurrences().iterator().next();
    Assert.assertTrue("item IDs were not merged",
               occ.getItemIdentifiers().size() == 2);
  }

  @Test
  public void testTopicNameReifiers() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "test");
    TopicIF r1 = builder.makeTopic();
    bn.setReifier(r1);
    builder.makeTopicName(r1, "r1");
    TopicNameIF bn2 = builder.makeTopicName(topic, "test");
    TopicIF r2 = builder.makeTopic();
    bn2.setReifier(r2);
    builder.makeTopicName(r2, "r2");

    DuplicateSuppressionUtils.removeDuplicates(topic);

    Assert.assertTrue("duplicate base names were not removed",
               topic.getTopicNames().size() == 1);
    bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    r1 = bn.getReifier();
    Assert.assertTrue("reifier was lost", r1 != null);
    Assert.assertTrue("reifiers were not merged", r1.getTopicNames().size() == 2);
  }

  @Test
  public void testOccurrenceReifiers() {
    TopicIF type = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, type, "test");
    TopicIF r1 = builder.makeTopic();
    occ.setReifier(r1);
    builder.makeTopicName(r1, "r1");
    
    OccurrenceIF occ2 = builder.makeOccurrence(topic, type, "test");
    TopicIF r2 = builder.makeTopic();
    occ2.setReifier(r2);
    builder.makeTopicName(r2, "r2");

    DuplicateSuppressionUtils.removeDuplicates(topic);

    Assert.assertTrue("duplicate occurrences were not removed",
               topic.getOccurrences().size() == 1);
    occ = (OccurrenceIF) topic.getOccurrences().iterator().next();
    r1 = occ.getReifier();
    Assert.assertTrue("reifier was lost", r1 != null);
    Assert.assertTrue("reifiers were not merged", r1.getTopicNames().size() == 2);
  }

  @Test
  public void testAssociationReifiers() {
    TopicIF type = builder.makeTopic();
    TopicIF role1 = builder.makeTopic();
    TopicIF role2 = builder.makeTopic();
    TopicIF player1 = builder.makeTopic();
    TopicIF player2 = builder.makeTopic();
    
    AssociationIF assoc = builder.makeAssociation(type);
    builder.makeAssociationRole(assoc, role1, player1);
    builder.makeAssociationRole(assoc, role2, player2);
    TopicIF r1 = builder.makeTopic();
    assoc.setReifier(r1);
    builder.makeTopicName(r1, "r1");

    assoc = builder.makeAssociation(type);
    builder.makeAssociationRole(assoc, role1, player1);
    builder.makeAssociationRole(assoc, role2, player2);
    TopicIF r2 = builder.makeTopic();
    assoc.setReifier(r2);
    builder.makeTopicName(r2, "r2");

    DuplicateSuppressionUtils.removeDuplicates(topicmap);

    Assert.assertTrue("duplicate association was not removed",
               topicmap.getAssociations().size() == 1);

    assoc = (AssociationIF) topicmap.getAssociations().iterator().next();
    Assert.assertTrue("reifier was lost", assoc.getReifier() != null);
    Assert.assertTrue("reifiers were not merged",
               assoc.getReifier().getTopicNames().size() == 2);
  }
}
