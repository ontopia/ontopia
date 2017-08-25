/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.utils;

import java.io.IOException;
import junit.framework.TestCase;
import net.ontopia.topicmaps.webed.impl.basic.ActionForwardPage;
import net.ontopia.topicmaps.webed.impl.basic.ActionForwardPageIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionGroupIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.basic.FieldInformationIF;
import net.ontopia.topicmaps.webed.impl.basic.ImageInformation;
import net.ontopia.topicmaps.webed.impl.basic.ImageInformationIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionConfigurator;
import net.ontopia.utils.TestFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class ActionConfigContentHandlerTest extends TestCase {
  
  // initialization of log facility
  private static Logger log = LoggerFactory
    .getLogger(ActionConfigContentHandlerTest.class.getName());

  ActionRegistryIF registry;  

  private final static String testdataDirectory = "webed";
  
  public ActionConfigContentHandlerTest(String name) {
    super(name);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    if (registry == null) {
      String configFile = TestFileUtils.getTestInputFile(testdataDirectory, "actionConfig.xml");
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
