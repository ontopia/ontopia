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
import java.util.Collections;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.utils.BasicIndex;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.utils.CollectionMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.CompositeCollection;

/**
 * INTERNAL: The basic dynamic class instance index implementation.
 */

public class ClassInstanceIndex extends BasicIndex implements ClassInstanceIndexIF {
  
  protected CollectionMap<TopicIF, TopicIF> topics;
  protected CollectionMap<TopicIF, TopicNameIF> bnames;
  protected CollectionMap<TopicIF, OccurrenceIF> occurs;
  protected CollectionMap<TopicIF, AssociationIF> assocs;
  protected CollectionMap<TopicIF, AssociationRoleIF> roles;
  protected final TopicMapIF topicmap;

  ClassInstanceIndex(IndexManagerIF imanager, EventManagerIF emanager, ObjectTreeManager otree) {
    
    // Initialize index maps
    topics = new CollectionMap<TopicIF, TopicIF>();
    bnames = new CollectionMap<TopicIF, TopicNameIF>();
    occurs = new CollectionMap<TopicIF, OccurrenceIF>();
    assocs = new CollectionMap<TopicIF, AssociationIF>();
    roles = new CollectionMap<TopicIF, AssociationRoleIF>();
    
    this.topicmap = imanager.getTransaction().getTopicMap();
    
    // Initialize object tree event handlers [objects added or removed]    
    otree.addListener(new TopicIF_added(topics, TopicIF.EVENT_ADD_TYPE), TopicIF.EVENT_ADDED);
    otree.addListener(new TopicIF_removed(topics, TopicIF.EVENT_REMOVE_TYPE), TopicIF.EVENT_REMOVED);

    otree.addListener(new TypedIF_added<AssociationIF>(assocs), AssociationIF.EVENT_ADDED);
    otree.addListener(new TypedIF_removed<AssociationIF>(assocs), AssociationIF.EVENT_REMOVED);
                          
    otree.addListener(new TypedIF_added<TopicNameIF>(bnames), TopicNameIF.EVENT_ADDED);
    otree.addListener(new TypedIF_removed<TopicNameIF>(bnames), TopicNameIF.EVENT_REMOVED);
                          
    otree.addListener(new TypedIF_added<OccurrenceIF>(occurs), OccurrenceIF.EVENT_ADDED);
    otree.addListener(new TypedIF_removed<OccurrenceIF>(occurs), OccurrenceIF.EVENT_REMOVED);
                          
    otree.addListener(new TypedIF_added<AssociationRoleIF>(roles), AssociationRoleIF.EVENT_ADDED);
    otree.addListener(new TypedIF_removed<AssociationRoleIF>(roles), AssociationRoleIF.EVENT_REMOVED);
    
    // Initialize object property event handlers
    handlers.put(TopicIF.EVENT_ADD_TYPE, new TopicIF_addType(topics));
    handlers.put(TopicIF.EVENT_REMOVE_TYPE, new TopicIF_removeType(topics));

    handlers.put(TopicNameIF.EVENT_SET_TYPE, new TypedIF_setType<TopicNameIF>(bnames));
    handlers.put(OccurrenceIF.EVENT_SET_TYPE, new TypedIF_setType<OccurrenceIF>(occurs));
    handlers.put(AssociationRoleIF.EVENT_SET_TYPE, new TypedIF_setType<AssociationRoleIF>(roles));
    handlers.put(AssociationIF.EVENT_SET_TYPE, new TypedIF_setType<AssociationIF>(assocs));

    // Register dynamic index as event listener
    for (String handler : handlers.keySet()) {
      emanager.addListener(this, handler);
    }
  }

  // -----------------------------------------------------------------------------
  // ClassInstanceIndexIF
  // -----------------------------------------------------------------------------
  
  @Override
  public Collection<TopicIF> getTopics(TopicIF topic_type) {
    return topics.containsKey(topic_type) ? 
            Collections.<TopicIF>unmodifiableCollection(
                    new ArrayList<TopicIF>(topics.get(topic_type))) :
            Collections.<TopicIF>emptyList();
  }
  
  @Override
  public Collection<TopicNameIF> getTopicNames(TopicIF basename_type) {
    if (basename_type == null) {
      basename_type = topicmap.getTopicBySubjectIdentifier(PSI.getSAMNameType());
    }
    return bnames.containsKey(basename_type) ? 
            Collections.<TopicNameIF>unmodifiableCollection(
                    new ArrayList<TopicNameIF>(bnames.get(basename_type))) :
            Collections.<TopicNameIF>emptyList();
  }
  
