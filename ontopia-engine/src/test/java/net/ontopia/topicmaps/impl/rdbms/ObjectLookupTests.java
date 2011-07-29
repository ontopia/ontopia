
package net.ontopia.topicmaps.impl.rdbms;

import java.io.File;
import java.io.IOException;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import junit.framework.TestCase;

/**
 * INTERNAL: Tests that verify that LocatorIF lookups work correctly
 * in a committed and reopened topic map store.
 */

public class ObjectLookupTests extends TestCase {
  
  private final static String testdataDirectory = "various";

  public ObjectLookupTests(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
    super.setUp();
  }

  public void testLookups() throws IOException {
    
    String file = TestFileUtils.getTestInputFile(testdataDirectory, "topicmap-object-lookup.xtm");
    LocatorIF base = URIUtils.getURI(file);

    // Load topic map, commit and close
    RDBMSTopicMapStore store = new RDBMSTopicMapStore();
    TopicMapIF tm = store.getTopicMap();
    TopicMapImporterIF importer = new XTMTopicMapReader(URIUtils.getURI(file));
    importer.importInto(tm);
    long topicmap_id = Long.parseLong(tm.getObjectId().substring(1));    
    store.commit();
    store.close();
    
    // Reopen store
    store = new RDBMSTopicMapStore(topicmap_id);
    tm = store.getTopicMap();

    // Test TopicMapIF.getTopicBySubjectIdentifier
    TopicIF topic = tm.getTopicBySubjectIdentifier(new URILocator("http://test.ontopia.net/indicator1"));
    assertTrue("topic not found by indicator [1]", topic != null);
    topic = tm.getTopicBySubjectIdentifier(new URILocator("http://test.ontopia.net/indicator2"));
    assertTrue("topic not found by indicator [2]", topic != null);
    
    // Test TopicMapIF.getObjectByItemIdentifier
    TMObjectIF tmobject = tm.getObjectByItemIdentifier(base.resolveAbsolute("#topicA"));
    assertTrue("tmobject not found by source locator [A]", tmobject != null);
    tmobject = tm.getObjectByItemIdentifier(base.resolveAbsolute("#topicB"));
    assertTrue("tmobject not found by source locator [B]", tmobject != null);
    tmobject = tm.getObjectByItemIdentifier(base.resolveAbsolute("#topicC"));
    assertTrue("tmobject not found by source locator [C]", tmobject != null);

    // Test TopicMapIF.getTopicBySubject
    topic = tm.getTopicBySubjectLocator(new URILocator("http://test.ontopia.net/subject"));
    assertTrue("topic not found by subject", topic != null);

    store.delete(true);
    //! store.close();    
  }
  
}





