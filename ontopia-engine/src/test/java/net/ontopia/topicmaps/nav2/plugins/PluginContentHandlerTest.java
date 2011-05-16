// $Id: PluginContentHandlerTest.java,v 1.5 2002/09/13 09:58:45 niko Exp $

package net.ontopia.topicmaps.nav2.plugins;

import java.io.*;
import java.util.*;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.nav2.plugins.*;

public class PluginContentHandlerTest extends AbstractOntopiaTestCase {

  private static final String pluginBasePath = "/plugins/";
  
  private Collection plugins;
  
  public PluginContentHandlerTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();
    String rootPath = AbstractOntopiaTestCase.getTestDirectory();
    String pluginPath = rootPath + File.separator + "nav2" +
      File.separator + "plugins" + File.separator + "sample-plugins.xml";
    plugins = PluginConfigFactory.getPlugins(new FileInputStream(pluginPath), pluginPath, pluginBasePath);
  }

  public void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testBasic() {
    assertTrue("Not the number of plug-ins matched in config file (" +
               plugins.size() + ")", plugins.size() == 4);
    Iterator it = plugins.iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      assertTrue("Not an instance of PluginIF: " + obj,
                 obj instanceof PluginIF);
    }
  }

  public void testOntopiaPlugin() {
    PluginIF ontopia_plugin = getPlugin("ontopia");

    PluginIF exp_plugin = new DefaultPlugin();
    exp_plugin.setId("ontopia");
    exp_plugin.setTitle("The Ontopia home page");
    exp_plugin.setDescription("A link to www.ontopia.net");
    exp_plugin.setURI("http://www.ontopia.net");
    
    assertTrue("ontopia plugin is not like expected",
               ontopia_plugin != exp_plugin);
  }
  
  public void testDictPlugin() {
    PluginIF dict_plugin = getPlugin("dict");
    assertTrue("Could not find dict plugin",
               dict_plugin != null);
    assertTrue("dict plugin has wrong title",
               dict_plugin.getTitle().equals("Dictionary"));
    assertTrue("dict plugin has wrong description",
               dict_plugin.getDescription().equals("Online Dictionary Database Query (dict.org)."));
    assertTrue("dict plugin has wrong state",
               dict_plugin.getState() == PluginIF.DEACTIVATED);
    List exp_groups = new ArrayList();
    exp_groups.add("topic");
    exp_groups.add("topicmap");
    assertTrue("dict plugin has wrong groups",
               dict_plugin.getGroups().equals(exp_groups));
    assertTrue("dict plugin has wrong uri",
               dict_plugin.getURI().equals(""));
    assertTrue("dict plugin has wrong target",
               dict_plugin.getTarget() == null);
    int paramLen = dict_plugin.getParameter("text").length();
    assertTrue("dict plugin has wrong text parameter: " + paramLen,
               paramLen == 336);
  }


  private PluginIF getPlugin(String id) {
    Iterator it = plugins.iterator();
    while (it.hasNext()) {
      PluginIF plugin = (PluginIF) it.next();
      if (plugin.getId().equals(id))
        return plugin;
    }
    return null;
  }
  
}
