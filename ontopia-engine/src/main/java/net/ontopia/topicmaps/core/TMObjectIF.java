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

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: The base topic map object interface that all topic map
 * objects implement. This interface serves as a common supertype for
 * all topic map objects, making very generic utilities possible.</p>
 *
 * If an object implementing this interface is marked as unmodifiable,
 * then an UnsupportedOperationException is thrown from any method
 * that causes a change in the state of that object.</p>
 */

public interface TMObjectIF {

  String EVENT_ADD_ITEMIDENTIFIER = "TMObjectIF.addItemIdentifier";
  String EVENT_REMOVE_ITEMIDENTIFIER = "TMObjectIF.removeItemIdentifier";

  /**
   * PUBLIC: Gets the id of this object. The object id is part of the topic
   * map id space, and must be unique. The object id is not
   * significant (or unique) outside the current topic map. The
   * object id is immutable. The object id is thus stable through this
   * object's lifetime.
   *
   * @return string which is this object's id.
   */
  String getObjectId();

  /**
   * PUBLIC: Returns true if this object is read-only, otherwise false.
   *
   * @return read-only (true) or not read-only (false).
   */
  boolean isReadOnly();

  /**
   * PUBLIC: Gets the topic map that this object belongs to. If the
   * object has been removed from its topic map or not added to a
   * topic map yet this will be null.
   *
   * @return A topic map; an object implementing TopicMapIF.
   */
  TopicMapIF getTopicMap();

  /**
   * PUBLIC: Gets the item identifiers of this object. These
   * locators are pointers back to the locations from where this
   * object originated.<p>
   * The purpose is to enable the engine to detect when
   * references to external objects refer to objects that are already
   * present within the system, such as topic maps which are already loaded.
   *
   * @return A collection of LocatorIF objects addressing the item.
   * @since 4.0
   */
  Collection<LocatorIF> getItemIdentifiers();

  /**
   * PUBLIC: Adds the given item identifier to the set of item
   * item identifiers for this object.
   *
   * @exception ConstraintViolationException Thrown if another object
   *            in the same topic map already has the given item
   *            identifier.
   * @param item_identifier The item identifier to be added; an object implementing LocatorIF.
   * @since 4.0
   */
  void addItemIdentifier(LocatorIF item_identifier)
      throws ConstraintViolationException;
  
  /**
   * PUBLIC: Removes the given item identifier from the set of item
   * identifiers. If this object does not have the given item
   * identifier the call has no effect.
   *
   * @param item_identifier The item identifier to be removed; an object implementing LocatorIF.
   * @since 4.0
   */
  void removeItemIdentifier(LocatorIF item_identifier);

  /**
   * PUBLIC: Removes the object from its parent.
   *
   * @since 4.0
   */
  void remove();
  
}
