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

import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TransactionNotActiveException;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.utils.CollectionFactoryIF;
import net.ontopia.utils.OntopiaUnsupportedException;

/**
 * INTERNAL: An abstract TopicMapTransactionIF implementation.
 */

public abstract class AbstractTopicMapTransaction implements TopicMapTransactionIF {

  protected boolean active = false;
  protected boolean invalid = false;

  protected AbstractTopicMapStore store;
  protected AbstractTopicMapTransaction parent;
  
  protected TopicMapIF topicmap;
  
  protected TopicMapBuilderIF builder;
  protected CollectionFactoryIF cfactory;
  protected IndexManagerIF imanager;
  
  @Override
  public boolean isActive() {
    // Return flag
    return active;
  }

  @Override
  public TopicMapBuilderIF getBuilder() {
    if (!isActive()) {
      throw new TransactionNotActiveException();
    }
    return builder;
  }

  public CollectionFactoryIF getCollectionFactory() {
    if (!isActive()) {
      throw new TransactionNotActiveException();
    }
    return cfactory;
  }

  @Override
  public IndexManagerIF getIndexManager() {
    if (!isActive()) {
      throw new TransactionNotActiveException();
    }
    return imanager;
  }

  @Override
  public TopicMapIF getTopicMap() {
    if (!isActive()) {
      throw new TransactionNotActiveException();
    }
    return topicmap;
  }

  @Override
  public TopicMapStoreIF getStore() {
    return store;
  }

  public TopicMapTransactionIF getParent() {
    return parent;
  }
  
  @Override
  public void commit() {
    if (!isActive()) {
      throw new TransactionNotActiveException();
    }
    
    // notify topic map reference
    TopicMapReferenceIF ref = store.getReference();
    if (ref instanceof TransactionEventListenerIF) {
      ((TransactionEventListenerIF)ref).transactionCommit(this);
    }
  }
  
  @Override
  public void abort() {
    if (!isActive()) {
      throw new TransactionNotActiveException();
    }
    abort(true);
  }

  public void abort(boolean invalidate) {
    // notify topic map reference
    TopicMapReferenceIF ref = store.getReference();
    if (ref instanceof TransactionEventListenerIF) {
      ((TransactionEventListenerIF)ref).transactionAbort(this);
    }
  }

  public abstract boolean validate();

  public TopicMapTransactionIF createNested() {
    // nested transactions are not supported by default
    throw new OntopiaUnsupportedException("Nested transactions not supported.");
  }

}
