
// $Id: ActionConfigContentHandlerErrorTest.java,v 1.4 2008/03/18 09:10:44 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.utils.test;

import java.io.File;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionConfigurator;
import net.ontopia.utils.OntopiaRuntimeException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ActionConfigContentHandlerErrorTest extends AbstractOntopiaTestCase {
  
  ActionRegistryIF registry;  
  
  public ActionConfigContentHandlerErrorTest(String name) {
    super(name);
  }

  public void testReadIn() {

    // HACK: set level to FATAL    
    Logger log = Logger.getLogger(ActionConfigurator.class.getName());
    Level oldlevel = log.getLevel();
    log.setLevel(Level.FATAL);

    String baseDir = getTestDirectory() + File.separator + "webed";
    String configFile = baseDir + File.separator + "errorActionConfig.xml";
    ActionConfigurator ac = new ActionConfigurator("omnieditor", "/", configFile);
    boolean failOccurred = false;
    try {
      ac.readRegistryConfiguration();
    } catch (OntopiaRuntimeException e) {
      failOccurred = true;
    }

    assertTrue("The config file should not have been readable", failOccurred);

    // HACK: revert to original log level
    log.setLevel(oldlevel);    
  }
  
}
