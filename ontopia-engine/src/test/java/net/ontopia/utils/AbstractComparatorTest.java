
package net.ontopia.utils;

import junit.framework.TestCase;

public abstract class AbstractComparatorTest extends TestCase {

  protected int intended_size = 8;
  
  public AbstractComparatorTest(String name) {
    super(name);
  }

  protected void setUp() {
  }

  protected void tearDown() {
  }

  protected void testComparator(int comp, int identical, int different) {
    assertTrue("comparator is not equal", comp == identical);
    assertTrue("comparator is equal", comp != different);
  }
  
}




