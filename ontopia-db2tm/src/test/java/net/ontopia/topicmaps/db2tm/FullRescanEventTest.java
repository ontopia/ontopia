
package net.ontopia.topicmaps.db2tm;

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

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.db2tm.*;
import net.ontopia.topicmaps.core.events.TopicMapEvents;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.StoreFactoryReference;
import net.ontopia.topicmaps.utils.SameStoreFactory;

import au.com.bytecode.opencsv.CSVReader;

/**
 * INTERNAL: Test for sync with full rescan. Requires H2 database.
 * Tests if syncing when no topics have changed causes events to be
 * fired.  For SDshare to function properly it's necessary that events
 * are not generated.
 */
public class FullRescanEventTest {
  private final static String dir = "db2tm";
  
  @Test
  public void testRescan() throws IOException, SQLException {
    TestFileUtils.transferTestInputDirectory(dir + "/in/sync");
    
    String casename = "EVENTS";
    String cfg = TestFileUtils.getTransferredTestInputFile(dir, "in", "sync", casename + ".xml").getPath();
    String tm = TestFileUtils.getTransferredTestInputFile(dir, "in", "sync", casename + ".ltm").getPath();
    String out = TestFileUtils.getTestOutputFile(dir, "out", casename + ".cxtm").getPath();
    String baseline = TestFileUtils.getTestInputFile(dir, "in/sync/baseline", casename + ".cxtm");
      
    // Connect to the DB
    Connection conn = ChangelogTestCase.getConnection();
    Statement stm = conn.createStatement();
    
    // Load the starter data into the table
    ChangelogTestCase.importCSV(stm, casename, casename + "-before.csv");
    conn.commit(); // necessary to avoid timeout from DB2TM connection
      
    // Import the topic map seed.
    TopicMapIF topicmap = ImportExportUtils.getReader("file:" + tm).read();
      
    // Extend the topic map seed with the the config file.
    DB2TM.add(cfg, topicmap);

    // register event listener to see if any events leak out
    ErrorListener listener = new ErrorListener();
    SameStoreFactory factory = new SameStoreFactory(topicmap.getStore());
    StoreFactoryReference ref =
      new StoreFactoryReference("jill.xtm", "jill.xtm", factory);
    ref.createStore(false);
    TopicMapEvents.addTopicListener(ref, listener);
    ref.createStore(false); // turn on events (grrr)
    
    // OK, now, finally, we can sync!
    DB2TM.sync(cfg, topicmap);

    // were there any events?
    assertEquals("there were events during sync", 0, listener.getEventCount());
  }

  static class ErrorListener implements TopicMapListenerIF {
    private int events;

    public int getEventCount() {
      return events;
    }
    
    public void objectAdded(TMObjectIF snapshot) {
      events++;
    }
    
    public void objectModified(TMObjectIF snapshot) {
      events++;
    }
    
    public void objectRemoved(TMObjectIF snapshot) {
      events++;
    }
  }
}