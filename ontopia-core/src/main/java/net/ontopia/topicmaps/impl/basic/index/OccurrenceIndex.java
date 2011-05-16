
// $Id: OccurrenceIndex.java,v 1.13 2008/07/15 09:02:07 lars.garshol Exp $

package net.ontopia.topicmaps.impl.basic.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedMap;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.impl.utils.BasicIndex;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.utils.DeciderIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.CollectionSortedMap;
import net.ontopia.utils.ObjectUtils;

/**
 * INTERNAL: The basic dynamic locator index implementation.
 */

public class OccurrenceIndex extends BasicIndex implements OccurrenceIndexIF {

  protected CollectionSortedMap occurs;
  
  OccurrenceIndex(IndexManagerIF imanager, EventManagerIF emanager, ObjectTreeManager otree) {
    
    // Initialize index maps
    occurs = new CollectionSortedMap(STRING_PREFIX_COMPARATOR);

    // Initialize object tree event handlers [objects added or removed]    
    otree.addListener(new OccurrenceIF_added(occurs), "OccurrenceIF.added");
    otree.addListener(new OccurrenceIF_removed(occurs), "OccurrenceIF.removed");

    // Initialize object property event handlers
    handlers.put("OccurrenceIF.setValue", new OccurrenceIF_setValue(occurs));

    // Register dynamic index as event listener
    Iterator iter = handlers.keySet().iterator();
    while (iter.hasNext())
      emanager.addListener(this, (String)iter.next());
  }

  // -----------------------------------------------------------------------------
  // Utility class
  // -----------------------------------------------------------------------------
  
  protected static final Comparator STRING_PREFIX_COMPARATOR = new Comparator() {
      // NOTE: need this comparator because otherwise we will get
      // null pointer exceptions when comparing with null values.
      public int compare(Object o1, Object o2) {
        String s1 = (String)o1;
        String s2 = (String)o2;
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
  
  public Collection getOccurrences(String value) {
    return extractExactValues(occurs, value);
  }
  
  public Collection getOccurrences(String value, final LocatorIF datatype) {
    return CollectionUtils.filterSet(extractExactValues(occurs, value), new DeciderIF() {
        public boolean ok(Object o) {
          OccurrenceIF occ = (OccurrenceIF)o;
          return ObjectUtils.equals(occ.getDataType(), datatype);
        }
      });
  }

  public Collection getOccurrencesByPrefix(String prefix) {
    return extractPrefixValues(occurs, prefix);
  }

  public Collection getOccurrencesByPrefix(String prefix, final LocatorIF datatype) {
    return CollectionUtils.filterSet(extractPrefixValues(occurs, prefix), new DeciderIF() {
        public boolean ok(Object o) {
          OccurrenceIF occ = (OccurrenceIF)o;
          return ObjectUtils.equals(occ.getDataType(), datatype);
        }
      });
  }

  public Iterator getValuesGreaterThanOrEqual(String value) {
    return occurs.tailMap(value).keySet().iterator();
  }

  public Iterator getValuesSmallerThanOrEqual(String value) {
    return occurs.headMap(value, true).navigableKeySet().descendingIterator();
  }
  
  // ----------------------------------------------------------------------------
  // Helper methods
  // ----------------------------------------------------------------------------

  private Collection extractExactValues(CollectionSortedMap map, String value) {
    Collection result = (Collection)map.get(value);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);
  }

  /**
   * INTERNAL: utility method used to extract all keys from the sorted
   * map that matches the prefix and aggregate all values stores as
   * entry values.
   */
  private Collection extractPrefixValues(CollectionSortedMap map, String prefix) {
    Collection result = null;
    SortedMap tail = map.tailMap(prefix);
    Iterator iter = tail.keySet().iterator();
    while (iter.hasNext()) {
      String key = (String)iter.next();
      if (key == null || !key.startsWith(prefix)) {
        break;
      }
      // add values to result
      if (result == null) result = new HashSet();
      Collection c = (Collection)map.get(key);
      result.addAll(c);
      
    }
    return (result == null ? Collections.EMPTY_SET : result);
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  /**
   * EventHandler: OccurrenceIF.setValue
   */
  class OccurrenceIF_setValue extends EventHandler {
    protected CollectionSortedMap objects;
    OccurrenceIF_setValue(CollectionSortedMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      objects.move(object, old_value, new_value);
    }
  }
  
  /**
   * EventHandler: OccurrenceIF.added
   */
  class OccurrenceIF_added extends EventHandler {
    protected CollectionSortedMap objects;
    OccurrenceIF_added(CollectionSortedMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      objects.add(((OccurrenceIF)new_value).getValue(), new_value);
    }
  }
  /**
   * EventHandler: OccurrenceIF.removed
   */
  class OccurrenceIF_removed extends EventHandler {
    protected CollectionSortedMap objects;
    OccurrenceIF_removed(CollectionSortedMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      objects.remove(((OccurrenceIF)old_value).getValue(), old_value);
    }
  }
 
}

