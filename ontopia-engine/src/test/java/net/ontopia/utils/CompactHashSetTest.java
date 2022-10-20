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

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CompactHashSetTest {
  protected Set set;
  
  @Before
  public void setUp() {
    set = new CompactHashSet();
  }

  // --- Test cases

  @Test
  public void testEmpty() {
    Assert.assertTrue("empty set doesn't know it's empty", set.isEmpty());

    Assert.assertTrue("empty set size != 0", set.size() == 0);

    Assert.assertTrue("iterator on empty set has next element", 
	   !set.iterator().hasNext());

    Assert.assertTrue("empty set claims to contain object", !set.contains("hei"));

    set.clear();
    Assert.assertTrue("empty set size != 0", set.size() == 0);
  }

  @Test
  public void testAdd() {
    set.add("hei");

    Assert.assertTrue("set with 1 element thinks it's empty", !set.isEmpty());
    Assert.assertTrue("set size != 1", set.size() == 1);

    Assert.assertTrue("add thinks object just added is not contained",
               !set.add("hei"));

    Assert.assertTrue("set with 1 element thinks it's empty", !set.isEmpty());
    Assert.assertTrue("set size != 1", set.size() == 1);

    Assert.assertTrue("set thinks new object is already contained",
	   set.add("hei2"));

    Assert.assertTrue("set size != 2", set.size() == 2);

    Assert.assertTrue("add thinks object just added is not contained",
               !set.add("hei"));
    Assert.assertTrue("add thinks object just added is not contained",
               !set.add("hei2"));
  }

  @Test
  public void testContains() {
    set.add("hei");
    Assert.assertTrue("set doesn't think just added object is contained",
               set.contains("hei"));
    Assert.assertTrue("set thinks not added object is contained",
               !set.contains("hei2"));
  }

  @Test
  public void testIterator1() {
    set.add("hei");
    
    Iterator it = set.iterator();
    Assert.assertTrue("iterator from set(1) doesn't think it has a next",
               it.hasNext());

    Assert.assertTrue("iterator didn't find object in set",
            "hei".equals(it.next()));
    
    Assert.assertTrue("iterator from set(1) thinks it has a second object",
               !it.hasNext());
  }

  @Test
  public void testIterator2() {
    set.add("hei");
    set.add("hei2");
    
    Iterator it = set.iterator();
    Assert.assertTrue("iterator from set(2) doesn't think it has a first",
               it.hasNext());

    Object obj = it.next();
    Assert.assertTrue("iterator didn't find object in set",
               "hei".equals(obj) || "hei2".equals(obj));

    Assert.assertTrue("iterator from set(2) doesn't think it has a second object",
               it.hasNext());
    
    obj = it.next();
    Assert.assertTrue("iterator didn't find object in set",
               "hei".equals(obj) || "hei2".equals(obj));    
    
    Assert.assertTrue("iterator from set(2) thinks it has a third object",
               !it.hasNext());
  }

  @Test
  public void testIterator3() {
    set.add("hei");
    set.add("hei2");
    set.add("hei3");

    Set otherSet = new HashSet();
    Iterator it = set.iterator();
    while (it.hasNext()) {
      otherSet.add(it.next());
    }

    Assert.assertTrue("not all objects in set(3) iterated to",
	   set.containsAll(otherSet) && otherSet.containsAll(set));
  }

  @Test
  public void testIteratorRemove() {

    set.add("hei");
    set.add("hei2");
    set.add("hei3");

    Set otherSet = new HashSet();
    Iterator it = set.iterator();

    try {
      it.remove();
      Assert.fail("could remove before iterator.next() called first time.");
    }
    catch (IllegalStateException e) {
    }

    while (it.hasNext()) {
      Object x = it.next();
      if ("hei2".equals(x)) {
        it.remove();
      } else {
        otherSet.add(x);
      }
    }

    Assert.assertTrue("set(2).size() != 2", set.size() == 2);
    Assert.assertTrue("iterator.remove() did not remove object",
	       !set.contains("hei2"));
    Assert.assertTrue("set(2) not equal otherSet(2)",
	       otherSet.equals(set));

    try {
      try {
	it.next();
	Assert.fail("could call next after !iterator.hasNext().");
      } catch (NoSuchElementException e) {
      }
      it.remove();
      Assert.fail("could remove when iterator.next() after last.");
    }
    catch (IllegalStateException e) {
    }
  }

  @Test
  public void testConcurrentModification() {
    set.add("hei");
    set.add("hei3");
    set.add("hei4");
    set.add("hei5");
    
    Iterator it = set.iterator();
    set.add("hei2");
    try {
      it.next();
      Assert.fail("set modification not detected");
    }
    catch (ConcurrentModificationException e) {
    }

    it = set.iterator();
    try {
      it.next();
      set.remove("hei4");
      it.next();
      Assert.fail("set modification not detected");
    }
    catch (ConcurrentModificationException e) {
    }

    it = set.iterator();
    set.clear();
    try {
      it.next();
      Assert.fail("set modification not detected");
    }
    catch (ConcurrentModificationException e) {
    }
  }
  
  @Test
  public void testClear() {
    set.add("hei");
    set.add("hei2");
    set.clear();

    Assert.assertTrue(set.isEmpty()); // for PMD
    
    testEmpty();
  }
  
  @Test
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

    Assert.assertTrue("set(24).size() != 24", set.size() == 24);
    Assert.assertTrue("contained object lost", set.contains("hei"));
    Assert.assertTrue("contained object lost", set.contains("hei2"));
    Assert.assertTrue("contained object lost", set.contains("hei3"));
    Assert.assertTrue("contained object lost", set.contains("hei4"));
    Assert.assertTrue("contained object lost", set.contains("_hei"));
    Assert.assertTrue("contained object lost", set.contains("_hei2"));
    Assert.assertTrue("contained object lost", set.contains("_hei3"));
    Assert.assertTrue("contained object lost", set.contains("_hei4"));
    Assert.assertTrue("contained object lost", set.contains("$_hei"));
    Assert.assertTrue("contained object lost", set.contains("$_hei2"));
    Assert.assertTrue("contained object lost", set.contains("$_hei3"));
    Assert.assertTrue("contained object lost", set.contains("$_hei4"));
  }
  
  @Test
  public void testHashcodeNastiness() {
    Object o1 = new ObjectWithStupidHashCode("o1"); 
    Object o2 = new ObjectWithStupidHashCode("o2"); 
    Object o3 = new ObjectWithStupidHashCode("o3"); 
    Object o4 = new ObjectWithStupidHashCode("o4"); 
    Object o5 = new ObjectWithStupidHashCode("o5"); 
    Object o6 = new ObjectWithStupidHashCode("o6");

    Assert.assertTrue("object number 1 was already there!", set.add(o1));
    Assert.assertTrue("object number 2 was already there!", set.add(o2));
    Assert.assertTrue("object number 3 was already there!", set.add(o3));
    Assert.assertTrue("object number 4 was already there!", set.add(o4));
    Assert.assertTrue("object number 5 was already there!", set.add(o5));
    Assert.assertTrue("object number 6 was already there!", set.add(o6));
    
    Assert.assertTrue("object number 1 was lost!", set.contains(o1));
    Assert.assertTrue("object number 2 was lost!", set.contains(o2));
    Assert.assertTrue("object number 3 was lost!", set.contains(o3));
    Assert.assertTrue("object number 4 was lost!", set.contains(o4));
    Assert.assertTrue("object number 5 was lost!", set.contains(o5));
    Assert.assertTrue("object number 6 was lost!", set.contains(o6));

    Assert.assertTrue("object number 1 was lost! (2)", set.remove(o1));
    Assert.assertTrue("object number 2 was lost! (2)", set.remove(o2));
    Assert.assertTrue("object number 3 was lost! (2)", set.remove(o3));
    Assert.assertTrue("object number 4 was lost! (2)", set.remove(o4));
    Assert.assertTrue("object number 5 was lost! (2)", set.remove(o5));
    Assert.assertTrue("object number 6 was lost! (2)", set.remove(o6));

    Assert.assertTrue("wrong set size", set.size() == 0);
    Assert.assertTrue("object number 1 still present!", !set.contains(o1));
    Assert.assertTrue("object number 2 still present!", !set.contains(o2));
    Assert.assertTrue("object number 3 still present!", !set.contains(o3));
    Assert.assertTrue("object number 4 still present!", !set.contains(o4));
    Assert.assertTrue("object number 5 still present!", !set.contains(o5));
    Assert.assertTrue("object number 6 still present!", !set.contains(o6));
  }
  
  @Test
  public void testNull() {
    set.add(null);
    
    Assert.assertTrue("null was not found", set.contains(null));
    Assert.assertTrue("null was not found with iterator", set.iterator().next() == null);
  }

  @Test
  public void testNull2() {
    Assert.assertTrue("null was found", !set.contains(null));
  }

  @Test
  public void testNull3() {
    set.add(null);
    Object[] array = set.toArray();
    Assert.assertTrue("wrong size of array", array.length == 1);
    Assert.assertTrue("array doesn't contain null: " + array[0],
               array[0] == null);
  }

  @Test
  public void testNull4() {
    set.add(null);
    Object[] array = set.toArray(new Object[1]);
    Assert.assertTrue("wrong size of array", array.length == 1);
    Assert.assertTrue("array doesn't contain null: " + array[0],
               array[0] == null);
  }
  
  @Test
  public void testRemove() {
    set.add("hei");
    Assert.assertTrue("remove didn't know element was in set",
               set.remove("hei"));
    Assert.assertTrue("removing only element in set does not make it empty",
               set.isEmpty());
  }

  @Test
  public void testRemove2() {
    set.add("hei");
    set.add("hei2");
    set.add("hei3");
    
    Assert.assertTrue("remove didn't know element was in set",
               set.remove("hei"));
    Assert.assertTrue("member count wrong after remove",
               set.size() == 2);
    Assert.assertTrue("element not removed by remove",
               !set.contains("hei"));
    
    Assert.assertTrue("remove didn't know element was in set",
               set.remove("hei2"));
    Assert.assertTrue("member count wrong after remove",
               set.size() == 1);
    Assert.assertTrue("element not removed by remove",
               !set.contains("hei2"));
    
    Assert.assertTrue("remove didn't know element was in set",
               set.remove("hei3"));
    Assert.assertTrue("member count wrong after remove",
               set.size() == 0);
    Assert.assertTrue("element not removed by remove",
               !set.contains("hei3"));
    
    Assert.assertTrue("removing all elements in set does not make it empty",
               set.isEmpty());
  }

  @Test
  public void testRemoveAll() {
    set.add("hei");
    set.add("hei2");
    set.add("hei3");

    List list = new ArrayList();
    list.add("hei2");
    list.add("hei4");

    set.removeAll(list);
    
    Assert.assertTrue("wrong set element lost after removeAll",
               set.contains("hei"));
    Assert.assertTrue("wrong set element lost after removeAll",
               set.contains("hei3"));
    Assert.assertTrue("element not removed by removeAll",
               !set.contains("hei2"));
    Assert.assertTrue("wrong set size after removeAll",
               set.size() == 2);
  }

  @Test
  public void testRemoveIteration() {
    testRemoveAll();
    assertIterator();
  }

  @Test
  public void testRemoveModification() {
    set.add("hei");
    set.add("hei123");
    set.add("hei5");
    
    Iterator it = set.iterator();
    set.remove("hei123");
    try {
      it.next();
      Assert.fail("set modification not detected");
    }
    catch (ConcurrentModificationException e) {
    }
  }

  @Test
  public void testRemoveAndAdd() {
    set.add("Lars Marius");
    set.add("Steve");
    set.add("Geir Ove");
    set.add("Kal Ahmed");
    set.add("Pam Gennusa");
    set.add("Murray Woodman");
    set.add("Sylvia Schwab");
    set.add("Ann Wrightson");

    Assert.assertTrue("wrong set size", set.size() == 8);
    assertIterator();

    set.remove("Kal Ahmed");
    set.remove("Pam Gennusa");
    set.remove("Murray Woodman");
    set.remove("Ann Wrightson");
    set.add("Niko Schmuck");
    
    Assert.assertTrue("wrong set size after modification (1)", set.size() == 5);
    assertIterator();
    Assert.assertTrue("element lost!", set.contains("Lars Marius"));
    Assert.assertTrue("element lost!", set.contains("Steve"));
    Assert.assertTrue("element lost!", set.contains("Geir Ove"));
    Assert.assertTrue("element lost!", set.contains("Sylvia Schwab"));
    Assert.assertTrue("element lost!", set.contains("Niko Schmuck"));
    Assert.assertTrue("element not gone!", !set.contains("Kal Ahmed"));
    Assert.assertTrue("element not gone!", !set.contains("Pam Gennusa"));
    Assert.assertTrue("element not gone!", !set.contains("Murray Woodman"));
    Assert.assertTrue("element not gone!", !set.contains("Ann Wrightson"));

    set.add("Harald Kuhn");
    set.remove("Harald Kuhn");
    set.remove("Niko Schmuck");
    
    Assert.assertTrue("wrong set size after modification (2)", set.size() == 4);
    assertIterator();
    Assert.assertTrue("element lost!", set.contains("Lars Marius"));
    Assert.assertTrue("element lost!", set.contains("Steve"));
    Assert.assertTrue("element lost!", set.contains("Geir Ove"));
    Assert.assertTrue("element lost!", set.contains("Sylvia Schwab"));
    Assert.assertTrue("element not gone!", !set.contains("Niko Schmuck"));
    Assert.assertTrue("element not gone!", !set.contains("Harald Kuhn"));

    set.add("Graham Moore");
    set.add("Pam Gennusa");

    Assert.assertTrue("wrong set size after modification (3)", set.size() == 6);
    assertIterator();
    Assert.assertTrue("element lost!", set.contains("Lars Marius"));
    Assert.assertTrue("element lost!", set.contains("Steve"));
    Assert.assertTrue("element lost!", set.contains("Geir Ove"));
    Assert.assertTrue("element lost!", set.contains("Sylvia Schwab"));
    Assert.assertTrue("element lost!", set.contains("Graham Moore"));
    Assert.assertTrue("element lost!", set.contains("Pam Gennusa"));
  }

  @Test
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
    while (it.hasNext()) {
      Assert.assertTrue("object to be removed not found",
                 set.remove(it.next()));
    }

    
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

    Assert.assertTrue("wrong size of reconstituted set",
               set.size() == 48);
  }

  @Test
  public void testProbabilistic() {
    Random random = new Random();
    Set hashset = new HashSet();
    
    for (int ix = 0; ix < 10000; ix++) {
      int value = random.nextInt(100);

      if (random.nextBoolean()) {
        // we're adding

        Assert.assertTrue("add returned wrong value",
                   set.add(value) == hashset.add(value));
        Assert.assertTrue("size was wrong after add",
                   set.size() == hashset.size());
        Assert.assertTrue("added object not found",
                   set.contains(value));
        
      } else {
        // we're removing
        
        Assert.assertTrue("remove returned wrong value",
                   set.remove(value) == hashset.remove(value));
        Assert.assertTrue("size was wrong after remove",
                   set.size() == hashset.size());
        Assert.assertTrue("removed object found",
                   !set.contains(value));
      }

      assertIterator();
      checkToArray();
    }
  }

  @Test
  public void testIteratorRemove2() {
    set.add("1");
    set.add("2");
    set.add("3");
    set.add("4");

    Assert.assertTrue("wrong size", set.size() == 4);

    Iterator it = set.iterator();
    while (it.hasNext()) {
      if (it.next().equals("2")) {
        it.remove();
      }
    }

    Assert.assertTrue("1 was lost!", set.contains("1"));
    Assert.assertFalse("2 was not removed!", set.contains("2"));
    Assert.assertTrue("3 was lost!", set.contains("3"));
    Assert.assertTrue("4 was lost!", set.contains("4"));
    Assert.assertTrue("wrong number of elements", set.size() == 3);
  }

  @Test
  public void testIteratorRemove3() {
    set.add("1");
    set.add("2");
    set.add("3");
    set.add("4");

    Assert.assertTrue("wrong size", set.size() == 4);

    Iterator it = set.iterator();
    it.next();

    Iterator it2 = set.iterator();
    it.remove(); // whoa!

    try {
      it2.next(); // should Assert.fail, because we modified the set
      Assert.fail("undetected modification");
    } catch (ConcurrentModificationException e) {
      // as required
    }
  }  

  @Test
  public void testIteratorRemove4() {
    set.add("1");
    set.add("2");
    set.add("3");
    set.add("4");

    Assert.assertTrue("wrong size", set.size() == 4);

    Iterator it = set.iterator();
    it.next();
    it.remove(); 
    it.next(); // verifies that removing a value doesn't cause ConcModExc
    
    Assert.assertTrue("wrong size", set.size() == 3);    
  }
  
  // --- Internal helper methods

  private void assertIterator() {
    List list = new ArrayList();
    Iterator it = set.iterator();
    while (it.hasNext()) {
      list.add(it.next());
    }

    Assert.assertTrue("wrong number of elements found",
               list.size() == set.size());
    Assert.assertTrue("not all objects in set iterated to",
               set.containsAll(list) && list.containsAll(set));
  }

  private void checkToArray() {
    Object[] ints = new Integer[1];
    ints = set.toArray(ints);

    List list = new ArrayList();    
    for (int ix = 0; ix < ints.length && ints[ix] != null; ix++) {
      list.add(ints[ix]);
    }

    Assert.assertTrue("wrong number of elements found",
               list.size() == set.size());
    Assert.assertTrue("not all objects in set iterated to",
               set.containsAll(list) && list.containsAll(set));
  }
  
  // --- Internal test object

  private class ObjectWithStupidHashCode {
    private String name;

    public ObjectWithStupidHashCode(String name) {
      this.name = name;
    }
    
    @Override
    public int hashCode() {
      return 0;
    }

    @Override
    public String toString() {
      return "<ObjectWithStupidHashCode " + name + ">";
    }

    @Override
    public boolean equals(Object other) {
      return ((ObjectWithStupidHashCode) other).name.equals(name);
    }
  }
}
