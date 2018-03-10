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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LTMTopicMapWriterTest {

  private final static String testdataDirectory = "ltmWriter";
  private TopicMapBuilderIF builder;
  private TopicMapIF tm;

  // --- Test cases
  
  @Before
  public void setUp() {
    tm = new InMemoryTopicMapStore().getTopicMap();
    builder = tm.getBuilder();
  }

  @Test
  public void testBadId() throws IOException {
    LocatorIF base = URILocator.create("http://example.com");
    TopicIF topic = builder.makeTopic();
    topic.addItemIdentifier(base.resolveAbsolute("#22"));
    
    String root = TestFileUtils.getTestdataOutputDirectory();
    TestFileUtils.verifyDirectory(root, testdataDirectory);
    String thebase = root + File.separator + testdataDirectory + File.separator;
    TestFileUtils.verifyDirectory(thebase, "out");
    File file = new File(thebase + File.separator + "out" + File.separator +
      "testBadId.ltm");
    
    new LTMTopicMapWriter(file).write(tm);

    tm = new LTMTopicMapReader(file).read();
    topic = (TopicIF) tm.getTopics().iterator().next();
    LocatorIF itemid = (LocatorIF) topic.getItemIdentifiers().iterator().next();
    Assert.assertTrue("Bad item ID was not filtered out",
               itemid.getAddress().endsWith("testBadId.ltm#id1"));
  }      

  @Test
  public void testWriteToFile() throws IOException {
    builder.makeTopic();
    File file = TestFileUtils.getTestOutputFile("ltm", "io-f.ltm");
    new LTMTopicMapWriter(file).write(tm);
    Assert.assertTrue(Files.size(file.toPath()) > 0);
  }

  @Test
  public void testWriteToOutputStream() throws IOException {
    builder.makeTopic();
    File file = TestFileUtils.getTestOutputFile("ltm", "io-o.ltm");
    new LTMTopicMapWriter(new FileOutputStream(file)).write(tm);
    Assert.assertTrue(Files.size(file.toPath()) > 0);
  }

  @Test
  public void testWriteToWriter() throws IOException {
    builder.makeTopic();
    File file = TestFileUtils.getTestOutputFile("ltm", "io-w.ltm");
    new LTMTopicMapWriter(new FileWriter(file), "utf-8").write(tm);
    Assert.assertTrue(Files.size(file.toPath()) > 0);
  }
}  
