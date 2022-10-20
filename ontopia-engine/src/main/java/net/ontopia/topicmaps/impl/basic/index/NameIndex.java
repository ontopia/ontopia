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
import java.util.Map;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.impl.utils.BasicIndex;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.utils.CollectionMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

/**
 * INTERNAL: The basic dynamic name index implementation.
 */

public class NameIndex extends BasicIndex implements NameIndexIF {
  
  protected CollectionMap<String, TopicNameIF> basenames;
  protected CollectionMap<String, VariantNameIF> variants;

  NameIndex(IndexManagerIF imanager, EventManagerIF emanager, ObjectTreeManager otree) {
    
    // Initialize index maps
    basenames = new CollectionMap<String, TopicNameIF>();
    variants = new CollectionMap<String, VariantNameIF>();

    // Initialize object tree event handlers [objects added or removed]    
    otree.addListener(new TopicNameIF_added(basenames), TopicNameIF.EVENT_ADDED);
    otree.addListener(new TopicNameIF_removed(basenames), TopicNameIF.EVENT_REMOVED);

    otree.addListener(new VariantNameIF_added(variants), VariantNameIF.EVENT_ADDED);
    otree.addListener(new VariantNameIF_removed(variants), VariantNameIF.EVENT_REMOVED);

    // Initialize object property event handlers
    handlers.put(TopicNameIF.EVENT_SET_VALUE, new TopicNameIF_setValue(basenames));
    handlers.put(VariantNameIF.EVENT_SET_VALUE, new VariantNameIF_setValue(variants));

    // Register dynamic index as event listener
    for (String handlerKey : handlers.keySet()) {
      emanager.addListener(this, handlerKey);
    }
  }
  
  // -----------------------------------------------------------------------------
  // NameIndexIF
  // -----------------------------------------------------------------------------
  
  @Override
  public Collection<TopicNameIF> getTopicNames(String value) {
    Collection<TopicNameIF> result = basenames.get(value);
    if (result == null) {
      return new ArrayList<TopicNameIF>();
    }
    // Create new collection
    return new ArrayList<TopicNameIF>(result);
  }
  
  @Override
  public Collection<TopicNameIF> getTopicNames(String value, final TopicIF topicNameType) {
    return CollectionUtils.select(extractExactValues(basenames, value), new TypedPredicate(topicNameType));
  }

  @Override
  public Collection<VariantNameIF> getVariants(String value) {
		return extractExactValues(variants, value);
  }
  
  @Override
  public Collection<VariantNameIF> getVariants(String value, final LocatorIF datatype) {
    return CollectionUtils.select(extractExactValues(variants, value), new Predicate<VariantNameIF>() {
      @Override
      public boolean evaluate(VariantNameIF vn) {
        return Objects.equals(vn.getDataType(), datatype);
      }
    });
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  /**
   * EventHandler: TopicNameIF.setValue
   */
  class TopicNameIF_setValue extends EventHandler<TopicNameIF, String> {
    protected CollectionMap<String, TopicNameIF> objects;
    TopicNameIF_setValue(CollectionMap<String, TopicNameIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(TopicNameIF object, String event, String new_value, String old_value) {
      objects.move(object, old_value, new_value);
    }
  }
  
  /**
   * EventHandler: TopicNameIF.added
   */
  class TopicNameIF_added extends EventHandler<Object, TopicNameIF> {
    protected CollectionMap<String, TopicNameIF> objects;
    TopicNameIF_added(CollectionMap<String, TopicNameIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(Object object, String event, TopicNameIF new_value, TopicNameIF old_value) {
      objects.add(new_value.getValue(), new_value);
    }
  }
  /**
   * EventHandler: TopicNameIF.removed
   */
  class TopicNameIF_removed extends EventHandler<Object, TopicNameIF> {
    protected CollectionMap<String, TopicNameIF> objects;
    TopicNameIF_removed(CollectionMap<String, TopicNameIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(Object object, String event, TopicNameIF new_value, TopicNameIF old_value) {
      objects.remove(old_value.getValue(), old_value);
    }
  }

  /**
   * EventHandler: VariantNameIF.setValue
   */
  class VariantNameIF_setValue extends EventHandler<VariantNameIF, String> {
    protected CollectionMap<String, VariantNameIF> objects;
    VariantNameIF_setValue(CollectionMap<String, VariantNameIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(VariantNameIF object, String event, String new_value, String old_value) {
      objects.move(object, old_value, new_value);
    }
  }
  
  /**
   * EventHandler: VariantNameIF.added
   */
  class VariantNameIF_added extends EventHandler<Object, VariantNameIF> {
    protected CollectionMap<String, VariantNameIF> objects;
    VariantNameIF_added(CollectionMap<String, VariantNameIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(Object object, String event, VariantNameIF new_value, VariantNameIF old_value) {
      objects.add(new_value.getValue(), new_value);
    }
  }
  /**
   * EventHandler: VariantNameIF.removed
   */
  class VariantNameIF_removed extends EventHandler<Object, VariantNameIF> {
    protected CollectionMap<String, VariantNameIF> objects;
    VariantNameIF_removed(CollectionMap<String, VariantNameIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(Object object, String event, VariantNameIF new_value, VariantNameIF old_value) {
      objects.remove(old_value.getValue(), old_value);
    }
  }

  // -----------------------------------------------------------------------------
  // Helper methods
  // -----------------------------------------------------------------------------

  /**
   * Returns the collection under the specified {@code value}.
   * 
   * @return An immutable collection if the {@code value} does not exist or a modifiable
   *          collection iff {@code value} exists in the {@code map}.
   */
  private <E> Collection<E> extractExactValues(Map<String, Collection<E>> map, String value) {
    Collection<E> result = map.get(value);
    return result == null ? new ArrayList<E>() : new ArrayList<E>(result);
  }
  
}
