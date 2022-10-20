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
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.impl.utils.BasicIndex;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.utils.CollectionMap;

/**
 * INTERNAL: The basic dynamic scope index implementation.
 */

public class ScopeIndex extends BasicIndex implements ScopeIndexIF {
  
  protected CollectionMap<TopicIF, TopicNameIF> basenames;
  protected CollectionMap<TopicIF, VariantNameIF> variants;
  protected CollectionMap<TopicIF, OccurrenceIF> occurs;
  protected CollectionMap<TopicIF, AssociationIF> assocs;

  ScopeIndex(IndexManagerIF imanager, EventManagerIF emanager, ObjectTreeManager otree) {

    // Initialize index maps
    basenames = new CollectionMap<TopicIF, TopicNameIF>();
    variants = new CollectionMap<TopicIF, VariantNameIF>();
    occurs = new CollectionMap<TopicIF, OccurrenceIF>();
    assocs = new CollectionMap<TopicIF, AssociationIF>();

    // Initialize object tree event handlers [objects added or removed]    
    otree.addListener(new ScopedIF_added<TopicNameIF>(basenames, TopicNameIF.EVENT_ADD_THEME), TopicNameIF.EVENT_ADDED);
    otree.addListener(new ScopedIF_removed<TopicNameIF>(basenames, TopicNameIF.EVENT_REMOVE_THEME), TopicNameIF.EVENT_REMOVED);
                          
    otree.addListener(new ScopedIF_added<VariantNameIF>(variants, VariantNameIF.EVENT_ADD_THEME), VariantNameIF.EVENT_ADDED);
    otree.addListener(new ScopedIF_removed<VariantNameIF>(variants, VariantNameIF.EVENT_REMOVE_THEME), VariantNameIF.EVENT_REMOVED);
                          
    otree.addListener(new ScopedIF_added<OccurrenceIF>(occurs, OccurrenceIF.EVENT_ADD_THEME), OccurrenceIF.EVENT_ADDED);
    otree.addListener(new ScopedIF_removed<OccurrenceIF>(occurs, OccurrenceIF.EVENT_REMOVE_THEME), OccurrenceIF.EVENT_REMOVED);
                          
    otree.addListener(new ScopedIF_added<AssociationIF>(assocs, AssociationIF.EVENT_ADD_THEME), AssociationIF.EVENT_ADDED);
    otree.addListener(new ScopedIF_removed<AssociationIF>(assocs, AssociationIF.EVENT_REMOVE_THEME), AssociationIF.EVENT_REMOVED);
        
    // Initialize object property event handlers
    handlers.put(TopicNameIF.EVENT_ADD_THEME, new ScopedIF_addTheme<TopicNameIF>(basenames));
    handlers.put(TopicNameIF.EVENT_REMOVE_THEME, new ScopedIF_removeTheme<TopicNameIF>(basenames));

    handlers.put(VariantNameIF.EVENT_ADD_THEME, new ScopedIF_addTheme<VariantNameIF>(variants));
    handlers.put(VariantNameIF.EVENT_REMOVE_THEME, new ScopedIF_removeTheme<VariantNameIF>(variants));

    handlers.put(OccurrenceIF.EVENT_ADD_THEME, new ScopedIF_addTheme<OccurrenceIF>(occurs));
    handlers.put(OccurrenceIF.EVENT_REMOVE_THEME, new ScopedIF_removeTheme<OccurrenceIF>(occurs));

    handlers.put(AssociationIF.EVENT_ADD_THEME, new ScopedIF_addTheme<AssociationIF>(assocs));
    handlers.put(AssociationIF.EVENT_REMOVE_THEME, new ScopedIF_removeTheme<AssociationIF>(assocs));
    
    // Register dynamic index as event listener
    for (String handlerKey : handlers.keySet()) {
      emanager.addListener(this, handlerKey);
    }
  }

  // -----------------------------------------------------------------------------
  // ScopeIndexIF
  // -----------------------------------------------------------------------------
    
  @Override
  public Collection<TopicNameIF> getTopicNames(TopicIF theme) {
    Collection<TopicNameIF> result = basenames.get(theme);
    if (result == null) {
      return Collections.<TopicNameIF>emptySet();
    }
    // Create new collection
    return new ArrayList<TopicNameIF>(result);    
  }
  
  @Override
  public Collection<VariantNameIF> getVariants(TopicIF theme) {
    Collection<VariantNameIF> result = variants.get(theme);
    if (result == null) {
      return Collections.<VariantNameIF>emptySet();
    }
    // Create new collection
    return new ArrayList<VariantNameIF>(result);    
  }
  
  @Override
  public Collection<OccurrenceIF> getOccurrences(TopicIF theme) {
    Collection result = (Collection)occurs.get(theme);
    if (result == null) {
      return Collections.<OccurrenceIF>emptySet();
    }
    // Create new collection
    return new ArrayList(result);    
  }
  
