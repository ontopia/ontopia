/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.plugins;

import org.junit.Assert;
import org.junit.Test;

public class DefaultPluginTest {

  @Test
  public void testBasic() {
    PluginIF p1 = new DefaultPlugin();
    p1.setId("p");
    Assert.assertTrue("Plug-in id is not correct.",
               p1.getId().equals("p"));
    p1.setTitle("MyPlugin");
    Assert.assertTrue("Plug-in title is not correct.",
               p1.getTitle().equals("MyPlugin"));
    p1.setURI("http://www.plugins.org");
    Assert.assertTrue("Plug-in uri is not correct.",
               p1.getURI().equals("http://www.plugins.org"));
  }
  
  @Test
  public void testEqual() {
    PluginIF p1 = new DefaultPlugin();
    p1.setId("p");
    p1.setTitle("MyPlugin");
    p1.setURI("http://www.plugins.org");
    PluginIF p2 = new DefaultPlugin();
    p2.setId("p");
    p2.setTitle("MyPlugin");
    p2.setURI("http://www.plugins.org");
    Assert.assertTrue("Plug-in objects are not equal.",
               p1.equals(p2));
  }
  
  @Test
  public void testUnEqual() {
    PluginIF p1 = new DefaultPlugin();
    p1.setId("p1");
    p1.setTitle("MyPlugin");
    p1.setURI("http://www.plugins.org");
    PluginIF p2 = new DefaultPlugin();
    p2.setId("p2");
    p2.setTitle("MyPlugin");
    p2.setURI("http://www.plugins.org");
    Assert.assertTrue("Plug-in objects are equal.",
               !p1.equals(p2));
  }
  
}





