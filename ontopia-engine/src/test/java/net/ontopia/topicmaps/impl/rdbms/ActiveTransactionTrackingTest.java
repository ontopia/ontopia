/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ActiveTransactionTrackingTest extends AbstractTopicMapTest {

  @Before
  @Override
  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
    super.setUp();
  }

  @Override
  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

  @Test
  public void testActiveTransactionTracking() throws Exception {

    RDBMSStorage storage = ((RDBMSTopicMapStore)topicmap.getStore()).storage;

    // should be 1 because setUp() opens 1 rw-store
    Assert.assertEquals("Incorrect starting count", 1, storage.getActiveTransactionCount());

    // should remain 1 on a RO store
    TopicMapStoreIF roStore = topicmapRef.createStore(true);
    Assert.assertEquals("Incorrect count after ro-store", 1, storage.getActiveTransactionCount());

    // should remain 1 on closing of RO store
    roStore.close();
    Assert.assertEquals("Incorrect count after closing ro-store", 1, storage.getActiveTransactionCount());

    // reopen to avoid non-store-pool exception
    roStore.open();

    // should be 2 on a RW store
    TopicMapStoreIF rwStore1 = topicmapRef.createStore(false);
    rwStore1.open();
    TransactionIF trans = ((RDBMSTopicMapStore)rwStore1).getTransactionIF();
    Assert.assertEquals("Incorrect count after rw-store", 2, storage.getActiveTransactionCount());
    
    // should be 1 after closing of RW store
    rwStore1.close();
    trans.close(); // store close keeps transaction open, and returns to pool
    Assert.assertEquals("Incorrect count after closing rw-store", 1, storage.getActiveTransactionCount());
  }
}
