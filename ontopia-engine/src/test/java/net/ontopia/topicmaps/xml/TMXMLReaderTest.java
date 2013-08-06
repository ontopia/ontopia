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

import java.io.IOException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.Assert;
import org.junit.Test;

public class TMXMLReaderTest extends AbstractXMLTestCase {

  private final static String testdataDirectory = "canonical";

  public void setUp() {
  }

  // --- Utilities

  protected TopicMapIF readTopicMap(String filename) throws IOException {
    filename = TestFileUtils.getTestInputFile(testdataDirectory, "extra", filename);
    TMXMLReader reader = new TMXMLReader(URIUtils.getURI(filename));
    return reader.read();
  }
    
  // --- Test cases

  @Test
  public void testReifiedTopicMap() throws IOException {
    TopicMapIF tm = readTopicMap("reified-tm.xml");
    TopicIF reifier = tm.getReifier();
    Assert.assertTrue("Reification was not preserved", reifier != null);
  }    
}
