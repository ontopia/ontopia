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

package net.ontopia.topicmaps.nav2.utils;

import java.io.IOException;
import java.util.Collection;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class NavigatorConfigurationTest {
  
  private final static String testdataDirectory = "nav2";

  NavigatorConfigurationIF navConf;

  @Before  
  public void setUp() throws IOException, SAXException {
    String configFile = TestFileUtils.getTestInputFile(testdataDirectory, "WEB-INF", "config", "application.xml");
    // read in configuration file and generate object
    navConf = NavigatorConfigFactory.getConfiguration(StreamUtils.getInputStream(configFile));    
  }
  
  @Test
  public void testProperties() {
    Assert.assertEquals("string did not match (1)",
                navConf.getProperty("msg.UntypedAssoc"), "untyped");
    Assert.assertEquals("string did not match (2)",
                navConf.getProperty("baseNameContextDecider"), "intersection");
  }
  
  @Test
  public void testAutoloadTopicMaps() {
    Collection retr = navConf.getAutoloadTopicMaps();
    Assert.assertEquals("autoload topicmaps collection wrong in size",
                 retr.size(), 1);
    Assert.assertTrue("autoload topicmaps does not contain expected tm",
               retr.contains("opera.xtm"));
  }
  
  @Test
  public void testDefaultMVS() {
    Assert.assertEquals("default view not correct",
                 navConf.getDefaultView(), "");
    Assert.assertEquals("default model not correct",
                 navConf.getDefaultModel(), "");
    Assert.assertEquals("default skin not correct",
                 navConf.getDefaultSkin(), "");
  }
  
  @Test
  public void testClassMap() {
    Assert.assertEquals("fqcn could not be found (1)",
                 navConf.getClass("topicComparator"), 
                 "net.ontopia.topicmaps.nav.utils.comparators.TopicComparator");
    Assert.assertEquals("fqcn could not be found (2)",
                 navConf.getClass("topicMapRefComparator"), 
                 "net.ontopia.topicmaps.nav.utils.comparators.TopicMapReferenceComparator");    
  }
  
}
