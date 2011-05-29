
package net.ontopia.topicmaps.nav2.utils;

import java.io.IOException;
import java.io.File;
import java.util.Hashtable;
import java.util.Collection;
import org.xml.sax.SAXException;

import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorConfigFactory;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.StreamUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NavigatorConfigurationTest {
  
  private final static String testdataDirectory = "nav2";

  NavigatorConfigurationIF navConf;

  @Before  
  public void setUp() throws IOException, SAXException {
    String configFile = FileUtils.getTestInputFile(testdataDirectory, "WEB-INF", "config", "application.xml");
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
