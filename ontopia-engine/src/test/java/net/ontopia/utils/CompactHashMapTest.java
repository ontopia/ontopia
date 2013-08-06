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

package net.ontopia.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;

public class CompactHashMapTest extends TestCase {
  protected Map map;
  
  public void setUp() {
    map = new CompactHashMap();
  }

  // --- Test cases

  public void testEmpty() {
    assertTrue("empty map doesn't know it's empty", map.isEmpty());
    assertTrue("empty map doesn't know it's empty", map.size() == 0);
    assertFalse("empty map claims to contain key", map.containsKey(""));
    assertFalse("empty map claims to contain value", map.containsValue(""));
    assertFalse("empty map claims to contain null", map.containsKey(null));
    assertFalse("empty map claims to contain null", map.containsValue(null));
    assertTrue("removing non-existent key returns value",
               map.remove("foo") == null);

    // testing key set
    Set keys = map.keySet();
    assertTrue("empty map keyset is non-empty", keys.isEmpty());
    assertTrue("empty map keyset is non-empty", keys.size() == 0);
    assertFalse("empty map keyset contains value", keys.contains("foo"));
    assertFalse("empty map keyset contains value", keys.contains(null));
    Iterator it = keys.iterator();
    assertFalse("empty map keyset iterator returns value", it.hasNext());

    // testing values collection
    Collection values = map.values();
    assertTrue("empty map value collection is non-empty",
               values.isEmpty());
    assertTrue("empty map value collection is non-empty",
               values.size() == 0);
    assertFalse("empty map value collection contains non-value",
                values.contains("bar"));
    assertFalse("empty map value collection contains non-value",
                values.contains(null));
    it = values.iterator();
    assertFalse("empty map value collection iterator returns value",
                it.hasNext());
  }

  public void testAddOne() {
    assertTrue("empty map claimed previous mapping for key",
               map.put("1", "one") == null);
    assertTrue("wrong size for map", map.size() == 1);
    assertFalse("map claims to be empty", map.isEmpty());
    assertTrue("map doesn't recognize key", map.containsKey("1"));
    assertTrue("map doesn't recognize value", map.containsValue("one"));
    assertFalse("map recognizes missing key", map.containsKey("2"));
    assertFalse("map recognizes missing value", map.containsValue("two"));
    assertFalse("map recognizes missing key", map.containsKey(null));
    assertFalse("map recognizes missing value", map.containsValue(null));

    assertTrue("map returns wrong value for key", map.get("1").equals("one"));

    // testing key set
    Set keys = map.keySet();
    assertFalse("non-empty map keyset is empty", keys.isEmpty());
    assertTrue("map keyset has wrong size", keys.size() == 1);
    assertFalse("map keyset contains wrong value", keys.contains("foo"));
    assertFalse("map keyset contains wrong value", keys.contains(null));
    assertTrue("map keyset lacks value", keys.contains("1"));

    Iterator it = keys.iterator();
    assertTrue("map keyset iterator has no values", it.hasNext());
    assertTrue("map keyset iterator produces wrong value",
               it.next().equals("1"));
    assertFalse("map keyset iterator has too many values", it.hasNext());

    // testing values collection
    Collection values = map.values();
    assertFalse("map value collection is empty",
                values.isEmpty());
    assertTrue("empty map value collection has wrong size",
               values.size() == 1);
    assertFalse("value collection contains non-value",
                values.contains("bar"));
    assertFalse("value collection contains non-value",
                values.contains(null));
    assertTrue("value collection cannot find value",
                values.contains("one"));
    it = values.iterator();
    assertTrue("value collection iterator is empty",
               it.hasNext());
    assertTrue("value collection iterator returns wrong value",
               it.next().equals("one"));
    assertFalse("value collection iterator has too many values",
                it.hasNext());

    // --- emptying the map
    assertTrue("removing mapping finds wrong value",
               map.remove("1").equals("one"));

    testEmpty(); // map should now be empty
  }

