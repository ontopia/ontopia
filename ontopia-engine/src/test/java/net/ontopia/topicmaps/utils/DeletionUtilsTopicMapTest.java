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

package net.ontopia.topicmaps.utils;

import java.util.List;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DeletionUtilsTopicMapTest {

  private final static String testdataDirectory = "canonical";

  private String filename;

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "in", ".ltm|.xtm");
  }

  public DeletionUtilsTopicMapTest(String root, String filename) {
    this.filename = filename;
  }
    
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    return store.getTopicMap();
  }

  // --- Test cases

  @Test
  public void testTopicMapDeletion() throws Exception {
    String name = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
    TopicMapIF tm = makeTopicMap();
    TopicMapReaderIF importer = ImportExportUtils.getReader(name);
    if (name.endsWith(".xtm")) {
      ((XTMTopicMapReader) importer).setValidation(false);
    }
    try {
      importer.importInto(tm);
    } catch (OntopiaRuntimeException ore) {
      // catch and re-throw to add filename to message
      Assert.fail(ore.getMessage() + " in " + name);
    }
    assertClearTopicMap(tm);
    tm.getStore().close();
  }

  // --- Helper methods
  
  private void assertClearTopicMap(TopicMapIF tm) throws Exception {

    // Remove all the objects from the topic map
    tm.clear();

    Assert.assertTrue("Not all topics was deleted", tm.getTopics().isEmpty());
    Assert.assertTrue("Not all associations was deleted", tm.getAssociations().isEmpty());
  }
  
}
