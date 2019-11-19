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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class ClassInstanceUtilsTest {
  protected TopicMapIF        topicmap; 
  protected LocatorIF         base;

  private final static String testdataDirectory = "various";

  protected void load(String dir, String filename) throws IOException {
    filename = TestFileUtils.getTestInputFile(dir, filename);
    topicmap = ImportExportUtils.getReader(filename).read();
    base = topicmap.getStore().getBaseAddress();
  }

  protected TopicIF getTopicById(String id) {
    return (TopicIF) topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#" + id));
  }
  
  // --- Test cases

  @Test
  public void testGetInstancesOfEmpty() throws IOException {
    load(testdataDirectory, "small-test.ltm");
    Collection instances = ClassInstanceUtils.getInstancesOf(Collections.EMPTY_SET,
                                                             getTopicById("person"));
    Assert.assertTrue("found instances in empty set", instances.isEmpty());
  }

  @Test
  public void testGetInstancesOfTopic() throws IOException {
    load(testdataDirectory, "small-test.ltm");
    Collection instances = ClassInstanceUtils.getInstancesOf(topicmap.getTopics(),
                                                             getTopicById("person"));
    Assert.assertTrue("wrong number of instances", instances.size() == 3);
    Assert.assertTrue("instances does not contain 'lmg'",
               instances.contains(getTopicById("lmg")));
    Assert.assertTrue("instances does not contain 'gra'",
               instances.contains(getTopicById("gra")));
    Assert.assertTrue("instances does not contain 'grove'",
               instances.contains(getTopicById("grove")));
  }
}
