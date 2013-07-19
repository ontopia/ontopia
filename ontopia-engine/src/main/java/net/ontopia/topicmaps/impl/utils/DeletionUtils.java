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
   * topic map. For the characteristics that have the topic in its
   * scope gets the topic removed from the scope. Characteristics that
   * have the topic as a type is removed from the topic map.
   *
   * @since 4.0
   * @param topic The given topic; an object implementing TopicIF.
   */
  public static void removeDependencies(TopicIF topic) {
    synchronized (topic) {
      // Get topic map to which topic belongs
      TopicMapIF tm = topic.getTopicMap();
      if (tm == null) return;
      
      // Get scope index; to be used when removing where topic is used as theme
      ScopeIndexIF sindex = (ScopeIndexIF)tm.getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");
      
      // Remove from association themes
      Object[] objects = sindex.getAssociations(topic).toArray();
      for (int i=0; i < objects.length; i++) {
        ((ScopedIF)objects[i]).remove();
      }
      // Remove from basename themes
      objects = sindex.getTopicNames(topic).toArray();
      for (int i=0; i < objects.length; i++) {
        ((ScopedIF)objects[i]).remove();
      }
      // Remove from occurrence themes
      objects = sindex.getOccurrences(topic).toArray();
      for (int i=0; i < objects.length; i++) {
        ((ScopedIF)objects[i]).remove();
      }
      // Remove from variant themes
      objects = sindex.getVariants(topic).toArray();
      for (int i=0; i < objects.length; i++) {
        ((ScopedIF)objects[i]).remove();
      }
      
      // Get class instance index; to be used when removing where topic is used as type
      ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
      
      // Remove associations where topic is role type
      objects = cindex.getAssociationRoles(topic).toArray();
      for (int i=0; i < objects.length; i++) {
        ((TypedIF)objects[i]).remove();
      }
      // Remove associations where topic is association type
      objects = cindex.getAssociations(topic).toArray();
      for (int i=0; i < objects.length; i++) {
        ((TypedIF)objects[i]).remove();
      }
      // Remove basenames where topic is name type
      objects = cindex.getTopicNames(topic).toArray();
      for (int i=0; i < objects.length; i++) {
        ((TypedIF)objects[i]).remove();
      }
      // Remove occurrences where topic is occurrence type
      objects = cindex.getOccurrences(topic).toArray();
      for (int i=0; i < objects.length; i++) {
        ((TypedIF)objects[i]).remove();
      }
      // Remove instances of the topic
      objects = cindex.getTopics(topic).toArray();
      for (int i=0; i < objects.length; i++) {
        if (objects[i] != topic) // avoids infinite recursion if instance of self
          ((TopicIF)objects[i]).remove();
      }
      
      // Remove associations
      Object[] roles = topic.getRoles().toArray();
      for (int i=0; i < roles.length; i++) {
        AssociationRoleIF role = (AssociationRoleIF)roles[i];        
        role.getAssociation().remove();
      }

      // Unregister as reifier
      ReifiableIF reified = topic.getReified();
      if (reified != null) reified.setReifier(null);
    }
  }

  public static void removeDependencies(ReifiableIF object) {
    synchronized (object) {
      TopicIF reifier = object.getReifier();
      if (reifier != null) object.setReifier(null);
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
      
      for (int i=0; i < topics.length; i++)
        topics[i].remove();
      
      // Delete associations
      Collection<AssociationIF> as = topicmap.getAssociations();
      AssociationIF[] associations = new AssociationIF[as.size()];
      as.toArray(associations);
      
      for (int i=0; i < associations.length; i++)
        associations[i].remove();
    }
  }

}
