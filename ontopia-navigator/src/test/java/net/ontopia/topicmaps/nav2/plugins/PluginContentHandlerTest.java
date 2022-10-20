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

package net.ontopia.topicmaps.nav2.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.StreamUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PluginContentHandlerTest {

  private final static String testdataDirectory = "nav2";
  private static final String pluginBasePath = "/plugins/";
  
  private Collection plugins;

  @Before  
  public void setUp() throws Exception {
    String pluginPath = TestFileUtils.getTestInputFile(testdataDirectory, "plugins", "sample-plugins.xml");
    plugins = PluginConfigFactory.getPlugins(StreamUtils.getInputStream(pluginPath), pluginPath, pluginBasePath);
  }

  @Test
  public void testBasic() {
    Assert.assertTrue("Not the number of plug-ins matched in config file (" +
               plugins.size() + ")", plugins.size() == 4);
    Iterator it = plugins.iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      Assert.assertTrue("Not an instance of PluginIF: " + obj,
                 obj instanceof PluginIF);
    }
  }

  @Test
  public void testOntopiaPlugin() {
    PluginIF ontopia_plugin = getPlugin("ontopia");

    PluginIF exp_plugin = new DefaultPlugin();
    exp_plugin.setId("ontopia");
    exp_plugin.setTitle("The Ontopia home page");
    exp_plugin.setDescription("A link to www.ontopia.net");
    exp_plugin.setURI("http://www.ontopia.net");
    
    Assert.assertTrue("ontopia plugin is not like expected",
               ontopia_plugin != exp_plugin);
  }
  
  @Test
  public void testDictPlugin() {
    PluginIF dict_plugin = getPlugin("dict");
    Assert.assertTrue("Could not find dict plugin",
               dict_plugin != null);
    Assert.assertTrue("dict plugin has wrong title",
               dict_plugin.getTitle().equals("Dictionary"));
    Assert.assertTrue("dict plugin has wrong description",
               dict_plugin.getDescription().equals("Online Dictionary Database Query (dict.org)."));
    Assert.assertTrue("dict plugin has wrong state",
               dict_plugin.getState() == PluginIF.DEACTIVATED);
    List exp_groups = new ArrayList();
    exp_groups.add("topic");
    exp_groups.add("topicmap");
    Assert.assertTrue("dict plugin has wrong groups",
               dict_plugin.getGroups().equals(exp_groups));
    Assert.assertTrue("dict plugin has wrong uri",
               dict_plugin.getURI().equals(""));
    Assert.assertTrue("dict plugin has wrong target",
               dict_plugin.getTarget() == null);
    int paramLen = dict_plugin.getParameter("text").length();
    Assert.assertTrue("dict plugin has wrong text parameter: " + paramLen,
               paramLen == 336);
  }


  private PluginIF getPlugin(String id) {
    Iterator it = plugins.iterator();
    while (it.hasNext()) {
      PluginIF plugin = (PluginIF) it.next();
      if (plugin.getId().equals(id)) {
        return plugin;
      }
    }
    return null;
  }
  
}
