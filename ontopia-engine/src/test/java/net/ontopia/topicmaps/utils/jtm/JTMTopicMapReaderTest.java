/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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

package net.ontopia.topicmaps.utils.jtm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class JTMTopicMapReaderTest {

  private final File file;
  private final URILocator locator;

  public JTMTopicMapReaderTest() throws IOException {
    file = TestFileUtils.getTransferredTestInputFile("jtm", "in", "topic.xtm2.jtm");
    locator = new URILocator(file);
  }

  @Test
  public void testReadFromURL() throws IOException {
    TopicMapIF tm = new JTMTopicMapReader(TestFileUtils.getTestInputURL("jtm", "in", "topic.xtm2.jtm")).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(1, tm.getTopics().size());
  }

  @Test
  public void testReadFromFile() throws IOException {
    TopicMapIF tm = new JTMTopicMapReader(file).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(1, tm.getTopics().size());
  }

  @Test
  public void testReadFromInputStream() throws IOException {
    TopicMapIF tm = new JTMTopicMapReader(new FileInputStream(file), locator).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(1, tm.getTopics().size());
  }

  @Test
  public void testReadFromReader() throws IOException {
    TopicMapIF tm = new JTMTopicMapReader(new FileReader(file), locator).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(1, tm.getTopics().size());
  }
}
