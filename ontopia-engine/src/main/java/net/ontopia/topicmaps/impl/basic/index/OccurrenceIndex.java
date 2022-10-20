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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedMap;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.impl.utils.BasicIndex;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.utils.CollectionSortedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.AndPredicate;

/**
 * INTERNAL: The basic dynamic locator index implementation.
 */

public class OccurrenceIndex extends BasicIndex implements OccurrenceIndexIF {

  protected CollectionSortedMap<String, OccurrenceIF> occurs;
  
  OccurrenceIndex(IndexManagerIF imanager, EventManagerIF emanager, ObjectTreeManager otree) {
    
    // Initialize index maps
    occurs = new CollectionSortedMap<String, OccurrenceIF>(STRING_PREFIX_COMPARATOR);

    // Initialize object tree event handlers [objects added or removed]    
    otree.addListener(new OccurrenceIF_added(occurs), OccurrenceIF.EVENT_ADDED);
    otree.addListener(new OccurrenceIF_removed(occurs), OccurrenceIF.EVENT_REMOVED);

    // Initialize object property event handlers
    handlers.put(OccurrenceIF.EVENT_SET_VALUE, new OccurrenceIF_setValue(occurs));

    // Register dynamic index as event listener
    for (String handlerKey : handlers.keySet()) {
      emanager.addListener(this, handlerKey);
    }
  }

  // -----------------------------------------------------------------------------
  // Utility class
  // -----------------------------------------------------------------------------
  
  protected static final Comparator<String> STRING_PREFIX_COMPARATOR = new Comparator<String>() {
      // NOTE: need this comparator because otherwise we will get
      // null pointer exceptions when comparing with null values.
      @Override
      public int compare(String s1, String s2) {
        if (s1 == null) {
          return s2 == null ? 0 : -1;
        } 
        else {
          return s2 == null ? 1 : s1.compareTo(s2);
        }
      }
    };
  
  // ----------------------------------------------------------------------------
  // OccurrenceIndexIF
  // ----------------------------------------------------------------------------
  
  @Override
  public Collection<OccurrenceIF> getOccurrences(String value) {
    return extractExactValues(occurs, value);
  }
  

  @Override
  public Collection<OccurrenceIF> getOccurrences(String value, final TopicIF occurrenceType) {
    return CollectionUtils.select(extractExactValues(occurs, value), new TypedPredicate(occurrenceType));
  }

  @Override
  public Collection<OccurrenceIF> getOccurrences(String value, final LocatorIF datatype) {
    return CollectionUtils.select(extractExactValues(occurs, value), new DataTypePredicate(datatype));
  }

  @Override
  public Collection<OccurrenceIF> getOccurrences(String value, final LocatorIF datatype, final TopicIF occurrenceType) {
    return CollectionUtils.select(extractExactValues(occurs, value), 
            new AndPredicate<>(new DataTypePredicate(datatype), new TypedPredicate(occurrenceType)));
  }

  @Override
  public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix) {
    return extractPrefixValues(occurs, prefix);
  }

  @Override
  public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix, final LocatorIF datatype) {
    return CollectionUtils.select(extractPrefixValues(occurs, prefix), new DataTypePredicate(datatype));
  }

  @Override
  public Iterator<String> getValuesGreaterThanOrEqual(String value) {
    return occurs.tailMap(value).keySet().iterator();
  }

  @Override
  public Iterator<String> getValuesSmallerThanOrEqual(String value) {
    return occurs.headMap(value, true).navigableKeySet().descendingIterator();
  }
  
  // ----------------------------------------------------------------------------
  // Helper methods
  // ----------------------------------------------------------------------------

  private <E> Collection<E> extractExactValues(CollectionSortedMap<String, E> map, String value) {
    Collection<E> result = map.get(value);
    if (result == null) {
      return new ArrayList<E>();
    }
    // Create new collection
    return new ArrayList<E>(result);
  }

  /**
   * INTERNAL: utility method used to extract all keys from the sorted
   * map that matches the prefix and aggregate all values stores as
   * entry values.
   */
  private <E> Collection<E> extractPrefixValues(CollectionSortedMap<String, E> map, String prefix) {
    Collection<E> result = null;
    SortedMap<String, Collection<E>> tail = map.tailMap(prefix);
    Iterator<String> iter = tail.keySet().iterator();
    while (iter.hasNext()) {
      String key = iter.next();
      if (key == null || !key.startsWith(prefix)) {
        break;
      }
      // add values to result
      if (result == null) {
        result = new HashSet<E>();
      }
      Collection<E> c = map.get(key);
      result.addAll(c);
      
    }
    return (result == null ? new HashSet<E>() : result);
  }

  private class DataTypePredicate implements Predicate<OccurrenceIF> {

    private final LocatorIF datatype;

    public DataTypePredicate(LocatorIF datatype) {
      this.datatype = datatype;
    }
    
    @Override
    public boolean evaluate(OccurrenceIF occurrence) {
      return Objects.equals(occurrence.getDataType(), datatype);
    }
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  /**
   * EventHandler: OccurrenceIF.setValue
   */
  class OccurrenceIF_setValue extends EventHandler<OccurrenceIF, String> {
    protected CollectionSortedMap<String, OccurrenceIF> objects;
    OccurrenceIF_setValue(CollectionSortedMap<String, OccurrenceIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(OccurrenceIF object, String event, String new_value, String old_value) {
      objects.move(object, old_value, new_value);
    }
  }
  
  /**
   * EventHandler: OccurrenceIF.added
   */
  class OccurrenceIF_added extends EventHandler<Object, OccurrenceIF> {
    protected CollectionSortedMap<String, OccurrenceIF> objects;
    OccurrenceIF_added(CollectionSortedMap<String, OccurrenceIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(Object object, String event, OccurrenceIF new_value, OccurrenceIF old_value) {
      objects.add(new_value.getValue(), new_value);
    }
  }
  /**
   * EventHandler: OccurrenceIF.removed
   */
  class OccurrenceIF_removed extends EventHandler<Object, OccurrenceIF> {
    protected CollectionSortedMap<String, OccurrenceIF> objects;
    OccurrenceIF_removed(CollectionSortedMap<String, OccurrenceIF> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(Object object, String event, OccurrenceIF new_value, OccurrenceIF old_value) {
      objects.remove(old_value.getValue(), old_value);
    }
  }
 
}

