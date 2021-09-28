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

package net.ontopia.topicmaps.impl.basic.index;

import java.util.ArrayList;
import java.util.Collection;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.impl.utils.BasicIndex;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.utils.CollectionMap;

/**
 * INTERNAL: The basic dynamic class instance index implementation.
 */

public class ClassInstanceIndex extends BasicIndex implements ClassInstanceIndexIF {
  
  protected CollectionMap topics;
  protected CollectionMap bnames;
  protected CollectionMap occurs;
  protected CollectionMap assocs;
  protected CollectionMap roles;

  ClassInstanceIndex(IndexManagerIF imanager, EventManagerIF emanager, ObjectTreeManager otree) {
    
    // Initialize index maps
    topics = new CollectionMap();
    bnames = new CollectionMap();
    occurs = new CollectionMap();
    assocs = new CollectionMap();
    roles = new CollectionMap();
    
    // Initialize object tree event handlers [objects added or removed]    
    otree.addListener(new TopicIF_added(topics, TopicIF.EVENT_ADD_TYPE), TopicIF.EVENT_ADDED);
    otree.addListener(new TopicIF_removed(topics, TopicIF.EVENT_REMOVE_TYPE), TopicIF.EVENT_REMOVED);

    otree.addListener(new TypedIF_added(assocs), AssociationIF.EVENT_ADDED);
    otree.addListener(new TypedIF_removed(assocs), AssociationIF.EVENT_REMOVED);
                          
    otree.addListener(new TypedIF_added(bnames), TopicNameIF.EVENT_ADDED);
    otree.addListener(new TypedIF_removed(bnames), TopicNameIF.EVENT_REMOVED);
                          
    otree.addListener(new TypedIF_added(occurs), OccurrenceIF.EVENT_ADDED);
    otree.addListener(new TypedIF_removed(occurs), OccurrenceIF.EVENT_REMOVED);
                          
    otree.addListener(new TypedIF_added(roles), AssociationRoleIF.EVENT_ADDED);
    otree.addListener(new TypedIF_removed(roles), AssociationRoleIF.EVENT_REMOVED);
    
    // Initialize object property event handlers
    handlers.put(TopicIF.EVENT_ADD_TYPE, new TopicIF_addType(topics));
    handlers.put(TopicIF.EVENT_REMOVE_TYPE, new TopicIF_removeType(topics));

    handlers.put(TopicNameIF.EVENT_SET_TYPE, new TypedIF_setType(bnames));
    handlers.put(OccurrenceIF.EVENT_SET_TYPE, new TypedIF_setType(occurs));
    handlers.put(AssociationRoleIF.EVENT_SET_TYPE, new TypedIF_setType(roles));
    handlers.put(AssociationIF.EVENT_SET_TYPE, new TypedIF_setType(assocs));

    // Register dynamic index as event listener
    Iterator iter = handlers.keySet().iterator();
    while (iter.hasNext()) {
      emanager.addListener(this, (String)iter.next());
    }
  }

  // -----------------------------------------------------------------------------
  // ClassInstanceIndexIF
  // -----------------------------------------------------------------------------
  
