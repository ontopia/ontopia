
// $Id: DeletionUtils.java,v 1.6 2008/08/28 09:32:44 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.utils;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.*;
import net.ontopia.utils.OntopiaRuntimeException;

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

  //! /**
  //!  * INTERNAL: Deletes all the given topics from the topic map. This
  //!  * method is more efficent than deleting topics individually.
  //!  *
  //!  * @since 3.2
  //!  */  
  //! public static boolean removeTopics(TopicMapIF tm,
  //!                                    Collection topics) {
  //! 
  //!   // Get topic map to which topic belongs
  //!   if (tm == null) return false;
  //!     
  //!   // Get class instance index; to be used when removing where topic is used as type
  //!   ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
  //!   
  //!   Object[] objects;    
  //!   // remove where used as association role type
  //!   objects = cindex.getAssociationRoleTypes().toArray();
  //!   for (int o=0; o < objects.length; o++) {
  //!     TopicIF type = (TopicIF)objects[o];
  //!     if (topics.contains(type)) {
  //!       Object[] deps = cindex.getAssociationRoles(type).toArray();
  //!       for (int d=0; d < deps.length; d++) {
  //!         ((TypedIF)deps[d]).setType(null);
  //!       }        
  //!     }
  //!   }
  //!   // remove where used as association type
  //!   objects = cindex.getAssociationTypes().toArray();
  //!   for (int o=0; o < objects.length; o++) {
  //!     TopicIF type = (TopicIF)objects[o];
  //!     if (topics.contains(type)) {
  //!       Object[] deps = cindex.getAssociations(type).toArray();
  //!       for (int d=0; d < deps.length; d++) {
  //!         ((TypedIF)deps[d]).setType(null);
  //!       }        
  //!     }
  //!   }
  //!   // remove where used as basename type
  //!   objects = cindex.getTopicNameTypes().toArray();
  //!   for (int o=0; o < objects.length; o++) {
  //!     TopicIF type = (TopicIF)objects[o];
  //!     if (topics.contains(type)) {
  //!       Object[] deps = cindex.getTopicNames(type).toArray();
  //!       for (int d=0; d < deps.length; d++) {
  //!         ((TypedIF)deps[d]).setType(null);
  //!       }        
  //!     }
  //!   }
  //!   // remove where used as occurrence type
  //!   objects = cindex.getOccurrenceTypes().toArray();
  //!   for (int o=0; o < objects.length; o++) {
  //!     TopicIF type = (TopicIF)objects[o];
  //!     if (topics.contains(type)) {
  //!       Object[] deps = cindex.getOccurrences(type).toArray();
  //!       for (int d=0; d < deps.length; d++) {
  //!         ((TypedIF)deps[d]).setType(null);
  //!       }        
  //!     }
  //!   }
  //!   // remove where used as topic type
  //!   objects = cindex.getTopicTypes().toArray();
  //!   for (int o=0; o < objects.length; o++) {
  //!     TopicIF type = (TopicIF)objects[o];
  //!     if (topics.contains(type)) {
  //!       Object[] deps = cindex.getTopics(type).toArray();
  //!       for (int d=0; d < deps.length; d++) {
  //!         ((TopicIF)deps[d]).removeType(type);
  //!       }        
  //!     }
  //!   }
  //!   
  //!   // Get scope index; to be used when removing where topic is used as theme
  //!   ScopeIndexIF sindex = (ScopeIndexIF)tm.getIndex("net.ontopia.topicmaps.core.index.ScopeIndexIF");
  //! 
  //!   // remove where used as association theme
  //!   objects = sindex.getAssociationThemes().toArray();
  //!   for (int o=0; o < objects.length; o++) {
  //!     TopicIF theme = (TopicIF)objects[o];
  //!     if (topics.contains(theme)) {
  //!       Object[] deps = sindex.getAssociations(theme).toArray();
  //!       for (int d=0; d < deps.length; d++) {
  //!         ((ScopedIF)deps[d]).removeTheme(theme);
  //!       }        
  //!     }
  //!   }
  //!   // remove where used as basename theme
  //!   objects = sindex.getTopicNameThemes().toArray();
  //!   for (int o=0; o < objects.length; o++) {
  //!     TopicIF theme = (TopicIF)objects[o];
  //!     if (topics.contains(theme)) {
  //!       Object[] deps = sindex.getTopicNames(theme).toArray();
  //!       for (int d=0; d < deps.length; d++) {
  //!         ((ScopedIF)deps[d]).removeTheme(theme);
  //!       }        
  //!     }
  //!   }
  //!   // remove where used as occurrence theme
  //!   objects = sindex.getOccurrenceThemes().toArray();
  //!   for (int o=0; o < objects.length; o++) {
  //!     TopicIF theme = (TopicIF)objects[o];
  //!     if (topics.contains(theme)) {
  //!       Object[] deps = sindex.getOccurrences(theme).toArray();
  //!       for (int d=0; d < deps.length; d++) {
  //!         ((ScopedIF)deps[d]).removeTheme(theme);
  //!       }        
  //!     }
  //!   }
  //!   // remove where used as variant theme
  //!   objects = sindex.getVariantThemes().toArray();
  //!   for (int o=0; o < objects.length; o++) {
  //!     TopicIF theme = (TopicIF)objects[o];
  //!     if (topics.contains(theme)) {
  //!       Object[] deps = sindex.getVariants(theme).toArray();
  //!       for (int d=0; d < deps.length; d++) {
  //!         ((ScopedIF)deps[d]).removeTheme(theme);
  //!       }        
  //!     }
  //!   }
  //! 
  //!   // now delete the topics
  //!   Iterator iter = topics.iterator();
  //!   while (iter.hasNext()) {
  //!     TopicIF topic = (TopicIF)iter.next();
  //!     
  //!     // Remove associations
  //!     Object[] roles = topic.getRoles().toArray();
  //!     for (int i=0; i < roles.length; i++) {
  //!       AssociationRoleIF role = (AssociationRoleIF)roles[i];        
  //!       role.getAssociation().remove();
  //!     }
  //!     
  //!     // Remove topic from topic map
  //!     topic.remove();
  //!   }
  //!   return true;      
  //! }
  
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
      Collection ts = topicmap.getTopics();
      TopicIF[] topics = new TopicIF[ts.size()];
      ts.toArray(topics);
      
      for (int i=0; i < topics.length; i++)
        topics[i].remove();
      
      // Delete associations
      Collection as = topicmap.getAssociations();
      AssociationIF[] associations = new AssociationIF[as.size()];
      as.toArray(associations);
      
      for (int i=0; i < associations.length; i++)
        associations[i].remove();
    }
  }

}
