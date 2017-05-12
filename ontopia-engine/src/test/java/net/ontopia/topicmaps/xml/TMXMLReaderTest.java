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

package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

public class TMXMLReaderTest extends AbstractXMLTestCase {

  private final static String testdataDirectory = "canonical";

  public void setUp() {
  }

  // --- Utilities

  protected TopicMapIF readTopicMap(String filename) throws IOException {
    filename = TestFileUtils.getTestInputFile(testdataDirectory, "extra", filename);
    TMXMLReader reader = new TMXMLReader(TestFileUtils.getTestInputURL(filename));
    return reader.read();
  }
    
  // --- Test cases

  @Test
  public void testReifiedTopicMap() throws IOException {
    TopicMapIF tm = readTopicMap("reified-tm.xml");
    TopicIF reifier = tm.getReifier();
    Assert.assertTrue("Reification was not preserved", reifier != null);
  }    
  
  @Test
  public void testReadFromURL() throws IOException {
    TopicMapIF tm = new TMXMLReader(TestFileUtils.getTestInputURL("tmxml", "in", "empty-topic.xml")).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(2, tm.getTopics().size());
    
  }

  @Test
  public void testReadFromFile() throws IOException {
    TopicMapIF tm = new TMXMLReader(TestFileUtils.getTransferredTestInputFile("tmxml", "in", "empty-topic.xml")).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(2, tm.getTopics().size());
  }

  @Test
  public void testReadFromInputStream() throws IOException {
    File file = TestFileUtils.getTransferredTestInputFile("tmxml", "in", "empty-topic.xml");
    TopicMapIF tm = new TMXMLReader(new FileInputStream(file), new URILocator(file)).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(2, tm.getTopics().size());
  }

  @Test
  public void testReadFromReader() throws IOException {
    File file = TestFileUtils.getTransferredTestInputFile("tmxml", "in", "empty-topic.xml");
    TopicMapIF tm = new TMXMLReader(new FileReader(file), new URILocator(file)).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(2, tm.getTopics().size());
  }

  @Test
  public void testReadFromInputSource() throws IOException {
    File file = TestFileUtils.getTransferredTestInputFile("tmxml", "in", "empty-topic.xml");
    TopicMapIF tm = new TMXMLReader(new InputSource(new FileReader(file)), new URILocator(file)).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(2, tm.getTopics().size());
  }
}
