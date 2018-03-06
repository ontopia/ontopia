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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * INTERNAL. The purpose of this test class is basically to verify
 * that all entry points in the API work as advertised.
 */
public class CanonicalXTMWriterTest extends AbstractXMLTestCase {
  private TopicMapIF topicmap;

  private final static String testdataDirectory = "cxtm";

  @Override
  public void setUp() throws IOException {
    topicmap = makeEmptyTopicMap();
    String root = TestFileUtils.getTestdataOutputDirectory();
    TestFileUtils.verifyDirectory(root, testdataDirectory, "out");
  }
  
  // --- Test cases

  @Test
  public void testOutputStream() throws IOException {
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "outputstream.cxtm");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "outputstream.cxtm");

    FileOutputStream outs = new FileOutputStream(out);
    new CanonicalXTMWriter(outs).write(topicmap);
    outs.close();

    Assert.assertTrue("OutputStream export gives incorrect output",
               TestFileUtils.compareFileToResource(out, baseline));
  }

  @Test
  public void testWriter() throws IOException {
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", "writer.cxtm");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "writer.cxtm");

    Writer outw = new OutputStreamWriter(new FileOutputStream(out), "utf-8");
    new CanonicalXTMWriter(outw).write(topicmap);
    outw.close();

    Assert.assertTrue("OutputStream export gives incorrect output",
               TestFileUtils.compareFileToResource(out, baseline));
  }

  // --- Utilities

  private TopicMapIF makeEmptyTopicMap() throws IOException {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    store.setBaseAddress(URILocator.create("http://www.ontopia.net"));
    return store.getTopicMap();
  }
}
