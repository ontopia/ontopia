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

/**
 * This class tests a TopicMapStoreIF implementation.
 */

public abstract class TopicMapStoreTest extends AbstractTopicMapTest {
  
  public TopicMapStoreTest(String name) {
    super(name);
  }

  /**
   * Tests that close and open update the status of the store appropriately.
   */
  public void testOpenClose() {
    TopicMapStoreIF _store = factory.makeStandaloneTopicMapStore();
    try {
      assertTrue("Store open", !_store.isOpen());
      _store.open();
      assertTrue("Store not open", _store.isOpen());
      TopicMapIF tm1 = _store.getTopicMap();
      _store.close();
      assertTrue("Store not closed", !_store.isOpen());
      try {
        TopicMapIF tm = _store.getTopicMap();
        // Expected.
        _store.close();
      } catch (StoreNotOpenException ex) {
        fail("Couldn't retrieve topic map [via TopicMapStoreIF.getTopicMap()] from a closed store!");
      }
      _store.open();
      assertTrue("Could not reopen store.", _store.isOpen());
    } finally {
      _store.delete(true);
    }
  }
}
  