  @Override
  public Collection<TopicNameIF> getAllTopicNames() {
    return Collections.<TopicNameIF>unmodifiableCollection(
            createComposite(bnames.values())
    );
  }

  @Override
  public Collection<VariantNameIF> getAllVariantNames() {
    Collection<Collection<VariantNameIF>> collected = CollectionUtils.collect(getAllTopicNames(), 
            new Transformer<TopicNameIF, Collection<VariantNameIF>>() {
      @Override
      public Collection<VariantNameIF> transform(TopicNameIF input) {
        return input.getVariants();
      }
    });
    return createComposite(collected);
  }

  @Override
  public Collection<OccurrenceIF> getOccurrences(TopicIF occurrence_type) {
    return occurs.containsKey(occurrence_type) ? 
            Collections.<OccurrenceIF>unmodifiableCollection(
                    new ArrayList<OccurrenceIF>(occurs.get(occurrence_type))) :
            Collections.<OccurrenceIF>emptyList();
  }

  @Override
  public Collection<OccurrenceIF> getAllOccurrences() {
    return Collections.<OccurrenceIF>unmodifiableCollection(
            createComposite(occurs.values())
    );
  }
  
  @Override
  public Collection<AssociationIF> getAssociations(TopicIF association_type) {
    return assocs.containsKey(association_type) ? 
            Collections.<AssociationIF>unmodifiableCollection(
                    new ArrayList<AssociationIF>(assocs.get(association_type))) :
            Collections.<AssociationIF>emptyList();
  }

  @Override
  public Collection<AssociationRoleIF> getAssociationRoles(TopicIF association_role_type) {
    return roles.containsKey(association_role_type) ? 
            Collections.<AssociationRoleIF>unmodifiableCollection(
                    new ArrayList<AssociationRoleIF>(roles.get(association_role_type))) :
            Collections.<AssociationRoleIF>emptyList();
  }

  @Override
  public Collection<AssociationRoleIF> getAssociationRoles(TopicIF association_role_type, final TopicIF association_type) {
    if (roles.containsKey(association_role_type)) {
      return CollectionUtils.select(roles.get(association_role_type), new Predicate<AssociationRoleIF>() {
        @Override
        public boolean evaluate(AssociationRoleIF role) {
          return role.getAssociation().getType().equals(association_type);
        }
      });
    } else {
      return Collections.<AssociationRoleIF>emptyList();
    }
  }

  @Override
  public Collection<TopicIF> getTopicTypes() {
    // Create new collection
    Collection<TopicIF> result = new ArrayList<TopicIF>(topics.keySet());
    result.remove(null);
    return Collections.unmodifiableCollection(result);
  }
  
  @Override
  public Collection<TopicIF> getTopicNameTypes() {
    // Create new collection
    Collection<TopicIF> result = new ArrayList<TopicIF>(bnames.keySet());
    result.remove(null);
    return Collections.unmodifiableCollection(result);
  }
  
  @Override
  public Collection<TopicIF> getOccurrenceTypes() {
    // Create new collection
    Collection<TopicIF> result = new ArrayList<TopicIF>(occurs.keySet());
    result.remove(null);
    return Collections.unmodifiableCollection(result);
  }
  
  @Override
  public Collection<TopicIF> getAssociationTypes() {
    // Create new collection
    Collection<TopicIF> result = new ArrayList<TopicIF>(assocs.keySet());
    result.remove(null);
    return Collections.unmodifiableCollection(result);
  }
  
  @Override
  public Collection<TopicIF> getAssociationRoleTypes() {
    // Create new collection
    Collection<TopicIF> result = new ArrayList<TopicIF>(roles.keySet());
    result.remove(null);
    return Collections.unmodifiableCollection(result);
  }
  
  @Override
  public boolean usedAsTopicType(TopicIF topic) {
    if (topic == null) { return false; }
    return topics.containsKey(topic);
  }

  @Override
  public boolean usedAsTopicNameType(TopicIF topic) {
    return bnames.containsKey(topic);
  }

  @Override
  public boolean usedAsOccurrenceType(TopicIF topic) {
    return occurs.containsKey(topic);
  }

  @Override
  public boolean usedAsAssociationType(TopicIF topic) {
    return assocs.containsKey(topic);
  }
  
  @Override
  public boolean usedAsAssociationRoleType(TopicIF topic) {
    return roles.containsKey(topic);
  }
  
