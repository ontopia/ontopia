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

import java.io.IOException;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;

/**
 * INTERNAL: A topic map reference that uses a topic map store factory
 * to create stores. To create a reference for an in-memory topic map
 * wrap it in the SameStoreFactory.
 *
 * @since 1.3.2
 */
public class StoreFactoryReference extends AbstractTopicMapReference {

  protected TopicMapStoreFactoryIF sfactory_rw;
  protected TopicMapStoreFactoryIF sfactory_ro;
  protected boolean deref_on_close = true;
  
  public StoreFactoryReference(String id, String title,
                               TopicMapStoreFactoryIF sfactory) {
    this(id, title, sfactory, sfactory);
    // store factory is used to create both readonly and mutable stores.
  }
  
  public StoreFactoryReference(String id, String title,
                               TopicMapStoreFactoryIF sfactory_rw,
                               TopicMapStoreFactoryIF sfactory_ro) {
    super(id, title);
    this.sfactory_rw = sfactory_rw;
    this.sfactory_ro = sfactory_ro;    
  }

  @Override
  public synchronized TopicMapStoreIF createStore(boolean readonly)
    throws IOException {
    if (!isOpen()) {
      open();
    }

    // create new store
    TopicMapStoreIF store;
    if (readonly) {
      store = sfactory_ro.createStore();
    } else {
      store = sfactory_rw.createStore();
    }
    // register store
    store.setReference(this);
    // register listeners
    ((AbstractTopicMapStore)store).setTopicListeners(getTopicListeners());
    return store;
  }

  /**
   * INTERNAL: Returns the flag that specifies if the reference should
   * deregister itself from the topic map source when the referenced
   * store is being closed. Default: true.<p>
   *
   * Note that this only works when the source is of type
   * DefaultTopicMapSource.<p>
   */
  public boolean getDereferenceOnClose() {
    return deref_on_close;
  }

  /**
   * INTERNAL: Sets the dereference on close flag.
   */
  public void setDereferenceOnClose(boolean deref_on_close) {
    this.deref_on_close = deref_on_close;
  }

  @Override
  public void storeClosed(TopicMapStoreIF store) {
    // when store is closed, we need to deregister with source
    if (deref_on_close && source instanceof DefaultTopicMapSource) {
      // remove self from source
      ((DefaultTopicMapSource)source).removeReference(this);
    }
    // dereference store
    store.setReference(null);
    // dereference listeners
    ((AbstractTopicMapStore)store).setTopicListeners(null);
  }
  
}
