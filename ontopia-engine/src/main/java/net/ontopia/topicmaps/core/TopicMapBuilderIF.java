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

import java.io.Reader;
import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: A helper for building topic map object structures.</p>
 *
 * The builder creates topic map objects.</p>
 */

public interface TopicMapBuilderIF {
  String MSG_TOPIC_TYPE_NOT_NULL = "Topic type must not be null.";
  String MSG_TOPIC_NAME_VALUE_NOT_NULL = "Topic name value must not be null.";
  String MSG_TOPIC_NOT_NULL = "Topic must not be null.";
  String MSG_TOPIC_NAME_NOT_NULL = "Topic name must not be null.";
  String MSG_VARIANT_VALUE_NOT_NULL = "Variant value must not be null.";
  String MSG_VARIANT_LOCATOR_NOT_NULL = "Variant locator must not be null.";
  String MSG_OCCURRENCE_TYPE_NOT_NULL = "Occurrence type must not be null.";
  String MSG_OCCURRENCE_VALUE_NOT_NULL = "Occurrence value must not be null.";
  String MSG_OCCURRENCE_LOCATOR_NOT_NULL = "Occurrence locator must not be null.";
  String MSG_ASSOCIATION_TYPE_NOT_NULL = "Association type must not be null.";
  String MSG_ASSOCATION_NOT_NULL = "Association must not be null.";
  String MSG_ASSOCIATION_ROLE_PLAYER_NOT_NULL = "Association role player must not be null.";
  String MSG_ASSOCIATION_ROLE_TYPE_NOT_NULL = "Association role type must not be null.";
  String MSG_VARIANT_DATATYPE_NOT_NULL = "Variant value datatype must not be null.";
  String MSG_OCCURRENCE_DATATYPE_NOT_NULL = "Occurrence value datatype must not be null.";
  
  /**
   * PUBLIC: Returns the topic map to which this builder belongs.
   *
   * @return An object implementing TopicMapIF.
   * @since 4.0
   */
  TopicMapIF getTopicMap();

  /**
   * PUBLIC: Makes a new topic for the current topic map.
   *
   * @return An object implementing TopicIF, and belonging to the
   * given topic map.
   */
  TopicIF makeTopic();  

  /**
   * PUBLIC: Makes a new topic of the given type for the current topic map.
   *   
   * @param topic_type The type of the created topic, an object
   * implementing TopicIF.
   *
   * @return An object implementing TopicIF, and belonging to the
   * current topic map.
   * @since 1.3.2
   */ 
  TopicIF makeTopic(TopicIF topic_type);


  /**
   * PUBLIC: Makes a new topic of the given types for the current topic map.
   *   
   * @param topic_types A collection of topics defining the type of
   *                    the created topic, all objects implementing TopicIF.
   *
   * @return An object implementing TopicIF, and belonging to the
   * current topic map.
   * @since 1.3.2
   */ 
  TopicIF makeTopic(Collection<TopicIF> topic_types);

  /**
   * PUBLIC: Makes a new untyped topic name with the given value for
   * the given topic.
   *
   * @param topic A topic; an object implementing TopicIF.
   *
   * @param value A string which is the value of the topic name.
   *
   * @return An object implementing TopicNameIF, having the given value, 
   *         and belonging to the given topic.  
   */
  TopicNameIF makeTopicName(TopicIF topic, String value);

  /**
   * PUBLIC: Makes a new topic name with the given type and value for
   * the given topic.
   *
   * @param topic A topic; an object implementing TopicIF.
   *
   * @param bntype The type of the created topic name; 
   *               an object implementing TopicIF.
   * @param value A string which is the value of the topic name.
   *
   * @return An object implementing TopicNameIF, having the given value, 
   *         and belonging to the given topic.  
   *
   * @since 3.0
   */
  TopicNameIF makeTopicName(TopicIF topic, TopicIF bntype, String value);

  /**
   * PUBLIC: Makes a new variant name with the specified name value
   * for the specified topic name in the given scope.
   *
   * @param name The given topic name; an object implmenting TopicNameIF.
   * @param value A string which is the value of the variant name.
	 * @param scope The scope in which this variant name is applicable.
   *
   * @return An object implementing VariantNameIF.
   * @since 4.0
   */
  VariantNameIF makeVariantName(TopicNameIF name, String value, Collection<TopicIF> scope); // NEW

  /**
   * PUBLIC: Makes a new variant name with the given locator for
   * the specified topic name in the given scope.
   *
   * @param name The given topic name; an object implmenting TopicNameIF.
   * @param locator The locator for the variant name.
	 * @param scope The scope in which this variant name is applicable.
   *
   * @return An object implementing VariantNameIF.
	 * @since 4.0
   */
  VariantNameIF makeVariantName(TopicNameIF name, LocatorIF locator, Collection<TopicIF> scope); // NEW

