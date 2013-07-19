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

import java.util.*;
import java.io.*;
import junit.framework.TestCase;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;

public class ClassInstanceUtilsTest extends TestCase {
  protected TopicMapIF        topicmap; 
  protected LocatorIF         base;

  private final static String testdataDirectory = "various";

  public ClassInstanceUtilsTest(String name) {
    super(name);
  }

  protected void load(String dir, String filename) throws IOException {
    filename = TestFileUtils.getTestInputFile(dir, filename);
    topicmap = ImportExportUtils.getReader(filename).read();
    base = topicmap.getStore().getBaseAddress();
  }

  protected TopicIF getTopicById(String id) {
    return (TopicIF) topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#" + id));
  }
  
  // --- Test cases

  public void testGetInstancesOfEmpty() throws IOException {
    load(testdataDirectory, "small-test.ltm");
    Collection instances = ClassInstanceUtils.getInstancesOf(Collections.EMPTY_SET,
                                                             getTopicById("person"));
    assertTrue("found instances in empty set", instances.isEmpty());
  }

  public void testGetInstancesOfTopic() throws IOException {
    load(testdataDirectory, "small-test.ltm");
    Collection instances = ClassInstanceUtils.getInstancesOf(topicmap.getTopics(),
                                                             getTopicById("person"));
    assertTrue("wrong number of instances", instances.size() == 3);
    assertTrue("instances does not contain 'lmg'",
               instances.contains(getTopicById("lmg")));
    assertTrue("instances does not contain 'gra'",
               instances.contains(getTopicById("gra")));
    assertTrue("instances does not contain 'grove'",
               instances.contains(getTopicById("grove")));
  }
}
