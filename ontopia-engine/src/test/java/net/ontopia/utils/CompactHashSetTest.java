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

import java.util.*;
import junit.framework.TestCase;

public class CompactHashSetTest extends TestCase {
  protected Set set;
  
  public CompactHashSetTest(String name) {
    super(name);
  }

  public void setUp() {
    set = new CompactHashSet();
  }

  protected void tearDown() {
  }

  // --- Test cases

  public void testEmpty() {
    assertTrue("empty set doesn't know it's empty", set.isEmpty());

    assertTrue("empty set size != 0", set.size() == 0);

    assertTrue("iterator on empty set has next element", 
	   !set.iterator().hasNext());

    assertTrue("empty set claims to contain object", !set.contains("hei"));

    set.clear();
    assertTrue("empty set size != 0", set.size() == 0);
  }

  public void testAdd() {
    set.add("hei");

    assertTrue("set with 1 element thinks it's empty", !set.isEmpty());
    assertTrue("set size != 1", set.size() == 1);

    assertTrue("add thinks object just added is not contained",
               !set.add("hei"));

    assertTrue("set with 1 element thinks it's empty", !set.isEmpty());
    assertTrue("set size != 1", set.size() == 1);

    assertTrue("set thinks new object is already contained",
	   set.add("hei2"));

    assertTrue("set size != 2", set.size() == 2);

    assertTrue("add thinks object just added is not contained",
               !set.add("hei"));
    assertTrue("add thinks object just added is not contained",
               !set.add("hei2"));
  }

  public void testContains() {
    set.add("hei");
    assertTrue("set doesn't think just added object is contained",
               set.contains("hei"));
    assertTrue("set thinks not added object is contained",
               !set.contains("hei2"));
  }

  public void testIterator1() {
    set.add("hei");
    
    Iterator it = set.iterator();
    assertTrue("iterator from set(1) doesn't think it has a next",
               it.hasNext());

    assertTrue("iterator didn't find object in set",
               it.next().equals("hei"));
    
    assertTrue("iterator from set(1) thinks it has a second object",
               !it.hasNext());
  }

  public void testIterator2() {
    set.add("hei");
    set.add("hei2");
    
    Iterator it = set.iterator();
    assertTrue("iterator from set(2) doesn't think it has a first",
               it.hasNext());

    Object obj = it.next();
    assertTrue("iterator didn't find object in set",
               obj.equals("hei") || obj.equals("hei2"));

    assertTrue("iterator from set(2) doesn't think it has a second object",
               it.hasNext());
    
    obj = it.next();
    assertTrue("iterator didn't find object in set",
               obj.equals("hei") || obj.equals("hei2"));    
    
    assertTrue("iterator from set(2) thinks it has a third object",
               !it.hasNext());
  }

  public void testIterator3() {
    set.add("hei");
    set.add("hei2");
    set.add("hei3");

    Set otherSet = new HashSet();
    Iterator it = set.iterator();
    while (it.hasNext())
      otherSet.add(it.next());

    assertTrue("not all objects in set(3) iterated to",
	   set.containsAll(otherSet) && otherSet.containsAll(set));
  }

  public void testIteratorRemove() {

    set.add("hei");
    set.add("hei2");
    set.add("hei3");

    Set otherSet = new HashSet();
    Iterator it = set.iterator();

    try {
      it.remove();
      fail("could remove before iterator.next() called first time.");
    }
    catch (IllegalStateException e) {
    }

    while (it.hasNext()) {
      Object x = it.next();
      if ("hei2".equals(x))
	it.remove();
      else
	otherSet.add(x);
    }

    assertTrue("set(2).size() != 2", set.size() == 2);
    assertTrue("iterator.remove() did not remove object",
	       !set.contains("hei2"));
    assertTrue("set(2) not equal otherSet(2)",
	       otherSet.equals(set));

    try {
      try {
	it.next();
	fail("could call next after !iterator.hasNext().");
      } catch (NoSuchElementException e) {
      }
      it.remove();
      fail("could remove when iterator.next() after last.");
    }
    catch (IllegalStateException e) {
    }
  }

