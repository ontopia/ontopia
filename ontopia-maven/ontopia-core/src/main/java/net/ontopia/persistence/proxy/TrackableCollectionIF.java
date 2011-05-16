// $Id: TrackableCollectionIF.java,v 1.4 2005/07/12 09:37:39 grove Exp $

package net.ontopia.persistence.proxy;

import java.util.Collection;

/**
 * INTERNAL: Interface used by Collection implementations that track
 * the changes performed on them. It keeps track of the objects that
 * have been added and the ones that has been removed.
 */

public interface TrackableCollectionIF extends Collection {
  
  /**
   * INTERNAL: Clears the list of added and removed objects without
   * touching the original collection.
   */
  public void resetTracking();

  /**
   * INTERNAL: Consider existing collection elements as having just
   * been added.
   */
  public void selfAdded();

  /**
   * INTERNAL: Adds the item to the collection tracking the change.
   */
  public boolean addWithTracking(Object item);

  /**
   * INTERNAL: Removes the item from the collection tracking the
   * change.
   */
  public boolean removeWithTracking(Object item);

  /**
   * INTERNAL: Removes all items from the collection tracking the
   * changes.
   */
  public void clearWithTracking();

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
  public Collection getAdded();

  /**
   * INTERNAL: Gets the objects that have been removed from the
   * set. This collection is immutable. Null is returned if the
   * removed collection has not been initialized, ie. it is empty.
   */
  public Collection getRemoved();
  
}
