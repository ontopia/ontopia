/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.utils.ltm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class LTMTopicMapReaderTest {

  private final static String testdataDirectory = "ltm";
    
  // --- Test cases

  @Test
  public void testReadFromURL() throws IOException {
    TopicMapIF tm = read("tmreify.ltm");
    Assert.assertNotNull(tm);
    Assert.assertEquals(2, tm.getTopics().size());
  }

  @Test
  public void testReadFromFile() throws IOException {
    File in = TestFileUtils.getTransferredTestInputFile(testdataDirectory, "extra", "tmreify.ltm");
    TopicMapIF tm = new LTMTopicMapReader(in).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(2, tm.getTopics().size());
  }

  @Test
  public void testReadFromInputStream() throws IOException {
    File in = TestFileUtils.getTransferredTestInputFile(testdataDirectory, "extra", "tmreify.ltm");
    TopicMapIF tm = new LTMTopicMapReader(new FileInputStream(in), new URILocator(in)).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(2, tm.getTopics().size());
  }

  @Test
  public void testReadFromReader() throws IOException {
    File in = TestFileUtils.getTransferredTestInputFile(testdataDirectory, "extra", "tmreify.ltm");
    TopicMapIF tm = new LTMTopicMapReader(new FileReader(in), new URILocator(in)).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(2, tm.getTopics().size());
  }

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
    // test for issue 143: https://github.com/ontopia/ontopia/issues/143
    try {
      read("mergemap-error.ltm");
      Assert.fail("No error found!");
    } catch (IOException e) {
      String msg = e.getMessage();
      Assert.assertTrue("error message does not mention file containing error: " + msg,
                 msg.indexOf("mergemap-error-sub.ltm") != -1);
    }
  }
  
  // --- Helpers

  public TopicMapIF read(String file) throws IOException {
    return new LTMTopicMapReader(
        TestFileUtils.getTestInputURL(testdataDirectory, "extra", file)
    ).read();
  }
}  
