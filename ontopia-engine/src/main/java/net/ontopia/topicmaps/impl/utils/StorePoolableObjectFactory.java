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

package net.ontopia.topicmaps.impl.utils;

import java.util.Collection;
import java.util.HashSet;

import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A commons-pool PoolableObjectFactory that uses a
 * TopicMapStoreFactoryIF to create TopicMapStoreIF objects. This
 * class must be used with a commons-pool pool instance.
 *
 * @since 2.1
 */

public class StorePoolableObjectFactory 
  implements org.apache.commons.pool.PoolableObjectFactory {

  // define a logging category.
  private static final Logger log = LoggerFactory.getLogger(StorePoolableObjectFactory.class.getName());
  
  // topic map store factory
  protected TopicMapStoreFactoryIF sfactory;

  // track all open stores
  public Collection<TopicMapStoreIF> stores = new HashSet<TopicMapStoreIF>();
  
  public StorePoolableObjectFactory(TopicMapStoreFactoryIF sfactory) {
    this.sfactory = sfactory;
  }

  @Override
  public Object makeObject()
    throws Exception {
    // tell store factory to create a new store instance
    TopicMapStoreIF o = sfactory.createStore();
    log.debug("makeObject " + o);
    stores.add(o);
    return o;
  }
  
  @Override
  public void destroyObject(Object o)
    throws Exception {
    log.debug("destroyObject " + o);
    AbstractTopicMapStore s = (AbstractTopicMapStore)o;
    stores.remove(s);
    // close topic map store
    if (s.isOpen()) {
      s.close(false);
    }    
  }

  @Override
  public boolean validateObject(Object o) {
    log.debug("validateObject " + o);
    // ask store to validate itself
    AbstractTopicMapStore store = (AbstractTopicMapStore)o;
    boolean valid = store.validate();
    log.debug("validate: " + valid);
    return valid;
  }

  @Override
  public void activateObject(Object o)
    throws Exception {
    log.debug("activateObject " + o);
  }

  @Override
  public void passivateObject(Object o)
    throws Exception {
    log.debug("passivateObject " + o);
  }

}
