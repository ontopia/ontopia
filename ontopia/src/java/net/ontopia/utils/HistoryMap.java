
// $Id: HistoryMap.java,v 1.11 2003/09/11 12:33:06 larsga Exp $

package net.ontopia.utils;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Helper class for storing elements up to a certain amount,
 * lower most elements will be removed to ensure a fixed size of the
 * collection.
 *
 * @since 1.2.5
 */
public class HistoryMap extends HashMap {
  protected int maxEntries;
  protected boolean suppressDuplicates;
  private int counter;
  
  // initialization of logging facility
  private static Logger log = Logger
    .getLogger(HistoryMap.class.getName());
  
  /**
   * Default constructor which uses 20 entries as the default history
   * size and does suppress duplicates.
   */
  public HistoryMap() {
    this(20, true);
  }
  
  /**
   * Constructor which allows to specify number of entries to store and
   * duplicate suppression behaviour.
   *
   * @param maxEntries Maxium number of entries to store in the history.
   * @param suppressDuplicates Should suppress duplicate entries in map.
   */
  public HistoryMap(int maxEntries, boolean suppressDuplicates) {
    super();
    this.maxEntries = maxEntries;
    this.counter = 0;
    this.suppressDuplicates = suppressDuplicates;
  }

  public int getMaxEntries() {
    return maxEntries;
  }

  public boolean doesSuppressDuplicates() {
    return suppressDuplicates;
  }
  
  public void add(Object obj) {
    // do not add if object already exists
    if (suppressDuplicates && containsValue(obj))
      return;
    counter++;
    put(new Integer(counter), obj);
    if (size() >= maxEntries)
      try {
        remove(new Integer(counter - maxEntries));
      } catch (Exception e) {
        log.error("Remove of entry from historymap without success." +
                  e.getMessage());
      }
  }

  public void removeEntry(Object obj) {
    Iterator it = keySet().iterator();
    log.info("A removing from history");
    while (it.hasNext()) {
      Integer key = (Integer) it.next();
      Object val = get(key);
      if (val.equals(obj)) {
        log.info("removing from history " + key);
        remove(key);
        break;
      }
    } // while it
  }
  
  public Object getEntry(int index) {
    return get(new Integer(counter - size() + index));
  }

  public Collection getEntries() {
    Collection result = new ArrayList();
    for (int i=1; i <= size(); i++) {
      if (getEntry(i) != null)
        result.add(getEntry(i));
    }

    return result;
  }

  public Collection getEntriesReverse() {
    Collection result = new ArrayList();
    for (int i=size(); i >= 1; i--) {
      if (getEntry(i) != null)
        result.add(getEntry(i));
    }

    return result;
  }
  
}
