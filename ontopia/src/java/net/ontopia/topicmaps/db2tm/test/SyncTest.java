
// $Id: SyncTest.java,v 1.2 2009/01/23 13:13:48 lars.garshol Exp $

package net.ontopia.topicmaps.db2tm.test;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

import net.ontopia.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.db2tm.*;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;

public class SyncTest extends AbstractOntopiaTestCase {
  private Connection conn;
  private Statement stm;
  private TopicMapIF topicmap;

  public SyncTest(String name) {
    super(name);
  }
  
  public void setUp() throws SQLException, IOException {
    // get JDBC connection to database
    conn = getConnection();

    // create tables
    // (1) data table (id, name, type)
    // (2) changes table
    stm = conn.createStatement();
//     stm.executeUpdate("create table testdata (id integer, name varchar, type integer)");
//     stm.executeUpdate("create table testchanges (id integer, changetype integer, seq integer)");
//     stm.executeUpdate("create table complexdata (id1 integer, id2 integer, name varchar)");
//     stm.executeUpdate("create table complexchanges (id1 integer, id2 integer, changetype integer, seq integer)");
//     conn.commit();

    // make empty TM
    TopicMapStoreIF store = new InMemoryTopicMapStore();
    LocatorIF baseloc = URIUtils.getURILocator("base:foo");
    store.setBaseAddress(baseloc);
    topicmap = store.getTopicMap();
  }

  public void tearDown() throws SQLException, IOException {
    // next three lines cause JDBC driver to hang
//     stm.executeUpdate("drop table testdata");
//     stm.executeUpdate("drop table testchanges");
//     stm.executeUpdate("drop table complexdata");
//     stm.executeUpdate("drop table complexchanges");
    // next three lines are workaround
    stm.executeUpdate("delete from testdata");
    stm.executeUpdate("delete from testchanges");
    stm.executeUpdate("delete from complexdata");
    stm.executeUpdate("delete from complexchanges");
    conn.commit();
    stm.close();
    conn.close();
  }

  // --- Tests

  /**
   * Tests just a single UPDATE.
   */
  public void testUpdate() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/test/SyncTest-mapping.xml");
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
  public void testMultipleUpdates() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic 1', 2)");
    stm.executeUpdate("insert into testdata values (2, 'Topic 2', 2)");
    stm.executeUpdate("insert into testdata values (3, 'Topic 3', 2)");
    conn.commit();

    // read data into TM
    RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/test/SyncTest-mapping.xml");
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
  public void testDeleteInsert() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/test/SyncTest-mapping.xml");
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
  public void testDeleteCreate() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/test/SyncTest-mapping.xml");
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
  public void testIgnore() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/test/SyncTest-mapping.xml");
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
  public void testNochanges() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/test/SyncTest-mapping.xml");
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
  public void testSequenceError() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into testdata values (1, 'Topic', 2)");
    conn.commit();

    // read data into TM
    RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/test/SyncTest-mapping.xml");
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
      fail("Erroneous sequence of changes not detected.");
    } catch (DB2TMException e) {
      // good; error detected
    }
  }

  /**
   * Tests just a single UPDATE on a compound key.
   */
  public void testCompoundKey() throws SQLException, IOException {
    // create initial data set
    stm.executeUpdate("insert into complexdata values (1, 2, 'Topic')");
    conn.commit();

    // read data into TM
    RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/test/SyncTest-mapping.xml");
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
  
  // --- Internal methods

  private Connection getConnection() throws SQLException, IOException {
    String propfile = System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile");
    DefaultConnectionFactory cf = new DefaultConnectionFactory(PropertyUtils.loadProperties(propfile), false);
    return cf.requestConnection();
  }

  private void exportTopicMap(String name) throws IOException {
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "db2tm" + File.separator;
    verifyDirectory(base, "out");

    String cxtm = base + "out" + File.separator + name + ".cxtm";
    String baseline = base + "baseline" + File.separator + name + ".cxtm";
    
    // Export the result topic map to cxtm
    FileOutputStream out = new FileOutputStream(cxtm);
    (new CanonicalXTMWriter(out)).write(topicmap);
    out.close();
      
      // Check that the cxtm output matches the baseline.
    assertTrue("The canonicalized conversion from " + name
               + " does not match the baseline.",
               FileUtils.compare(cxtm, baseline));
  }
}
