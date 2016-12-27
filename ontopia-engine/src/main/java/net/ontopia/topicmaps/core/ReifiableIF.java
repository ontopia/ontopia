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
 * PUBLIC: Implemented by topic map objects that can be
 * reified. Reification means making a topic for another object (that
 * is not itself a topic) so that you can say something about it. This
 * interface is implemented by AssociationIF, AssociationRoleIF,
 * TopicNameIF, VariantNameIF, OccurrenceIF and TopicMapIF.</p>
   *
   * @since 4.0
 */

public interface ReifiableIF extends TMObjectIF {

  String EVENT_SET_REIFIER = "ReifiableIF.setReifier";
  
  /**
   * PUBLIC: Returns the topic that reifies this object.
   */
  TopicIF getReifier();
  
  /**
   * PUBLIC: Sets the reifier of this object.
   *
   * @exception DuplicateReificationException Thrown when the reifier
   *            already reifies another object.
   */
  void setReifier(TopicIF reifier) throws DuplicateReificationException;

}
