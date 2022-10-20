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

package net.ontopia.topicmaps.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Characteristic processing utilities.
 */
public class CharacteristicUtils {

  /**
   * INTERNAL: Finds first characteristic of a specified type.
   *
   * @param objects A collection of TypedIF objects.
   * @return The first object that has the specified type.
   *
   * @since 2.0
   */
  public static <T extends TypedIF> T getByType(Collection<T> objects, TopicIF type) {
    Iterator<T> it = objects.iterator();
    while (it.hasNext()) {
      T typed = it.next();
      if (type.equals(typed.getType())) {
        return typed;
      }
    }
    return null;
  }

  /**
   * INTERNAL: Gets the basenames of the given collection of topics.
   *
   * @param topics A scope; a collection of TopicIF objects.
   * @return An ArrayList of TopicNameIF objects; the base names of 
   *             all the topics in the given collection.
   */
  public static Collection<TopicNameIF> getTopicNames(Collection<TopicIF> topics) {
    // Initialize result
    List<TopicNameIF> result = new ArrayList<TopicNameIF>();
    // Loop over topics
    Iterator<TopicIF> iter = topics.iterator();
    while (iter.hasNext()) {
      TopicIF topic = iter.next();
      result.addAll(topic.getTopicNames());
    }
    return result;
  }
  
  /**
   * Gets all the variant names of the given collecton of basenames,
   * including nested ones.   
   *
   * @param names A collection of TopicNameIF objects.
   * @return The variant names of all the basenames in the given collection; 
   *                an ArrayList of VariantNameIF objects.
   */
  public static Collection<VariantNameIF> getVariants(Collection<TopicNameIF> names) {
    // Initialize result
    List<VariantNameIF> result = new ArrayList<VariantNameIF>();
    // Loop over the names
    Iterator<TopicNameIF> iter = names.iterator();
    while (iter.hasNext()) {
      TopicNameIF name = iter.next();
      result.addAll(name.getVariants());
    }
    return result;
  }

  /**
   * Gets the occurrences of all the topics in the given collection.
   *
   * @param topics A collection of TopicIF objects; a scope.
   * @return An ArrayList of OccurrenceIF objects; all the occurrences of
   *              the topics in the given collection.
   */
  public static Collection<OccurrenceIF> getOccurrences(Collection<TopicIF> topics) {
    // Initialize result
    List<OccurrenceIF> result = new ArrayList<OccurrenceIF>();
    // Loop over topics
    Iterator<TopicIF> iter = topics.iterator();
    while (iter.hasNext()) {
      TopicIF topic = iter.next();
      result.addAll(topic.getOccurrences());
    }
    return result;
  }
  
  /**
   * Gets the association roles of the topics in the given collection.
   *
   * @param topics A collection of TopicIF objects; a scope.

   * @return An ArrayList of AssociationRoleIF objects; all the 
   *         association roles of the topics in the given collection.
   */
  public static Collection<AssociationRoleIF> getRoles(Collection<TopicIF> topics) {
    // Initialize result
    List<AssociationRoleIF> result = new ArrayList<AssociationRoleIF>();
    // Loop over topics
    Iterator<TopicIF> iter = topics.iterator();
    while (iter.hasNext()) {
      TopicIF topic = iter.next();
      result.addAll(topic.getRoles());
    }
    return result;
  }

  /**
   * INTERNAL: Gets the topics of a collection of basenames. 
   *
   * @param basenames A collection of TopicNameIFs.
   * @return A collection of TopicIF objects; the topics to which the
   * basenames belong.
   */
  public static Collection<TopicIF> getTopicsOfTopicNames(Collection<TopicNameIF> basenames) {
    Collection<TopicIF> topics = new HashSet<TopicIF>();
    Iterator<TopicNameIF> iter = basenames.iterator();
    while (iter.hasNext()) {
      TopicIF topic = iter.next().getTopic();
      if (topic != null) {
        topics.add(topic);
      }
    }
    return topics;
  }

  /**
   * INTERNAL: Returns all the directly associated topics of the given
   * topic.
   *
   * @since 3.4
   */
  public static Collection<TopicIF> getAssociatedTopics(TopicIF topic) {
    Collection<TopicIF> result = new HashSet<TopicIF>();
    Collection<AssociationRoleIF> roles = topic.getRoles();
    if (roles.isEmpty()) {
      return Collections.emptySet();
    }
    Iterator<AssociationRoleIF> iter = roles.iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role1 = iter.next();
      AssociationIF assoc = role1.getAssociation();
      Iterator<AssociationRoleIF> riter = assoc.getRoles().iterator();
      while (riter.hasNext()) {
        AssociationRoleIF role2 = riter.next();
        if (Objects.equals(role1, role2)) {
          continue;
        }
        TopicIF other = role2.getPlayer();
        if (other != null) {
          result.add(other);
        }
      }
    }
    return result;
  }
  
}