  /**
   * PUBLIC: Makes a new variant name with the specified name value and datatype
   * for the specified name in the given scope.
   *
   * @param name The given topic name; an object implmenting TopicNameIF.
   * @param value A string which is the value of the variant name.
	 * @param datatype The datatype of the value
	 * @param scope The scope in which this variant name is applicable.
   *
   * @return An object implementing VariantNameIF.
	 * @since 4.0
   */
  VariantNameIF makeVariantName(TopicNameIF name, String value, LocatorIF datatype, Collection<TopicIF> scope); // NEW

  /**
   * PUBLIC: Makes a new variant name with the specified name value reader and datatype
   * for the specified topic name in the given scope.
   *
   * @param name The given topic name; an object implmenting TopicNameIF.
   * @param value A reader which contains the value of the variant name.
	 * @param length The length of the reader value.
	 * @param datatype The datatype of the value
	 * @param scope The scope in which this variant name is applicable.
   *
   * @return An object implementing VariantNameIF.
	 * @since 4.0
   */
  VariantNameIF makeVariantName(TopicNameIF name, Reader value, long length, LocatorIF datatype, Collection<TopicIF> scope); // NEW
  
  /**
   * PUBLIC: Makes a new internal occurrence with the given type and value
   * for the given topic.
   *
   * @param topic The given topic; an object implementing TopicIF.
   * @param occurs_type The type of the created occurrence; 
   *                    an object implementing TopicIF.
   * @param value The value of the occurrence;
   *
   * @return An object implementing OccurrenceIF, having the given
   *         type and value, and belonging to the given topic.
   */
  OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, String value);
  
  /**
   * PUBLIC: Makes a new internal occurrence with the given type and value
   * for the given topic.
   *
   * @param topic The given topic; an object implementing TopicIF.
   * @param occurs_type The type of the created occurrence; 
   *                    an object implementing TopicIF.
   * @param value The value of the occurrence;
   * @param datatype The datatype of the value.
   *
   * @return An object implementing OccurrenceIF, having the given
   *         type and value, and belonging to the given topic.
   * @since 4.0
   */
  OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, String value, LocatorIF datatype); // NEW
  
  /**
   * PUBLIC: Makes a new internal occurrence with the given type and value
   * for the given topic.
   *
   * @param topic The given topic; an object implementing TopicIF.
   * @param occurs_type The type of the created occurrence; 
   *                    an object implementing TopicIF.
   * @param value The reader that contains the value of the occurrence;
	 * @param length The length of the reader value.
	 * @param datatype The datatype of the value.
   *
   * @return An object implementing OccurrenceIF, having the given
   *         type and value, and belonging to the given topic.
	 * @since 4.0
   */
  OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, Reader value, long length, LocatorIF datatype); // NEW
  
  /**
   * PUBLIC: Makes a new external occurrence with the given type and locator
   * for the given topic.
   *
   * @param topic The given topic; an object implementing TopicIF.
   * @param occurs_type The role type of the created occurrence; 
   *                    an object implementing TopicIF.
   * @param locator The locator of the occurrence;
   *                an object implementing LocatorIF.
   *
   * @return An object implementing OccurrenceIF, having the given
   *         type and locator, and belonging to the given topic.
   */
  OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, LocatorIF locator);

  /**
   * PUBLIC: Makes a new association with the given type in the
   * current topic map.
   *
   * @param assoc_type The association type; an object implementing TopicIF.
   *
   * @return An object implementing AssociationIF, having the given type,
   *         and belonging to the current topic map.
   */
  AssociationIF makeAssociation(TopicIF assoc_type); // DEPRECATE?

  /**
   * PUBLIC: Makes a new unary association with the given type in the
   * current topic map.
   *
   * @param assoc_type The association type; an object implementing TopicIF.
	 * @param role_type The role type of the first role.
	 * @param player The player of the first role.
   *
   * @return An object implementing AssociationIF, having the given type,
   *         a single role and belonging to the current topic map.
	 * @since 4.0
   */
  AssociationIF makeAssociation(TopicIF assoc_type, TopicIF role_type, TopicIF player); // NEW

  /**
   * PUBLIC: Makes a new association role with the given type and
   * player for the specified association.
   *
   * @param assoc The given association; an object implementing AssociationIF.
   * @param role_type The given type; an object implementing TopicIF.
   * @param player The given role player; an object implementing TopicIF.
   *
   * @return An object implementing AssociationRoleIF, of the given type,
   *         and belonging to the given association.
   *
   * @since 1.3
   */
  AssociationRoleIF makeAssociationRole(AssociationIF assoc,
                                               TopicIF role_type, TopicIF player);  
}
