
package net.ontopia.topicmaps.impl.utils;

import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

/**
 * PUBLIC: A topic map transaction is used to represent a transaction
 * in a topic map store. This interface can be used to retrieve a
 * topic map from such a repository and to save changes back to the
 * repository.</p>
 * 
 * To make persistent changes in the topic map, use the commit method.
 * To roll back any changes since the transaction started, call the
 * abort method.  (Note that this only works with transactional
 * stores).</p>
 *
 * The transaction is marked as inactive after a commit or an
 * abort. An inactive transaction can in general not be
 * reactivated.</p>
 *
 * @deprecated All the useful methods have been moved to
 * TopicMapStoreIF and TopicMapIF.
 */

public interface TopicMapTransactionIF {

  public static final String EVENT_COMMIT = "TopicMapTransactionIF.commit";
  public static final String EVENT_ABORT = "TopicMapTransactionIF.abort";

  /**
   * PUBLIC: Gets a topic map builder for use with this transaction.
   *
   * @return An object implementing TopicMapBuilderIF
   * @since 1.2.2
   */
  public TopicMapBuilderIF getBuilder();

  /**
   * PUBLIC: Gets the store to which the transaction is connected.</p>
   */
  public TopicMapStoreIF getStore();

  /**
   * PUBLIC: Gets the topic map that is accessible through the
   * transaction.
   *
   * @return The topic map in the transaction; an object implementing
   * TopicMapIF.
   */
  public TopicMapIF getTopicMap();

  /**
   * PUBLIC: Gets the index manager that manages the topic map indexes
   * in the transaction.
   *
   * @return The index manager used by the transaction: an object
   * implementing IndexManagerIF.
   */
  public IndexManagerIF getIndexManager();

  /**
   * PUBLIC: Returns true if the transaction is active (in process). A
   * transaction is started immediately after it has been created, so
   * there is no explicit method to call in order to activate a
   * transaction.</p>
   *
   * @return Boolean: true if active, false if not active (either not
   * yet aborted, or commited).
   */
  public boolean isActive();

  /**
   * PUBLIC: Commits the transaction. The changes made are written to
   * the persistent store.</p>
   *
   * The transaction will resume after the commit meaning that the
   * objects retrieved through is still usable after the commit.
   */
  public void commit();

  /**
   * PUBLIC: Aborts the transaction; all changes made inside the
   * transaction are rolled back.</p>
   *
   * The transaction will resume after the abort meaning that the
   * objects retrieved through is still usable after the abort, but
   * their state has been reverted to the state in the persistent
   * store.
   */
  public void abort();
  
}
