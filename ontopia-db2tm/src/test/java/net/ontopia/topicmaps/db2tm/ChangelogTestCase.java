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

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.TestFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * INTERNAL: Test for sync with changelogs. Requires H2 database.
 */
@RunWith(Parameterized.class)
public class ChangelogTestCase {
  
  private final static String testdataDirectory = "db2tm";
      
  private String base;
  private String casename;

  @Parameters
  public static List<String[]> generateTests() throws IOException {
    TestFileUtils.transferTestInputDirectory(testdataDirectory + "/in/sync");
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in/sync", ".xml");
  }

  public ChangelogTestCase(String root, String xmlfile) {
    this.casename = xmlfile.substring(0, xmlfile.length() - 4);
    this.base = TestFileUtils.getTestdataOutputDirectory() + testdataDirectory;
  }
    
  @Test
  public void testFile() throws IOException, SQLException {
    // this particular test file is for FullRescanEventTest, and we don't
    // want to test it again here. we lack the -changelog.csv in any case.
    if ("EVENTS".equals(casename)) {
      return;
    }
    
    TestFileUtils.verifyDirectory(base, "out");
      
    String cfg = TestFileUtils.getTransferredTestInputFile(testdataDirectory, "in", "sync", casename + ".xml").getPath();
    File tm = TestFileUtils.getTransferredTestInputFile(testdataDirectory, "in", "sync", casename + ".ltm");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", casename + ".cxtm");
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "in/sync/baseline", casename + ".cxtm");
      
    // Connect to the DB
    Connection conn = getConnection();
    Statement stm = conn.createStatement();

    // Load the starter data into the table
    importCSV(stm, casename, casename + "-before.csv");

    // Create the changelog table (DB2TM.add needs it), but leave it empty
    importCSV(stm, casename + "_changelog", casename + "-changelog.csv",
              false);
      
    // Import the topic map seed.
    TopicMapIF topicmap = ImportExportUtils.getReader(tm).read();
      
    // Extend the topic map seed with the the config file.
    DB2TM.add(cfg, topicmap);

    // Now load the database with the changed data
    importCSV(stm, casename, casename + "-after.csv");
    importCSV(stm, casename + "_changelog", casename + "-changelog.csv");
    conn.commit(); // necessary to avoid timeout from DB2TM connection

    // OK, now, finally, we can sync!
    DB2TM.sync(cfg, topicmap);

    // Canonicalize!
    new CanonicalXTMWriter(out).write(topicmap);
      
    // Check that the cxtm output matches the baseline.
    Assert.assertTrue("The canonicalized conversion from " + casename
                      + " does not match the baseline: " + out + " " + baseline,
                      TestFileUtils.compareFileToResource(out, baseline));
  }

  // public so it can be accessed from FullRescanEventTest
  public static Connection getConnection() throws SQLException, IOException {
    File propfile = TestFileUtils.getTransferredTestInputFile(testdataDirectory, "in", "sync", "h2.properties");
    Map<Object, Object> props = PropertyUtils.loadProperties(propfile);
    props.put("net.ontopia.topicmaps.impl.rdbms.ConnectionPool", "false");
    DefaultConnectionFactory cf = new DefaultConnectionFactory(props, false);
    return cf.requestConnection();
  }

  // public so it can be accessed from FullRescanEventTest
  public static void importCSV(Statement stm, String table, String file)
    throws IOException, SQLException {
    importCSV(stm, table, file, true);
  }
    
  private static void importCSV(Statement stm, String table, String file,
                                boolean load_data)
    throws IOException, SQLException {
    // first, get rid of the table if it's already there
    try {
      stm.executeUpdate("drop table " + table);
    } catch (SQLException e) {
      // table wasn't there. never mind
    }
      
    // open the CSV file
    String csv = TestFileUtils.getTransferredTestInputFile(testdataDirectory, "in", "sync", file).getPath();
    FileReader reader = new FileReader(csv);
    CSVReader csvreader = new CSVReader(reader, ';', '"');

    // read the first line to get the column names
    String[] colnames = csvreader.readNext();
    String[] columndefs = new String[colnames.length];
    for (int ix = 0; ix < colnames.length; ix++) {
      columndefs[ix] = colnames[ix] + " varchar";
    }

    // now we can create the table
    stm.executeUpdate("create table " + table + " (" +
                      StringUtils.join(columndefs, ", ") + ")");

    // are we just creating the table, or should we load the data?
    if (!load_data) {
      return;
    }

    // ok, now insert the actual data
    String cols = StringUtils.join(colnames, ", ");
    String[] tuple = csvreader.readNext();
    while (tuple != null) {
      String[] values = new String[tuple.length];
      for (int ix = 0; ix < tuple.length; ix++) {
        values[ix] = "'" + tuple[ix] + "'"; // escaping? hah!
      }
          
      stm.executeUpdate("insert into " + table + " (" + cols + ") values (" +
                        StringUtils.join(values, ", ") + ")");
          
      tuple = csvreader.readNext();
    }
  }

  // ===== FUNCTION METHODS

  // this method is used from the XML file of at least one test case
  public static String parseID(String pkey) {
    int pos = pkey.indexOf('=');
    return pkey.substring(pos + 1);
  }
}
