
// $Id: ActionConfigContentHandlerErrorTest.java,v 1.4 2008/03/18 09:10:44 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.utils.test;

import java.io.File;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionConfigurator;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionConfigContentHandlerErrorTest extends AbstractOntopiaTestCase {
  
  ActionRegistryIF registry;  
  
  public ActionConfigContentHandlerErrorTest(String name) {
    super(name);
  }

  public void testReadIn() {

    String baseDir = getTestDirectory() + File.separator + "webed";
    String configFile = baseDir + File.separator + "errorActionConfig.xml";
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
