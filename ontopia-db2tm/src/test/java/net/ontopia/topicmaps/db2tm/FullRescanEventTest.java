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
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.events.TopicMapEvents;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.entry.StoreFactoryReference;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.SameStoreFactory;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

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
    File tm = TestFileUtils.getTransferredTestInputFile(dir, "in", "sync", casename + ".ltm");
      
    // Connect to the DB
    Connection conn = ChangelogTestCase.getConnection();
    Statement stm = conn.createStatement();
    
    // Load the starter data into the table
    ChangelogTestCase.importCSV(stm, "ACTIVITYLOG", "ACTIVITYLOG-before.csv");
    ChangelogTestCase.importCSV(stm, casename, casename + "-before.csv");
    conn.commit(); // necessary to avoid timeout from DB2TM connection
      
    // Import the topic map seed.
    TopicMapIF topicmap = ImportExportUtils.getReader(tm).read();
      
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
    Assert.assertEquals("there were events during sync", 0, listener.getEventCount());
  }

  static class ErrorListener implements TopicMapListenerIF {
    private int events;

    public int getEventCount() {
      return events;
    }
    
    @Override
    public void objectAdded(TMObjectIF snapshot) {
      events++;
    }
    
    @Override
    public void objectModified(TMObjectIF snapshot) {
      events++;
    }
    
    @Override
    public void objectRemoved(TMObjectIF snapshot) {
      events++;
    }
  }
}