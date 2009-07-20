
// $Id$

package net.ontopia.topicmaps.utils.ltm.test;

import java.io.*;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.xml.test.AbstractCanonicalTestCase;
import net.ontopia.topicmaps.utils.ltm.*;

public class LTMTopicMapWriterTest extends AbstractCanonicalTestCase {

  public LTMTopicMapWriterTest(String name) {
    super(name);
  }
  
  // --- Test cases

  public void testBadId() throws IOException {
    LocatorIF base = new URILocator("http://example.com");
    TopicMapIF tm = new InMemoryTopicMapStore().getTopicMap();
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF topic = builder.makeTopic();
    topic.addItemIdentifier(base.resolveAbsolute("#22"));

    String root = getTestDirectory();
    String thebase = root + File.separator + "ltmWriter" + File.separator;
    String filename = thebase + File.separator + "out" + File.separator +
      "testBadId.ltm";
    
    FileOutputStream fos = new FileOutputStream(filename);
    new LTMTopicMapWriter(fos).write(tm);
    fos.close();

    tm = new LTMTopicMapReader(new File(filename)).read();
    topic = (TopicIF) tm.getTopics().iterator().next();
    LocatorIF itemid = (LocatorIF) topic.getItemIdentifiers().iterator().next();
    assertTrue("Bad item ID was not filtered out",
               itemid.getAddress().endsWith("testBadId.ltm#id1"));
  }      
}  
