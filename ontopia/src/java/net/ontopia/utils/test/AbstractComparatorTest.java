// $Id: AbstractComparatorTest.java,v 1.6 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils.test;

import java.util.*;
import net.ontopia.test.*;
import net.ontopia.utils.*;

public abstract class AbstractComparatorTest extends AbstractOntopiaTestCase {

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




