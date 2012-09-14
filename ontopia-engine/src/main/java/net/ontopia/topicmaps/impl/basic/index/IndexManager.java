
package net.ontopia.topicmaps.impl.basic.index;

import java.util.Collection;
import java.util.Map;

import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.core.TransactionNotActiveException;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.impl.utils.AbstractIndex;
import net.ontopia.topicmaps.impl.utils.AbstractIndexManager;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.utils.CollectionFactoryIF;
import net.ontopia.utils.OntopiaUnsupportedException;

/**
 * INTERNAL: The basic index manager implementation.</p>
 */
public class IndexManager extends AbstractIndexManager implements java.io.Serializable {

  protected TopicMapTransactionIF transaction;
  protected Map indexes;
  
  public IndexManager(TopicMapTransactionIF transaction, CollectionFactoryIF cfactory, 
		      EventManagerIF emanager, ObjectTreeManager otree) {
    this.transaction = transaction;
    
    // initialize index map
    indexes = cfactory.makeSmallMap();

    // register default indexes
    indexes.put("net.ontopia.topicmaps.core.index.NameIndexIF", new NameIndex(this, emanager, otree));
    indexes.put("net.ontopia.topicmaps.core.index.OccurrenceIndexIF", new OccurrenceIndex(this, emanager, otree));      
    indexes.put("net.ontopia.topicmaps.core.index.ScopeIndexIF", new ScopeIndex(this, emanager, otree)); 
    indexes.put("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF", new ClassInstanceIndex(this, emanager, otree));
    indexes.put("net.ontopia.topicmaps.core.index.StatisticsIndexIF", new StatisticsIndex(this, transaction));
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

  public Collection getSupportedIndexes() {
    return indexes.keySet();
  }

  public Collection getActiveIndexes() {
    return indexes.values();
  }

  public boolean isActive(String name) {
    return indexes.containsKey(name);
  }

  public void registerIndex(String name, AbstractIndex index) {
    indexes.put(name, index);
  }
  
}