  public void testConcurrentModification() {
    set.add("hei");
    set.add("hei3");
    set.add("hei4");
    set.add("hei5");
    
    Iterator it = set.iterator();
    set.add("hei2");
    try {
      it.next();
      fail("set modification not detected");
    }
    catch (ConcurrentModificationException e) {
    }

    it = set.iterator();
    try {
      it.next();
      set.remove("hei4");
      it.next();
      fail("set modification not detected");
    }
    catch (ConcurrentModificationException e) {
    }

    it = set.iterator();
    set.clear();
    try {
      it.next();
      fail("set modification not detected");
    }
    catch (ConcurrentModificationException e) {
    }
  }
  
  public void testClear() {
    set.add("hei");
    set.add("hei2");
    set.clear();

    testEmpty();
  }
  
  public void testRehash() {
    set.add("hei");
    set.add("hei2");
    set.add("hei3");
    set.add("hei4");
    set.add("bei");
    set.add("bei2");
    set.add("bei3");
    set.add("bei4");
    set.add("_hei");
    set.add("_hei2");
    set.add("_hei3");
    set.add("_hei4");
    set.add("_bei");
    set.add("_bei2");
    set.add("_bei3");
    set.add("_bei4");
    set.add("$_hei");
    set.add("$_hei2");
    set.add("$_hei3");
    set.add("$_hei4");
    set.add("$_bei");
    set.add("$_bei2");
    set.add("$_bei3");
    set.add("$_bei4");

    assertTrue("set(24).size() != 24", set.size() == 24);
    assertTrue("contained object lost", set.contains("hei"));
    assertTrue("contained object lost", set.contains("hei2"));
    assertTrue("contained object lost", set.contains("hei3"));
    assertTrue("contained object lost", set.contains("hei4"));
    assertTrue("contained object lost", set.contains("_hei"));
    assertTrue("contained object lost", set.contains("_hei2"));
    assertTrue("contained object lost", set.contains("_hei3"));
    assertTrue("contained object lost", set.contains("_hei4"));
    assertTrue("contained object lost", set.contains("$_hei"));
    assertTrue("contained object lost", set.contains("$_hei2"));
    assertTrue("contained object lost", set.contains("$_hei3"));
    assertTrue("contained object lost", set.contains("$_hei4"));
  }
  
  public void testHashcodeNastiness() {
    Object o1 = new ObjectWithStupidHashCode("o1"); 
    Object o2 = new ObjectWithStupidHashCode("o2"); 
    Object o3 = new ObjectWithStupidHashCode("o3"); 
    Object o4 = new ObjectWithStupidHashCode("o4"); 
    Object o5 = new ObjectWithStupidHashCode("o5"); 
    Object o6 = new ObjectWithStupidHashCode("o6");

    assertTrue("object number 1 was already there!", set.add(o1));
    assertTrue("object number 2 was already there!", set.add(o2));
    assertTrue("object number 3 was already there!", set.add(o3));
    assertTrue("object number 4 was already there!", set.add(o4));
    assertTrue("object number 5 was already there!", set.add(o5));
    assertTrue("object number 6 was already there!", set.add(o6));
    
    assertTrue("object number 1 was lost!", set.contains(o1));
    assertTrue("object number 2 was lost!", set.contains(o2));
    assertTrue("object number 3 was lost!", set.contains(o3));
    assertTrue("object number 4 was lost!", set.contains(o4));
    assertTrue("object number 5 was lost!", set.contains(o5));
    assertTrue("object number 6 was lost!", set.contains(o6));

    assertTrue("object number 1 was lost! (2)", set.remove(o1));
    assertTrue("object number 2 was lost! (2)", set.remove(o2));
    assertTrue("object number 3 was lost! (2)", set.remove(o3));
    assertTrue("object number 4 was lost! (2)", set.remove(o4));
    assertTrue("object number 5 was lost! (2)", set.remove(o5));
    assertTrue("object number 6 was lost! (2)", set.remove(o6));

    assertTrue("wrong set size", set.size() == 0);
    assertTrue("object number 1 still present!", !set.contains(o1));
    assertTrue("object number 2 still present!", !set.contains(o2));
    assertTrue("object number 3 still present!", !set.contains(o3));
    assertTrue("object number 4 still present!", !set.contains(o4));
    assertTrue("object number 5 still present!", !set.contains(o5));
    assertTrue("object number 6 still present!", !set.contains(o6));
  }
  