  @Override
  public Collection<AssociationIF> getAssociations(TopicIF theme) {
    Collection<AssociationIF> result = assocs.get(theme);
    if (result == null) {
      return Collections.<AssociationIF>emptySet();
    }
    // Create new collection
    return new ArrayList<AssociationIF>(result);    
  }
    
  @Override
  public Collection<TopicIF> getTopicNameThemes() {
    // Create new collection
    Collection<TopicIF> result = new ArrayList(basenames.keySet());
    result.remove(null);
    return result;
  }

  @Override
  public Collection<TopicIF> getVariantThemes() {
    // Create new collection
    Collection<TopicIF> result = new ArrayList(variants.keySet()); 
    result.remove(null);
    return result;
 }

 @Override
  public Collection<TopicIF> getOccurrenceThemes() {
    // Create new collection
    Collection<TopicIF> result = new ArrayList(occurs.keySet());
    result.remove(null);
    return result;
  }
  
  @Override
  public Collection<TopicIF> getAssociationThemes() {
    // Create new collection
    Collection<TopicIF> result = new ArrayList(assocs.keySet());
    result.remove(null);
    return result;
  }

  @Override
  public boolean usedAsTopicNameTheme(TopicIF topic) {
    return basenames.containsKey(topic);
  }

  @Override
  public boolean usedAsVariantTheme(TopicIF topic) {
    return variants.containsKey(topic);
  }

  @Override
  public boolean usedAsOccurrenceTheme(TopicIF topic) {
    return occurs.containsKey(topic);
  }

  @Override
  public boolean usedAsAssociationTheme(TopicIF topic) {
    return assocs.containsKey(topic);
  }
  
  @Override
  public boolean usedAsTheme(TopicIF topic) {
    return (basenames.containsKey(topic) ||
            variants.containsKey(topic) ||
            occurs.containsKey(topic) ||
            assocs.containsKey(topic));
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  /**
   * EventHandler: ScopedIF.addTheme
   */
  class ScopedIF_addTheme<S extends ScopedIF> extends EventHandler<S, TopicIF> {
    protected CollectionMap<TopicIF, S> objects;
    ScopedIF_addTheme(CollectionMap<TopicIF, S> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(S scoped, String event, TopicIF new_value, TopicIF old_value) {
      // Register scope
      Collection<TopicIF> scope = scoped.getScope();
      if (scope.isEmpty()) {
        // Unregister null theme
        objects.remove(null, scoped);
      }

      // Register theme
      objects.add(new_value, scoped);
    }
  }
  /**
   * EventHandler: ScopedIF.removeTheme
   */
  class ScopedIF_removeTheme<S extends ScopedIF> extends EventHandler<S, TopicIF> {
    protected CollectionMap<TopicIF, S> objects;
    ScopedIF_removeTheme(CollectionMap<TopicIF, S> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(S scoped, String event, TopicIF new_value, TopicIF old_value) {
      // Register themes
      Collection<TopicIF> scope = scoped.getScope();
      if (scope.size() == 1 && scope.contains(old_value)) {
        // Unregister null theme
        objects.add(null, scoped);
      }

      // Unregister theme
      objects.remove(old_value, scoped);
    }
  }

  /**
   * EventHandler: ScopedIF.added
   */
  class ScopedIF_added<S extends ScopedIF> extends EventHandler<Object, S> {
    protected CollectionMap<TopicIF, S> objects;
    protected String child_event;
    ScopedIF_added(CollectionMap<TopicIF, S> objects, String child_event) {
      this.objects = objects;
      this.child_event = child_event;
    }
    @Override
    public void processEvent(Object object, String event, S added, S old_value) {
      // Register themes
      Collection<TopicIF> scope = added.getScope();
      if (scope.isEmpty()) {
        // Register the null theme
        objects.add(null, added);       
      } else {
        for (TopicIF _scope : scope) {
          addEvent(added, child_event, _scope);
        }
      }
    }
  }
  /**
   * EventHandler: ScopedIF.removed
   */
  class ScopedIF_removed<S extends ScopedIF> extends EventHandler<Object, S> {
    protected CollectionMap<TopicIF, S> objects;
    protected String child_event;
    ScopedIF_removed(CollectionMap<TopicIF, S> objects, String child_event) {
      this.objects = objects;
      this.child_event = child_event;
    }
    @Override
    public void processEvent(Object object, String event, S new_value, S removed) {
      // Unregister themes
      Collection<TopicIF> scope = removed.getScope();
      if (!scope.isEmpty()) {
        for (TopicIF _scope : scope) {
          removeEvent(removed, child_event, _scope);
        }
      }
      // Unregister null theme
      objects.remove(null, removed);
    }
  }
  
}





