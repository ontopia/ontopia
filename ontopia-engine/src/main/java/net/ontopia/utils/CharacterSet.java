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

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * INTERNAL: Represents a set of Unicode characters, and provides a
 * method to quickly determine whether or not a particular character
 * is in the set. Useful for large, complex sets like "the set of XML
 * name start characters". Characters outside the BMP (ie: above
 * U+FFFF) are not supported.
 */
public class CharacterSet {
  private List<CharacterInterval> tempset; // used while building the set
  private CharacterInterval[] set;
  
  public CharacterSet() {
    tempset = new ArrayList<CharacterInterval>();
  }

  /**
   * Adds the interval of characters to the set. To add a single
   * character make low and high the same value. Cannot be called
   * after close() has been called.
   */
  public void addInterval(char low, char high) {
    tempset.add(new CharacterInterval(low, high));
  }

  /**
   * Called after the last interval has been added. Compiles the
   * internal, efficient representation of the set. No more additions
   * can be made after this method has been called.
   */
  public void close() {
    set = new CharacterInterval[tempset.size()];
    tempset.toArray(set);
    tempset = null;

    Arrays.sort(set, new IntervalComparator());
  }

  /**
   * Used to determine whether or not the character is a member of the
   * set.
   */
  public boolean contains(char ch) {
    for (int ix = 0; ix < set.length; ix++) {
      if (ch >= set[ix].low && ch <= set[ix].high) {
        return true;
      }
    }

    return false;
  }

  // It's tempting to turn this into a binary search, but performance
  // testing seems to indicate that there is no point. Results below
  // from running ExportSpeed on opera.xtm

  // --- BEFORE ID CHECK

  // Average export time in seconds: 0.27578
  // Object count: 5223
  // Obj/sec: 18939.01

  // --- AFTER ID CHECK

  // Average export time in seconds: 0.27407
  // Object count: 5223
  // Obj/sec: 19057.176

  // Conclusion: no measurable difference, therefore the complexity of
  // binary search is not warranted.

  // --- IntervalComparator

  /**
   * Compares character intervals for sorting.
   */
  static class IntervalComparator implements java.util.Comparator<CharacterInterval> {

    @Override
    public int compare(CharacterInterval c1, CharacterInterval c2) {
      // INV: we assume o1 and o2 are both CharacterIntervals
      return c1.low - c2.low;
    }
    
  }

  // --- CharacterInterval

  /**
   * The set is made up from intervals represented using this class.
   * Single, isolated characters in the set are represented as an
   * interval one character wide.
   */
  static class CharacterInterval {
    public char low;
    public char high;

    public CharacterInterval(char low, char high) {
      this.low = low;
      this.high = high;
    }
  }
}
