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

package net.ontopia.topicmaps.core;

import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public abstract class AbstractTopicMapTest {

  protected TestFactoryIF factory;
  protected TopicMapReferenceIF topicmapRef;
  protected TopicMapIF topicmap;       // topic map of object being tested
  protected TopicMapBuilderIF builder; // builder used for creating new objects

  protected abstract TestFactoryIF getFactory() throws Exception;
  
  @Before
  public void setUp() throws Exception {
    factory = getFactory();
    // Get a new topic map object from the factory.
    topicmapRef = factory.makeTopicMapReference();
    topicmap = topicmapRef.createStore(false).getTopicMap();
    Assert.assertTrue("Null topic map!" , topicmap != null);
    // Get the builder of that topic map.
    builder = topicmap.getBuilder();
    Assert.assertTrue("Null builder!", builder != null);
  }

  @After
  public void tearDown() {
    if (topicmapRef != null) {
      // Inform the factory that the topic map is not needed anymore.
      topicmap.getStore().close();
      factory.releaseTopicMapReference(topicmapRef);
      // Reset the member variables.
      topicmapRef = null;
      topicmap = null;
      builder = null;
    }
  }
}