  public void testAddOneToTen() {
    Set allkeys = new HashSet();
    Set allvalues = new HashSet();
    
    for (int ix = 1; ix <= 10; ix++) {
      assertTrue("map erroneously claimed previous mapping for key",
                 map.put("" + ix, new Integer(ix)) == null);
      allkeys.add("" + ix);
      allvalues.add(new Integer(ix));
    }
    assertTrue("wrong size for map", map.size() == 10);
    assertFalse("map claims to be empty", map.isEmpty());

    for (int ix = 1; ix <= 10; ix++) {
      assertTrue("map doesn't recognize key " + ix, map.containsKey("" + ix));
      assertTrue("map doesn't recognize value",
                 map.containsValue(new Integer(ix)));
      assertTrue("map returns wrong value for key",
                 map.get("" + ix).equals(new Integer(ix)));
    }
    
    assertFalse("map recognizes missing key", map.containsKey("x"));
    assertFalse("map recognizes missing value", map.containsValue("two"));
    assertFalse("map recognizes missing key", map.containsKey(null));
    assertFalse("map recognizes missing value", map.containsValue(null));

    // testing key set
    Set keys = map.keySet();
    assertFalse("non-empty map keyset is empty", keys.isEmpty());
    assertTrue("map keyset has wrong size", keys.size() == 10);
    assertFalse("map keyset contains wrong value", keys.contains("foo"));
    assertFalse("map keyset contains wrong value", keys.contains(null));
    for (int ix = 1; ix <= 10; ix++)
      assertTrue("map keyset lacks value", keys.contains("" + ix));

    Iterator it = keys.iterator();
    while (it.hasNext()) {
      Object key = it.next();
      assertTrue("map keyset produces non-key value",
                 allkeys.contains(key));
      allkeys.remove(key);
    }
    assertTrue("map keyset doesn't contain all keys", allkeys.isEmpty());

    // testing values collection
    Collection values = map.values();
    assertFalse("map value collection is empty",
                values.isEmpty());
    assertTrue("empty map value collection has wrong size",
               values.size() == 10);
    assertFalse("value collection contains non-value",
                values.contains("bar"));
    assertFalse("value collection contains non-value",
                values.contains(null));
    assertTrue("value collection cannot find value",
               values.contains(new Integer(5)));

    it = values.iterator();
    while (it.hasNext()) {
      Object value = it.next();
      assertTrue("map value collection iterator produces nonvalue",
                 allvalues.contains(value));
      allvalues.remove(value);
    }
    assertTrue("map value collection doesn't contain all values",
               allvalues.isEmpty());
    
    // --- emptying the map
    for (int ix = 1; ix <= 10; ix++)
      assertTrue("removing mapping finds wrong value",
                 map.remove("" + ix).equals(new Integer(ix)));

    testEmpty(); // map should now be empty
  }

  public void testClear() {
    for (int ix = 1; ix <= 10; ix++)
      assertTrue("map erroneously claimed previous mapping for key",
                 map.put("" + ix, new Integer(ix)) == null);

    map.clear();
    testEmpty();
  }

  public void testOverwrite() {
    for (int ix = 1; ix <= 10; ix++)
      assertTrue("map erroneously claimed previous mapping for key",
                 map.put("" + ix, new Integer(ix)) == null);

    map.put("" + 5, "five");

    for (int ix = 1; ix <= 10; ix++) {
      if (ix == 5) {
        assertTrue("map doesn't recognize key " + ix, map.containsKey("" + ix));
        assertTrue("map doesn't recognize value",
                   map.containsValue("five"));
        assertTrue("map returns wrong value for key",
                   map.get("" + ix).equals("five"));
      } else {
        assertTrue("map doesn't recognize key " + ix, map.containsKey("" + ix));
        assertTrue("map doesn't recognize value",
                   map.containsValue(new Integer(ix)));
        assertTrue("map returns wrong value for key",
                   map.get("" + ix).equals(new Integer(ix)));
      }
    }
  }

  public void testRemoveAndReadd() {
    for (int ix = 1; ix <= 10; ix++)
      assertTrue("map erroneously claimed previous mapping for key",
                 map.put("" + ix, new Integer(ix)) == null);

    map.remove("" + 5);
    map.put("" + 5, "five");

    for (int ix = 1; ix <= 10; ix++) {
      if (ix == 5) {
        assertTrue("map doesn't recognize key " + ix, map.containsKey("" + ix));
        assertTrue("map doesn't recognize value",
                   map.containsValue("five"));
        assertTrue("map returns wrong value for key",
                   map.get("" + ix).equals("five"));
      } else {
        assertTrue("map doesn't recognize key " + ix, map.containsKey("" + ix));
        assertTrue("map doesn't recognize value",
                   map.containsValue(new Integer(ix)));
        assertTrue("map returns wrong value for key",
                   map.get("" + ix).equals(new Integer(ix)));
      }
    }
  }
  
  // test null keys
  // test null values
  // test concmodexc
}
