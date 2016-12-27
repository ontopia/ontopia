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

/**
 * PUBLIC: Implemented by objects representing associations in the
 * topic map model.</p>
 *
 * Note: in XTM 1.0 terminology, associations have members playing
 * roles in the association. In ISO 13250 terminology, playing a role
 * in an association is a characteristic of a topic. These notions are
 * intended to be equivalent.</p>
 */

public interface AssociationIF extends ScopedIF, TypedIF, ReifiableIF {

  String EVENT_ADDED = "AssociationIF.added";
  String EVENT_REMOVED = "AssociationIF.removed";
  String EVENT_SET_TYPE = "AssociationIF.setType";
  String EVENT_ADD_ROLE = "AssociationIF.addRole";
  String EVENT_REMOVE_ROLE = "AssociationIF.removeRole";
  String EVENT_ADD_THEME = "AssociationIF.addTheme";
  String EVENT_REMOVE_THEME = "AssociationIF.removeTheme";

  /**
   * PUBLIC: Gets a Collection of association role types found
   * in this association. The returned collection does not contain any
   * duplicates. Even if some roles have no type, null will <em>not</em> be
   * returned as one of the role types in the returned set.
   *
   * @return A Collection of TopicIF objects.
   */
  Collection<TopicIF> getRoleTypes();
  
  /**
   * PUBLIC: Gets the association roles in this association which are of the given
   * role type.
   *
   * @param roletype The type of the roles returned; an object implementing TopicIF.
   *                  If null the method will return the roles that have no type.
   *
   * @return A Collection of AssociationRoleIF objects.
   */
  Collection<AssociationRoleIF> getRolesByType(TopicIF roletype);

  /**
   * PUBLIC: Gets all the association roles of the association. The
   * returned roles may appear in any order.
   *
   * @return A collection of AssociationRoleIF objects.
   */
  Collection<AssociationRoleIF> getRoles();

}
