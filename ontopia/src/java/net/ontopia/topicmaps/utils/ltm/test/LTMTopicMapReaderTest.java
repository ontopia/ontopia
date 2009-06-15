
// $Id: LTMTopicMapReaderTest.java,v 1.15 2008/06/13 08:36:29 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.ltm.test;

import java.io.File;
import java.io.IOException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;
import net.ontopia.topicmaps.utils.ltm.*;

public class LTMTopicMapReaderTest extends AbstractTopicMapTestCase {

  public LTMTopicMapReaderTest(String name) {
    super(name);
  }
    
  public void setUp() {
  }
  
  // --- Test cases

  public void testReifiedTopicMap() throws IOException {
    TopicMapIF tm = read("tmreify.ltm");
    LocatorIF loc = tm.getStore().getBaseAddress().
                    resolveAbsolute("#example");

    assertTrue("Topic map does not have correct source locator",
           tm.getItemIdentifiers().contains(loc));

    TopicIF reifier = tm.getTopicBySubjectIdentifier(loc);
    assertTrue("No topic reifying topic map",
           reifier != null);
  }

  public void testMergedInReifiedTopicMap() throws IOException {
    TopicMapIF tm = read("tmreify-mergemap.ltm");
    assertTrue("Source locator of merged-in TM applied to master TM",
               tm.getItemIdentifiers().isEmpty());
  }

  public void testMergedInReifiedTopicMapWithBaseURI() throws IOException {
    TopicMapIF tm = read("baseuri-reifytm.ltm");
    assertNull("Internal subject indicator ref affected by base URI",
							 tm.getReifier());
  }

  public void testSourceLocatorForId() throws IOException {
    TopicMapIF tm = read("tmreify.ltm");
    LocatorIF base = tm.getStore().getBaseAddress();
    
    LocatorIF tmtopic = base.resolveAbsolute("#tm-topic");
    LocatorIF example = base.resolveAbsolute("#example");

    assertNotNull("Can't find topic with ID 'tm-topic'",
                  tm.getObjectByItemIdentifier(tmtopic));
    assertNotNull("Can't find topic map with ID 'example'",
                  tm.getObjectByItemIdentifier(example));
    assertNotNull("Can't find topic with subject indicator '#example'",
                  tm.getTopicBySubjectIdentifier(example));
  }
  
  // --- Helpers

  public TopicMapIF read(String file) throws IOException {
    file = resolveFileName("ltm" + File.separator + "extra" + File.separator +
                           file);

    return new LTMTopicMapReader(new File(file)).read();
  }
}  
