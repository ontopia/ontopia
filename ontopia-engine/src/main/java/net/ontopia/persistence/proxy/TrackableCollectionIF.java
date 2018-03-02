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

package net.ontopia.persistence.proxy;

import java.util.Collection;

/**
 * INTERNAL: Interface used by Collection implementations that track
 * the changes performed on them. It keeps track of the objects that
 * have been added and the ones that has been removed.
 */

public interface TrackableCollectionIF<E> extends Collection<E> {
  
  /**
   * INTERNAL: Clears the list of added and removed objects without
   * touching the original collection.
   */
  void resetTracking();

  /**
   * INTERNAL: Consider existing collection elements as having just
   * been added.
   */
  void selfAdded();

  /**
   * INTERNAL: Adds the item to the collection tracking the change.
   */
  boolean addWithTracking(E item);

  /**
   * INTERNAL: Removes the item from the collection tracking the
   * change.
   */
  boolean removeWithTracking(E item);

  /**
   * INTERNAL: Removes all items from the collection tracking the
   * changes.
   */
  void clearWithTracking();

  //! /**
  //!  * INTERNAL: Adds the item to the collection tracking the change.
  //!  */
  //! public boolean addWithoutTracking(Object item);
  //! 
  //! /**
  //!  * INTERNAL: Removes the item from the collection tracking the
  //!  * change.
  //!  */
  //! public boolean removeWithoutTracking(Object item);

  //! /**
  //!  * INTERNAL: Returns a collection that contains the added elements
  //!  * and the elements that remain after elements marked for removal
  //!  * has been removed. This method effectively returns a collection
  //!  * object that is up-to-date with the tracked changes. The
  //!  * collection is immutable.
  //!  */
  //! public Collection getCollection();

  /**
   * INTERNAL: Gets the objects that have been added to the set. This
   * collection is immutable. Null is returned if the added collection
   * has not been initialized, ie. it is empty.
   */
  Collection<E> getAdded();

  /**
   * INTERNAL: Gets the objects that have been removed from the
   * set. This collection is immutable. Null is returned if the
   * removed collection has not been initialized, ie. it is empty.
   */
  Collection<E> getRemoved();
  
}