  public Collection getTopics(TopicIF topic_type) {
    Collection result = (Collection)topics.get(topic_type);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);
  }
  
  public Collection getTopicNames(TopicIF basename_type) {
    Collection result = (Collection)bnames.get(basename_type);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);
  }
  
  public Collection getOccurrences(TopicIF occurrence_type) {
    Collection result = (Collection)occurs.get(occurrence_type);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);
  }
  
  public Collection getAssociations(TopicIF association_type) {
    Collection result = (Collection)assocs.get(association_type);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);
  }

  public Collection getAssociationRoles(TopicIF association_role_type) {
    Collection result = (Collection)roles.get(association_role_type);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);
  }

  public Collection getTopicTypes() {
    // Create new collection
    Collection result = new ArrayList(topics.keySet());
    result.remove(null);
    return result;
  }
  
  public Collection getTopicNameTypes() {
    // Create new collection
    Collection result = new ArrayList(bnames.keySet());
    result.remove(null);
    return result;
  }
  
  public Collection getOccurrenceTypes() {
    // Create new collection
    Collection result = new ArrayList(occurs.keySet());
    result.remove(null);
    return result;
  }
  
  public Collection getAssociationTypes() {
    // Create new collection
    Collection result = new ArrayList(assocs.keySet());
    result.remove(null);
    return result;
  }
  
  public Collection getAssociationRoleTypes() {
    // Create new collection
    Collection result = new ArrayList(roles.keySet());
    result.remove(null);
    return result;
  }
  
  public boolean usedAsTopicType(TopicIF topic) {
    return topics.containsKey(topic);
  }

  public boolean usedAsTopicNameType(TopicIF topic) {
    return bnames.containsKey(topic);
  }

  public boolean usedAsOccurrenceType(TopicIF topic) {
    return occurs.containsKey(topic);
  }

  public boolean usedAsAssociationType(TopicIF topic) {
    return assocs.containsKey(topic);
  }
  
  public boolean usedAsAssociationRoleType(TopicIF topic) {
    return roles.containsKey(topic);
  }
  
  public boolean usedAsType(TopicIF topic) {
    return (topics.containsKey(topic) ||
            occurs.containsKey(topic) ||
            assocs.containsKey(topic) ||
            roles.containsKey(topic) ||
            bnames.containsKey(topic));
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  /**
   * EventHandler: TypedIF.setType
   */
  class TypedIF_setType extends EventHandler {
    protected CollectionMap objects;
    TypedIF_setType(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      objects.move(object, old_value, new_value);
    }
  }

  /**
   * EventHandler: TopicIF.addType
   */
  class TopicIF_addType extends EventHandler {
    protected CollectionMap objects;
    TopicIF_addType(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicIF topic = (TopicIF)object;

      // Register types
      Collection types = topic.getTypes();
      if (types.isEmpty())
        // Unregister null type
        objects.remove(null, topic);

      // Register type
      objects.add(new_value, topic);
    }
  }
  /**
   * EventHandler: TopicIF.removeType
   */
  class TopicIF_removeType extends EventHandler {
    protected CollectionMap objects;
    TopicIF_removeType(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicIF topic = (TopicIF)object;

      // Register types
      Collection types = topic.getTypes();
      if (types.size() == 1 && types.contains(old_value))
        // Unregister null type
        objects.add(null, topic);

      // Unregister type
      objects.remove(old_value, topic);
    }
  }

  /**
   * EventHandler: TopicIF.added
   */
  class TopicIF_added extends EventHandler {
    protected CollectionMap objects;
    protected String child_event;
    TopicIF_added(CollectionMap objects, String child_event) {
      this.objects = objects;
      this.child_event = child_event;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicIF added = (TopicIF)new_value;

      // Register types
      Collection types = added.getTypes();
      if (types.isEmpty())
        // Register the null type 
        objects.add(null, added);
      else {
        Object[] _types = types.toArray();
        for (int i=0; i < _types.length; i++)
          addEvent(added, child_event, _types[i]);
      }
    }
  }
  /**
   * EventHandler: TopicIF.removed
   */
  class TopicIF_removed extends EventHandler {
    protected CollectionMap objects;
    protected String child_event;
    TopicIF_removed(CollectionMap objects, String child_event) {
      this.objects = objects;
      this.child_event = child_event;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TopicIF removed = (TopicIF)old_value;

      // Unregister types
      Collection types = removed.getTypes();
      if (!types.isEmpty()) {
        Object[] _types = types.toArray();
        for (int i=0; i < _types.length; i++)
          removeEvent(removed, child_event, _types[i]);
      }
      // Unregister null type
      objects.remove(null, removed);

    }
  }
  /**
   * EventHandler: TypedIF.added
   */
  class TypedIF_added extends EventHandler {
    protected CollectionMap objects;
    TypedIF_added(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TypedIF added = (TypedIF)new_value;
      // Register type
      objects.add(added.getType(), added);
    }
  }
  /**
   * EventHandler: TypedIF.removed
   */
  class TypedIF_removed extends EventHandler {
    protected CollectionMap objects;
    TypedIF_removed(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      TypedIF removed = (TypedIF)old_value;
      // Unregister type
      objects.remove(removed.getType(), removed);
    }
  }
  
}
