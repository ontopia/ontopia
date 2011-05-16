// $Id: IndexManagerIF.java,v 1.1 2008/06/12 14:37:16 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.utils;

import java.util.Collection;
import net.ontopia.topicmaps.core.index.IndexIF;

/**
 * DEPRECATED: Interface implemented by objects that manage one or
 * more indexes on a topic map.<p>
 * 
 * Indexes are referred to by name, where the name is the full class
 * name of the interface of the index that is wanted,
 * e.g. <tt>"net.ontopia.topicmaps.core.index.ClassInstanceIndexIF"</tt>. This
 * is what is meant by an "index name" below.  There is only one index
 * with each given name.<p>
 *
 * @deprecated Use the TopicMapIF.getIndex(String) method instead.
 */

public interface IndexManagerIF {

  /**
   * DEPRECATED: Gets the topic map store to which this index manager
   * belongs.
   *
   * @return The topic map store; an object implementing TopicMapStoreIF.
   */
  public TopicMapTransactionIF getTransaction();

  /**

   * DEPRECATED: Gets an index by name. An index is usually named by
   * the IndexIF subinterface that it implements.<p>
   *
   * If no such index is currently active it is created and populated
   * automatically when this method is called. In the case when the
   * index implementation is not dynamic it may take a while for the
   * index to populate itself, depending on the size of the topic
   * map.<p>
   *
   * @exception OntopiaUnsupportedException Thrown if the index is either
   *                       unknown or not supported by the index manager.
   
   * @param name A string; the index name, usually the IndexIF
   * subinterface that it implements.
   */
  public IndexIF getIndex(String name);
  
  /**
   * DEPRECATED: Returns the names of the indexes that this index
   * manager supports.
   * @return A collection of strings which are index names.
   */
  public Collection<String> getSupportedIndexes();

  /**
   * DEPRECATED: Returns true if the index is active. An active index
   * is an index that has been loaded and populated. Note that the
   * index need not be up to date.
   *
   * @param name A string which is an index name.
   *
   * @return Boolean: true if the given index is populated, otherwise false.
   */
  public boolean isActive(String name);

  /**
   * DEPRECATED: Returns all the active indexes. An active index is an
   * index that has been loaded and populated.
   *
   * @return A collection of IndexIF objects.
   */
  public Collection<IndexIF> getActiveIndexes();

}





