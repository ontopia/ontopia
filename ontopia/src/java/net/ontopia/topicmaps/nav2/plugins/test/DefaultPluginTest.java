// $Id: DefaultPluginTest.java,v 1.3 2002/05/29 13:38:41 hca Exp $

package net.ontopia.topicmaps.nav2.plugins.test;

import java.util.*;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.nav2.plugins.*;

public class DefaultPluginTest extends AbstractOntopiaTestCase {

  public DefaultPluginTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();
  }

  public void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testBasic() {
    PluginIF p1 = new DefaultPlugin();
    p1.setId("p");
    assertTrue("Plug-in id is not correct.",
               p1.getId().equals("p"));
    p1.setTitle("MyPlugin");
    assertTrue("Plug-in title is not correct.",
               p1.getTitle().equals("MyPlugin"));
    p1.setURI("http://www.plugins.org");
    assertTrue("Plug-in uri is not correct.",
               p1.getURI().equals("http://www.plugins.org"));
  }
  
  public void testEqual() {
    PluginIF p1 = new DefaultPlugin();
    p1.setId("p");
    p1.setTitle("MyPlugin");
    p1.setURI("http://www.plugins.org");
    PluginIF p2 = new DefaultPlugin();
    p2.setId("p");
    p2.setTitle("MyPlugin");
    p2.setURI("http://www.plugins.org");
    assertTrue("Plug-in objects are not equal.",
               p1.equals(p2));
  }
  
  public void testUnEqual() {
    PluginIF p1 = new DefaultPlugin();
    p1.setId("p1");
    p1.setTitle("MyPlugin");
    p1.setURI("http://www.plugins.org");
    PluginIF p2 = new DefaultPlugin();
    p2.setId("p2");
    p2.setTitle("MyPlugin");
    p2.setURI("http://www.plugins.org");
    assertTrue("Plug-in objects are equal.",
               !p1.equals(p2));
  }
  
}





