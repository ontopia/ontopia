
// $Id: AbstractTopicMapTransaction.java,v 1.15 2008/06/11 16:55:59 geir.gronmo Exp $

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
  
  public AbstractTopicMapTransaction() {
  }

  public boolean isActive() {
    // Return flag
    return active;
  }

  public TopicMapBuilderIF getBuilder() {
    if (!isActive()) throw new TransactionNotActiveException("Transaction is not active.");
    return builder;
  }

  public CollectionFactoryIF getCollectionFactory() {
    if (!isActive()) throw new TransactionNotActiveException("Transaction is not active.");
    return cfactory;
  }

  public IndexManagerIF getIndexManager() {
    if (!isActive()) throw new TransactionNotActiveException("Transaction is not active.");
    return imanager;
  }

  public TopicMapIF getTopicMap() {
    if (!isActive()) throw new TransactionNotActiveException("Transaction is not active.");
    return topicmap;
  }

  public TopicMapStoreIF getStore() {
    return store;
  }

  public TopicMapTransactionIF getParent() {
    return parent;
  }
  
  public void commit() {
    if (!isActive()) throw new TransactionNotActiveException("Transaction is not active.");
    
    // notify topic map reference
    TopicMapReferenceIF ref = store.getReference();
    if (ref instanceof TransactionEventListenerIF)
      ((TransactionEventListenerIF)ref).transactionCommit(this);
  }
  
  public void abort() {
    if (!isActive()) throw new TransactionNotActiveException("Transaction is not active.");
    abort(true);
  }

  public void abort(boolean invalidate) {
    // notify topic map reference
    TopicMapReferenceIF ref = store.getReference();
    if (ref instanceof TransactionEventListenerIF)
      ((TransactionEventListenerIF)ref).transactionAbort(this);
  }

  public abstract boolean validate();

  public TopicMapTransactionIF createNested() {
    // nested transactions are not supported by default
    throw new OntopiaUnsupportedException("Nested transactions not supported.");
  }

}
