/*-
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2024 The Ontopia Project
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
package net.ontopia.topicmaps.impl.rdbms;

import java.util.Collections;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.AbstractRWPersistent;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

// tests exposed API's use of NonTransactional reads
public class NonTransactionalReadTest extends AbstractTopicMapTest {

  protected RDBMSStorage storage;
  protected TopicIF topic;
  protected TopicNameIF name;
  protected OccurrenceIF occurrence;
  protected VariantNameIF variant;
  protected AssociationRoleIF role;
  protected AssociationIF association;

  protected ClassInstanceIndexIF cii;
  protected OccurrenceIndexIF oi;
  protected NameIndexIF ni;
  protected ScopeIndexIF si;

  @Override
  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    storage = ((RDBMSTopicMapStore) topicmap.getStore()).getStorage();

    topic = builder.makeTopic();
    name = builder.makeTopicName(topic, "foo");
    occurrence = builder.makeOccurrence(topic, topic, "foo");
    variant = builder.makeVariantName(name, "foo", Collections.singletonList(topic));
    association = builder.makeAssociation(topic);
    role = builder.makeAssociationRole(association, topic, topic);

    topicmap.setReifier(topic);

    cii = (ClassInstanceIndexIF) topicmap.getIndex(ClassInstanceIndexIF.class.getName());
    oi = (OccurrenceIndexIF) topicmap.getIndex(OccurrenceIndexIF.class.getName());
    ni = (NameIndexIF) topicmap.getIndex(NameIndexIF.class.getName());
    si = (ScopeIndexIF) topicmap.getIndex(ScopeIndexIF.class.getName());

    // then commit and close the store
    try (TopicMapStoreIF store = topicmap.getStore()) {
      store.commit();
    }

    ((AbstractRWPersistent) topic).clearAll();
    ((AbstractRWPersistent) name).clearAll();
    ((AbstractRWPersistent) occurrence).clearAll();
    ((AbstractRWPersistent) variant).clearAll();
    ((AbstractRWPersistent) role).clearAll();
    ((AbstractRWPersistent) association).clearAll();
    ((AbstractRWPersistent) topicmap).clearAll();
  }

  private void assertNonTransactionalReadConnectionUsed(Runnable test) {
    Assert.assertEquals(0, storage.getNonTransactionalReadConnectionCount());
    test.run();
    Assert.assertEquals(1, storage.getNonTransactionalReadConnectionCount());
  }

  private void assertNonTransactionalReadConnectionNotUsed(Runnable test) {
    Assert.assertEquals(0, storage.getNonTransactionalReadConnectionCount());
    test.run();
    Assert.assertEquals(0, storage.getNonTransactionalReadConnectionCount());
  }

  // --- TMObjectIF ---

  @Test
  public void testGetObjectId() {
    // getObjectId is based on identity, which is un-unloadable
    assertNonTransactionalReadConnectionNotUsed(() -> {
      Assert.assertNotNull(topic.getObjectId());
      Assert.assertNotNull(name.getObjectId());
      Assert.assertNotNull(occurrence.getObjectId());
      Assert.assertNotNull(variant.getObjectId());
      Assert.assertNotNull(role.getObjectId());
      Assert.assertNotNull(association.getObjectId());
      Assert.assertNotNull(topicmap.getObjectId());
    });
  }

  @Test
  public void testGetTopicMap() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(topic.getTopicMap());
      Assert.assertNotNull(name.getTopicMap());
      Assert.assertNotNull(occurrence.getTopicMap());
      Assert.assertNotNull(variant.getTopicMap());
      Assert.assertNotNull(role.getTopicMap());
      Assert.assertNotNull(association.getTopicMap());
      Assert.assertNotNull(topicmap.getTopicMap());
    });
  }

  @Test
  public void testGetItemIdentifiers() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, topic.getItemIdentifiers().size());
      Assert.assertEquals(0, name.getItemIdentifiers().size());
      Assert.assertEquals(0, occurrence.getItemIdentifiers().size());
      Assert.assertEquals(0, variant.getItemIdentifiers().size());
      Assert.assertEquals(0, role.getItemIdentifiers().size());
      Assert.assertEquals(0, association.getItemIdentifiers().size());
      Assert.assertEquals(0, topicmap.getItemIdentifiers().size());
    });
  }

  // --- TopicIF ---

  @Test
  public void testGetSubjectLocators() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, topic.getSubjectLocators().size());
    });
  }

  @Test
  public void testGetSubjectIdentifiers() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, topic.getSubjectIdentifiers().size());
    });
  }

  @Test
  public void testGetTypes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, topic.getTypes().size());
    });
  }

  @Test
  public void testGetTopicNames() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, topic.getTopicNames().size());
    });
  }

  @Test
  public void testGetTopicNamesByType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, topic.getTopicNamesByType(topic).size());
    });
  }

  @Test
  public void testGetOccurrences() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, topic.getOccurrences().size());
    });
  }

  @Test
  public void testGetOccurrencesByType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, topic.getOccurrencesByType(topic).size());
    });
  }

  @Test
  public void testGetRoles() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, topic.getRoles().size());
    });
  }

  @Test
  public void testGetRolesByType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, topic.getRolesByType(topic).size());
    });
  }

  @Test
  public void testGetRolesByType2() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, topic.getRolesByType(topic, topic).size());
    });
  }

  @Test
  public void testGetAssociations() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, topic.getAssociations().size());
    });
  }

  @Test
  public void testGetAssociationsByType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, topic.getAssociationsByType(topic).size());
    });
  }

  @Test
  public void testGetReified() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(topic.getReified());
    });
  }

  // --- NameIF ---

  @Test
  public void testNGetTopic() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(name.getTopic());
      Assert.assertNotNull(variant.getTopic());
    });
  }

  @Test
  public void testGetValue() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(name.getValue());
      Assert.assertNotNull(variant.getValue());
    });
  }

  // --- ScopedIF ---

  @Test
  public void testGetScope() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(name.getScope());
      Assert.assertNotNull(variant.getScope());
      Assert.assertNotNull(occurrence.getScope());
      Assert.assertNotNull(association.getScope());
    });
  }

  // --- TypedIF ---

  @Test
  public void testGetType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(name.getType());
      Assert.assertNotNull(occurrence.getType());
      Assert.assertNotNull(association.getType());
      Assert.assertNotNull(role.getType());
    });
  }

  // --- ReifiableIF ---

  @Test
  public void testGetReifier() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNull(name.getReifier());
      Assert.assertNull(variant.getReifier());
      Assert.assertNull(occurrence.getReifier());
      Assert.assertNull(association.getReifier());
      Assert.assertNull(role.getReifier());
      Assert.assertNotNull(topicmap.getReifier());
    });
  }

  // --- TopicNameIF ---

  @Test
  public void testGetVariants() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(name.getVariants());
    });
  }

  // --- VariantNameIF ---

  @Test
  public void testGetTopicName() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(variant.getTopicName());
    });
  }

  @Test
  public void testGetDataType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(variant.getDataType());
    });
  }

  @Test
  public void testGetReader() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(variant.getReader());
    });
  }

  @Test
  public void testVGetLength() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(variant.getLength());
    });
  }

  // skipped getLocator as it diverts to getValue

  // --- OccurrenceIF ---

  @Test
  public void testOGetTopic() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(occurrence.getTopic());
    });
  }

  @Test
  public void testOgetDataType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(occurrence.getDataType());
    });
  }

  @Test
  public void testOgetValue() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(occurrence.getValue());
    });
  }

  @Test
  public void testOgetReader() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(occurrence.getReader());
    });
  }

  @Test
  public void testOGetLength() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(occurrence.getLength());
    });
  }

  // skipped getLocator as it diverts to getValue

  // --- AssociationIF ---

  @Test
  public void testGetRoleTypes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(association.getRoleTypes());
    });
  }

  @Test
  public void testAGetRolesByType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(association.getRolesByType(topic));
    });
  }

  @Test
  public void testAGetRoles() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(association.getRoles());
    });
  }

  // --- AssociationRoleIF ---

  @Test
  public void testGetAssociation() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(role.getAssociation());
    });
  }

  @Test
  public void testGetPlayer() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNotNull(role.getPlayer());
    });
  }

  // --- TopicMapIF ---

  @Test
  public void testGetTopics() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(2, topicmap.getTopics().size());
    });
  }

  @Test
  public void testMGetAssociations() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, topicmap.getAssociations().size());
    });
  }

  @Test
  public void testMGetObjectById() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNull(topicmap.getObjectById("T12354123"));
    });
  }

  @Test
  public void testMGetTopicBySubjectIdentifier() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertNull(topicmap.getTopicBySubjectIdentifier(URILocator.create("foo:bar")));
    });
  }

  // --- ClassInstanceIndexIF ---

  @Test
  public void testCIIgetTopics() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, cii.getTopics(topic).size());
    });
  }

  @Test
  public void testCIIgetTopicNames() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, cii.getTopicNames(topic).size());
    });
  }

  @Test
  public void testCIIgetAllTopicNames() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getAllTopicNames().size());
    });
  }

  @Test
  public void testCIIgetAllVariantNames() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getAllVariantNames().size());
    });
  }

  @Test
  public void testCIIgetOccurrences() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getOccurrences(topic).size());
    });
  }

  @Test
  public void testCIIgetAllOccurrences() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getAllOccurrences().size());
    });
  }

  @Test
  public void testCIIgetAssociations() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getAssociations(topic).size());
    });
  }

  @Test
  public void testCIIgetAssociationRoles() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getAssociationRoles(topic).size());
    });
  }

  @Test
  public void testCIIgetAssociationRoles2() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getAssociationRoles(topic, topic).size());
    });
  }

  @Test
  public void testCIIgetTopicTypes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, cii.getTopicTypes().size());
    });
  }

  @Test
  public void testCIIgetTopicNameTypes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getTopicNameTypes().size());
    });
  }

  @Test
  public void testCIIgetOccurrenceTypes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getOccurrenceTypes().size());
    });
  }

  @Test
  public void testCIIgetAssociationTypes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getAssociationTypes().size());
    });
  }

  @Test
  public void testCIIgetAssociationRoleTypes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, cii.getAssociationRoleTypes().size());
    });
  }

  @Test
  public void testCIIusedAsTopicType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertFalse(cii.usedAsTopicType(topic));
    });
  }

  @Test
  public void testCIIusedAsTopicNameType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertFalse(cii.usedAsTopicNameType(topic));
    });
  }

  @Test
  public void testCIIusedAsOccurrenceType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertTrue(cii.usedAsOccurrenceType(topic));
    });
  }

  @Test
  public void testCIIusedAsAssociationType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertTrue(cii.usedAsAssociationType(topic));
    });
  }

  @Test
  public void testCIIusedAsAssociationRoleType() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertTrue(cii.usedAsAssociationRoleType(topic));
    });
  }

  // --- OccurrenceIndexIF ---

  @Test
  public void testOIgetOccurrences() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, oi.getOccurrences("foo").size());
    });
  }

  @Test
  public void testOIgetOccurrences2() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, oi.getOccurrences("foo", topic).size());
    });
  }

  @Test
  public void testOIgetOccurrences3() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, oi.getOccurrences("foo", DataTypes.TYPE_STRING).size());
    });
  }

  @Test
  public void testOIgetOccurrences4() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, oi.getOccurrences("foo", DataTypes.TYPE_STRING, topic).size());
    });
  }

  @Test
  public void testOIgetOccurrencesByPrefix() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, oi.getOccurrencesByPrefix("fo").size());
    });
  }

  @Test
  public void testOIgetOccurrencesByPrefix2() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, oi.getOccurrencesByPrefix("fo", DataTypes.TYPE_STRING).size());
    });
  }

  @Test
  public void testOIgetValuesGreaterThanOrEqual() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertTrue(oi.getValuesGreaterThanOrEqual("f").hasNext());
    });
  }

  @Test
  public void testOIgetValuesSmallerThanOrEqual() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertTrue(oi.getValuesSmallerThanOrEqual("g").hasNext());
    });
  }

  // --- NameIndexIF ---

  @Test
  public void testNIgetTopicNames() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, ni.getTopicNames("foo").size());
    });
  }

  @Test
  public void testNIgetTopicNames2() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, ni.getTopicNames("foo", topic).size());
    });
  }

  @Test
  public void testNIgetVariants() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, ni.getVariants("foo").size());
    });
  }

  @Test
  public void testNIgetVariants2() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, ni.getVariants("foo", DataTypes.TYPE_STRING).size());
    });
  }

  // --- ScopeIndex ---

  @Test
  public void testSIgetTopicNames() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, si.getTopicNames(topic).size());
    });
  }

  @Test
  public void testSIgetVariants() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, si.getVariants(topic).size());
    });
  }

  @Test
  public void testSIgetOccurrences() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, si.getOccurrences(topic).size());
    });
  }

  @Test
  public void testSIgetAssociations() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, si.getAssociations(topic).size());
    });
  }

  @Test
  public void testSIgetTopicNameThemes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, si.getTopicNameThemes().size());
    });
  }

  @Test
  public void testSIgetVariantThemes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(1, si.getVariantThemes().size());
    });
  }

  @Test
  public void testSIgetOccurrenceThemes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, si.getOccurrenceThemes().size());
    });
  }

  @Test
  public void testSIgetAssociationThemes() {
    assertNonTransactionalReadConnectionUsed(() -> {
      Assert.assertEquals(0, si.getAssociationThemes().size());
    });
  }
}
