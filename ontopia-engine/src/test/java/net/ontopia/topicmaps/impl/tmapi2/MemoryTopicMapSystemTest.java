/*
 * #!
 * Ontopia Engine
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
package net.ontopia.topicmaps.impl.tmapi2;

import junit.framework.TestCase;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

public class MemoryTopicMapSystemTest 
  extends TestCase {

  private TopicMapSystemFactory tmsf;
  private TopicMapSystem tms;

  private Locator locFirst; 

  public MemoryTopicMapSystemTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    tmsf = TopicMapSystemFactory.newInstance();
    tms = tmsf.newTopicMapSystem();
    
    locFirst = tms.createLocator("http://ontopia.net/first");
  }

  @Override
  protected void tearDown() throws Exception {
    tms.close();
  }

  public void testCreate() {
    try {
      TopicMap tm = tms.createTopicMap(locFirst);
      assertNotNull("could not create new TopicMap", tm);
      
    } catch (TopicMapExistsException e) {
      fail("failed to create new TopicMap in empty TopicMapSystem");
    }
  }
  
  public void testGet() {
    TopicMap tm = tms.getTopicMap(locFirst);
    assertNull("found a TopicMap in an empty TopicMapSystem", tm);
    
    try {
      tm = tms.createTopicMap(locFirst);
      assertNotNull("could not create new TopicMap", tm);
      
    } catch (TopicMapExistsException e) {
      fail("failed to create new TopicMap in empty TopicMapSystem");
    }

    TopicMap tm2 = tms.getTopicMap(locFirst);
    assertNotNull("could not get newly created TopicMap", tm2);
  }
  
  public void testDelete() {
    TopicMap tm;
    
    try {
      tm = tms.createTopicMap(locFirst);
      assertNotNull("could not create new TopicMap", tm);
      
    } catch (TopicMapExistsException e) {
      fail("failed to create new TopicMap in empty TopicMapSystem");
    }

    TopicMap tm2 = tms.getTopicMap(locFirst);
    assertNotNull("could not get newly created TopicMap", tm2);
    
    tm2.remove();
    
    tm = tms.getTopicMap(locFirst);
    assertNull("TopicMap has not been removed from TopicMapSystem after remove operation", tm);
  }
}
