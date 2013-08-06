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

package net.ontopia.topicmaps.entry;

import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import junit.framework.TestCase;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

public class DefaultTopicMapSourceTest extends TestCase {
  TopicMapStoreIF store;
  DefaultTopicMapSource source;

  public DefaultTopicMapSourceTest(String name) {
    super(name);
  }

  public void setUp() {
    store = new InMemoryTopicMapStore();
    source = new DefaultTopicMapSource();
  }
  
  // --- Test cases

  public void testReferences() {
    assertTrue("source not empty by default",
           source.getReferences().size() == 0);

    TopicMapReferenceIF ref =
      new DefaultTopicMapReference("id", "title", store);
    source.addReference(ref);

    assertTrue("source not registered with reference",
               ref.getSource() == source);

    assertTrue("source did not discover add",
           source.getReferences().size() == 1);
    assertTrue("reference identity lost",
           source.getReferences().iterator().next() == ref);

    source.removeReference(ref);
    
    assertTrue("source not deregistered with reference",
               ref.getSource() == null);
    
  }
}






