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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.basic.index.IndexManager;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapTransaction;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.topicmaps.impl.utils.TopicModificationManager;
import net.ontopia.utils.OntopiaUnsupportedException;
import net.ontopia.utils.SynchronizedCollectionFactory;

/**
 * INTERNAL: The in-memory transaction implementation.
 */

public class InMemoryTopicMapTransaction extends AbstractTopicMapTransaction {
  
  protected ObjectTreeManager otree;
  protected TopicModificationManager topicmods;
  protected TopicEvents te;

  protected InMemoryTopicMapTransaction(InMemoryTopicMapStore store) {
    this(store, null);
  }
  
  protected InMemoryTopicMapTransaction(InMemoryTopicMapStore store, InMemoryTopicMapTransaction parent) {

    // Activate transaction (note: must be activated at this point, because of dependencies)
    this.active = true;
    
    this.store = store;
    this.parent = parent;
    
    // Initialize collection factory
    this.cfactory = new SynchronizedCollectionFactory();

    // Create a new topic map using the factory
    this.topicmap = new TopicMap(this);
    EventManagerIF emanager = (EventManagerIF)topicmap;
    
    // Initialize topic map builder
    this.builder = new TopicMapBuilder((TopicMap)topicmap);
    
    // Register object tree event listener with store event manager
    this.otree = new ObjectTreeManager(emanager, cfactory);
    this.topicmods = new TopicModificationManager(emanager, cfactory);
    this.te = new TopicEvents(store);
    this.te.registerListeners(emanager);
    this.topicmods.addListener(this.te, TopicIF.EVENT_MODIFIED);
    
    // Register a subject identity cache object with the topic map
    SubjectIdentityCache sicache = new SubjectIdentityCache(this, cfactory);
    sicache.registerListeners(emanager, otree);
    ((TopicMap)topicmap).setSubjectIdentityCache(sicache);

    // Create new index manager
    this.imanager = new IndexManager(this, cfactory, emanager, otree, sicache);
  }

  @Override
  public boolean validate() {
    return !invalid;
  }

  @Override
  public TopicMapTransactionIF createNested() {
    // Nested transactions are not supported
    throw new OntopiaUnsupportedException("Nested transactions not supported.");
  }

  public ObjectTreeManager getObjectTreeManager() {
    return otree;
  }
  
}
