
// $Id: UniqueSetTest.java,v 1.6 2003/06/04 17:24:20 grove Exp $

package net.ontopia.utils.test;

import java.util.*;
import net.ontopia.test.*;
import net.ontopia.utils.*;

public class UniqueSetTest extends AbstractOntopiaTestCase {

  // This class does not subclass CompactHashSetTest because the set
  // implementation is immutable.
  
  public UniqueSetTest(String name) {
    super(name);
  }

  public void setUp() {
  }

  protected void tearDown() {
  }

  // --- Test cases

  public void testUniqueSetGarbageCollect() {
    UniqueSet setpool = new UniqueSet();      
    assertTrue("Set pool is not empty", setpool.size() == 0 && setpool.isEmpty());

    // Make sure that sets are garbage collected when no longer referenced
    boolean dereference = true;
    int times = 3;
    for (int i=0; i < times; i++) {
      UniqueSet emptyset1 = setpool.get(Collections.EMPTY_SET);
      assertTrue("emptyset1 is not empty", emptyset1.size() == 0 && emptyset1.isEmpty());
      
      // adding first element should return new set: [a]
      UniqueSet setA1 = setpool.add(emptyset1, "a", dereference);
      assertTrue("setA1 is incorrect", setA1.size() == 1 && setA1.contains("a"));
      
      // adding existing element should return same set: [a]
      UniqueSet setA2 = setpool.add(setA1, "a", dereference);
      assertTrue("!CollectionUtils.equalsUnorderedSet(setA1, setA2)",
                 CollectionUtils.equalsUnorderedSet(setA1, setA2));
      
      // adding second element should return new set: [a,b]
      UniqueSet setAB1 = setpool.add(setA2, "b", dereference);
      assertTrue("setAB1.size() != 2", setAB1.size() == 2);
      assertTrue("!setAB1.contains('a')", setAB1.contains("a"));
      assertTrue("!setAB1.contains('b')", setAB1.contains("b"));
      
      // adding third element should return new set: [a,b,c]
      UniqueSet setABC1 = setpool.add(setAB1, "c", dereference);
      assertTrue("setABC1.size() != 3", setABC1.size() == 3);
      assertTrue("!setABC1.contains('a')", setABC1.contains("a"));
      assertTrue("!setABC1.contains('b')", setABC1.contains("b"));
      assertTrue("!setABC1.contains('c')", setABC1.contains("c"));
      
      // removing third element should return setAB1: [a,b]
      UniqueSet setAB2 = setpool.remove(setABC1, "c", dereference);
      assertTrue("!CollectionUtils.equalsUnorderedSet(setAB1, setAB2)",
                 CollectionUtils.equalsUnorderedSet(setAB1, setAB2));
      
      // removing second element should return setA1: [a]
      UniqueSet setA3 = setpool.remove(setAB1, "b", dereference);
      assertTrue("!CollectionUtils.equalsUnorderedSet(setA3, setA1)",
                 CollectionUtils.equalsUnorderedSet(setA3, setA1));
      
      // removing first element should return emptyset1: []
      UniqueSet emptyset2 = setpool.remove(setA3, "a", dereference);
      assertTrue("!CollectionUtils.equalsUnorderedSet(emptyset2, emptyset1)",
                 CollectionUtils.equalsUnorderedSet(emptyset2, emptyset1));

      // emptyset1 and setAB2 should still be in setpool
      // FIXME: This test fails occationally (bug #674)
      assertTrue("setpool.size() != 2", setpool.size() == 2);
      
      // dereference setAB2, and check that setpool is empty
      setpool.dereference(setAB2);
      assertTrue("setpool.size() != 1", setpool.size() == 1);

      // dereference emptyset1, and check that setpool is empty
      setpool.dereference(emptyset2);
      assertTrue("setpool.size() != 0", setpool.size() == 0 && setpool.isEmpty());
    }    
  }
  
  public void testUniqueSetNoGarbageCollect() {
    UniqueSet setpool = new UniqueSet();      
    assertTrue("Set pool is not empty", setpool.size() == 0 && setpool.isEmpty());

    // Make sure that sets are not dereferenced
    boolean dereference = false;
    int times = 3;
    for (int i=0; i < times; i++) {
      UniqueSet emptyset1 = setpool.get(Collections.EMPTY_SET);
      assertTrue("emptyset1 is not empty", emptyset1.size() == 0 && emptyset1.isEmpty());
      
      // adding first element should return new set: [a]
      UniqueSet setA1 = setpool.add(emptyset1, "a", dereference);
      assertTrue("setA1 is incorrect", setA1.size() == 1 && setA1.contains("a"));
      
      // adding existing element should return same set: [a]
      UniqueSet setA2 = setpool.add(setA1, "a", dereference);
      assertTrue("setA1 != setA2", setA1 == setA2);
      
      // adding second element should return new set: [a,b]
      UniqueSet setAB1 = setpool.add(setA2, "b", dereference);
      assertTrue("setAB1.size() != 2", setAB1.size() == 2);
      assertTrue("!setAB1.contains('a')", setAB1.contains("a"));
      assertTrue("!setAB1.contains('b')", setAB1.contains("b"));
      
      // adding third element should return new set: [a,b,c]
      UniqueSet setABC1 = setpool.add(setAB1, "c", dereference);
      assertTrue("setABC1.size() != 3", setABC1.size() == 3);
      assertTrue("!setABC1.contains('a')", setABC1.contains("a"));
      assertTrue("!setABC1.contains('b')", setABC1.contains("b"));
      assertTrue("!setABC1.contains('c')", setABC1.contains("c"));
      
      // removing third element should return setAB1: [a,b]
      UniqueSet setAB2 = setpool.remove(setABC1, "c", dereference);
      assertTrue("setAB2 != setAB1 " + setAB2 + ":" + setAB1, setAB2 == setAB1);
      
      // removing second element should return setA1: [a]
      UniqueSet setA3 = setpool.remove(setAB1, "b", dereference);
      assertTrue("setA3 != setA1", setA3 == setA1);
      
      // removing first element should return emptyset1: []
      UniqueSet emptyset2 = setpool.remove(setA3, "a", dereference);
      assertTrue("emptyset2 != emptyset1", emptyset2 == emptyset1);
    }
    
  }
  
}
