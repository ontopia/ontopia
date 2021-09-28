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

package net.ontopia.topicmaps.utils;

import junit.framework.TestCase;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.core.Locators;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;

public class IdentityUtilsTest extends TestCase {
  protected TopicMapIF    topicmap; 
  protected TopicMapBuilderIF builder;

  public IdentityUtilsTest(String name) {
    super(name);
  }
    
  public void setUp() {
    topicmap = makeTopicMap();
    builder = topicmap.getBuilder();
  }
  
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    store.setBaseAddress(Locators.getURILocator("http://example.org/base/"));
    return store.getTopicMap();
  }

  public void testGetObjectBySymbolicId() {
    String symbolicId = "foo";
    TopicIF topic = builder.makeTopic();
    LocatorIF base = topicmap.getStore().getBaseAddress();
    LocatorIF loc = base.resolveAbsolute("#" + symbolicId);
    topic.addItemIdentifier(loc);

    TMObjectIF topic2 = IdentityUtils.getObjectBySymbolicId(topicmap, symbolicId);
    assertEquals("Topic not found by symbolic id", topic, topic2);    
  }

  public void testGetSymbolicIdLocator() {
    String symbolicId = "foo";
    LocatorIF base = topicmap.getStore().getBaseAddress();
    LocatorIF loc = base.resolveAbsolute("#" + symbolicId);
    LocatorIF loc2 = IdentityUtils.getSymbolicIdLocator(topicmap, symbolicId);
    assertEquals("Symbolic locators not equal", loc, loc2);
  }
  
}