  public void testNull() {
    set.add(null);
    
    assertTrue("null was not found", set.contains(null));
    assertTrue("null was not found with iterator", set.iterator().next() == null);
  }

  public void testNull2() {
    assertTrue("null was found", !set.contains(null));
  }

  public void testNull3() {
    set.add(null);
    Object[] array = set.toArray();
    assertTrue("wrong size of array", array.length == 1);
    assertTrue("array doesn't contain null: " + array[0],
               array[0] == null);
  }

  public void testNull4() {
    set.add(null);
    Object[] array = set.toArray(new Object[1]);
    assertTrue("wrong size of array", array.length == 1);
    assertTrue("array doesn't contain null: " + array[0],
               array[0] == null);
  }
  
  public void testRemove() {
    set.add("hei");
    assertTrue("remove didn't know element was in set",
               set.remove("hei"));
    assertTrue("removing only element in set does not make it empty",
               set.isEmpty());
  }

  public void testRemove2() {
    set.add("hei");
    set.add("hei2");
    set.add("hei3");
    
    assertTrue("remove didn't know element was in set",
               set.remove("hei"));
    assertTrue("member count wrong after remove",
               set.size() == 2);
    assertTrue("element not removed by remove",
               !set.contains("hei"));
    
    assertTrue("remove didn't know element was in set",
               set.remove("hei2"));
    assertTrue("member count wrong after remove",
               set.size() == 1);
    assertTrue("element not removed by remove",
               !set.contains("hei2"));
    
    assertTrue("remove didn't know element was in set",
               set.remove("hei3"));
    assertTrue("member count wrong after remove",
               set.size() == 0);
    assertTrue("element not removed by remove",
               !set.contains("hei3"));
    
    assertTrue("removing all elements in set does not make it empty",
               set.isEmpty());
  }

  public void testRemoveAll() {
    set.add("hei");
    set.add("hei2");
    set.add("hei3");

    List list = new ArrayList();
    list.add("hei2");
    list.add("hei4");

    set.removeAll(list);
    
    assertTrue("wrong set element lost after removeAll",
               set.contains("hei"));
    assertTrue("wrong set element lost after removeAll",
               set.contains("hei3"));
    assertTrue("element not removed by removeAll",
               !set.contains("hei2"));
    assertTrue("wrong set size after removeAll",
               set.size() == 2);
  }

  public void testRemoveIteration() {
    testRemoveAll();
    checkIterator();
  }

