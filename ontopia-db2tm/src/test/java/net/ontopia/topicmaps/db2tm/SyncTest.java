/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.TestFileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SyncTest {
  private Connection conn;
  private Statement stm;
  private TopicMapIF topicmap;
  private RelationMapping mapping;

  private final static String testdataDirectory = "db2tm";

  @Before
  public void setUp() throws SQLException, IOException {
    // get JDBC connection to database
    conn = getConnection();

    // create tables
    // (1) data table (id, name, type)
    // (2) changes table
    stm = conn.createStatement();
    stm.executeUpdate("create table testdata (id integer, name varchar, type integer)");
    stm.executeUpdate("create table testchanges (id integer, changetype integer, seq integer)");
    stm.executeUpdate("create table complexdata (id1 integer, id2 integer, name varchar)");
    stm.executeUpdate("create table complexchanges (id1 integer, id2 integer, changetype integer, seq integer)");
    conn.commit();

    // make empty TM
    TopicMapStoreIF store = new InMemoryTopicMapStore();
    LocatorIF baseloc = URILocator.create("base:foo");
    store.setBaseAddress(baseloc);
    topicmap = store.getTopicMap();
  }

  @After
  public void tearDown() throws SQLException, IOException {
    // must close DB2TM's connection to the database, otherwise the drop
    // table statements below will hang indefinitely
    if (mapping != null) {
      mapping.close();
    }

    // sometimes connnections get into an error state. reopening to avoid
    // this problem.
    stm.close();
    conn.close();
    conn = getConnection();
    stm = conn.createStatement();
    
    // next three lines cause JDBC driver to hang
    stm.executeUpdate("drop table testdata");
    stm.executeUpdate("drop table testchanges");
    stm.executeUpdate("drop table complexdata");
    stm.executeUpdate("drop table complexchanges");

    // close everything
    conn.commit();
    stm.close();
    conn.close();
  }

  // --- Tests

  /**
   * Tests just a single UPDATE.
   */
  @Test
  public void testUpdate() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/SyncTest-mapping.xml");
    Processor.addRelations(mapping, null, topicmap,
                           topicmap.getStore().getBaseAddress());

    // update data set
    stm.executeUpdate("update testdata set type = 3 where id = 1");
    stm.executeUpdate("insert into testchanges values (1, 0, 1)");
    conn.commit();

    // sync TM with database
    Processor.synchronizeRelations(mapping, null, topicmap,
                                   topicmap.getStore().getBaseAddress());
    exportTopicMap("sync-change-type");
  }

  /**
   * Tests UPDATEs to more than one topic.
   */
  @Test
  public void testMultipleUpdates() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic 1', 2)");
    stm.executeUpdate("insert into testdata values (2, 'Topic 2', 2)");
    stm.executeUpdate("insert into testdata values (3, 'Topic 3', 2)");
    conn.commit();

    // read data into TM
    mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/SyncTest-mapping.xml");
    Processor.addRelations(mapping, null, topicmap,
                           topicmap.getStore().getBaseAddress());

    // update data set
    stm.executeUpdate("update testdata set type = 1 where id = 1");
    stm.executeUpdate("update testdata set type = 2 where id = 2");
    stm.executeUpdate("update testdata set type = 3 where id = 3");
    stm.executeUpdate("insert into testchanges values (1, 0, 1)");
    stm.executeUpdate("insert into testchanges values (2, 0, 2)");
    stm.executeUpdate("insert into testchanges values (3, 0, 3)");
    conn.commit();

    // sync TM with database
    Processor.synchronizeRelations(mapping, null, topicmap,
                                   topicmap.getStore().getBaseAddress());
    exportTopicMap("multiple-updates");
  }
  
  /**
   * Tests first a DELETE, then an INSERT.
   */
  @Test
  public void testDeleteInsert() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/SyncTest-mapping.xml");
    Processor.addRelations(mapping, null, topicmap,
                           topicmap.getStore().getBaseAddress());

    // update data set
    stm.executeUpdate("delete from testdata where id = 1");
    stm.executeUpdate("insert into testchanges values (1, -1, 1)");
    conn.commit();

    // sync TM with database
    Processor.synchronizeRelations(mapping, null, topicmap,
                                   topicmap.getStore().getBaseAddress());
    topicmap.getStore().commit();

    // update data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 3)");
    stm.executeUpdate("insert into testchanges values (1, 1, 2)");
    conn.commit();

    // sync TM with database
    Processor.synchronizeRelations(mapping, null, topicmap,
                                   topicmap.getStore().getBaseAddress());
    exportTopicMap("sync-change-type2");
  }  

  /**
   * Tests an UPDATE described as first a DELETE, then a CREATE. This
   * tests for bug #2178.
   */
  @Test
  public void testDeleteCreate() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/SyncTest-mapping.xml");
    Processor.addRelations(mapping, null, topicmap,
                           topicmap.getStore().getBaseAddress());

    // update data set
    stm.executeUpdate("update testdata set type = 3 where id = 1");
    stm.executeUpdate("insert into testchanges values (1, -1, 1)");
    stm.executeUpdate("insert into testchanges values (1, 1, 2)");
    conn.commit();

    // sync TM with database
    Processor.synchronizeRelations(mapping, null, topicmap,
                                   topicmap.getStore().getBaseAddress());
    exportTopicMap("sync-change-type3");
  }

  /**
   * Tests an IGNORE value. So nothing should happen.
   */
  @Test
  public void testIgnore() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/SyncTest-mapping.xml");
    Processor.addRelations(mapping, null, topicmap,
                           topicmap.getStore().getBaseAddress());

    // update data set
    stm.executeUpdate("insert into testchanges values (1, 2, 1)");
    conn.commit();

    // sync TM with database
    Processor.synchronizeRelations(mapping, null, topicmap,
                                   topicmap.getStore().getBaseAddress());

    exportTopicMap("ignore");
  }


  /**
   * Tests situation when no changes have actually been made.
   */
  @Test
  public void testNochanges() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/SyncTest-mapping.xml");
    Processor.addRelations(mapping, null, topicmap,
                           topicmap.getStore().getBaseAddress());

    // do not insert any changes

    // sync TM with database
    Processor.synchronizeRelations(mapping, null, topicmap,
                                   topicmap.getStore().getBaseAddress());
    exportTopicMap("nochanges");
  }

  /**
   * Tests just a single UPDATE on a compound key.
   */
  @Test
  public void testCompoundKey() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into complexdata values (1, 2, 'Topic')");
    conn.commit();

    // read data into TM
    mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/SyncTest-mapping.xml");
    Processor.addRelations(mapping, null, topicmap,
                           topicmap.getStore().getBaseAddress());

    // update data set
    stm.executeUpdate("update complexdata set name = 'Changed' where id1 = 1 and id2 = 2");
    stm.executeUpdate("insert into complexchanges values (1, 2, 0, 1)");
    conn.commit();

    // sync TM with database
    Processor.synchronizeRelations(mapping, null, topicmap,
                                   topicmap.getStore().getBaseAddress());
    exportTopicMap("compound-key");
  }

  /**
   * Tests for loss of types (as in issue 193).
   */
  @Test
  public void testTypeLoss() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // create a topic for the topic type, and give it a topic type
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF topictype = builder.makeTopic();
    TopicIF t2 = builder.makeTopic();
    URILocator psi = URILocator.create("psi:test/2");
    t2.addSubjectIdentifier(psi); // will cause test data to match it

    // verify that t2 has a topic type
    t2.addType(topictype);
    Assert.assertTrue("t2 has no topic type", !t2.getTypes().isEmpty());

    TopicIF t1 = topicmap.getTopicBySubjectIdentifier(URILocator.create("http://example.org/test/1"));
    Assert.assertTrue("t1 was found", t1 == null);

    // synchronize
    mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/association-mapping.xml");
    Processor.synchronizeRelations(mapping, null, topicmap,
                                   topicmap.getStore().getBaseAddress());
    t1 = topicmap.getTopicBySubjectIdentifier(URILocator.create("http://example.org/test/1"));
    Assert.assertTrue("t1 was not found", t1 != null);
    Assert.assertTrue("t1 did not have t2 as its type", t1.getTypes().size() == 1 && t1.getTypes().contains(t2));

    // t2 should not have lost its topic type in the sync!
    Assert.assertTrue("t2 lost its topic type", !t2.getTypes().isEmpty());
  }
  
  // --- Internal methods

  private Connection getConnection() throws SQLException, IOException {
    Map<Object, Object> props = PropertyUtils.loadPropertiesFromClassPath("db2tm.h2.props");
    props.put("net.ontopia.topicmaps.impl.rdbms.ConnectionPool", "false");
    DefaultConnectionFactory cf = new DefaultConnectionFactory(props, false);
    return cf.requestConnection();
  }

  private void exportTopicMap(String name) throws IOException {
    File cxtm = TestFileUtils.getTestOutputFile(testdataDirectory, "out", name + ".cxtm");
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", name + ".cxtm");
    
    // Export the result topic map to cxtm
    new CanonicalXTMWriter(cxtm).write(topicmap);
    
      // Check that the cxtm output matches the baseline.
    Assert.assertTrue("The canonicalized conversion from " + name
                      + " does not match the baseline: " + cxtm + " != " +
                      baseline,
                      TestFileUtils.compareFileToResource(cxtm, baseline));
  }
}
