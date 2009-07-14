
// $Id: ActionConfigContentHandlerTest.java,v 1.31 2008/03/18 09:10:44 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.utils.test;

import java.io.File;
import java.io.IOException;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.webed.impl.basic.ActionForwardPage;
import net.ontopia.topicmaps.webed.impl.basic.ActionForwardPageIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionGroupIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.basic.FieldInformationIF;
import net.ontopia.topicmaps.webed.impl.basic.ImageInformation;
import net.ontopia.topicmaps.webed.impl.basic.ImageInformationIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionConfigurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class ActionConfigContentHandlerTest extends AbstractOntopiaTestCase {
  
  // initialization of log facility
  private static Logger log = LoggerFactory
    .getLogger(ActionConfigContentHandlerTest.class.getName());

  ActionRegistryIF registry;  
  
  public ActionConfigContentHandlerTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();
    if (registry == null) {
      String baseDir = getTestDirectory() + File.separator + "webed";
      String configFile = baseDir + File.separator + "actionConfig.xml";
      ActionConfigurator ac = new ActionConfigurator("/omnieditor", "/", configFile);
      ac.readRegistryConfiguration();
      registry = ac.getRegistry();
    }
  }

  public void testReadIn() throws SAXException, IOException {
    assertTrue("Could not read in XML specification of action configuration",
               registry != null);
    // log.debug( registry );
  }

  public void testImageInformation() {
    // retrieve instance from configuration
    ImageInformationIF image_retr = registry.getImage("entry_ok");
    
    // construct fresh instance
    ImageInformationIF image_cons =
      new ImageInformation("entry_ok",
                           "/" + "omnieditor" +
                           "/" + "images" +
                           "/" + "entry_ok.gif", "20", "20", null, null);
    
    // compare them
    assertTrue("Expected image info: " + image_cons +
               ", but got from config file: " + image_retr,
               image_retr.equals(image_cons));
  }
  
  public void testFieldInformation() {
    // retrieve instance from configuration
    FieldInformationIF field = registry.getField("tfm");

    assertEquals("Field name not as expected", field.getName(), "tfm");
    assertEquals("Field type not as expected", field.getType(), "text");
    assertEquals("Field length not as expected", field.getMaxLength(), "255");
    assertEquals("Field columns not as expected", field.getColumns(), "50");
    assertEquals("Field rows not as expected", field.getRows(), "1");
  }
  

  public void testDefaultForwardPage() {
    ActionGroupIF actions = registry.getActionGroup("topicEditNames");
    ActionForwardPageIF fp = new ActionForwardPage("/omnieditor/topic_edit.jsp", "edit");
    assertEquals(fp, actions.getDefaultForwardPage(Constants.FORWARD_GENERIC));
  }
  
  public void testLockedForwardPage() {
    ActionGroupIF actions = registry.getActionGroup("topicEditNames");
    ActionForwardPageIF fp = actions.getLockedForwardPage();
    assertTrue("No forward page found!", fp != null);
    assertTrue("Wrong forward URL", fp.getURL().equals("/omnieditor/locked.jsp"));
    assertTrue("Wrong frame name: " + fp.getFramename(),
               fp.getFramename().equals("search"));
    assertTrue("Didn't have empty parameter map",
               fp.getParameters().size() == 0);
   
  }
  
}
