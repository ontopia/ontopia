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

public class DefaultTopicMapReferenceTest extends TestCase {
  private TopicMapStoreIF store;
  private DefaultTopicMapSource source;
  private TopicMapReferenceIF reference;

  public DefaultTopicMapReferenceTest(String name) {
    super(name);
  }

  @Override
  public void setUp() {
    store = new InMemoryTopicMapStore();
    source = new DefaultTopicMapSource();
    reference = new DefaultTopicMapReference("id", "title", store);
    source.addReference(reference);
  }
  
  // --- Test cases

  public void testId() {
    assertTrue("default id not set", reference.getId().equals("id"));
    reference.setId("newid");
    assertTrue("id not set correctly", reference.getId().equals("newid"));
  }

  public void testTitle() {
    assertTrue("default title not set", reference.getTitle().equals("title"));
    reference.setTitle("newtitle");
    assertTrue("title not set correctly", reference.getTitle().equals("newtitle"));
  }

  public void testStore() throws java.io.IOException {
    assertTrue("default store not set [mutable]", reference.createStore(false) == store);
    assertTrue("default store not set [readonly]", reference.createStore(true) == store);
  }
}






