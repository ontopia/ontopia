
package net.ontopia.topicmaps.db2tm.test;

import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

import net.ontopia.utils.FileUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.db2tm.*;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;

import au.com.bytecode.opencsv.CSVReader;

/**
 * INTERNAL: Test for sync with changelogs. Requires H2 database.
 */
public class ChangelogTestGenerator implements TestCaseGeneratorIF {
  private Connection conn;
  private Statement stm;
  
  public Iterator generateTests() {
    Collection tests = new ArrayList();
    String root = AbstractCanonicalTestCase.getTestDirectory();
    String base = root + File.separator + "db2tm" + File.separator + "in" +
      File.separator + "sync" + File.separator;

    // Create test cases for each xml config file in 'in/sync'.
    File indir = new File(base);
    File[] infiles = indir.listFiles();
    if (infiles == null)
      return Collections.EMPTY_SET.iterator();
      
    for (int i = 0; i < infiles.length; i++) {
      String name = infiles[i].getName ();
      if (name.endsWith (".xml"))
        tests.add(new ChangelogTestCase(name, indir));
    }

    return tests.iterator();
  }

  public class ChangelogTestCase extends AbstractCanonicalTestCase {
    private File dir;
    private String casename;

    public ChangelogTestCase(String xmlfile, File dir) {
      super("testFile");
      this.casename = xmlfile.substring(0, xmlfile.length() - 4);
      this.dir = dir;
    }
    
    public void testFile() throws IOException, SQLException {
      verifyDirectory(dir.getPath(), "out");
      
      String cfg = dir.getPath() + File.separator + casename + ".xml";
      String tm = dir.getPath() + File.separator + casename + ".ltm";
      String out = dir.getPath() + File.separator + "out" +
                   File.separator + casename + ".cxtm";
      String baseline = dir.getPath() + File.separator + "baseline" +
                        File.separator + casename + ".cxtm";
      
      // Connect to the DB
      Connection conn = getConnection();
      Statement stm = conn.createStatement();

      // Load the starter data into the table
      importCSV(stm, casename, casename + "-before.csv");

      // Create the changelog table (DB2TM.add needs it), but leave it empty
      importCSV(stm, casename + "_changelog", casename + "-changelog.csv",
                false);
      
      // Import the topic map seed.
      TopicMapIF topicmap = ImportExportUtils.getReader("file:" + tm).read();
      
      // Extend the topic map seed with the the config file.
      DB2TM.add(cfg, topicmap);

      // Now load the database with the changed data
      importCSV(stm, casename, casename + "-after.csv");
      importCSV(stm, casename + "_changelog", casename + "-changelog.csv");
      conn.commit(); // necessary to avoid timeout from DB2TM connection

      // OK, now, finally, we can sync!
      DB2TM.sync(cfg, topicmap);

      // Canonicalize!
      FileOutputStream fos = new FileOutputStream(out);
      (new CanonicalXTMWriter(fos)).write(topicmap);
      fos.close();
      
      // Check that the cxtm output matches the baseline.
      assertTrue("The canonicalized conversion from " + casename
          + " does not match the baseline: " + out + " " + baseline,
                 FileUtils.compare(out, baseline));
    }
    
    private Connection getConnection() throws SQLException, IOException {
      String propfile = dir.getPath() + File.separator + "h2.properties";
      Map props = PropertyUtils.loadProperties(propfile);
      props.put("net.ontopia.topicmaps.impl.rdbms.ConnectionPool", "false");
      DefaultConnectionFactory cf = new DefaultConnectionFactory(props, false);
      return cf.requestConnection();
    }

    private void importCSV(Statement stm, String table, String file)
      throws IOException, SQLException {
      importCSV(stm, table, file, true);
    }
    
    private void importCSV(Statement stm, String table, String file,
                           boolean load_data)
      throws IOException, SQLException {
      // first, get rid of the table if it's already there
      try {
        stm.executeUpdate("drop table " + table);
      } catch (SQLException e) {
        // table wasn't there. never mind
      }
      
      // open the CSV file
      String csv = dir.getPath() + File.separator + file;
      FileReader reader = new FileReader(csv);
      CSVReader csvreader = new CSVReader(reader, ';', '"');

      // read the first line to get the column names
      String[] colnames = csvreader.readNext();
      String[] columndefs = new String[colnames.length];
      for (int ix = 0; ix < colnames.length; ix++)
        columndefs[ix] = colnames[ix] + " varchar";

      // now we can create the table
      stm.executeUpdate("create table " + table + " (" +
                        StringUtils.join(columndefs, ", ") + ")");

      // are we just creating the table, or should we load the data?
      if (!load_data)
        return;

      // ok, now insert the actual data
      String cols = StringUtils.join(colnames, ", ");
      String[] tuple = csvreader.readNext();
      while (tuple != null) {
        String[] values = new String[tuple.length];
        for (int ix = 0; ix < tuple.length; ix++)
          values[ix] = "'" + tuple[ix] + "'"; // escaping? hah!
          
        stm.executeUpdate("insert into " + table + " (" + cols + ") values (" +
                          StringUtils.join(values, ", ") + ")");
          
        tuple = csvreader.readNext();
      }
    }
  }

  // ===== FUNCTION METHODS

  // this method is used from the XML file of at least one test case
  public static String parseID(String pkey) {
    int pos = pkey.indexOf('=');
    return pkey.substring(pos + 1);
  }
}