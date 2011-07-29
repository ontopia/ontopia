
// $Id$

package net.ontopia.topicmaps.utils.ltm;

import java.io.*;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.ltm.*;

import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class LTMTopicMapWriterTest {

  private final static String testdataDirectory = "ltmWriter";

  // --- Test cases

  @Test
  public void testBadId() throws IOException {
    LocatorIF base = new URILocator("http://example.com");
    TopicMapIF tm = new InMemoryTopicMapStore().getTopicMap();
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF topic = builder.makeTopic();
    topic.addItemIdentifier(base.resolveAbsolute("#22"));
    
    String root = TestFileUtils.getTestdataOutputDirectory();
    TestFileUtils.verifyDirectory(root, testdataDirectory);
    String thebase = root + File.separator + testdataDirectory + File.separator;
    TestFileUtils.verifyDirectory(thebase, "out");
    String filename = thebase + File.separator + "out" + File.separator +
      "testBadId.ltm";
    
    FileOutputStream fos = new FileOutputStream(filename);
    new LTMTopicMapWriter(fos).write(tm);
    fos.close();

    tm = new LTMTopicMapReader(new File(filename)).read();
    topic = (TopicIF) tm.getTopics().iterator().next();
    LocatorIF itemid = (LocatorIF) topic.getItemIdentifiers().iterator().next();
    Assert.assertTrue("Bad item ID was not filtered out",
               itemid.getAddress().endsWith("testBadId.ltm#id1"));
  }      
}  
