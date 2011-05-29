
package net.ontopia.topicmaps.db2tm;

import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

import net.ontopia.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.db2tm.*;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

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
    LocatorIF baseloc = URIUtils.getURILocator("base:foo");
    store.setBaseAddress(baseloc);
    topicmap = store.getTopicMap();
  }

  @After
  public void tearDown() throws SQLException, IOException {
    // must close DB2TM's connection to the database, otherwise the drop
    // table statements below will hang indefinitely
    if (mapping != null)
      mapping.close();

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
    exportTopicMap("ignore");
  }
  
  /**
   * Tests an INSERT followed by another INSERT. This should produce an error.
   */
  @Test
  public void testSequenceError() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/SyncTest-mapping.xml");
    Processor.addRelations(mapping, null, topicmap,
                           topicmap.getStore().getBaseAddress());

    // update data set
    stm.executeUpdate("insert into testchanges values (1, 1, 1)");
    stm.executeUpdate("insert into testchanges values (1, 1, 2)");
    conn.commit();

    // sync TM with database
    try {
      Processor.synchronizeRelations(mapping, null, topicmap,
                                     topicmap.getStore().getBaseAddress());
      Assert.fail("Erroneous sequence of changes not detected.");
    } catch (DB2TMException e) {
      // good; error detected
    }
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
    URILocator psi = new URILocator("psi:test/2");
    t2.addSubjectIdentifier(psi); // will cause test data to match it

    // verify that t2 has a topic type
    t2.addType(topictype);
    Assert.assertTrue("t2 has no topic type", !t2.getTypes().isEmpty());

    TopicIF t1 = topicmap.getTopicBySubjectIdentifier(URIUtils.getURILocator("http://example.org/test/1"));
    Assert.assertTrue("t1 was found", t1 == null);

    // synchronize
    mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/association-mapping.xml");
    Processor.synchronizeRelations(mapping, null, topicmap,
                                   topicmap.getStore().getBaseAddress());
    t1 = topicmap.getTopicBySubjectIdentifier(URIUtils.getURILocator("http://example.org/test/1"));
    Assert.assertTrue("t1 was not found", t1 != null);
    Assert.assertTrue("t1 did not have t2 as its type", t1.getTypes().size() == 1 && t1.getTypes().contains(t2));

    // t2 should not have lost its topic type in the sync!
    Assert.assertTrue("t2 lost its topic type", !t2.getTypes().isEmpty());
  }
  
  // --- Internal methods

  private Connection getConnection() throws SQLException, IOException {
    Map props = PropertyUtils.loadPropertiesFromClassPath("db2tm.h2.props");
    props.put("net.ontopia.topicmaps.impl.rdbms.ConnectionPool", "false");
    DefaultConnectionFactory cf = new DefaultConnectionFactory(props, false);
    return cf.requestConnection();
  }

  private void exportTopicMap(String name) throws IOException {
    File cxtm = FileUtils.getTestOutputFile(testdataDirectory, "out", name + ".cxtm");
    String baseline = FileUtils.getTestInputFile(testdataDirectory, "baseline", name + ".cxtm");
    
    // Export the result topic map to cxtm
    FileOutputStream out = new FileOutputStream(cxtm);
    (new CanonicalXTMWriter(out)).write(topicmap);
    out.close();
      
      // Check that the cxtm output matches the baseline.
    Assert.assertTrue("The canonicalized conversion from " + name
               + " does not match the baseline.",
               FileUtils.compareFileToResource(cxtm, baseline));
  }
}
