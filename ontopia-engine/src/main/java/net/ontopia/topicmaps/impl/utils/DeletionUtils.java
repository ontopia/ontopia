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

package net.ontopia.topicmaps.impl.utils;

import java.util.Collection;
import java.util.HashSet;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;

/**
 * INTERNAL: Topic map object deletion utilities.
 */

public class DeletionUtils {
  
  /**
   * INTERNAL: Removes the dependencies to the given topic from its
   * topic map. Characteristics that have the topic in its scope get 
   * removed. Characteristics that have the topic as a type are 
   * removed from the topic map.
   *
   * @since 4.0
   * @param topic The given topic; an object implementing TopicIF.
   */
  public static void removeDependencies(TopicIF topic) {
    synchronized (topic) {
      // Get topic map to which topic belongs
      TopicMapIF tm = topic.getTopicMap();
      if (tm == null) {
        return;
      }
      
      // Get scope index; to be used when removing where topic is used as scope
      ScopeIndexIF sindex = (ScopeIndexIF)tm.getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");
      
      // Remove associations scoped by topic
      for (ScopedIF object : sindex.getAssociations(topic)) {
        object.remove();
      }
      // Remove topicnames scoped by topic
      for (ScopedIF object : sindex.getTopicNames(topic)) {
        object.remove();
      }
      // Remove occurrences scoped by topic
      for (ScopedIF object : sindex.getOccurrences(topic)) {
        object.remove();
      }
      // Remove variants scoped by topic
      for (ScopedIF object : sindex.getVariants(topic)) {
        object.remove();
      }
      
      // Get class instance index; to be used when removing where topic is used as type
      ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
      
      // Remove associations where topic is role type
      for (TypedIF object : cindex.getAssociationRoles(topic)) {
        object.remove();
      }
      // Remove associations where topic is association type
      for (TypedIF object : cindex.getAssociations(topic)) {
        object.remove();
      }
      // Remove basenames where topic is name type
      for (TypedIF object : cindex.getTopicNames(topic)) {
        object.remove();
      }
      // Remove occurrences where topic is occurrence type
      for (TypedIF object : cindex.getOccurrences(topic)) {
        object.remove();
      }
      // Remove instances of the topic
      // preload instances to avoid index changes on type change
      Collection<TopicIF> instances = cindex.getTopics(topic);
      for (TopicIF object : instances) {
        if ((object != null) && !object.equals(topic)) {
          object.removeType(topic); // prevent secondary loops, see #347
          object.remove();
        }
      }
      
      // Remove associations
      // wrap to avoid concurrent modification
      HashSet<AssociationRoleIF> roles = new HashSet<AssociationRoleIF>(topic.getRoles());
      for (AssociationRoleIF role : roles) {
        role.getAssociation().remove();
      }

      // Unregister as reifier
      ReifiableIF reified = topic.getReified();
      if (reified != null) {
        reified.setReifier(null);
      }
    }
  }

  public static void removeDependencies(ReifiableIF object) {
    synchronized (object) {
      TopicIF reifier = object.getReifier();
      if (reifier != null) {
        object.setReifier(null);
      }
    }
  }
  
  /**
   * INTERNAL: Deletes all the topics and associations from the topic
   * map. Note that this is not the best method for emptying a topic
   * map; use TopicMapStoreIF.clear() instead, which is much faster
   * with the RDBMS.
   *  
   * @param topicmap The given topicmap; an object implementing TopicMapIF.
   *
   * @since 2.0
   */
  public static void clear(TopicMapIF topicmap) {
    synchronized (topicmap) {
      // Delete topics
      Collection<TopicIF> ts = topicmap.getTopics();
      TopicIF[] topics = new TopicIF[ts.size()];
      ts.toArray(topics);
      
      for (TopicIF topic : topics) {
        topic.remove();
      }
      
      // Delete associations
      Collection<AssociationIF> as = topicmap.getAssociations();
      AssociationIF[] associations = new AssociationIF[as.size()];
      as.toArray(associations);
      
      for (AssociationIF association : associations) {
        association.remove();
      }
    }
  }

}
