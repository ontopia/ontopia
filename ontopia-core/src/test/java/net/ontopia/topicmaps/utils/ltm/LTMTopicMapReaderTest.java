
// $Id: LTMTopicMapReaderTest.java,v 1.15 2008/06/13 08:36:29 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.ltm;

import java.io.File;
import java.io.IOException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;

import net.ontopia.utils.FileUtils;
import net.ontopia.utils.URIUtils;

import org.junit.Assert;
import org.junit.Test;

public class LTMTopicMapReaderTest {

  private final static String testdataDirectory = "ltm";
    
  // --- Test cases

  @Test
  public void testReifiedTopicMap() throws IOException {
    TopicMapIF tm = read("tmreify.ltm");
    LocatorIF loc = tm.getStore().getBaseAddress().
                    resolveAbsolute("#example");

    Assert.assertTrue("Topic map does not have correct source locator",
           tm.getItemIdentifiers().contains(loc));

    TopicIF reifier = tm.getTopicBySubjectIdentifier(loc);
    Assert.assertTrue("No topic reifying topic map",
           reifier != null);
  }

  @Test
  public void testMergedInReifiedTopicMap() throws IOException {
    TopicMapIF tm = read("tmreify-mergemap.ltm");
    Assert.assertTrue("Source locator of merged-in TM applied to master TM",
               tm.getItemIdentifiers().isEmpty());
  }

  @Test
  public void testMergedInReifiedTopicMapWithBaseURI() throws IOException {
    TopicMapIF tm = read("baseuri-reifytm.ltm");
    Assert.assertNull("Internal subject indicator ref affected by base URI",
							 tm.getReifier());
  }

  @Test
  public void testSourceLocatorForId() throws IOException {
    TopicMapIF tm = read("tmreify.ltm");
    LocatorIF base = tm.getStore().getBaseAddress();
    
    LocatorIF tmtopic = base.resolveAbsolute("#tm-topic");
    LocatorIF example = base.resolveAbsolute("#example");

    Assert.assertNotNull("Can't find topic with ID 'tm-topic'",
                  tm.getObjectByItemIdentifier(tmtopic));
    Assert.assertNotNull("Can't find topic map with ID 'example'",
                  tm.getObjectByItemIdentifier(example));
    Assert.assertNotNull("Can't find topic with subject indicator '#example'",
                  tm.getTopicBySubjectIdentifier(example));
  }

  @Test
  public void testSubfileErrorReporting() throws IOException {
    // test for issue 143: http://code.google.com/p/ontopia/issues/detail?id=143
    try {
      TopicMapIF tm = read("mergemap-error.ltm");
      Assert.fail("No error found!");
    } catch (IOException e) {
      String msg = e.getMessage();
      Assert.assertTrue("error message does not mention file containing error: " + msg,
                 msg.indexOf("mergemap-error-sub.ltm") != -1);
    }
  }
  
  // --- Helpers

  public TopicMapIF read(String file) throws IOException {
    file = FileUtils.getTestInputFile(testdataDirectory, "extra", 
                           file);

    return new LTMTopicMapReader(URIUtils.getURI(file)).read();
  }
}  
