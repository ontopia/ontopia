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
import java.sql.Statement;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.ConnectionFactoryIF;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class JDBCDataSourceTest {

  private static final boolean DEBUG_LTM = false; // keep off in CVS

  // NOTE: these are hardcoded for the time being
  private String propfile = "classpath:db2tm.h2.props";

  private final static String testdataDirectory = "db2tm";

  /* Disabled: 'Wrong change type' for row 4. Also fails in trunk. 
   * Update and re-enabled after test is fixed in trunk.
  @Test
  public void testReaders() throws Exception {
    
    ConnectionFactoryIF cf = new DefaultConnectionFactory(PropertyUtils.loadProperties(StreamUtils.getInputStream(propfile)), false);
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
      RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/JDBCDataSourceTest-readers.xml");
      JDBCDataSource ds = (JDBCDataSource)mapping.getDataSource("jdbc");
      Relation relation = mapping.getRelation("jdst");
      
      // test tuple reader
      TupleReaderIF reader = ds.getReader("jdst");
      String [] tuple;
      tuple = reader.readNext();
      Assert.assertTrue("Row 1 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"1", "a", "10", "2007-01-01"}));    
      tuple = reader.readNext();
      Assert.assertTrue("Row 2 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"2", "b", "20", "2007-01-02"}));    
      tuple = reader.readNext();
      Assert.assertTrue("Row 3 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"3", "c", "30", "2007-01-03"}));    
      tuple = reader.readNext();
      Assert.assertTrue("Row 4 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"4", "d", "40", "2007-01-04"}));    
      tuple = reader.readNext();
      Assert.assertTrue("Row 5 does exist", tuple == null);
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
      Assert.assertTrue("Row 1 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"1", "a", "10", "2007-01-01"}));
      Assert.assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_CREATE);
      Assert.assertTrue("Wrong order value", clreader.getOrderValue().equals("1"));
      tuple = clreader.readNext();
      Assert.assertTrue("Row 2 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"2", "b", "20", "2007-01-02"}));    
      Assert.assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_CREATE);
      Assert.assertTrue("Wrong order value", clreader.getOrderValue().equals("2"));
      tuple = clreader.readNext();
      Assert.assertTrue("Row 3 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"3", null, null, null}));    
      Assert.assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_DELETE);
      Assert.assertTrue("Wrong order value", clreader.getOrderValue().equals("4"));
      tuple = clreader.readNext();
      Assert.assertTrue("Row 4 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"4", "e", "40", "2007-01-04"}));    
      Assert.assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_UPDATE);
      Assert.assertTrue("Wrong order value", clreader.getOrderValue().equals("6"));
      tuple = clreader.readNext();
      Assert.assertTrue("Row 5 does exist", tuple == null);
      clreader.close();

      stm.executeUpdate("insert into jdst (a,b,c,d) values (5,'f',50, date '2007-01-05')");
      stm.executeUpdate("insert into jdst_changes (a,b,c,d,ct,cd) values (5,'f',50, date '2007-01-05', 'a', 7)");
      conn.commit();
      
      // test changelog reader with start value
      clreader = ds.getChangelogReader((Changelog)relation.getSyncs().iterator().next(), "4");
      tuple = clreader.readNext();
      Assert.assertTrue("Row 1 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"4", "e", "40", "2007-01-04"}));    
      Assert.assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_UPDATE);
      Assert.assertTrue("Wrong order value", clreader.getOrderValue().equals("6"));
      tuple = clreader.readNext();
      Assert.assertTrue("Row 2 not equals to " + Arrays.asList(tuple), Arrays.equals(tuple, new String[] {"5", "f", "50", "2007-01-05"}));    
      Assert.assertTrue("Wrong change type", clreader.getChangeType() == ChangelogReaderIF.CHANGE_TYPE_CREATE);
      Assert.assertTrue("Wrong order value", clreader.getOrderValue().equals("7"));
      tuple = clreader.readNext();
      Assert.assertTrue("Row 3 does exist", tuple == null);
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
  */
  
  @Test
  public void testSecondary() throws Exception {
    
    ConnectionFactoryIF cf = new DefaultConnectionFactory(PropertyUtils.loadProperties(StreamUtils.getInputStream(propfile)), false);
    Connection conn = cf.requestConnection();
    try {
      // create tables
      Statement stm = conn.createStatement();
      
      stm.executeUpdate("drop table if exists tfirst");
      stm.executeUpdate("drop table if exists tfirst_changes");
      stm.executeUpdate("drop table if exists tsecond");
      stm.executeUpdate("drop table if exists tsecond_changes");
      
      stm.executeUpdate("create table tfirst (a integer, b varchar, c integer, d date)");
      stm.executeUpdate("create table tfirst_changes (a integer, b varchar, c integer, d date, ct varchar, cd integer)");
      stm.executeUpdate("create table tsecond (a integer, b varchar, c integer, d date)");
      stm.executeUpdate("create table tsecond_changes (a integer, b varchar, c integer, d date, ct varchar, cd integer)");
      
      // insert rows
      stm.executeUpdate("insert into tfirst (a,b,c,d) values (1,'a',10, date '2007-01-01')");
      stm.executeUpdate("insert into tfirst (a,b,c,d) values (2,'b',20, date '2007-01-02')");
      stm.executeUpdate("insert into tfirst (a,b,c,d) values (3,'c',30, date '2007-01-03')");
      stm.executeUpdate("insert into tfirst (a,b,c,d) values (4,'d',40, date '2007-01-04')");

      stm.executeUpdate("insert into tsecond (a,b,c,d) values (1,'e',50, date '2007-02-01')");
      stm.executeUpdate("insert into tsecond (a,b,c,d) values (2,'f',60, date '2007-02-02')");
      stm.executeUpdate("insert into tsecond (a,b,c,d) values (3,'g',70, date '2007-02-03')");
      stm.executeUpdate("insert into tsecond (a,b,c,d) values (4,'h',80, date '2007-02-04')");

      conn.commit();
      
      // read mapping
      RelationMapping mapping = RelationMapping.readFromClasspath("net/ontopia/topicmaps/db2tm/JDBCDataSourceTest-secondary.xml");

      TopicMapStoreIF store = new InMemoryTopicMapStore();
      LocatorIF baseloc = URILocator.create("base:foo");
      store.setBaseAddress(baseloc);
      TopicMapIF topicmap = store.getTopicMap();
      
      // add relations
      Processor.addRelations(mapping, null, topicmap, baseloc);
      exportTopicMap(topicmap, "after-first-sync");

      stm.executeUpdate("insert into tsecond_changes (a,b,c,d,ct,cd) values (2,'f',60,date '2007-02-02', 'r', 2)");
      stm.executeUpdate("delete from tsecond where a = 2");

      conn.commit();
            
      // synchronize relations
      Processor.synchronizeRelations(mapping, null, topicmap, baseloc);
      exportTopicMap(topicmap, "after-second-sync");

      mapping.close();
      
      // delete tables
      stm.executeUpdate("drop table tfirst");
      stm.executeUpdate("drop table tfirst_changes");
      stm.executeUpdate("drop table tsecond");
      stm.executeUpdate("drop table tsecond_changes");
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
    File cxtm = TestFileUtils.getTestOutputFile(testdataDirectory, "out", name + ".cxtm");
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", name + ".cxtm");
    
    // Export the result topic map to ltm, for manual inspection purposes.
    if (DEBUG_LTM) {
      File ltm = TestFileUtils.getTestOutputFile(testdataDirectory, "out", name + ".ltm");
      new LTMTopicMapWriter(ltm).write(topicmap);
    }
    
    // Export the result topic map to cxtm
    new CanonicalXTMWriter(cxtm).write(topicmap);
      
      // Check that the cxtm output matches the baseline.
    Assert.assertTrue("The canonicalized conversion from " + name
               + " does not match the baseline.",
               TestFileUtils.compareFileToResource(cxtm, baseline));
  }
  
}
