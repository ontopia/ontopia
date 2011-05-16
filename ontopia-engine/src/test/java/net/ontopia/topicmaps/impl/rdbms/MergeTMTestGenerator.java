// $Id: MergeTMTestGenerator.java,v 1.8 2007/05/14 07:22:29 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.utils.FileUtils;

public class MergeTMTestGenerator extends net.ontopia.topicmaps.utils.test.MergeTMTestGenerator {

  protected TestCase createTestCase(String filename, String base) {
    return new MergeTMTestCase(filename, base);
  }
  
  // --- Test case class

  public class MergeTMTestCase extends AbstractCanonicalTestCase {
    private String base;
    private String filename;
        
    public MergeTMTestCase(String filename, String base) {
      super("testMergeTM");
      this.filename = filename;
      this.base = base;
    }

    public void testMergeTM() throws IOException {
      verifyDirectory(base, "out");
      
      // produce canonical output
      String in = base + File.separator + "in" + File.separator + filename;
      String in2 = base + File.separator + "in" + File.separator + 
        filename.substring(0, filename.length() - 3) + "sub";
      String out = base + File.separator + "out" + File.separator + filename;


      // Import first document
      RDBMSTopicMapStore store1 = new RDBMSTopicMapStore();
      TopicMapIF source1;
      long topicmap_id1;
      try {
        source1 = store1.getTopicMap();
        topicmap_id1 = Long.parseLong(source1.getObjectId().substring(1));    
        new XTMTopicMapReader(new File(in)).importInto(source1);
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
        new XTMTopicMapReader(new File(in2)).importInto(source2);
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
      assertTrue("test file " + filename + " canonicalized wrongly",
             FileUtils.compare(out, base + File.separator + "baseline" +
                     File.separator + filename));
    }
  }
}





