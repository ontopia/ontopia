
package net.ontopia.utils;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class DeciderIteratorTest extends AbstractIteratorTest {

  public DeciderIteratorTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(DeciderIteratorTest.class);
  }
  
  public void testDeciderIterator() {

    int size = 5;
    
    DeciderIF decider = new DeciderIF() {
      public boolean ok(Object object) {
        if (object.equals("B")) return false;
        return true;
      }
    };
    
    Iterator iter = new DeciderIterator(decider, getIterator(size));

    testIterator(iter, size - 1);
  }

}




