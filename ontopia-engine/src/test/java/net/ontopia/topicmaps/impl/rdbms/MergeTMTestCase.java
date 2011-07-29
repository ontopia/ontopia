
package net.ontopia.topicmaps.impl.rdbms;

import java.io.*;
import junit.framework.TestCase;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.Assert;
import org.junit.BeforeClass;

public class MergeTMTestCase extends net.ontopia.topicmaps.utils.MergeTMTestCase {

  private final static String testdataDirectory = "merge";

  @BeforeClass
  public static void checkDatabasePresence() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
  }

  // --- Test case class

    public MergeTMTestCase(String root, String filename) {
      super(root, filename);
    }

    public void testMergeTM() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");
      
      // produce canonical output
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
      String in2 = TestFileUtils.getTestInputFile(testdataDirectory, "in", 
        filename.substring(0, filename.length() - 3) + "sub");
      String out = base + File.separator + "out" + File.separator + filename;
      String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", filename);


      // Import first document
      RDBMSTopicMapStore store1 = new RDBMSTopicMapStore();
      TopicMapIF source1;
      long topicmap_id1;
      try {
        source1 = store1.getTopicMap();
        topicmap_id1 = Long.parseLong(source1.getObjectId().substring(1));    
        new XTMTopicMapReader(URIUtils.getURI(in)).importInto(source1);
        store1.commit();
      } finally {
        store1.close();
      }
      
      // Import second document
      RDBMSTopicMapStore store2 = new RDBMSTopicMapStore();
      TopicMapIF source2;
      long topicmap_id2;
      try {
        source2 = store2.getTopicMap();
        topicmap_id2 = Long.parseLong(source2.getObjectId().substring(1));    
        new XTMTopicMapReader(URIUtils.getURI(in2)).importInto(source2);
        store2.commit();
      } finally {
        store2.close();
      }

      // Reopen stores
      store1 = new RDBMSTopicMapStore(topicmap_id1);
      source1 = store1.getTopicMap();
      store2 = new RDBMSTopicMapStore(topicmap_id2);
      store2.setReadOnly(true); // mark as readonly to prevent sql server locking issue
      source2 = store2.getTopicMap();

      // Merge the two topic maps
      MergeUtils.mergeInto(source1, source2);
      store1.commit();
      store1.close();
      store2.commit();
      
      store2 = new RDBMSTopicMapStore(topicmap_id2);
      store2.delete(true);
      //! store2.close();

      // Reopen the merged topic map
      store1 = new RDBMSTopicMapStore(topicmap_id1);
      source1 = store1.getTopicMap();

      // Canonicalize
      new CanonicalTopicMapWriter(out).write(source1);
      store1.delete(true);
      //! store1.close();

      // compare results
      Assert.assertTrue("test file " + filename + " canonicalized wrongly",
             FileUtils.compareFileToResource(out, baseline));
    }
}





