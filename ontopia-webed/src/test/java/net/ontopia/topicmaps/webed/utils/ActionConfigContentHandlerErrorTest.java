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

import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionConfigurator;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionConfigContentHandlerErrorTest extends TestCase {
  
  ActionRegistryIF registry;  

  private final static String testdataDirectory = "webed";
  
  public ActionConfigContentHandlerErrorTest(String name) {
    super(name);
  }

  public void testReadIn() {

    String configFile = TestFileUtils.getTestInputFile(testdataDirectory, "errorActionConfig.xml");
    ActionConfigurator ac = new ActionConfigurator("omnieditor", "/", configFile);
    ac.logErrors(false); // disable error logging while running test
    boolean failOccurred = false;
    try {
      ac.readRegistryConfiguration();
    } catch (OntopiaRuntimeException e) {
      failOccurred = true;
    }

    assertTrue("The config file should not have been readable", failOccurred);

  }
  
}
