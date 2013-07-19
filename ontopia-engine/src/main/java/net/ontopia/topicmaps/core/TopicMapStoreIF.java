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

package net.ontopia.topicmaps.core;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

/**
 * PUBLIC: A topic map store is used to represent the connection
 * between one topic map and a repository where the data instantiating
 * that topic map is held (in an implementation-specific form).  This
 * interface is used to retrieve a topic map from such a repository
 * and to save changes back to the repository. Implementations can be
 * made for different kinds of repositories.  The interface supports a
 * simple transaction model.<p>
 *
 * A topic map store is opened either explicitly through the open()
 * method or implicitly via the getTopicMap() method. If the store is
 * transactional, a new transaction is created when the store is
 * opened.<p>
 * 
 * To make persistent changes in the topic map, use the commit method.
 * To roll back any changes since the last commit (or open), call the
 * abort method.  (Note that this only works with transactional
 * stores).<p>
 *
 * Make sure that you close the store when you are done in order to
 * release resources held by the store. A closed store can be
 * reopened. Closing a transactional store aborts the current
 * transaction, so commit before you close.<p>
 */

public interface TopicMapStoreIF {

  /**
   * PUBLIC: Constant that identifies the in-memory topic map
   * implementation. For use with the getImplementation() method.
   */  
  public static final int IN_MEMORY_IMPLEMENTATION = 1;

  /**
   * PUBLIC: Constant that identifies the rdbms topic map implementation. For
   * use with the getImplementation() method.
   */  
  public static final int RDBMS_IMPLEMENTATION = 2;

  /**
   * PUBLIC: Returns the topic map implementation identifier.
   * 
   * @return {@link #IN_MEMORY_IMPLEMENTATION} or {@link #RDBMS_IMPLEMENTATION}
   *         flags.
   * @since 3.1
   */
  public int getImplementation();

  /**
   * PUBLIC: Returns true if the store supports transactions. 
   *
   * @return Boolean: true if transactional, false if not.
   */
  public boolean isTransactional();

  /**
   * PUBLIC: Returns true if the store is open (because opening a
   * transactional store starts a transaction, "true" also means a
   * transaction is in progress).
   *
   * @return Boolean: true if open, false if not open (either not yet
   * opened, or closed).
   */
  public boolean isOpen();

  /**
   * PUBLIC: Opens the store, and starts a new transaction on a transactional store.
   */
  public void open();
  
  /**
   * PUBLIC: Closes the store and aborts the transaction if active.
   */
  public void close();
  
  /**
   * PUBLIC: Gets a locator of the topic map in the store. This can be
   * used as a locator for the topic map as a whole. The locator can
   * be resolved to a store that holds the topic map.
   *
   * @return A locator to the topic map in the store; an object
   * implementing LocatorIF.
   */
  public LocatorIF getBaseAddress();

  /**
   * EXPERIMENTAL: Sets the persistent base address of the store.
   *
   * @since 3.2.4
   */
  public abstract void setBaseAddress(LocatorIF base_address);

  /**
   * PUBLIC: Gets the topic map that is accessible through the
   * root transaction of the store.
   *
   * @return The topic map in the root transaction; an object
   * implementing TopicMapIF. This method is a shorthand for
   * getTransaction().getTopicMap().
   *
   * If the store is not open when this method is called it will be
   * opened automatically.<p>
   */
  public TopicMapIF getTopicMap();

  /**
   * PUBLIC: Commits and deactivates the top-level transaction. This
   * method is a shorthand for getTransaction().commit().
   */
  public void commit();

  /**
   * PUBLIC: Aborts and deactivates the top-level transaction; all
   * changes made inside the root transaction are lost. This method is
   * a shorthand for getTransaction().abort().
   */
  public void abort();

  //! /**
  //!  * INTERNAL: Clears the TopicMapIF by removing all data. This is
  //!  * effectively the same as deleting the topic map, but retaining the
  //!  * identity of the topic map in the data store, so that one can
  //!  * continue working with the now empty topic map. The store is
  //!  * closed after the topic map has been cleared.<p>
  //!  *
  //!  * Note: if you've retrieved the store via a TopicMapReferenceIF
  //!  * then call clear() on the reference instead.
  //!  */
  //! public void clear();

  /**
   * PUBLIC: Deletes the TopicMapIF from the data store. The store is
   * closed after the topic map has been deleted. A deleted store
   * cannot be reopened.<p>
   * 
   * If the force flag is false and the topic map contains any
   * objects, i.e. topics and associations, a NotRemovableException
   * will be thrown. The topic map will not be modified or closed if
   * this is so.<p>
   *
   * If the force flag is true, the topic map will be deleted even if
   * it contains any objects.<p>
   *
   * Note: if you're retrieved the store via a TopicMapReferenceIF
   * then call delete(boolean) on the reference instead.
   *
   * @since 1.3.4
   */
  public void delete(boolean force) throws NotRemovableException;
  
  /**
   * PUBLIC: Returns true if the store is usable for read-only
   * purposes only.
   *
   * @return True if the store is a read-only store, otherwise false.
   */
  public boolean isReadOnly();

  /**
   * PUBLIC: Returns the value of the specified topic map store
   * property.
   *
   * @since 3.2.3
   */
  public String getProperty(String propertyName); 

  /**
   * INTERNAL: Returns a topic map reference for this store.
   */
  public TopicMapReferenceIF getReference(); 

  /**
   * INTERNAL: Sets the topic map reference for this store.
   * <b>Warning:</b> Intended for internal use only.
   */
  public void setReference(TopicMapReferenceIF reference);
  
}