  public void testRemoveModification() {
    set.add("hei");
    set.add("hei123");
    set.add("hei5");
    
    Iterator it = set.iterator();
    set.remove("hei123");
    try {
      it.next();
      fail("set modification not detected");
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testRemoveAndAdd() {
    set.add("Lars Marius");
    set.add("Steve");
    set.add("Geir Ove");
    set.add("Kal Ahmed");
    set.add("Pam Gennusa");
    set.add("Murray Woodman");
    set.add("Sylvia Schwab");
    set.add("Ann Wrightson");

    assertTrue("wrong set size", set.size() == 8);
    checkIterator();

    set.remove("Kal Ahmed");
    set.remove("Pam Gennusa");
    set.remove("Murray Woodman");
    set.remove("Ann Wrightson");
    set.add("Niko Schmuck");
    
    assertTrue("wrong set size after modification (1)", set.size() == 5);
    checkIterator();
    assertTrue("element lost!", set.contains("Lars Marius"));
    assertTrue("element lost!", set.contains("Steve"));
    assertTrue("element lost!", set.contains("Geir Ove"));
    assertTrue("element lost!", set.contains("Sylvia Schwab"));
    assertTrue("element lost!", set.contains("Niko Schmuck"));
    assertTrue("element not gone!", !set.contains("Kal Ahmed"));
    assertTrue("element not gone!", !set.contains("Pam Gennusa"));
    assertTrue("element not gone!", !set.contains("Murray Woodman"));
    assertTrue("element not gone!", !set.contains("Ann Wrightson"));

    set.add("Harald Kuhn");
    set.remove("Harald Kuhn");
    set.remove("Niko Schmuck");
    
    assertTrue("wrong set size after modification (2)", set.size() == 4);
    checkIterator();
    assertTrue("element lost!", set.contains("Lars Marius"));
    assertTrue("element lost!", set.contains("Steve"));
    assertTrue("element lost!", set.contains("Geir Ove"));
    assertTrue("element lost!", set.contains("Sylvia Schwab"));
    assertTrue("element not gone!", !set.contains("Niko Schmuck"));
    assertTrue("element not gone!", !set.contains("Harald Kuhn"));

    set.add("Graham Moore");
    set.add("Pam Gennusa");

    assertTrue("wrong set size after modification (3)", set.size() == 6);
    checkIterator();
    assertTrue("element lost!", set.contains("Lars Marius"));
    assertTrue("element lost!", set.contains("Steve"));
    assertTrue("element lost!", set.contains("Geir Ove"));
    assertTrue("element lost!", set.contains("Sylvia Schwab"));
    assertTrue("element lost!", set.contains("Graham Moore"));
    assertTrue("element lost!", set.contains("Pam Gennusa"));
  }

  public void testRemoveRehash() {
    set.add("hei");
    set.add("hei2");
    set.add("hei3");
    set.add("hei4");
    set.add("bei");
    set.add("bei2");
    set.add("bei3");
    set.add("bei4");
    set.add("_hei");
    set.add("_hei2");
    set.add("_hei3");
    set.add("_hei4");
    set.add("_bei");
    set.add("_bei2");
    set.add("_bei3");
    set.add("_bei4");
    set.add("$_hei");
    set.add("$_hei2");
    set.add("$_hei3");
    set.add("$_hei4");
    set.add("$_bei");
    set.add("$_bei2");
    set.add("$_bei3");
    set.add("$_bei4");

    Iterator it = new ArrayList(set).iterator();
    while (it.hasNext())
      assertTrue("object to be removed not found",
                 set.remove(it.next()));

    
    set.add("hei");
    set.add("hei2");
    set.add("hei3");
    set.add("hei4");
    set.add("bei");
    set.add("bei2");
    set.add("bei3");
    set.add("bei4");
    set.add("_hei");
    set.add("_hei2");
    set.add("_hei3");
    set.add("_hei4");
    set.add("_bei");
    set.add("_bei2");
    set.add("_bei3");
    set.add("_bei4");
    set.add("$_hei");
    set.add("$_hei2");
    set.add("$_hei3");
    set.add("$_hei4");
    set.add("$_bei");
    set.add("$_bei2");
    set.add("$_bei3");
    set.add("$_bei4");
    set.add("xyxhei");
    set.add("xyxhei2");
    set.add("xyxhei3");
    set.add("xyxhei4");
    set.add("xyxbei");
    set.add("xyxbei2");
    set.add("xyxbei3");
    set.add("xyxbei4");
    set.add("xyx_hei");
    set.add("xyx_hei2");
    set.add("xyx_hei3");
    set.add("xyx_hei4");
    set.add("xyx_bei");
    set.add("xyx_bei2");
    set.add("xyx_bei3");
    set.add("xyx_bei4");
    set.add("xyx$_hei");
    set.add("xyx$_hei2");
    set.add("xyx$_hei3");
    set.add("xyx$_hei4");
    set.add("xyx$_bei");
    set.add("xyx$_bei2");
    set.add("xyx$_bei3");
    set.add("xyx$_bei4");

    assertTrue("wrong size of reconstituted set",
               set.size() == 48);
  }

  public void testProbabilistic() {
    Random random = new Random();
    Set hashset = new HashSet();
    
    for (int ix = 0; ix < 10000; ix++) {
      Integer value = new Integer(random.nextInt(100));

      if (random.nextBoolean()) {
        // we're adding

        assertTrue("add returned wrong value",
                   set.add(value) == hashset.add(value));
        assertTrue("size was wrong after add",
                   set.size() == hashset.size());
        assertTrue("added object not found",
                   set.contains(value));
        
      } else {
        // we're removing
        
        assertTrue("remove returned wrong value",
                   set.remove(value) == hashset.remove(value));
        assertTrue("size was wrong after remove",
                   set.size() == hashset.size());
        assertTrue("removed object found",
                   !set.contains(value));
      }

      checkIterator();
      checkToArray();
    }
  }

  public void testIteratorRemove2() {
    set.add("1");
    set.add("2");
    set.add("3");
    set.add("4");

    assertTrue("wrong size", set.size() == 4);

    Iterator it = set.iterator();
    while (it.hasNext()) {
      if (it.next().equals("2"))
        it.remove();
    }

    assertTrue("1 was lost!", set.contains("1"));
    assertFalse("2 was not removed!", set.contains("2"));
    assertTrue("3 was lost!", set.contains("3"));
    assertTrue("4 was lost!", set.contains("4"));
    assertTrue("wrong number of elements", set.size() == 3);
  }

  public void testIteratorRemove3() {
    set.add("1");
    set.add("2");
    set.add("3");
    set.add("4");

    assertTrue("wrong size", set.size() == 4);

    Iterator it = set.iterator();
    it.next();

    Iterator it2 = set.iterator();
    it.remove(); // whoa!

    try {
      it2.next(); // should fail, because we modified the set
      fail("undetected modification");
    } catch (ConcurrentModificationException e) {
      // as required
    }
  }  

  public void testIteratorRemove4() {
    set.add("1");
    set.add("2");
    set.add("3");
    set.add("4");

    assertTrue("wrong size", set.size() == 4);

    Iterator it = set.iterator();
    it.next();
    it.remove(); 
    it.next(); // verifies that removing a value doesn't cause ConcModExc
    
    assertTrue("wrong size", set.size() == 3);    
  }
  
  // --- Internal helper methods

  private void checkIterator() {
    List list = new ArrayList();
    Iterator it = set.iterator();
    while (it.hasNext())
      list.add(it.next());

    assertTrue("wrong number of elements found",
               list.size() == set.size());
    assertTrue("not all objects in set iterated to",
               set.containsAll(list) && list.containsAll(set));
  }

  private void checkToArray() {
    Object[] ints = new Integer[1];
    ints = set.toArray(ints);

    List list = new ArrayList();    
    for (int ix = 0; ix < ints.length && ints[ix] != null; ix++)
      list.add(ints[ix]);

    assertTrue("wrong number of elements found",
               list.size() == set.size());
    assertTrue("not all objects in set iterated to",
               set.containsAll(list) && list.containsAll(set));
  }
  
  // --- Internal test object

  private class ObjectWithStupidHashCode {
    private String name;

    public ObjectWithStupidHashCode(String name) {
      this.name = name;
    }
    
    public int hashCode() {
      return 0;
    }

    public String toString() {
      return "<ObjectWithStupidHashCode " + name + ">";
    }

    public boolean equals(Object other) {
      return ((ObjectWithStupidHashCode) other).name.equals(name);
    }
  }
}
