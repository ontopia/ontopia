/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

import java.io.IOException;
import java.util.Collections;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.utils.StreamUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

// note: scopes are not tested due to #265, comment 5.
// During the merging, a query is done which flushes the changes to the database
public class Issue499Test {

  private RDBMSTestFactory factory;
  private TopicMapReferenceIF reference;
  private RDBMSTopicMapStore store1;
  private RDBMSTopicMapStore store2;
  private TopicMapIF tm1;
  private TopicMapIF tm2;
  private TopicIF foo2;
  private TopicIF foo1;

  private LocatorIF psi = URILocator.create("foo:bar");

  @Before
  public void setup() throws Exception {
    factory = new RDBMSTestFactory();
    reference = factory.makeTopicMapReference();
    prepare();

    store1 = (RDBMSTopicMapStore)reference.createStore(false);
    store2 = (RDBMSTopicMapStore)reference.createStore(false);
    tm1 = store1.getTopicMap();
    tm2 = store2.getTopicMap();
    foo1 = tm1.getTopicBySubjectIdentifier(psi);
    foo2 = tm2.getTopicBySubjectIdentifier(psi);
  }

  @After
  public void tearDown() {
    psi = null;
    foo1 = null;
    foo2 = null;
    tm1 = null;
    tm2 = null;
    if ((store1 != null) && (store1.isOpen())) {
      store1.close();
    }
    if ((store2 != null) && (store2.isOpen())) {
      store2.close();
    }
    //factory.releaseTopicMapReference(reference);
  }

  @Test
  public void testOccurrence() throws Exception {
    OccurrenceIF occ = tm1.getBuilder().makeOccurrence(foo1, foo1, "foo");
    occ.setReifier(foo1);
    // todo: occ.addTheme(foo1); pending #265

    concurrentCommit();

    // verify no inconsistenties were created
    occ = (OccurrenceIF) tm1.getObjectById(occ.getObjectId());
    assertNotNull(tm1, occ.getType(), "Occurrence", "type");
    assertNotNull(tm1, occ.getTopic(), "Occurrence", "topic");
    assertNotNull(tm1, occ.getReifier(), "Occurrence", "reifier");
    // todo check(tm1, occ.getScope().iterator().next().getObjectId(), "Occurrence", "scope"); pending #265
  }

  @Test
  public void testAssociation() throws Exception {
    AssociationIF assoc = tm1.getBuilder().makeAssociation(foo1);
    tm1.getBuilder().makeAssociationRole(assoc, tm1.getBuilder().makeTopic(), tm1.getBuilder().makeTopic());
    assoc.setReifier(foo1);
    // todo: assoc.addTheme(foo1); pending #265

    concurrentCommit();

    // verify no inconsistenties were created
    assertNotNull(tm1, assoc.getType(), "Association", "type");
    assertNotNull(tm1, assoc.getReifier(), "Association", "reifier");
    // todo: check(tm1, assoc.getScope().iterator().next().getObjectId(), "Association", "scope"); pending #265
  }
 
  @Test
  public void testAssociationRole() throws Exception {
    AssociationIF assoc = tm1.getBuilder().makeAssociation(tm1.getBuilder().makeTopic());
    AssociationRoleIF role = tm1.getBuilder().makeAssociationRole(assoc, foo1, foo1);
    role.setReifier(foo1);

    concurrentCommit();

    // verify no inconsistenties were created
    assertNotNull(tm1, role.getType(), "Association role", "type");
    assertNotNull(tm1, role.getPlayer(), "Association role", "player");
    assertNotNull(tm1, role.getReifier(), "Association role", "reifier");
  }

  @Test
  public void testTopicName() throws Exception {
    TopicNameIF name = tm1.getBuilder().makeTopicName(foo1, foo1, "foo");
    name.setReifier(foo1);
    // todo: scope, pending #265

    concurrentCommit();

    // verify no inconsistenties were created
    assertNotNull(tm1, name.getType(), "Topic name", "type");
    assertNotNull(tm1, name.getTopic(), "Topic name", "topic");
    assertNotNull(tm1, name.getReifier(), "Topic name", "reifier");
  }

  @Test
  public void testVariant() throws Exception {
    TopicNameIF name = tm1.getBuilder().makeTopicName(foo1, foo1, "foo");
    VariantNameIF variant = tm1.getBuilder().makeVariantName(name, "foo", Collections.singletonList(foo1));
    variant.setReifier(foo1);

    concurrentCommit();

    // verify no inconsistenties were created
    assertNotNull(tm1, variant.getTopicName(), "Variant", "name");
    assertNotNull(tm1, variant.getReifier(), "Variant", "reifier");
    // todo: scope, pending #265
  }

  @Test
  public void testTopicmap() throws Exception {
    tm1.setReifier(foo1);

    concurrentCommit();

    // verify no inconsistenties were created
    assertNotNull(tm1, tm1.getReifier(), "Topicmap", "reifier");
  }

  @Test
  public void testImport() throws Exception {
    OccurrenceIF occ = tm1.getBuilder().makeOccurrence(foo1, foo1, "foo");

    // concurrent import causing a merge
    MergeUtils.mergeInto(tm1,
            new LTMTopicMapReader(
                    StreamUtils.getInputStream("classpath:net/ontopia/topicmaps/impl/rdbms/issue499.ltm"),
                    URILocator.create("foo:bar"))
                    .read());

    store2.commit();
    store1.commit();

    occ = (OccurrenceIF) tm1.getObjectById(occ.getObjectId());
    assertNotNull(tm1, occ.getType(), "Occurrence", "type");
    assertNotNull(tm1, occ.getTopic(), "Occurrence", "topic");
//    check(tm1, occ.getReifier(), "Occurrence", "reifier");
  }

  @Test
  public void testTopic() throws Exception {
    TopicIF topic = tm1.getBuilder().makeTopic(foo1);
    tm1.getBuilder().makeTopicName(topic, "foo").setReifier(topic);

    concurrentCommit();

    // verify no inconsistenties were created
    // todo: check(tm1, topic.getTypes().iterator().next().getObjectId(), "Topicmap", "type"); // peding #265
    assertNotNull(tm1, topic.getReified(), "Topicmap", "reified");
  }

  private void assertNotNull(TopicMapIF tm, TMObjectIF object, String construct, String property) {
    Assert.assertNotNull(construct + " has null as " + property + ": data inconsistency!", object);
    TMObjectIF objectById = tm.getObjectById(object.getObjectId());
    Assert.assertNotNull(construct + " " + property + "'s actual object is missing: data inconsistency!", objectById);
  }

  private void prepare() throws IOException {
    TopicMapStoreIF store = reference.createStore(false);
    TopicMapIF tm = store.getTopicMap();
    tm.getBuilder().makeTopic().addSubjectIdentifier(psi);
    store.commit();
    store.close();
  }

  private void concurrentCommit() {
    // store2: merge foo into bar
    TopicIF bar = tm2.getBuilder().makeTopic();
    bar.addSubjectIdentifier(URILocator.create("bar:foo"));
    MergeUtils.mergeInto(bar, foo2);

    // commit store2
    store2.commit();

    // then store 1
    store1.commit();
  }
}
