
// $Id: RemoteTopicMapStore.java,v 1.8 2008/06/11 16:55:59 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.remote;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.utils.tmrap.*;
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
  
  public TopicMapTransactionIF getTransaction() {
    // Open store automagically if store is not open at this point.
    if (!isOpen()) open();
    
    // Create a new transaction if it doesn't exist or it has been
    // deactivated.
    if (transaction == null || !transaction.isActive())
      transaction = new RemoteTopicMapTransaction(this);
    return transaction;
  }
  
  public RemoteTopicIndex getTopicIndex() {
    return remoteIndex;  
  }
}
