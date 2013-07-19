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

public class SetPoolTest extends TestCase {

  // This class does not subclass CompactHashSetTest because the set
  // implementation is immutable.
  
  public SetPoolTest(String name) {
    super(name);
  }

  public void setUp() {
  }

  protected void tearDown() {
  }

  // --- Test cases

  public void testSetPoolGarbageCollect() {
    SetPoolIF setpool = new SetPool();      
    assertTrue("Set pool is not empty", setpool.size() == 0 && setpool.isEmpty());

    // Make sure that sets are "garbage collected"/dropped when no longer referenced
    boolean dereference = true;
    int times = 3;
    for (int i=0; i < times; i++) {
      Set emptyset1 = setpool.reference(Collections.EMPTY_SET);
      assertTrue("emptyset1 is not empty", emptyset1.size() == 0 && emptyset1.isEmpty());
      
      // adding first element should return new set: [a]
      Set setA1 = setpool.add(emptyset1, "a", dereference);
      assertTrue("setA1 is incorrect", setA1.size() == 1 && setA1.contains("a"));
      
      // adding existing element should return same set: [a]
      Set setA2 = setpool.add(setA1, "a", dereference);
      assertTrue("!CollectionUtils.equalsUnorderedSet(setA1, setA2)",
                 CollectionUtils.equalsUnorderedSet(setA1, setA2));
      
      // adding second element should return new set: [a,b]
      Set setAB1 = setpool.add(setA2, "b", dereference);
      assertTrue("setAB1.size() != 2", setAB1.size() == 2);
      assertTrue("!setAB1.contains('a')", setAB1.contains("a"));
      assertTrue("!setAB1.contains('b')", setAB1.contains("b"));
      
      // adding third element should return new set: [a,b,c]
      Set setABC1 = setpool.add(setAB1, "c", dereference);
      assertTrue("setABC1.size() != 3", setABC1.size() == 3);
      assertTrue("!setABC1.contains('a')", setABC1.contains("a"));
      assertTrue("!setABC1.contains('b')", setABC1.contains("b"));
      assertTrue("!setABC1.contains('c')", setABC1.contains("c"));
      
      // removing third element should return setAB1: [a,b]
      Set setAB2 = setpool.remove(setABC1, "c", dereference);
      assertTrue("!CollectionUtils.equalsUnorderedSet(setAB1, setAB2)",
                 CollectionUtils.equalsUnorderedSet(setAB1, setAB2));
      
      // removing second element should return setA1: [a]
      Set setA3 = setpool.remove(setAB1, "b", dereference);
      assertTrue("!CollectionUtils.equalsUnorderedSet(setA3, setA1)",
                 CollectionUtils.equalsUnorderedSet(setA3, setA1));
      
      // removing first element should return emptyset1: []
      Set emptyset2 = setpool.remove(setA3, "a", dereference);
      assertTrue("!CollectionUtils.equalsUnorderedSet(emptyset2, emptyset1)",
                 CollectionUtils.equalsUnorderedSet(emptyset2, emptyset1));

      // emptyset1 and setAB2 should still be in setpool
      assertTrue("setpool.size() != 2", setpool.size() == 2);
      
      // dereference setAB2, and check that setpool is empty
      setpool.dereference(setAB2);
      assertTrue("setpool.size() != 1", setpool.size() == 1);

      // dereference emptyset1, and check that setpool is empty
      setpool.dereference(emptyset2);
      assertTrue("setpool.size() != 0", setpool.size() == 0 && setpool.isEmpty());
    }    
  }
  
  public void testSetPoolNoGarbageCollect() {
    SetPoolIF setpool = new SetPool();      
    assertTrue("Set pool is not empty", setpool.size() == 0 && setpool.isEmpty());

    // Make sure that sets are not dereferenced
    boolean dereference = false;
    int times = 3;
    for (int i=0; i < times; i++) {
      Set emptyset1 = setpool.reference(Collections.EMPTY_SET);
      assertTrue("emptyset1 is not empty", emptyset1.size() == 0 && emptyset1.isEmpty());
      
      // adding first element should return new set: [a]
      Set setA1 = setpool.add(emptyset1, "a", dereference);
      assertTrue("setA1 is incorrect", setA1.size() == 1 && setA1.contains("a"));
      
      // adding existing element should return same set: [a]
      Set setA2 = setpool.add(setA1, "a", dereference);
      assertTrue("setA1 != setA2", setA1 == setA2);
      
      // adding second element should return new set: [a,b]
      Set setAB1 = setpool.add(setA2, "b", dereference);
      assertTrue("setAB1.size() != 2", setAB1.size() == 2);
      assertTrue("!setAB1.contains('a')", setAB1.contains("a"));
      assertTrue("!setAB1.contains('b')", setAB1.contains("b"));
      
      // adding third element should return new set: [a,b,c]
      Set setABC1 = setpool.add(setAB1, "c", dereference);
      assertTrue("setABC1.size() != 3", setABC1.size() == 3);
      assertTrue("!setABC1.contains('a')", setABC1.contains("a"));
      assertTrue("!setABC1.contains('b')", setABC1.contains("b"));
      assertTrue("!setABC1.contains('c')", setABC1.contains("c"));
      
      // removing third element should return setAB1: [a,b]
      Set setAB2 = setpool.remove(setABC1, "c", dereference);
      assertTrue("setAB2 != setAB1 " + setAB2 + ":" + setAB1, setAB2 == setAB1);
      
      // removing second element should return setA1: [a]
      Set setA3 = setpool.remove(setAB1, "b", dereference);
      assertTrue("setA3 != setA1", setA3 == setA1);
      
      // removing first element should return emptyset1: []
      Set emptyset2 = setpool.remove(setA3, "a", dereference);
      assertTrue("emptyset2 != emptyset1", emptyset2 == emptyset1);
    }
    
  }
  
}
