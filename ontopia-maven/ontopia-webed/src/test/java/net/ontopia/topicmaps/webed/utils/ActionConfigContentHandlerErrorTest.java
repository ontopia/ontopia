
// $Id: ActionConfigContentHandlerErrorTest.java,v 1.4 2008/03/18 09:10:44 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.utils;

import java.io.File;

import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionConfigurator;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.FileUtils;
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

    String configFile = FileUtils.getTestInputFile(testdataDirectory, "errorActionConfig.xml");
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
