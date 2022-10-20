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

package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Collection;
import java.util.Map;

import net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapTransaction;
import net.ontopia.topicmaps.core.TransactionNotActiveException;
import net.ontopia.topicmaps.core.index.IdentifierIndexIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.impl.utils.AbstractIndex;
import net.ontopia.topicmaps.impl.utils.AbstractIndexManager;
import net.ontopia.utils.CollectionFactoryIF;
import net.ontopia.utils.OntopiaUnsupportedException;

/**
 * INTERNAL: The rdbms index manager.</p>
 */
public class IndexManager extends AbstractIndexManager {
  protected transient TopicMapTransactionIF transaction;
  protected Map<String, IndexIF> indexes;

  public IndexManager(TopicMapTransactionIF transaction,
                      CollectionFactoryIF cfactory) {
    this.transaction = transaction;
      
    // initialize index map
    indexes = cfactory.makeSmallMap();

    // register default indexes
    indexes.put("net.ontopia.topicmaps.core.index.NameIndexIF",
                new NameIndex(this));
    indexes.put("net.ontopia.topicmaps.core.index.OccurrenceIndexIF",
                new OccurrenceIndex(this));
    indexes.put("net.ontopia.topicmaps.core.index.ScopeIndexIF",
                new ScopeIndex(this));
    indexes.put("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF",
                new ClassInstanceIndex(this));
    indexes.put("net.ontopia.topicmaps.core.index.StatisticsIndexIF",
                new StatisticsIndex(this));
    indexes.put("net.ontopia.infoset.fulltext.core.SearcherIF",
                new RDBMSSearcher((RDBMSTopicMapTransaction)transaction));
    indexes.put(IdentifierIndexIF.class.getName(),
                new IdentifierIndex(this));
  }

  @Override
  public TopicMapTransactionIF getTransaction() {
    return transaction;
  }
  
  @Override
  public IndexIF getIndex(String name) {
    // Check to see if transaction is active.
    if (!transaction.isActive()) {
      throw new TransactionNotActiveException("Transaction to which the index manager belongs is not active.");
    }

    // Create index
    IndexIF ix = indexes.get(name);
    if (ix == null) {
      // Throw unsupported exception if index is unsupported.
      throw new OntopiaUnsupportedException("Unknown index: " + name);
    }
    if (ix instanceof AbstractIndex) {
      return ((AbstractIndex) ix).getIndex();
    } else {
      return ix;
    }
  }

  @Override
  public Collection<String> getSupportedIndexes() {
    return indexes.keySet();
  }

  @Override
  public Collection<IndexIF> getActiveIndexes() {
    return indexes.values();
  }

  @Override
  public boolean isActive(String name) {
    return indexes.containsKey(name);
  }

  @Override
  public void registerIndex(String name, IndexIF index) {
    indexes.put(name, index);
  }
  
}
