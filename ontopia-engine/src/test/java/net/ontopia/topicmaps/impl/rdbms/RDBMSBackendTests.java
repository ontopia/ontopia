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

package net.ontopia.topicmaps.impl.rdbms;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.IdentityNotFoundException;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.persistence.proxy.StorageIF;
import net.ontopia.persistence.proxy.TransactionNotActiveException;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * INTERNAL: Tests that tests various aspects about the RDBMS Backend
 * Connector.
 */

public class RDBMSBackendTests {
  
  @Before
  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
  }

  protected TopicMapReferenceIF createReference(String id, String title, StorageIF storage, long topicmap_id, LocatorIF base_address) {
    RDBMSTopicMapSource source = new RDBMSTopicMapSource(); // create empty source just for supports methods
    source.setPropertyFile(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));
    source.setSupportsCreate(true);
    source.setSupportsDelete(true);    
    RDBMSTopicMapReference ref = new RDBMSTopicMapReference(id, title, storage, topicmap_id, base_address);
    ref.setSource(source);
    return ref;
  }
  
  protected TopicIF getTopic(TopicMapIF tm, String psi) {
    return tm.getTopicBySubjectIdentifier(URILocator.create(psi));
  }

  protected long importTopicMap(String filename) throws IOException {

    // import sample topic map
    TopicMapStoreIF store = new RDBMSTopicMapStore(); // don't use storage
    TopicMapReaderIF importer = ImportExportUtils.getReader(filename);
    importer.importInto(store.getTopicMap());

    long topicmap_id = Long.parseLong(store.getTopicMap().getObjectId().substring(1)); 

    store.commit();
    store.close();

    return topicmap_id;
  }

  /**
   * INTERNAL: Tests that verify that the shared cache works correctly
   * when concurrent stores are being accessed and modified.
   */
  @Test
  public void testModificationsSharedCache() throws IOException {
    // Test will add one association and one occurrence

    // verify that shared cache is enabled
    StorageIF storage = new RDBMSStorage();
    if (!storage.isSharedCache()) {
      System.out.println("Shared cache disabled (skipping test).");
      return;
    }

    // import topic map
    String filename = TestFileUtils.getTestInputFile("various", "gcache.ltm");
    
    // create reference
    long topicmap_id = importTopicMap(filename);
    TopicMapReferenceIF ref = createReference("gcache", "Shared cache test",
                                              storage, topicmap_id, null);
    try {
      TopicMapStoreIF store1 = ref.createStore(false);
      TopicMapStoreIF store2 = ref.createStore(false);
      TopicMapStoreIF store3 = ref.createStore(false);

      TopicMapIF tm1 = store1.getTopicMap();
      TopicMapIF tm2 = store2.getTopicMap();
      TopicMapIF tm3 = store3.getTopicMap(); // changing this one

      // simple topic lookup to check that topic map is there
      TopicIF topic1 = getTopic(tm1, "http://psi.kulturnett.no/person/eva_kernst");
      Assert.assertTrue("topic1 not found by indicator", topic1 != null);
      TopicIF topic2 = getTopic(tm2, "http://psi.kulturnett.no/person/eva_kernst");
      Assert.assertTrue("topic2 not found by indicator", topic2 != null);
      TopicIF topic3 = getTopic(tm3, "http://psi.kulturnett.no/person/eva_kernst");
      Assert.assertTrue("topic3 not found by indicator", topic3 != null);

      tm1.getBuilder();
      tm2.getBuilder();
      TopicMapBuilderIF b3 = tm3.getBuilder();

      // look up ontology topics
      getTopic(tm1, "http://psi.kulturnett.no/ontologi/virker_som");
      getTopic(tm2, "http://psi.kulturnett.no/ontologi/virker_som");
      getTopic(tm3, "http://psi.kulturnett.no/ontologi/virker_som");

      getTopic(tm1, "http://psi.kulturnett.no/ontologi/person");
      getTopic(tm2, "http://psi.kulturnett.no/ontologi/person");
      TopicIF rtA3 = getTopic(tm3, "http://psi.kulturnett.no/ontologi/person");

      getTopic(tm1, "http://psi.kulturnett.no/ontologi/profesjon");
      getTopic(tm2, "http://psi.kulturnett.no/ontologi/profesjon");
      TopicIF rtB3 = getTopic(tm3, "http://psi.kulturnett.no/ontologi/profesjon");

      getTopic(tm1, "http://psi.kulturnett.no/profesjon/jazzmusiker");
      getTopic(tm2, "http://psi.kulturnett.no/profesjon/jazzmusiker");
      TopicIF rp3 = getTopic(tm3, "http://psi.kulturnett.no/profesjon/jazzmusiker");

      TopicIF ot3 = getTopic(tm3, "http://psi.kulturnett.no/ontologi/ingress");

      Assert.assertTrue("topic2 does not have one role", topic2.getRoles().size()  == 1);
      Assert.assertTrue("topic3 does not have one role", topic3.getRoles().size()  == 1);

      Assert.assertTrue("topic2 does have occurrences", topic2.getOccurrences().size()  == 0);
      Assert.assertTrue("topic3 does have occurrences", topic3.getOccurrences().size()  == 0);

      // add association
      AssociationIF a3 = b3.makeAssociation(b3.makeTopic());
      b3.makeAssociationRole(a3, rtA3, topic3);
      b3.makeAssociationRole(a3, rtB3, rp3);

      Assert.assertTrue("topic3 does not have two roles", topic3.getRoles().size()  == 2);

      // add occurrence
      OccurrenceIF o3 = b3.makeOccurrence(topic3, ot3, "");
      String o3id = o3.getObjectId();
      Assert.assertTrue("topic3 does not have one occurrence", topic3.getOccurrences().size()  == 1);

      // add subject indicator
      topic3.addSubjectIdentifier(URILocator.create("test:eva_kernst"));

      TopicIF _topic3 = getTopic(tm3, "test:eva_kernst");
      Assert.assertTrue("topic3 != _topic3", topic3 == _topic3);

      TopicIF _topic2 = getTopic(tm2, "test:eva_kernst");
      Assert.assertTrue("_topic2 != null", _topic2 == null);

      TopicIF _topic1 = getTopic(tm1, "test:eva_kernst");
      Assert.assertTrue("_topic1 != null", _topic1 == null);

      store3.commit();
      store3.close();    

      // NOTE: topic2 probably only has one role and no occurrences
      Assert.assertTrue("topic1 does not have two roles", topic1.getRoles().size()  == 2);
      Assert.assertTrue("topic1 does not have one occurrence", topic1.getOccurrences().size()  == 1);
      Assert.assertTrue("topic1 does not have one occurrence with right oid", o3id.equals(((OccurrenceIF)topic1.getOccurrences().iterator().next()).getObjectId()));

      // commit store and reacquire objects
      store2.commit();
      tm2 = store2.getTopicMap();
      topic2 = getTopic(tm2, "http://psi.kulturnett.no/person/eva_kernst");
      Assert.assertTrue("topic2 not found by indicator", topic2 != null);
      Assert.assertTrue("topic2 does not have two roles", topic2.getRoles().size()  == 2);
      Assert.assertTrue("topic2 does not have one occurrence", topic2.getOccurrences().size()  == 1);
      Assert.assertTrue("topic2 does not have one occurrence with right oid", o3id.equals(((OccurrenceIF)topic2.getOccurrences().iterator().next()).getObjectId()));

      // verify subject indicator
      _topic2 = getTopic(tm2, "test:eva_kernst");
      Assert.assertTrue("topic2 != _topic2", topic2 == _topic2);

      _topic1 = getTopic(tm1, "test:eva_kernst");
      Assert.assertTrue("topic1 != _topic1", topic1 == _topic1);

      // remove subject indicator
      _topic2.removeSubjectIdentifier(URILocator.create("test:eva_kernst"));      
      store2.commit();

      _topic1 = getTopic(tm1, "test:eva_kernst");
      Assert.assertTrue("topic1 != null", _topic1 == null);

      // add subject indicator, but abort txn
      topic1.addSubjectIdentifier(URILocator.create("test:eva_kernst2"));      

      _topic2 = getTopic(tm2, "test:eva_kernst2");
      Assert.assertTrue("_topic2 != null", _topic2 == null);

      store1.abort();

      _topic2 = getTopic(tm2, "test:eva_kernst2");
      Assert.assertTrue("_topic2 != null", _topic2 == null);

      store1.close();    
      store2.close();    

      TopicMapStoreIF store4 = ref.createStore(false);
      TopicMapIF tm4 = store4.getTopicMap();

      TopicIF topic4 = getTopic(tm4, "http://psi.kulturnett.no/person/eva_kernst");
      Assert.assertTrue("topic4 not found by indicator", topic4 != null);

      Assert.assertTrue("topic4 does not have two roles", topic4.getRoles().size()  == 2);
      Assert.assertTrue("topic4 does not have one occurrence", topic4.getOccurrences().size()  == 1);
      Assert.assertTrue("topic4 does not have one occurrence with right oid", o3id.equals(((OccurrenceIF)topic4.getOccurrences().iterator().next()).getObjectId()));

      TopicIF _topic4;
      _topic4 = getTopic(tm4, "test:eva_kernst");
      Assert.assertTrue("_topic4 != null", _topic4 == null);
      _topic4 = getTopic(tm4, "test:eva_kernst2");
      Assert.assertTrue("_topic4 != null", _topic4 == null);

    } finally {
      ref.delete();
    }
  }

  /**
   * INTERNAL: Tests that verify that the API works gracefully on
   * deleted objects.
   */
  @Test
  public void testGracefulAPISharedCache() throws IOException {
    // Test will add one association and one occurrence

    // verify that shared cache is enabled
    StorageIF storage = new RDBMSStorage();
    if (!storage.isSharedCache()) {
      System.out.println("Shared cache disabled (skipping test).");
      return;
    }

    // import topic map
    String filename = TestFileUtils.getTestInputFile("various", "gcache.ltm");
    
    // create reference
    long topicmap_id = importTopicMap(filename);
    
    TopicMapReferenceIF ref = createReference("gcache", "Graceful API test", storage, topicmap_id, null);
    try {
    
      TopicMapStoreIF store1 = ref.createStore(false);
      TopicMapStoreIF store2 = ref.createStore(false);

      TopicMapIF tm1 = store1.getTopicMap();
      TopicMapIF tm2 = store2.getTopicMap();

      // simple topic lookup to check that topic map is there
      TopicIF topic1 = getTopic(tm1, "http://psi.kulturnett.no/person/eva_kernst");
      Assert.assertTrue("topic1 not found by indicator", topic1 != null);
      TopicIF topic2 = getTopic(tm2, "http://psi.kulturnett.no/person/eva_kernst");
      Assert.assertTrue("topic2 not found by indicator", topic2 != null);

      // load roles of topic1      
      AssociationRoleIF r1 = (AssociationRoleIF)topic1.getRoles().iterator().next();

      // have txn2 delete entire association
      AssociationRoleIF r2 = (AssociationRoleIF)topic2.getRoles().iterator().next();
      r2.getAssociation().remove();
      store2.commit();

      // try to access deleted role in txn1
      String oid = r1.getObjectId(); // returns correct objectId
      Assert.assertTrue("r1.objectId is null", oid != null);

      r1.getType(); // returns null
      r1.getPlayer(); // returns null

      // should get fake association
      AssociationIF a1 = r1.getAssociation();
      Assert.assertTrue("a1 is null", a1 != null);

      // interrogate fake association
      a1.getType(); // returns null
      Assert.assertTrue("a1 is null", a1.getRoles() != null); // returns empty collection
      
      store1.close();    
      store2.close();    

    } finally {
      ref.delete();
    }
  }

  /**
   * INTERNAL: Tests object access after commits and rollbacks. It
   * should be possible to continue working with objects after
   * commits, but not after rollbacks. If the transaction rolled back
   * all objects must be reaquired through a new transaction instance.
   */
  @Test
  public void testAfterTxnEnd() throws IOException {
    // initialize storage
    StorageIF storage = new RDBMSStorage();
    if (!storage.isSharedCache()) {
      System.out.println("Shared cache disabled (skipping test).");
      return;
    }

    // import topic map
    String filename = TestFileUtils.getTestInputFile("various", "commroll.ltm");
    
    // create reference
    long topicmap_id = importTopicMap(filename);
    TopicMapReferenceIF ref = createReference("commroll", "After transaction end test",
                                              storage, topicmap_id, null);
    
    try {
    
      TopicMapStoreIF store1 = ref.createStore(false);
      TopicMapIF tm1 = store1.getTopicMap();

      // find topic1
      TopicIF topic1 = getTopic(tm1, "test:topic1");
      Assert.assertTrue("topic1 not found by indicator", topic1 != null);

      // test topic1 before commit
      Assert.assertTrue("topic1.subjectIndicators.size != 1", topic1.getSubjectIdentifiers().size() == 1);
      Assert.assertTrue("topic1.baseNames.size != 1", topic1.getTopicNames().size() == 1);
      String bnv1 = ((TopicNameIF)topic1.getTopicNames().iterator().next()).getValue();
      Assert.assertTrue("bnv1.value != 'Topic 1'", "Topic 1".equals(bnv1));

      // find topic3
      TopicIF topic3 = getTopic(tm1, "test:topic3");
      Assert.assertTrue("topic3 not found by indicator", topic3 != null);

      // remove topic3 from topic map
      Assert.assertTrue("topic3.baseNames.size != 1 (A)", topic3.getTopicNames().size() == 1);
      topic3.remove();
      Assert.assertTrue("topic3.baseNames.size != 1 (B)", topic3.getTopicNames().size() == 1);

      store1.commit();

      // test topic1 after commit
      Assert.assertTrue("topic1.subjectIndicators.size != 1", topic1.getSubjectIdentifiers().size() == 1);
      Assert.assertTrue("topic1.baseNames.size != 1", topic1.getTopicNames().size() == 1);
      bnv1 = ((TopicNameIF)topic1.getTopicNames().iterator().next()).getValue();
      Assert.assertTrue("bnv1.value != 'Topic 1'", "Topic 1".equals(bnv1));

      // find topic2
      TopicIF topic2 = getTopic(tm1, "test:topic2");
      Assert.assertTrue("topic2 not found by indicator", topic2 != null);

      // test topic2 after commit
      Assert.assertTrue("topic2.subjectIndicators.size != 1", topic2.getSubjectIdentifiers().size() == 1);
      Assert.assertTrue("topic2.baseNames.size != 1", topic2.getTopicNames().size() == 1);
      String bnv2 = ((TopicNameIF)topic2.getTopicNames().iterator().next()).getValue();
      Assert.assertTrue("bnv2.value != 'Topic 2'", "Topic 2".equals(bnv2));

      // test topic3 after commit
      try {
        // try to access topic3
        topic3.getTopicNames().size();
        Assert.fail("Could access topic3.baseNames");
      } catch (IdentityNotFoundException e) {
        // ok
      }

      store1.abort();

      // test topic1 after rollback
      // this is possible since connection pooling changes, via non transactional reads
      topic1.getTopicNames().size();

      // reaquire topic map instance and topic1 
      topic1 = getTopic(store1.getTopicMap(), "test:topic1");
      Assert.assertTrue("topic1 not found by indicator", topic1 != null);

      // test topic1 before commit (2)
      Assert.assertTrue("topic1.subjectIndicators.size != 1", topic1.getSubjectIdentifiers().size() == 1);
      Assert.assertTrue("topic1.baseNames.size != 1", topic1.getTopicNames().size() == 1);
      bnv1 = ((TopicNameIF)topic1.getTopicNames().iterator().next()).getValue();
      Assert.assertTrue("bnv1.value != 'Topic 1'", "Topic 1".equals(bnv1));

      store1.close();    

    } finally {
      ref.delete();
    }
  }

  /**
   * INTERNAL: Verify that it is not possible to look up object by id
   * from other committed transactions.
   */
  @Test
  public void testLookupByObjectId() throws IOException {
    // initialize storage
    StorageIF storage = new RDBMSStorage();

    TopicMapStoreIF store1 = null;
    TopicMapStoreIF store2 = null;
    try {
      // create topic map with one topic
      store1 = new RDBMSTopicMapStore(storage);
      TopicMapIF tm1 = store1.getTopicMap();
      TopicIF topic1 = tm1.getBuilder().makeTopic();
      String oid1 = topic1.getObjectId();
      store1.commit();
      
      // create a second topic map with one topic
      store2 = new RDBMSTopicMapStore(storage);
      TopicMapIF tm2 = store2.getTopicMap();
      TopicIF topic2 = tm2.getBuilder().makeTopic();
      String oid2 = topic2.getObjectId();
      store2.commit();

      // try to do cross lookup      
      Assert.assertTrue("Possible to look up topic from first topic map.", tm2.getObjectById(oid1) == null);
      Assert.assertTrue("Possible to look up topic from second topic map.", tm1.getObjectById(oid2) == null);

    } finally {
      if (store1 != null) {
        store1.delete(true);
      }
      if (store2 != null) {
        store2.delete(true);
      }
    }
  }

  /**
   * INTERNAL: Verify that it is not possible to look up object by uri
   * identity from other uncommitted transactions.
   */
  @Test
  public void testLookupObjectsByIdentity() throws IOException {

    // verify that shared cache is enabled
    StorageIF storage = new RDBMSStorage();
    if (!storage.isSharedCache()) {
      System.out.println("Shared cache disabled (skipping test).");
      return;
    }

    // import topic map
    String filename = TestFileUtils.getTestInputFile("various", "gcache.ltm");
    
    // create reference
    long topicmap_id = importTopicMap(filename);
    TopicMapReferenceIF ref = createReference("uriobjects", "Graceful API test",
                                              storage, topicmap_id, null);
    
    try {
    
      TopicMapStoreIF store1 = ref.createStore(false);
      TopicMapStoreIF store2 = ref.createStore(false);

      TopicMapIF tm1 = store1.getTopicMap();
      TopicMapIF tm2 = store2.getTopicMap();

      // add new identities
      TopicIF topic1 = getTopic(tm1, "http://psi.kulturnett.no/person/eva_kernst");
      Assert.assertTrue("topic1 not found by indicator", topic1 != null);

      LocatorIF subind = URILocator.create("test:subind:eva_kernst");
      LocatorIF subloc = URILocator.create("test:subloc:eva_kernst");
      LocatorIF srcloc = URILocator.create("test:srcloc:eva_kernst");
      
      topic1.addSubjectIdentifier(subind);
      topic1.addSubjectLocator(subloc);
      topic1.addItemIdentifier(srcloc);

      // then look them up through the same transaction
      TopicIF topic1_ = tm1.getTopicBySubjectIdentifier(subind);
      Assert.assertTrue("topic1_ not found by indicator", topic1_ != null);
      topic1_ = tm1.getTopicBySubjectLocator(subloc);
      Assert.assertTrue("topic1_ not found by subject locator", topic1_ != null);
      topic1_ = (TopicIF)tm1.getObjectByItemIdentifier(srcloc);
      Assert.assertTrue("topic1_ not found by item identifier", topic1_ != null);

      // then look it up through the other transaction
      TopicIF topic2 = getTopic(tm2, "http://psi.kulturnett.no/person/eva_kernst");
      Assert.assertTrue("topic2 not found by indicator", topic2 != null);

      TopicIF topic2_ = tm2.getTopicBySubjectIdentifier(subind);
      Assert.assertTrue("topic2_ found by subject identifier when it shouldn't", topic2_ == null);
      topic2_ = tm2.getTopicBySubjectLocator(subloc);
      Assert.assertTrue("topic2_ found by subject locator when it shouldn't", topic2_ == null);
      topic2_ = (TopicIF)tm2.getObjectByItemIdentifier(srcloc);
      Assert.assertTrue("topic2_ found by item identifier when it shouldn't", topic2_ == null);

      // now a new topic and track its object id
      TopicIF newtopic = tm1.getBuilder().makeTopic();
      String noid = newtopic.getObjectId();
      topic1_ = (TopicIF)tm1.getObjectById(noid);
      Assert.assertTrue("topic1_ not found by object id", topic1_ != null);

      // should not find it in the other topic map
      topic2_ = (TopicIF)tm2.getObjectById(noid);
      Assert.assertTrue("topic2_ found by object id when it shouldn't", topic2_ == null);
      
      store1.commit();

      // should now find them
      topic2_ = tm2.getTopicBySubjectIdentifier(subind);
      Assert.assertTrue("topic2_ not found by subject identifier", topic2_ != null);
      topic2_ = tm2.getTopicBySubjectLocator(subloc);
      Assert.assertTrue("topic2_ not found by subject locator", topic2_ != null);
      topic2_ = (TopicIF)tm2.getObjectByItemIdentifier(srcloc);
      Assert.assertTrue("topic2_ not found by item identifier", topic2_ != null);
      topic2_ = (TopicIF)tm2.getObjectById(noid);
      Assert.assertTrue("topic2_ not found by object id", topic2_ != null);

      // now remove the identities      
      topic1.removeSubjectIdentifier(subind);
      topic1.removeSubjectLocator(subloc);
      topic1.removeItemIdentifier(srcloc);

      // should still find them
      topic2_ = tm2.getTopicBySubjectIdentifier(subind);
      Assert.assertTrue("topic2_ not found by subject identifier", topic2_ != null);
      topic2_ = tm2.getTopicBySubjectLocator(subloc);
      Assert.assertTrue("topic2_ not found by subject locator", topic2_ != null);
      topic2_ = (TopicIF)tm2.getObjectByItemIdentifier(srcloc);
      Assert.assertTrue("topic2_ not found by item identifier", topic2_ != null);
      topic2_ = (TopicIF)tm2.getObjectById(noid);
      Assert.assertTrue("topic2_ not found by object id", topic2_ != null);
      
      store1.commit();

      // should not find them anymore
      topic2_ = tm2.getTopicBySubjectIdentifier(subind);
      Assert.assertTrue("topic2_ found by subject identifier when it shouldn't", topic2_ == null);
      topic2_ = tm2.getTopicBySubjectLocator(subloc);
      Assert.assertTrue("topic2_ found by subject locator when it shouldn't", topic2_ == null);
      topic2_ = (TopicIF)tm2.getObjectByItemIdentifier(srcloc);
      Assert.assertTrue("topic2_ found by item identifier when it shouldn't", topic2_ == null);
      topic2_ = (TopicIF)tm2.getObjectById(noid);
      Assert.assertTrue("topic2_ not found by object id", topic2_ != null);
      
      store2.commit();
      
      store1.close();    
      store2.close();    

    } finally {
      ref.delete();
    }
  }

  /**
   * INTERNAL: Test that reproduces bug #2025.
   */
  @Test
  public void testBug2025() throws IOException {
    // initialize storage
    RDBMSTopicMapStore store1 = null;
    long tmid;
    String oid;
    try {
      // create topic map with one topic and one occurrence
      store1 = new RDBMSTopicMapStore();
      TopicMapIF tm1 = store1.getTopicMap();
      TopicIF topic1 = tm1.getBuilder().makeTopic();
      TopicIF otype1 = tm1.getBuilder().makeTopic();
      tm1.getBuilder().makeOccurrence(topic1, otype1, "FOO");
      oid = topic1.getObjectId();
      tmid = store1.getLongId();
      store1.commit();

    } finally {
      if (store1 != null) {
        store1.close();
      }
    }

    TopicMapStoreIF store2 = null;
    try {
      // create a second topic map with one topic
      store2 = new RDBMSTopicMapStore(tmid);
      TopicIF topic2 = (TopicIF)store2.getTopicMap().getObjectById(oid);
      Iterator iter = topic2.getOccurrences().iterator();
      while (iter.hasNext()) {
        OccurrenceIF occ2 = (OccurrenceIF)iter.next();
        LocatorIF loc = occ2.getLocator();
        String value = occ2.getValue();
        Assert.assertTrue("Found locator value when there shouldn't be one: " + loc, loc == null);
        Assert.assertTrue("Incorrect occurrence value:" + value, "FOO".equals(value));
      }
      store2.commit();

    } finally {
      if (store2 != null) {
        store2.delete(true);
      }
    }
  }
  
  @Test
  public void testIssue61() throws Exception {
    // initialize storage
    RDBMSTopicMapStore store1 = null;
    long tmid;
    String occid;
    String reifierid;
    try {
      // create topic map with one topic and one occurrence
      store1 = new RDBMSTopicMapStore();
      TopicMapIF tm1 = store1.getTopicMap();
      TopicIF topic = tm1.getBuilder().makeTopic();
      TopicIF otype = tm1.getBuilder().makeTopic();
      OccurrenceIF occurrence = tm1.getBuilder().makeOccurrence(topic, otype, "SomeValue");
      occid = occurrence.getObjectId();
      TopicIF oreifier = tm1.getBuilder().makeTopic();
      reifierid = oreifier.getObjectId();
      occurrence.setReifier(oreifier);

      Assert.assertTrue("Wrong reifier (rw)", Objects.equals(occurrence.getReifier(), oreifier));
      Assert.assertTrue("Wrong reified (rw)", Objects.equals(occurrence, oreifier.getReified()));

      tmid = store1.getLongId();
      store1.commit();
    } finally {
      if (store1 != null) {
        store1.close();
      }
    }

    RDBMSTopicMapStore store2 = null;
    try {
      // create a second topic map with one topic
      store2 = new RDBMSTopicMapStore(tmid);
      store2.setReadOnly(true);
      TopicMapIF tm2 = store2.getTopicMap();
      OccurrenceIF occurrence = (OccurrenceIF)tm2.getObjectById(occid);
      TopicIF oreifier = (TopicIF)tm2.getObjectById(reifierid);

      Assert.assertTrue("Wrong reifier (ro)", Objects.equals(occurrence.getReifier(), oreifier));
      Assert.assertTrue("Wrong reified (ro)", Objects.equals(occurrence, oreifier.getReified()));

    } finally {
      if (store2 != null) {
        store2.close();
      }
    }

    RDBMSTopicMapStore store3 = null;
    try {
      store3 = new RDBMSTopicMapStore(tmid);
    } finally {
      if (store3 != null) {
        store3.delete(true);
      }
    }

  }

  @Test
  public void testIssue159a() throws Exception {
    // make long string
    char[] chars = new char[65536];
    Arrays.fill(chars, 'a');
    String largeValue = new String(chars);

    // initialize storage
    RDBMSTopicMapStore store1 = null;
    long tmid;
    String otypeid;
    String occid;
    try {
      // create topic map with one topic and one occurrence
      store1 = new RDBMSTopicMapStore();
      TopicMapIF tm1 = store1.getTopicMap();
      TopicIF topic = tm1.getBuilder().makeTopic();
      TopicIF otype = tm1.getBuilder().makeTopic();
      OccurrenceIF occurrence = tm1.getBuilder().makeOccurrence(topic, otype, largeValue);

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype));

      // look up arbitrary object to force flushing
      tm1.getObjectByItemIdentifier(new URILocator("test:1"));

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype));

      TopicIF otype2 = tm1.getBuilder().makeTopic();
      occurrence.setType(otype2);

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype2));

      tmid = store1.getLongId();
      otypeid = otype2.getObjectId();
      occid = occurrence.getObjectId();
      store1.commit();
    } finally {
      if (store1 != null) {
        store1.close();
      }
    }

    RDBMSTopicMapStore store2 = null;
    try {
      // create a second topic map with one topic
      store2 = new RDBMSTopicMapStore(tmid);
      store2.setReadOnly(true);
      TopicMapIF tm2 = store2.getTopicMap();
      TopicIF otype2 = (TopicIF)tm2.getObjectById(otypeid);
      OccurrenceIF occurrence = (OccurrenceIF)tm2.getObjectById(occid);
      occurrence.getType();
      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype2));

    } finally {
      if (store2 != null) {
        store2.close();
      }
    }

  }

  @Test
  public void testIssue159b() throws Exception {
    // make long string
    char[] chars = new char[65536];
    Arrays.fill(chars, 'a');
    String largeValue = new String(chars);

    // initialize storage
    RDBMSTopicMapStore store1 = null;
    long tmid;
    String topicid;
    String otypeid;
    try {
      // create topic map with one topic and one occurrence
      store1 = new RDBMSTopicMapStore();
      TopicMapIF tm1 = store1.getTopicMap();
      TopicIF topic = tm1.getBuilder().makeTopic();
      TopicIF otype = tm1.getBuilder().makeTopic();
      OccurrenceIF occurrence = tm1.getBuilder().makeOccurrence(topic, otype, largeValue);

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype));

      // look up arbitrary object to force flushing
      tm1.getObjectByItemIdentifier(new URILocator("test:1"));

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype));

      TopicIF otype2 = tm1.getBuilder().makeTopic();
      occurrence.setType(otype2);

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype2));

      tmid = store1.getLongId();
      topicid = topic.getObjectId();
      otypeid = otype2.getObjectId();
      occurrence.getObjectId();
      store1.commit();
    } finally {
      if (store1 != null) {
        store1.close();
      }
    }

    RDBMSTopicMapStore store2 = null;
    try {
      // create a second topic map with one topic
      store2 = new RDBMSTopicMapStore(tmid);
      store2.setReadOnly(true);
      TopicMapIF tm2 = store2.getTopicMap();
      TopicIF otype2 = (TopicIF)tm2.getObjectById(otypeid);
      TopicIF topic = (TopicIF)tm2.getObjectById(topicid);
      OccurrenceIF occurrence = (OccurrenceIF)topic.getOccurrences().iterator().next();
      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype2));

    } finally {
      if (store2 != null) {
        store2.close();
      }
    }

  }

  @Test
  public void testIssue159c() throws Exception {
    // make long string
    char[] chars = new char[65536];
    Arrays.fill(chars, 'a');
    String smallValue = "abc";
    String largeValue = new String(chars);

    // initialize storage
    RDBMSTopicMapStore store1 = null;
    long tmid;
    String topicid;
    String otypeid;
    try {
      // create topic map with one topic and one occurrence
      store1 = new RDBMSTopicMapStore();
      TopicMapIF tm1 = store1.getTopicMap();
      TopicIF topic = tm1.getBuilder().makeTopic();
      TopicIF otype = tm1.getBuilder().makeTopic();
      OccurrenceIF occurrence = tm1.getBuilder().makeOccurrence(topic, otype, smallValue);

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), smallValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype));

      // look up arbitrary object to force flushing
      tm1.getObjectByItemIdentifier(new URILocator("test:1"));

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), smallValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype));

      TopicIF otype2 = tm1.getBuilder().makeTopic();
      occurrence.setType(otype2);
      occurrence.setValue(largeValue);

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype2));

      // look up arbitrary object to force flushing
      tm1.getObjectByItemIdentifier(new URILocator("test:2"));

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype2));

      tmid = store1.getLongId();
      topicid = topic.getObjectId();
      otypeid = otype2.getObjectId();
      occurrence.getObjectId();
      store1.commit();
    } finally {
      if (store1 != null) {
        store1.close();
      }
    }

    RDBMSTopicMapStore store2 = null;
    try {
      // create a second topic map with one topic
      store2 = new RDBMSTopicMapStore(tmid);
      store2.setReadOnly(true);
      TopicMapIF tm2 = store2.getTopicMap();
      TopicIF otype2 = (TopicIF)tm2.getObjectById(otypeid);
      TopicIF topic = (TopicIF)tm2.getObjectById(topicid);
      OccurrenceIF occurrence = (OccurrenceIF)topic.getOccurrences().iterator().next();
      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype2));

    } finally {
      if (store2 != null) {
        store2.close();
      }
    }

  }

  @Test
  public void testIssue159d() throws Exception {
    // make long string
    char[] chars = new char[65536];
    Arrays.fill(chars, 'a');
    String smallValue = "abc";
    String largeValue = new String(chars);

    // initialize storage
    RDBMSTopicMapStore store1 = null;
    long tmid;
    String otype1id;
    String otype2id;
    try {
      // create topic map with one topic and one occurrence
      store1 = new RDBMSTopicMapStore();
      TopicMapIF tm1 = store1.getTopicMap();
      TopicIF topic = tm1.getBuilder().makeTopic();
      TopicIF otype1 = tm1.getBuilder().makeTopic();
      OccurrenceIF occurrence = tm1.getBuilder().makeOccurrence(topic, otype1, smallValue);

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), smallValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype1));

      // look up arbitrary object to force flushing
      tm1.getObjectByItemIdentifier(new URILocator("test:1"));

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), smallValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype1));

      TopicIF otype2 = tm1.getBuilder().makeTopic();
      occurrence.setType(otype2);
      occurrence.setValue(largeValue);

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype2));

      // look up arbitrary object to force flushing
      tm1.getObjectByItemIdentifier(new URILocator("test:2"));

      Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
      Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype2));

      tmid = store1.getLongId();
      topic.getObjectId();
      otype1id = otype1.getObjectId();
      otype2id = otype2.getObjectId();
      occurrence.getObjectId();
      store1.commit();
    } finally {
      if (store1 != null) {
        store1.close();
      }
    }

    RDBMSTopicMapStore store2 = null;
    try {
      // create a second topic map with one topic
      store2 = new RDBMSTopicMapStore(tmid);
      //store2.setReadOnly(true);
      TopicMapIF tm2 = store2.getTopicMap();

      TopicIF otype1 = (TopicIF)tm2.getObjectById(otype1id);
      TopicIF otype2 = (TopicIF)tm2.getObjectById(otype2id);
      ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)tm2.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
      Iterator iter = cindex.getOccurrences(otype2).iterator();
      while (iter.hasNext()) {
        OccurrenceIF occurrence = (OccurrenceIF)iter.next();
        occurrence.getType();
        occurrence.setType(otype1);

        // look up arbitrary object to force flushing
        tm2.getObjectByItemIdentifier(new URILocator("test:1"));

        Assert.assertTrue("Wrong occurrence value", Objects.equals(occurrence.getValue(), largeValue));
        Assert.assertTrue("Wrong occurrence type", Objects.equals(occurrence.getType(), otype1));
      }

    } finally {
      if (store2 != null) {
        store2.close();
      }
    }

  }

}
