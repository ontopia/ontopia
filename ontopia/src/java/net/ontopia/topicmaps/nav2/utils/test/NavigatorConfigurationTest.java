// $Id: NavigatorConfigurationTest.java,v 1.4 2002/09/13 09:58:45 niko Exp $

package net.ontopia.topicmaps.nav2.utils.test;

import java.io.IOException;
import java.io.File;
import java.util.Hashtable;
import java.util.Collection;
import org.xml.sax.SAXException;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorConfigFactory;

public class NavigatorConfigurationTest extends AbstractOntopiaTestCase {
  
  NavigatorConfigurationIF navConf;
  
  public NavigatorConfigurationTest(String name) {
    super(name);
  }

  public void setUp() throws IOException, SAXException {
    String root = getTestDirectory();
    String baseDir = root + File.separator + "nav2" +
                     File.separator + "WEB-INF" + File.separator + "config";
    File configFile = new File(baseDir, "application.xml"); 
    // read in configuration file and generate object
    navConf = NavigatorConfigFactory.getConfiguration(configFile);    
  }
  
  public void testProperties() {
    assertEquals("string did not match (1)",
                navConf.getProperty("msg.UntypedAssoc"), "untyped");
    assertEquals("string did not match (2)",
                navConf.getProperty("baseNameContextDecider"), "intersection");
  }
  
  public void testAutoloadTopicMaps() {
    Collection retr = navConf.getAutoloadTopicMaps();
    assertEquals("autoload topicmaps collection wrong in size",
                 retr.size(), 1);
    assertTrue("autoload topicmaps does not contain expected tm",
               retr.contains("opera.xtm"));
  }
  
  public void testDefaultMVS() {
    assertEquals("default view not correct",
                 navConf.getDefaultView(), "");
    assertEquals("default model not correct",
                 navConf.getDefaultModel(), "");
    assertEquals("default skin not correct",
                 navConf.getDefaultSkin(), "");
  }
  
  public void testClassMap() {
    assertEquals("fqcn could not be found (1)",
                 navConf.getClass("topicComparator"), 
                 "net.ontopia.topicmaps.nav.utils.comparators.TopicComparator");
    assertEquals("fqcn could not be found (2)",
                 navConf.getClass("topicMapRefComparator"), 
                 "net.ontopia.topicmaps.nav.utils.comparators.TopicMapReferenceComparator");    
  }
  
}
