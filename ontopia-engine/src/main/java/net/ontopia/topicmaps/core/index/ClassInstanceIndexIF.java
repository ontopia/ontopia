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

package net.ontopia.topicmaps.core.index;

import java.util.Collection;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * PUBLIC: Interface implemented by objects providing quick lookup
 * facilities to find topics used as types, and the instances of those
 * types, within a topic map.</p>
 */

public interface ClassInstanceIndexIF extends IndexIF {
  
  /**
   * PUBLIC: Gets all topics that are instances of the given type. 
   * Topic types are an abbreviated form of an association having 
   * roles for type and instance. This index provides optimized handling for topic
   * types.
   *
   * @param topic_type The given type; an object implementing TopicIF.
   *
   * @return A collection of TopicIF objects; the instances of the type 
   *         represented by the given topic.
   */
  public Collection<TopicIF> getTopics(TopicIF topic_type);
  
  /**
   * PUBLIC: Gets all topic names that are of the given type.
   *
   * @param name_type The given type; an object implementing TopicIF.
   *
   * @return A collection of TopicNameIF objects; topic names with the type 
   *         represented by the given topic.
   * @since 3.0
   */
  public Collection<TopicNameIF> getTopicNames(TopicIF name_type);
  
  /**
   * PUBLIC: Gets all occurrences that are of the given type.
   *
   * @param occurrence_type The given type; an object implementing TopicIF.
   *
   * @return A collection of OccurrenceIF objects; occurrences with the type 
   *         represented by the given topic.
   */
  public Collection<OccurrenceIF> getOccurrences(TopicIF occurrence_type);
  
  /**
   * PUBLIC: Gets all associations that are of the given type.
   *
   * @param association_type The given type; an object implementing TopicIF.
   *
   * @return A collection of AssociationIF objects; associations with the type 
   *         represented by the given topic.
   */
  public Collection<AssociationIF> getAssociations(TopicIF association_type);

  /**
   * PUBLIC: Gets all association roles that are of the given type.
   *
   * @param association_role_type The given type; an object implementing TopicIF.
   *
   * @return A collection of AssociationRoleIF objects; association roles with the type 
   *         represented by the given topic.
   */
  public Collection<AssociationRoleIF> getAssociationRoles(TopicIF association_role_type);

  /**
   * PUBLIC: Gets all topics that are used as topic types.
   *
   * @return A collection of TopicIF objects, each of which serves as a type
   *          for some topic.
   */
  public Collection<TopicIF> getTopicTypes();
  
  /**
   * PUBLIC: Gets the topics that are used as topic name types.
   *
   * @return A collection of TopicIF objects, each of which serves as a type
   *          for some topic name.
   * @since 3.0
   */
  public Collection<TopicIF> getTopicNameTypes();
  
  /**
   * PUBLIC: Gets the topics that are used as occurrence types.
   *
   * @return A collection of TopicIF objects, each of which serves as a type
   *          for some occurrence.
   */
  public Collection<TopicIF> getOccurrenceTypes();
  
  /**
   * PUBLIC: Gets the topics that are used as association types.
   *
   * @return A collection of TopicIF objects, each of which serves as a type
   *          for some association.
   */
  public Collection<TopicIF> getAssociationTypes();
  
  /**
   * PUBLIC: Gets the topics that are used as association role types.
   *
   * @return A collection of TopicIF objects, each of which serves as a type
   *          for some association role.
   */
  public Collection<TopicIF> getAssociationRoleTypes();
  
  /**
   * PUBLIC: Returns true if the topic is used as a topic type somewhere.
   *
   * @param topic An object implementing TopicIF.
   *
   * @return Boolean: true if the given topic serves as a type for some topic; 
   *                  false otherwise.
   */
  public boolean usedAsTopicType(TopicIF topic);

  /**
   * PUBLIC: Returns true if the topic is used as an topic name type.
   *
   * @param topic An object implementing TopicIF.
   *
   * @return Boolean: true if the given topic serves as a type for
   *                  some topic name; false otherwise.
   * @since 3.0
   */
  public boolean usedAsTopicNameType(TopicIF topic);

  /**
   * PUBLIC: Returns true if the topic is used as an occurrence type.
   *
   * @param topic An object implementing TopicIF.
   *
   * @return Boolean: true if the given topic serves as a type for some occurrence; 
   *                  false otherwise.
   */
  public boolean usedAsOccurrenceType(TopicIF topic);

  /**
   * PUBLIC: Returns true if the topic is used as an association type.
   *
   * @param topic An object implementing TopicIF.
   *
   * @return Boolean: true if the given topic serves as a type for some association; 
   *                  false otherwise.
   */
  public boolean usedAsAssociationType(TopicIF topic);
  
  /**
   * PUBLIC: Returns true if the topic is used as an association role type.
   *
   * @param topic An object implementing TopicIF.
   *
   * @return Boolean: true if the given topic serves as a type for some association role; 
   *                  false otherwise.
   */
  public boolean usedAsAssociationRoleType(TopicIF topic);
  
  /**
   * PUBLIC: Returns true if the topic is used as a type somewhere.
   *
   * @param topic An object implementing TopicIF.
   *
   * @return Boolean: true if the given topic serves as a type for any topic
   *                    map object; false otherwise.
   */
  public boolean usedAsType(TopicIF topic);

}
