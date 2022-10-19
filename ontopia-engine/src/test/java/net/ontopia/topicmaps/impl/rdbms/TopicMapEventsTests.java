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
package net.ontopia.topicmaps.impl.rdbms;

import java.io.IOException;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.events.AbstractTopicMapListener;
import net.ontopia.topicmaps.core.events.TopicMapEvents;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if the modifications to the TopicEvents class result in correct behavior of the
 * TopicMapEvents.
 */
public class TopicMapEventsTests extends AbstractTopicMapTest {

  private int added = 0;

  @Override
  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    added = 0;
  }

  @Test
  public void testTransactionCommit() throws IOException {
    TopicMapEvents.addTopicListener(topicmapRef, new AbstractTopicMapListener() {

      @Override
      public void objectAdded(TMObjectIF snapshot) {
        added++;
      }
      
    });
    
    TopicMapStoreIF store = null;
    try {
      store = topicmapRef.createStore(false);
      store.getTopicMap().getBuilder().makeTopic();

      // before commit: 0 calls should have been made to objectAdded
      Assert.assertEquals(0, added);

      store.commit();
      
      // after commit: 1 call should have been made to objectAdded
      Assert.assertEquals(1, added);
      
    } finally {
      store.close();
    }
  }
  
  @Test
  public void testTransactionAbort() throws IOException {
    TopicMapEvents.addTopicListener(topicmapRef, new AbstractTopicMapListener() {

      @Override
      public void objectAdded(TMObjectIF snapshot) {
        added++;
      }
      
    });
    
    TopicMapStoreIF store = null;
    try {
      store = topicmapRef.createStore(false);
      store.getTopicMap().getBuilder().makeTopic();
      
      // before commit: 0 calls should have been made to objectAdded
      Assert.assertEquals(0, added);

      store.abort();
      
      // after abort: 0 calls should have been made to objectAdded
      Assert.assertEquals(0, added);
      
    } finally {
      store.close();
    }
  }
}
