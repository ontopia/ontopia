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
import java.util.Map;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.impl.utils.BasicIndex;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.DeciderIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.CollectionMap;
import net.ontopia.utils.ObjectUtils;

/**
 * INTERNAL: The basic dynamic name index implementation.
 */

public class NameIndex extends BasicIndex implements NameIndexIF {
  
  protected CollectionMap basenames;
  protected CollectionMap variants;

  NameIndex(IndexManagerIF imanager, EventManagerIF emanager, ObjectTreeManager otree) {
    
    // Initialize index maps
    basenames = new CollectionMap();
    variants = new CollectionMap();

    // Initialize object tree event handlers [objects added or removed]    
    otree.addListener(new TopicNameIF_added(basenames), TopicNameIF.EVENT_ADDED);
    otree.addListener(new TopicNameIF_removed(basenames), TopicNameIF.EVENT_REMOVED);

    otree.addListener(new VariantNameIF_added(variants), VariantNameIF.EVENT_ADDED);
    otree.addListener(new VariantNameIF_removed(variants), VariantNameIF.EVENT_REMOVED);

    // Initialize object property event handlers
    handlers.put(TopicNameIF.EVENT_SET_VALUE, new TopicNameIF_setValue(basenames));
    handlers.put(VariantNameIF.EVENT_SET_VALUE, new VariantNameIF_setValue(variants));

    // Register dynamic index as event listener
    Iterator iter = handlers.keySet().iterator();
    while (iter.hasNext()) {
      emanager.addListener(this, (String)iter.next());
    }
  }
  
  // -----------------------------------------------------------------------------
  // NameIndexIF
  // -----------------------------------------------------------------------------
  
  public Collection getTopicNames(String value) {
    Collection result = (Collection)basenames.get(value);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);
  }
  
  public Collection getVariants(String value) {
		return extractExactValues(variants, value);
  }
  
  public Collection getVariants(String value, final LocatorIF datatype) {
    return CollectionUtils.filterSet(extractExactValues(variants, value), new DeciderIF() {
        public boolean ok(Object o) {
          VariantNameIF vn = (VariantNameIF)o;
          return ObjectUtils.equals(vn.getDataType(), datatype);
        }
      });
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  /**
   * EventHandler: TopicNameIF.setValue
   */
  class TopicNameIF_setValue extends EventHandler {
    protected CollectionMap objects;
    TopicNameIF_setValue(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      objects.move(object, old_value, new_value);
    }
  }
  
  /**
   * EventHandler: TopicNameIF.added
   */
  class TopicNameIF_added extends EventHandler {
    protected CollectionMap objects;
    TopicNameIF_added(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      objects.add(((TopicNameIF)new_value).getValue(), new_value);
    }
  }
  /**
   * EventHandler: TopicNameIF.removed
   */
  class TopicNameIF_removed extends EventHandler {
    protected CollectionMap objects;
    TopicNameIF_removed(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      objects.remove(((TopicNameIF)old_value).getValue(), old_value);
    }
  }

  /**
   * EventHandler: VariantNameIF.setValue
   */
  class VariantNameIF_setValue extends EventHandler {
    protected CollectionMap objects;
    VariantNameIF_setValue(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      objects.move(object, old_value, new_value);
    }
  }
  
  /**
   * EventHandler: VariantNameIF.added
   */
  class VariantNameIF_added extends EventHandler {
    protected CollectionMap objects;
    VariantNameIF_added(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      objects.add(((VariantNameIF)new_value).getValue(), new_value);
    }
  }
  /**
   * EventHandler: VariantNameIF.removed
   */
  class VariantNameIF_removed extends EventHandler {
    protected CollectionMap objects;
    VariantNameIF_removed(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      objects.remove(((VariantNameIF)old_value).getValue(), old_value);
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
  private Collection extractExactValues(Map map, String value) {
    Collection result = (Collection)map.get(value);
    return result == null ? Collections.EMPTY_SET : new ArrayList(result);
  }
  
}
