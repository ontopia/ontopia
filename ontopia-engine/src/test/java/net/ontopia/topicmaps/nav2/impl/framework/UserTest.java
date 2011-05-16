
// $Id: UserTest.java,v 1.7 2003/08/28 13:27:30 larsga Exp $

package net.ontopia.topicmaps.nav2.impl.framework;

import java.io.IOException;
import java.io.File;
import java.util.*;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.impl.framework.User;
import net.ontopia.topicmaps.nav2.utils.NavigatorConfigFactory;

public class UserTest extends AbstractOntopiaTestCase {

  UserIF user;
  
  public UserTest(String name) {
    super(name);
  }
  
  public void setUp() throws IOException, org.xml.sax.SAXException {
    String root = getTestDirectory();
    String baseDir = root + File.separator + "nav2" + File.separator +
                     "WEB-INF" + File.separator + "config";
    File configFile = new File(baseDir, "application.xml"); 
    // read in configuration file and generate object
    NavigatorConfigurationIF navConf = 
      NavigatorConfigFactory.getConfiguration(configFile);    
    user = new User("niko", navConf);
  }

  public void testId() {
    assertEquals("id is not correct.", user.getId(), "niko");
  }

  public void testMVS() {
    assertEquals("model name is not correct.",
                 UserIF.DEFAULT_MODEL, user.getModel());
    assertEquals("view name is not correct.",
                 UserIF.DEFAULT_VIEW, user.getView());
    assertEquals("skin name is not correct.",
                 UserIF.DEFAULT_SKIN, user.getSkin());
  }

  public void testLogMessage() {
    user.addLogMessage("log");
    List log = user.getLogMessages();
    assertTrue("log does not have a single message",
               log.size() == 1);
    assertTrue("log message is not 'log': " + log,
               log.get(0).equals("log"));
  }


  public void testClearLog() {
    user.addLogMessage("log");

    List log = user.getLogMessages();

    user.clearLog();
    
    assertTrue("retrieved log does not have a single message",
               log.size() == 1);
    assertTrue("retrieved log message is not 'log': " + log,
               log.get(0).equals("log"));

    log = user.getLogMessages();

    assertTrue("cleared log is not empty",
               log.isEmpty());
    
  }
  
}
