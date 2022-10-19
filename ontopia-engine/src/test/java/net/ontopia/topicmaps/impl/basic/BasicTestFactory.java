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

package net.ontopia.topicmaps.impl.basic;

import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.StoreFactoryReference;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

public class BasicTestFactory implements TestFactoryIF {

  public TestFactoryIF getFactory() {
    return this;
  }

  @Override
  public TopicMapStoreIF makeStandaloneTopicMapStore() {
    return new InMemoryTopicMapStore();
  }

  @Override
  public TopicMapReferenceIF makeTopicMapReference() {
    //! // Create new store
    //! InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    //! // Return topic map object
    //! return store.getTopicMap();

    return new StoreFactoryReference("basic", "Basic Implementation",
                                     new TopicMapStoreFactoryIF() {
      @Override
                                       public TopicMapStoreIF createStore() {
                                         return new InMemoryTopicMapStore();
                                       }
                                     });
  }

  @Override
  public void releaseTopicMapReference(TopicMapReferenceIF topicmapRef) {
    topicmapRef.close();
    //! TopicMapStoreIF store = topicmap.getStore();
    //! store.close();
  }
  
}
