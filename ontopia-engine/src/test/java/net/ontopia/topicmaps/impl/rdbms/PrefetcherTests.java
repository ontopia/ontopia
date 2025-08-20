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
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.persistence.proxy.StorageIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** 
 * INTERNAL: Tests that tests the prefetching code used with the
 * RDBMS Backend Connector and the in-memory tolog implementation.  
 */

public class PrefetcherTests {
  
  private final static String testdataDirectory = "various";

  @Before
  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
  }

  protected TopicIF getTopic(TopicMapIF tm, String psi) {
    return tm.getTopicBySubjectIdentifier(URILocator.create(psi));
  }

  protected long importTopicMap(URL filename) throws IOException {

    // import sample topic map
    TopicMapStoreIF store = new RDBMSTopicMapStore(); // don't use storage
    TopicMapReaderIF importer = ImportExportUtils.getReader(filename.toString());
    importer.importInto(store.getTopicMap());

    long topicmap_id = Long.parseLong(store.getTopicMap().getObjectId().substring(1)); 

    store.commit();
    store.close();

    return topicmap_id;
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

  /**
   * INTERNAL: 
   */
  @Test
  public void testAssociationPrefetching() throws IOException {
    // Test will add one association and one occurrence

    // verify that shared cache is enabled
    StorageIF storage = new RDBMSStorage();
    if (!storage.isSharedCache()) {
      System.out.println("Shared cache disabled (skipping test).");
      return;
    }

    // import topic map
    URL filename = TestFileUtils.getTestInputURL(testdataDirectory, "prefetch.ltm");
    LocatorIF base = new URILocator(filename);

    // create reference
    long topicmap_id = importTopicMap(filename);
    TopicMapReferenceIF ref = createReference("gcache", "Prefetcher test",
                                              storage, topicmap_id, null);
    try {
      TopicMapStoreIF store1 = ref.createStore(false);
      TopicMapStoreIF store2 = ref.createStore(false);
      TopicMapStoreIF store3 = ref.createStore(false);

      TopicMapIF tm1 = store1.getTopicMap(); // changing this one
      TopicMapIF tm2 = store2.getTopicMap();
      TopicMapIF tm3 = store3.getTopicMap();

      TopicMapBuilderIF b1 = tm1.getBuilder();
      tm2.getBuilder();
      tm3.getBuilder();

      // add topics to #1
      TopicIF at1 = getTopic(tm1, "test:har_sprak");
      TopicIF rtA1 = getTopic(tm1, "test:objekt");
      TopicIF rtB1 = getTopic(tm1, "test:feltverdi");

      TopicIF rpA1 = b1.makeTopic();
      String rpA_id = rpA1.getObjectId();
      TopicIF rpB1 = b1.makeTopic();
      String rpB_id = rpB1.getObjectId();

      // run query in #1
      Map props1 = Collections.singletonMap("net.ontopia.topicmaps.query.core.QueryProcessorIF", "in-memory");
      QueryProcessorIF qp1 = QueryUtils.createQueryProcessor(tm1, base, props1);

      try {	  
	  //! QueryResultIF qr1 = qp1.parse("select $SPRAK from har_sprak(%DOC% : objekt, $SPRAK : feltverdi)?").execute(Collections.singletonMap("DOC", rpA1));
	  QueryResultIF qr1 = qp1.parse("/* #OPTION: optimizer.reorder = false */ select $SPRAK from role-player($R1, %DOC%), association-role($A, $R1), type($A, har_sprak), association-role($A, $R2), role-player($R2, $SPRAK), $R1 /= $R2 order by $SPRAK?").execute(Collections.singletonMap("DOC", rpA1));

	  if (qr1.next()) {
	      Assert.fail("Query returned no rows.");
	  }
      } catch (InvalidQueryException e) {
	  throw new OntopiaRuntimeException(e);
      }

      // add association to #1
      AssociationIF a1 = b1.makeAssociation(at1);
      b1.makeAssociationRole(a1, rtA1, rpA1);
      b1.makeAssociationRole(a1, rtB1, rpB1);

      // run query in #1
      try {	  
	  //! QueryResultIF qr1 = qp1.parse("/* #OPTION: optimizer.reorder = false */ select $SPRAK from har_sprak(%DOC% : objekt, $SPRAK : feltverdi)?").execute(Collections.singletonMap("DOC", rpA1));
	  QueryResultIF qr1 = qp1.parse("/* #OPTION: optimizer.reorder = false */ select $SPRAK from role-player($R1, %DOC%), association-role($A, $R1), type($A, har_sprak), association-role($A, $R2), role-player($R2, $SPRAK), $R1 /= $R2 order by $SPRAK?").execute(Collections.singletonMap("DOC", rpA1));

	  if (qr1.next()) {
	      TopicIF rvB = (TopicIF)qr1.getValue(0);
	      Assert.assertTrue("rvB.id != rpB.id", rpB_id.equals(rvB.getObjectId()));
	      Assert.assertTrue("rvB.roles.size != 1", rvB.getRoles().size() == 1);
	  } else {
	      Assert.fail("Query returned no rows.");
	  }
      } catch (InvalidQueryException e) {
	  throw new OntopiaRuntimeException(e);
      }

      // commit #1
      store1.commit();

      // run query in #2
      Map props2 = Collections.singletonMap("net.ontopia.topicmaps.query.core.QueryProcessorIF", "in-memory");
      QueryProcessorIF qp2 = QueryUtils.createQueryProcessor(tm2, base, props2);

      try {	  
	  TopicIF rpA2 = (TopicIF)tm2.getObjectById(rpA_id);
	  //! QueryResultIF qr2 = qp2.parse("/* #OPTION: optimizer.reorder = false */ select $SPRAK from har_sprak(%DOC% : objekt, $SPRAK : feltverdi)?").execute(Collections.singletonMap("DOC", rpA2));
	  // QueryResultIF qr2 = qp2.execute("select $SPRAK from har_sprak(@" + rpA_id + " : objekt, $SPRAK : feltverdi)?");
	  QueryResultIF qr2 = qp2.parse("/* #OPTION: optimizer.reorder = false */ select $SPRAK from role-player($R1, %DOC%), association-role($A, $R1), type($A, har_sprak), association-role($A, $R2), role-player($R2, $SPRAK), $R1 /= $R2 order by $SPRAK?").execute(Collections.singletonMap("DOC", rpA2));

	  if (qr2.next()) {
	      TopicIF rvB = (TopicIF)qr2.getValue(0);
	      Assert.assertTrue("rvB.id != rpB.id", rpB_id.equals(rvB.getObjectId()));
	      Assert.assertTrue("rvB.roles.size != 1", rvB.getRoles().size() == 1);
	  } else {
	      Assert.fail("Query returned no rows.");
	  }
      } catch (InvalidQueryException e) {
	  throw new OntopiaRuntimeException(e);
      }

      // look up topics in #3
      TopicIF rpA3 = (TopicIF)tm3.getObjectById(rpA_id);
      TopicIF rpB3 = (TopicIF)tm3.getObjectById(rpB_id);
      Assert.assertTrue("rvA.roles.size != 1", rpA3.getRoles().size() == 1);
      Assert.assertTrue("rvB.roles.size != 1", rpB3.getRoles().size() == 1);

      Assert.assertTrue("rvA.roles.association.roles.size != 2", ((AssociationRoleIF)rpA3.getRoles().iterator().next()).getAssociation().getRoles().size() == 2);
      Assert.assertTrue("rvB.roles.association.roles.size != 2", ((AssociationRoleIF)rpB3.getRoles().iterator().next()).getAssociation().getRoles().size() == 2);

      store1.close();
      store2.close();    
      store3.close();    

    } finally {
      ref.delete();
    }
  }
  
}
