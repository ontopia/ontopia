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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;

/**
 * INTERNAL: Utilities for working with class-instance relationships.
 */
public class ClassInstanceUtils {

  /**
   * INTERNAL: Replaces all class-instance associations in a topic map
   * by direct references from the topics in question to their types.
   * Only associations that use the PSIs defined in XTM 1.0, that
   * match the templates exactly, that are not reified, and that are
   * not scoped are replaced.
   * @since 1.2.3
   */
  public static void resolveAssociations1(TopicMapIF topicmap) {
    resolveAssociations(topicmap, PSI.getXTMClassInstance(),
                        PSI.getXTMClass(),
                        PSI.getXTMInstance());
  }

  /** 
   * INTERNAL: Replaces all class-instance associations using the XTM
   * 2.0 PSIs in a topic map by direct references from the topics in
   * question to their types.  Only associations that use the PSIs
   * defined in XTM 2.0, that match the templates exactly, that are
   * not reified, and that are not scoped are replaced.
   */
  public static void resolveAssociations2(TopicMapIF topicmap) {
    resolveAssociations(topicmap, PSI.getSAMTypeInstance(),
                        PSI.getSAMType(),
                        PSI.getSAMInstance());
  }

  /** 
   * INTERNAL: The actual implementation, shared across PSI sets.
   */
  private static void resolveAssociations(TopicMapIF topicmap,
                                          LocatorIF assoctype,
                                          LocatorIF typetype,
                                          LocatorIF insttype) {
    // first, resolve the PSIs
    TopicIF classinstance = topicmap.getTopicBySubjectIdentifier(assoctype);
    TopicIF klass = topicmap.getTopicBySubjectIdentifier(typetype);
    TopicIF instance = topicmap.getTopicBySubjectIdentifier(insttype);

    if (classinstance == null ||
        klass == null ||
        instance == null) {
      return;
    }

    // then, get the index
    ClassInstanceIndexIF typeIndex = (ClassInstanceIndexIF)
      topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    // then, look over those associations
    Iterator<AssociationIF> it = typeIndex.getAssociations(classinstance).iterator();
    while (it.hasNext()) {
      AssociationIF assoc = it.next();
      if (assoc.getRoles().size() != 2 ||
          !assoc.getScope().isEmpty() ||
          assoc.getReifier() != null) {
        continue;
      }
      
      TopicIF klasstopic = null;
      TopicIF insttopic = null;

      Iterator<AssociationRoleIF> it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role = it2.next();
        TopicIF type = role.getType();
        if (type == null) {
          break;
        } else if (type.equals(klass)) {
          klasstopic = role.getPlayer();
        } else if (type.equals(instance)) {
          insttopic = role.getPlayer();
        } else {
          break;
        }
      }

      if (klasstopic == null || insttopic == null) {
        continue;
      }

      insttopic.addType(klasstopic);
      assoc.remove();
    }
  }
  
  /**
   * INTERNAL: Returns true if the TypedIF object is an instance the
   * given type.
   */  
  public static boolean isInstanceOf(TypedIF typed, TopicIF type) {
    return (type != null && type.equals(typed.getType()));
  }
  
  /**
   * INTERNAL: Returns true if the TopicIF object is an instance
   * the given type.  
   */
  public static boolean isInstanceOf(TopicIF topic, TopicIF type) {
    return (topic.getTypes().contains(type));
  }
  
  /**
   * INTERNAL: Returns the typed objects that are instances of the given type.
   */
  public static Collection getInstancesOf(Collection typed, TopicIF type) {
    Collection result = new ArrayList();
    
    // Loop over the typed objects to see if they're instances of the type
    Iterator iter = typed.iterator();
    while (iter.hasNext()) {
      Object object = iter.next();
      if (object instanceof TypedIF) {
        if (isInstanceOf((TypedIF)object, type)) {
          result.add(object);
        }
      } else {
        if (isInstanceOf((TopicIF)object, type)) {
          result.add(object);
        }
      }
    }
    return result;
  }
                 
  /**
   * INTERNAL: Returns the topics that are the type/class topic of the
   * typed objects.   
   */
  public static Collection getTypes(Collection typed) {
    return getTypes(typed, new ArrayList());
  }
  
  /**
   * INTERNAL: Modifies the accumulated collection to also include the
   * types of the typed objects.
   */
  public static Collection getTypes(Collection typed, Collection accumulated) {
    // Loop over typed objects
    Iterator iter = typed.iterator();
    while (iter.hasNext()) {
      Object object = iter.next();
      if (object instanceof TypedIF) {
        accumulated.add(((TypedIF)object).getType());
      } else {
        accumulated.add(((TopicIF)object).getTypes());
      }
    }
    return accumulated;
  }
                 
  /**
   * INTERNAL: Returns a Map containing the typed objects keyed by type. The
   * value of a map entry is always a Collection instance.
   */
  public static Map getTypeMap(Collection typed) {
    return getTypeMap(typed, new HashMap());
  }
  
  /**
   * INTERNAL: Modifies the accumulated map to also include the type map
   * of the typed objects. The value of a map entry is always a
   * Collection instance.   
   */
  public static Map getTypeMap(Collection typed, Map accumulated) {
    // Loop over the typed objects
    Iterator iter = typed.iterator();
    while (iter.hasNext()) {
      Object object = iter.next();

      // Check to see if object implements TypedIF or TopicIF
      TopicIF type;
      if (object instanceof TypedIF) {
        // TypedIF
        type = ((TypedIF)object).getType();
        if (!accumulated.containsKey(type)) {
          accumulated.put(type, new HashSet());
        }
        ((Set)accumulated.get(type)).add(object);
      } else {
        // TopicIF
        Iterator<TopicIF> iter2 = ((TopicIF)object).getTypes().iterator();
        while (iter2.hasNext()) {
          type = iter2.next();
          if (!accumulated.containsKey(type)) {
            accumulated.put(type, new HashSet());
          }
          ((Set)accumulated.get(type)).add(object);
        }

      }
    }
    return accumulated;
  }
  
}
