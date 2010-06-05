
// $Id: IndexManager.java,v 1.29 2008/06/11 17:14:48 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapTransaction;
import net.ontopia.topicmaps.core.TransactionNotActiveException;
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
    indexes.put("net.ontopia.infoset.fulltext.core.SearcherIF",
                new RDBMSSearcher((RDBMSTopicMapTransaction)transaction));
  }

  public TopicMapTransactionIF getTransaction() {
    return transaction;
  }
  
  public IndexIF getIndex(String name) {
    // Check to see if transaction is active.
    if (!transaction.isActive())
      throw new TransactionNotActiveException("Transaction to which the index manager belongs is not active.");

    // Create index
    AbstractIndex ix = (AbstractIndex)indexes.get(name);
    if (ix == null)
      // Throw unsupported exception if index is unsupported.
      throw new OntopiaUnsupportedException("Unknown index: " + name);
    return ix.getIndex();
  }

  public Collection<String> getSupportedIndexes() {
    return indexes.keySet();
  }

  public Collection<IndexIF> getActiveIndexes() {
    return indexes.values();
  }

  public boolean isActive(String name) {
    return indexes.containsKey(name);
  }

  public void registerIndex(String name, AbstractIndex index) {
    indexes.put(name, index);
  }
  
}
