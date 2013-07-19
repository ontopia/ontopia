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


/**
 * PUBLIC: Implemented by objects representing association roles in
 * the topic map model.  Association roles are first-class objects
 * which represent a topic playing a role in an association.</p>
 *
 * Note: in XTM 1.0 terminology, associations have members playing
 * roles in the association. In ISO 13250 terminology, playing a role
 * in an association is a characteristic of a topic. These notions are
 * intended to be equivalent.</p>
 */

public interface AssociationRoleIF extends TypedIF, ReifiableIF {

  public static final String EVENT_ADDED = "AssociationRoleIF.added";
  public static final String EVENT_REMOVED = "AssociationRoleIF.removed";
  public static final String EVENT_SET_TYPE = "AssociationRoleIF.setType";
  public static final String EVENT_SET_PLAYER = "AssociationRoleIF.setPlayer";

  /**
   * PUBLIC: Gets the association to which this association role
   * belongs.
   *
   * @return The association to which this association role belongs;
   *           an object implementing AssociationIF.
   */
  public AssociationIF getAssociation();

  /**
   * PUBLIC: Gets the topic that plays this association role (this member of the association).
   *
   * @return The topic (member) which plays this role in the association.
   *
  */
  public TopicIF getPlayer();

  /**
   * PUBLIC: Sets the topic that plays this association role. Note
   * that this has the side-effect of removing the role from its
   * current player, if any, and inserting it on the new player, if
   * any.
   *
   * @param player The topic (member) which plays this role in the association.
   *            Can be null; if null, then the effect is that there is no player
   *            of this role in this association.
   */
  public void setPlayer(TopicIF player);

}
