/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.utils;

import java.io.File;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.webed.impl.actions.basename.AddBasename;
import net.ontopia.topicmaps.webed.impl.utils.ActionUtils;
import net.ontopia.topicmaps.webed.impl.utils.ActionConfigurator;
import net.ontopia.utils.TestFileUtils;
import junit.framework.TestCase;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionInGroup;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;

public class ActionUtilsTest extends TestCase {

  ActionRegistryIF registry;
  TopicMapIF topicmap;
  TopicMapBuilderIF builder;

  private final static String testdataDirectory = "webed";
  
  public ActionUtilsTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();
    // only read in registry once (we are not modifying it)
    if (registry == null) {
      File realFile = TestFileUtils.getTransferredTestInputFile(testdataDirectory, "actionConfig.xml");
      ActionConfigurator ac = new ActionConfigurator("omnieditor", realFile.getParent(), "actionConfig.xml");
      ac.readAndWatchRegistry();
      registry = ac.getRegistry();
    }
    topicmap = makeTopicMap();
  }

  
  public void testGetActionAvail() {
    ActionInGroup action = ActionUtils.getAction(registry, "topicEditNames",
                                            "addBasename");

    assertEquals("Retrieved action had wrong name",
                 action.getName(), "addBasename");
    assertTrue("Retrieved action of wrong class",
               action.getAction() instanceof AddBasename);
  }
  
  public void testGetActionFailWrongName() {
    // -- wrong name
    ActionInGroup retrAction = ActionUtils.getAction(registry, "topicEditNames",
                                                     "non-existing");
    ActionInGroup expAction = null;
    assertEquals("Action should not exist.", expAction, retrAction);
  }
  
  public void testGetActionFailWrongGroup() {
    // -- wrong group
    try {
      ActionInGroup retrAction = ActionUtils.getAction(registry, "non-existing",
                                                  "addBasename");
      fail("It should have been signaled that no group is available.");
    } catch (OntopiaRuntimeException e) {
      assertTrue("Fine.", true);
    }
  }
  
  // ---------- internal helper methods
  
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    return store.getTopicMap();
  }
 
}
