/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.impl.remote;

import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.utils.tmrap.RemoteTopicIndex;
import net.ontopia.topicmaps.utils.SameStoreFactory;

/**
 * INTERNAL: The remote (remote in the sense that it deals with remote
 * topics - and not that the transactions work in a remote or
 * distributed fashion) extension of AbstractTopicMapStore.
 */
public class RemoteTopicMapStore extends InMemoryTopicMapStore {
  private RemoteTopicIndex remoteIndex;
  
  public RemoteTopicMapStore(String baseuri) {
    super();    
    remoteIndex = new RemoteTopicIndex(null, baseuri, new SameStoreFactory(this));  
  }

  public RemoteTopicMapStore(String baseuri, String tmid) {
    super();    
    remoteIndex = new RemoteTopicIndex(null, baseuri, new SameStoreFactory(this),
                                       tmid);  
  }
  
  @Override
  public TopicMapTransactionIF getTransaction() {
    // Open store automagically if store is not open at this point.
    if (!isOpen()) {
      open();
    }
    
    // Create a new transaction if it doesn't exist or it has been
    // deactivated.
    if (transaction == null || !transaction.isActive()) {
      transaction = new RemoteTopicMapTransaction(this);
    }
    return transaction;
  }
  
  public RemoteTopicIndex getTopicIndex() {
    return remoteIndex;  
  }
}
