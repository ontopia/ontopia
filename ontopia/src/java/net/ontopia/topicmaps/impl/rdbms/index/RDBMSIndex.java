// $Id: RDBMSIndex.java,v 1.3 2008/06/11 17:14:48 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms.index;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapTransaction;
import net.ontopia.topicmaps.impl.utils.AbstractIndex;

/**
 * INTERNAL: An abstract super class used by the rdbms indexes.
 */

public abstract class RDBMSIndex extends AbstractIndex implements IndexIF {

  IndexManagerIF imanager;
  RDBMSTopicMapTransaction transaction;
  TopicMapIF topicmap;
  
  public RDBMSIndex(IndexManagerIF imanager) {
    this.imanager = imanager;
    transaction = (RDBMSTopicMapTransaction)imanager.getTransaction();
  }

  public IndexIF getIndex() {
    return this;
  }
  
  // -----------------------------------------------------------------------------
  // Query
  // -----------------------------------------------------------------------------

  TopicMapIF getTopicMap() {
    return transaction.getTopicMap();
  }

  protected Object executeQuery(String name, Object[] params) {
    return transaction.getTransaction().executeQuery(name, params);
  }
  
}





