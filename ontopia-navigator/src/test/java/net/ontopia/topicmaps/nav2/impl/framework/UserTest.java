/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.impl.framework;

import java.io.IOException;
import java.util.List;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorConfigFactory;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.StreamUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

  private final static String testdataDirectory = "nav2";

  private UserIF user;
  
  @Before
  public void setUp() throws IOException, org.xml.sax.SAXException {
    String configFile = TestFileUtils.getTestInputFile(testdataDirectory, "WEB-INF", "config", "application.xml");
    // read in configuration file and generate object
    NavigatorConfigurationIF navConf = 
      NavigatorConfigFactory.getConfiguration(StreamUtils.getInputStream(configFile));    
    user = new User("niko", navConf);
  }

  @Test
  public void testId() {
    Assert.assertEquals("id is not correct.", user.getId(), "niko");
  }

  @Test
  public void testMVS() {
    Assert.assertEquals("model name is not correct.",
                 UserIF.DEFAULT_MODEL, user.getModel());
    Assert.assertEquals("view name is not correct.",
                 UserIF.DEFAULT_VIEW, user.getView());
    Assert.assertEquals("skin name is not correct.",
                 UserIF.DEFAULT_SKIN, user.getSkin());
  }

  @Test
  public void testLogMessage() {
    user.addLogMessage("log");
    List log = user.getLogMessages();
    Assert.assertTrue("log does not have a single message",
               log.size() == 1);
    Assert.assertTrue("log message is not 'log': " + log,
               log.get(0).equals("log"));
  }


  @Test
  public void testClearLog() {
    user.addLogMessage("log");

    List log = user.getLogMessages();

    user.clearLog();
    
    Assert.assertTrue("retrieved log does not have a single message",
               log.size() == 1);
    Assert.assertTrue("retrieved log message is not 'log': " + log,
               log.get(0).equals("log"));

    log = user.getLogMessages();

    Assert.assertTrue("cleared log is not empty",
               log.isEmpty());
    
  }
  
}