  @Override
  public boolean usedAsType(TopicIF topic) {
    if (topic == null) { return false; }
    return (topics.containsKey(topic) ||
            occurs.containsKey(topic) ||
            assocs.containsKey(topic) ||
            roles.containsKey(topic) ||
            bnames.containsKey(topic));
  }

  // -----------------------------------------------------------------------------
  // Utilities
  // -----------------------------------------------------------------------------

  /**
   * Avoids creating a generic array, which is converted back to list in CompositeCollection.
   */
  private <T extends TMObjectIF> CompositeCollection<T> createComposite(Collection<Collection<T>> collections) {
    CompositeCollection<T> result = new CompositeCollection<>();
    for (Collection<T> collection : collections) {
      result.addComposited(collection);
    }
    return result;
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  /**
   * EventHandler: TypedIF.setType
   */
  class TypedIF_setType<T extends TypedIF> extends EventHandler<T, TopicIF> {
    protected CollectionMap<TopicIF, T> objects;
    TypedIF_setType(CollectionMap<TopicIF, T> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(T object, String event, TopicIF new_value, TopicIF old_value) {
      objects.move(object, old_value, new_value);
    }
  }

  /**
   * EventHandler: TopicIF.addType
   */
  class TopicIF_addType extends EventHandler<TopicIF, TopicIF> {
    protected CollectionMap<TopicIF, TopicIF> objects;
    TopicIF_addType(CollectionMap<TopicIF, TopicIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(TopicIF topic, String event, TopicIF new_value, TopicIF old_value) {
      // Register types
      Collection<TopicIF> types = topic.getTypes();
      if (types.isEmpty()) {
        // Unregister null type
        objects.remove(null, topic);
      }

      // Register type
      objects.add(new_value, topic);
    }
  }
  /**
   * EventHandler: TopicIF.removeType
   */
  class TopicIF_removeType extends EventHandler<TopicIF, TopicIF> {
    protected CollectionMap<TopicIF, TopicIF> objects;
    TopicIF_removeType(CollectionMap<TopicIF, TopicIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(TopicIF topic, String event, TopicIF new_value, TopicIF old_value) {
      // Register types
      Collection<TopicIF> types = topic.getTypes();
      if (types.size() == 1 && types.contains(old_value)) {
        // Unregister null type
        objects.add(null, topic);
      }

      // Unregister type
      objects.remove(old_value, topic);
    }
  }

  /**
   * EventHandler: TopicIF.added
   */
  class TopicIF_added extends EventHandler<Object, TopicIF> {
    protected CollectionMap<TopicIF, TopicIF> objects;
    protected String child_event;
    TopicIF_added(CollectionMap<TopicIF, TopicIF> objects, String child_event) {
      this.objects = objects;
      this.child_event = child_event;
    }
    @Override
    public void processEvent(Object object, String event, TopicIF added, TopicIF old_value) {
      // Register types
      Collection<TopicIF> types = added.getTypes();
      if (types.isEmpty()) {
        // Register the null type 
        objects.add(null, added);
      } else {
        for (TopicIF type : types) {
          addEvent(added, child_event, type);
        }
      }
    }
  }
  /**
   * EventHandler: TopicIF.removed
   */
  class TopicIF_removed extends EventHandler<Object, TopicIF> {
    protected CollectionMap<TopicIF, TopicIF> objects;
    protected String child_event;
    TopicIF_removed(CollectionMap<TopicIF, TopicIF> objects, String child_event) {
      this.objects = objects;
      this.child_event = child_event;
    }
    @Override
    public void processEvent(Object object, String event, TopicIF new_value, TopicIF removed) {
      // Unregister types
      Collection<TopicIF> types = removed.getTypes();
      if (!types.isEmpty()) {
        for (TopicIF type : types) {
          removeEvent(removed, child_event, type);
        }
      }
      // Unregister null type
      objects.remove(null, removed);

    }
  }
  /**
   * EventHandler: TypedIF.added
   */
  class TypedIF_added<T extends TypedIF> extends EventHandler<Object, T> {
    protected CollectionMap<TopicIF, T> objects;
    TypedIF_added(CollectionMap<TopicIF, T> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(Object object, String event, T added, T old_value) {
      // Register type
      objects.add(added.getType(), added);
    }
  }
  /**
   * EventHandler: TypedIF.removed
   */
  class TypedIF_removed<T extends TypedIF> extends EventHandler<Object, T> {
    protected CollectionMap<TopicIF, T> objects;
    TypedIF_removed(CollectionMap<TopicIF, T> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(Object object, String event, T new_value, T removed) {
      // Unregister type
      objects.remove(removed.getType(), removed);
    }
  }
  
}
