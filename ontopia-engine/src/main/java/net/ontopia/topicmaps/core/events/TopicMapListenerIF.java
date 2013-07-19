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

package net.ontopia.topicmaps.core.events;

import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * PUBLIC: Event callback interface for topic map objects. Register
 * listeners with the static methods in
 * <code>TopicMapEvents</code>. See the same class for more
 * information about the event system. Note that this interface is
 * generic and may accommodate objects other than topics in the
 * future. Which object the listener will receive callbacks from
 * depends on which registration method in
 * <code>TopicMapEvents</code>was used for the listener.
 *
 * <p><b>NOTE:</b> The objects received through this callback
 * interface are <em>not</em> normal topic map objects, since they do
 * not belong to any topic map transaction.  Instead, the objects
 * received are minimal snapshots, and what information the snapshots
 * contain will depend on the callback method they were received
 * through. See the documention of each method to see what the
 * constraints are.
 *
 * @since 3.1
 */

public interface TopicMapListenerIF {

  /**
   * PUBLIC: Callback method called when a topic map object has been
   * added to the topic map. The callback will be made after the
   * transaction has been committed.</p>
   *
   * The snapshot of a TMObjectIF given as the first argument is very
   * minimal as it only holds the object ID of the topic map
   * object. To get more information about the object, look it up in
   * your own transaction using the object ID.</p>
   */
  public void objectAdded(TMObjectIF snapshot);

  /**
   * PUBLIC: Callback method called when a topic map object has been
   * modified. The callback will be made after the transaction has
   * been committed.</p>
   *
   * The snapshot of a TMObjectIF given as the first argument is very
   * minimal as it only holds the object ID of the topic map
   * object. To get more information about the object, look it up in
   * your own transaction using the object ID.</p>
   *
   * @since 3.4.3
   */
  public void objectModified(TMObjectIF snapshot);

  /**
   * PUBLIC: Callback method called when a topic map object has been
   * removed from the topic map. The callback will be made after the
   * transaction has been committed.</p>
   *
   * The snapshot of a TMObjectIF given as the first argument will
   * contain a snapshot view of the topic map object at the time the
   * object was removed from the topic map. If the snapshot object was
   * a topic then it will include all directly contained information,
   * except association roles, at the time when the topic was removed
   * through the method
   * <code>TopicMapIF.removeTopic(TopicIF)</code>. The data included
   * are: the object ID, the topic types, all topic names and their
   * complete data including their variant names, all occurrences and
   * all their complete data. Note that any topic references in this
   * snapshot will only be stubs and thus only contain the topic's
   * object ID.
   */
  public void objectRemoved(TMObjectIF snapshot);
  
}
