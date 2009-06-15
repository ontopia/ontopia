
package net.ontopia.topicmaps.db2tm.test;

import net.ontopia.topicmaps.db2tm.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.persistence.proxy.*;
import net.ontopia.utils.*;

import java.io.*;
import java.sql.*;
import java.util.*;

public class JDBCDataSourceTest extends AbstractOntopiaTestCase {

  private static final boolean DEBUG_LTM = false; // keep off in CVS

  // NOTE: these are hardcoded for the time being
  String propfile = "/tmp/grove.postgresql.props";

  public JDBCDataSourceTest(String name) {
    super(name);
  }

  public void testReaders() throws Exception {
    
    ConnectionFactoryIF cf = new DefaultConnectionFactory(PropertyUtils.loadProperties(new File(propfile)), false);
    Connection conn = cf.requestConnection();
    try {
      // create tables
      Statement stm = conn.createStatement();
      
      stm.executeUpdate("drop table if exists jdst");
      stm.executeUpdate("drop table if exists jdst_changes");
      
      stm.executeUpdate("create table jdst (a integer, b varchar, c integer, d date)");
      stm.executeUpdate("create table jdst_changes (a integer, b varchar, c integer, d date, ct varchar, cd integer)");
      
      // insert rows
      stm.executeUpdate("insert into jdst (a,b,c,d) values (1,'a',10, date '2007-01-01')");
      stm.executeUpdate("insert into jdst (a,b,c,d) values (2,'b',20, date '2007-01-02')");
      stm.executeUpdate("insert into jdst (a,b,c,d) values (3,'c',30, date '2007-01-03')");
      stm.executeUpdate("insert into jdst (a,b,c,d) values (4,'d',40, date '2007-01-04')");
      conn.commit();
      
      // read mapping
      RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/test/JDBCDataSourceTest-readers.xml");
      JDBCDataSource ds = (JDBCDataSource)mapping.getDataSource("jdbc");
      Relation relation = mapping.getRelation("jdst");
      
      // test tuple reader
      TupleReaderIF reader = ds.getReader("jdst");
      String [] tuple;
      tuple = reader.readNext();
      assertTrue("Row 1 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"1", "a", "10", "2007-01-01"}));    
      tuple = reader.readNext();
      assertTrue("Row 2 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"2", "b", "20", "2007-01-02"}));    
      tuple = reader.readNext();
      assertTrue("Row 3 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"3", "c", "30", "2007-01-03"}));    
      tuple = reader.readNext();
      assertTrue("Row 4 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"4", "d", "40", "2007-01-04"}));    
      tuple = reader.readNext();
      assertTrue("Row 5 does exist", tuple == null);
      reader.close();
      
      stm.executeUpdate("delete from jdst where a = 3");
      stm.executeUpdate("update jdst set b = 'e' where a = 4");

      stm.executeUpdate("insert into jdst_changes (a,b,c,d,ct,cd) values (1,'a',10, date '2007-01-01', 'a', 1)");
      stm.executeUpdate("insert into jdst_changes (a,b,c,d,ct,cd) values (2,'b',20, date '2007-01-02', 'a', 2)");
      stm.executeUpdate("insert into jdst_changes (a,b,c,d,ct,cd) values (2,'x',22, date '2007-01-02', 'x', 3)");
      stm.executeUpdate("insert into jdst_changes (a,b,c,d,ct,cd) values (3,'c',30, date '2007-01-03', 'r', 4)");
      stm.executeUpdate("insert into jdst_changes (a,b,c,d,ct,cd) values (4,'d',40, date '2007-01-04', 'a', 5)");
      stm.executeUpdate("insert into jdst_changes (a,b,c,d,ct,cd) values (4,'e',40, date '2007-01-04', 'u', 6)");
      conn.commit();
      
      // test changelog reader
      ChangelogReaderIF clreader = ds.getChangelogReader((Changelog)relation.getSyncs().iterator().next(), null);
      tuple = clreader.readNext();
      assertTrue("Row 1 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"1", "a", "10", "2007-01-01"}));
      assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_CREATE);
      assertTrue("Wrong order value", clreader.getOrderValue().equals("1"));
      tuple = clreader.readNext();
      assertTrue("Row 2 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"2", "b", "20", "2007-01-02"}));    
      assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_CREATE);
      assertTrue("Wrong order value", clreader.getOrderValue().equals("2"));
      tuple = clreader.readNext();
      assertTrue("Row 3 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"3", null, null, null}));    
      assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_DELETE);
      assertTrue("Wrong order value", clreader.getOrderValue().equals("4"));
      tuple = clreader.readNext();
      assertTrue("Row 4 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"4", "e", "40", "2007-01-04"}));    
      assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_UPDATE);
      assertTrue("Wrong order value", clreader.getOrderValue().equals("6"));
      tuple = clreader.readNext();
      assertTrue("Row 5 does exist", tuple == null);
      clreader.close();

      stm.executeUpdate("insert into jdst (a,b,c,d) values (5,'f',50, date '2007-01-05')");
      stm.executeUpdate("insert into jdst_changes (a,b,c,d,ct,cd) values (5,'f',50, date '2007-01-05', 'a', 7)");
      conn.commit();
      
      // test changelog reader with start value
      clreader = ds.getChangelogReader((Changelog)relation.getSyncs().iterator().next(), "4");
      tuple = clreader.readNext();
      assertTrue("Row 1 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"4", "e", "40", "2007-01-04"}));    
      assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_UPDATE);
      assertTrue("Wrong order value", clreader.getOrderValue().equals("6"));
      tuple = clreader.readNext();
      assertTrue("Row 2 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"5", "f", "50", "2007-01-05"}));    
      assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_CREATE);
      assertTrue("Wrong order value", clreader.getOrderValue().equals("7"));
      tuple = clreader.readNext();
      assertTrue("Row 3 does exist", tuple == null);
      clreader.close();

      mapping.close();
      
      // delete tables
      stm.executeUpdate("drop table jdst");
      stm.executeUpdate("drop table jdst_changes");
      stm.close();
      conn.commit();
      
    } catch (Exception e) {
      conn.rollback();
      throw e;
    } finally {
      conn.close();
    }
  }

  public void testSecondary() throws Exception {
    
    ConnectionFactoryIF cf = new DefaultConnectionFactory(PropertyUtils.loadProperties(new File(propfile)), false);
    Connection conn = cf.requestConnection();
    try {
      // create tables
      Statement stm = conn.createStatement();
      
      stm.executeUpdate("drop table if exists first");
      stm.executeUpdate("drop table if exists first_changes");
      stm.executeUpdate("drop table if exists second");
      stm.executeUpdate("drop table if exists second_changes");
      
      stm.executeUpdate("create table first (a integer, b varchar, c integer, d date)");
      stm.executeUpdate("create table first_changes (a integer, b varchar, c integer, d date, ct varchar, cd integer)");
      stm.executeUpdate("create table second (a integer, b varchar, c integer, d date)");
      stm.executeUpdate("create table second_changes (a integer, b varchar, c integer, d date, ct varchar, cd integer)");
      
      // insert rows
      stm.executeUpdate("insert into first (a,b,c,d) values (1,'a',10, date '2007-01-01')");
      stm.executeUpdate("insert into first (a,b,c,d) values (2,'b',20, date '2007-01-02')");
      stm.executeUpdate("insert into first (a,b,c,d) values (3,'c',30, date '2007-01-03')");
      stm.executeUpdate("insert into first (a,b,c,d) values (4,'d',40, date '2007-01-04')");

      stm.executeUpdate("insert into second (a,b,c,d) values (1,'e',50, date '2007-02-01')");
      stm.executeUpdate("insert into second (a,b,c,d) values (2,'f',60, date '2007-02-02')");
      stm.executeUpdate("insert into second (a,b,c,d) values (3,'g',70, date '2007-02-03')");
      stm.executeUpdate("insert into second (a,b,c,d) values (4,'h',80, date '2007-02-04')");

      conn.commit();
      
      // read mapping
      RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/test/JDBCDataSourceTest-secondary.xml");

      TopicMapStoreIF store = new InMemoryTopicMapStore();
      LocatorIF baseloc = URIUtils.getURILocator("base:foo");
      store.setBaseAddress(baseloc);
      TopicMapIF topicmap = store.getTopicMap();
      
      // add relations
      Processor.addRelations(mapping, null, topicmap, baseloc);
      exportTopicMap(topicmap, "after-first-sync");

      stm.executeUpdate("insert into second_changes (a,b,c,d,ct,cd) values (2,'f',60,date '2007-02-02', 'r', 2)");
      stm.executeUpdate("delete from second where a = 2");

      conn.commit();
            
      // synchronize relations
      Processor.synchronizeRelations(mapping, null, topicmap, baseloc);
      exportTopicMap(topicmap, "after-second-sync");

      mapping.close();
      
      // delete tables
      stm.executeUpdate("drop table first");
      stm.executeUpdate("drop table first_changes");
      stm.executeUpdate("drop table second");
      stm.executeUpdate("drop table second_changes");
      stm.close();

      store.close();
      
      conn.commit();
    } catch (Exception e) {
      conn.rollback();
      throw e;
    } finally {
      conn.close();
    }
  }

  private void exportTopicMap(TopicMapIF topicmap, String name) throws IOException {
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "db2tm" + File.separator;
    verifyDirectory(base, "out");

    String cxtm = base + "out" + File.separator + name + ".cxtm";
    String baseline = base + "baseline" + File.separator + name + ".cxtm";
    
    // Export the result topic map to ltm, for manual inspection purposes.
    if (DEBUG_LTM) {
      String ltm = base + "out" + File.separator + name + ".ltm";
      (new LTMTopicMapWriter(new FileOutputStream(ltm))).write(topicmap);
    }
    
    // Export the result topic map to cxtm
    (new CanonicalXTMWriter(new FileOutputStream(cxtm))).write(topicmap);
      
      // Check that the cxtm output matches the baseline.
    assertTrue("The canonicalized conversion from " + name
               + " does not match the baseline.",
               FileUtils.compare(cxtm, baseline));
  }
  
}
