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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IdentityUtilsTest {
  protected TopicMapIF    topicmap; 
  protected TopicMapBuilderIF builder;

  @Before
  public void setUp() {
    topicmap = makeTopicMap();
    builder = topicmap.getBuilder();
  }
  
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    store.setBaseAddress(URILocator.create("http://example.org/base/"));
    return store.getTopicMap();
  }

  @Test
  public void testGetObjectBySymbolicId() {
    String symbolicId = "foo";
    TopicIF topic = builder.makeTopic();
    LocatorIF base = topicmap.getStore().getBaseAddress();
    LocatorIF loc = base.resolveAbsolute("#" + symbolicId);
    topic.addItemIdentifier(loc);

    TMObjectIF topic2 = IdentityUtils.getObjectBySymbolicId(topicmap, symbolicId);
    Assert.assertEquals("Topic not found by symbolic id", topic, topic2);    
  }

  @Test
  public void testGetSymbolicIdLocator() {
    String symbolicId = "foo";
    LocatorIF base = topicmap.getStore().getBaseAddress();
    LocatorIF loc = base.resolveAbsolute("#" + symbolicId);
    LocatorIF loc2 = IdentityUtils.getSymbolicIdLocator(topicmap, symbolicId);
    Assert.assertEquals("Symbolic locators not equal", loc, loc2);
  }
  
}
