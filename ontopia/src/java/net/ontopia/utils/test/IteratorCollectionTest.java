// $Id: IteratorCollectionTest.java,v 1.6 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils.test;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class IteratorCollectionTest extends AbstractCollectionTest {

  public IteratorCollectionTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(IteratorCollectionTest.class);
  }
  
  public void testCollection(Collection coll, Collection identical, Collection smaller) {
    super.testCollection(coll, identical, smaller);

    String[] strings = new String[intended_size];
    assertTrue("coll toarray[2]", coll.toArray(strings).length == intended_size);
  }

  public void testIteratorCollection() {
    testCollection(new IteratorCollection(getCollection(8).iterator()), getCollection(8), getCollection(3));
    int old_size = intended_size;
    intended_size = 3;
    testCollection(new IteratorCollection(getCollection(8).iterator(), 3), getCollection(3), getCollection(2));
    testCollection(new IteratorCollection(getCollection(8).iterator(), 3, 5), getCollection(3), getCollection(2));
    testCollection(new IteratorCollection(getCollection(8).iterator(), 3, 17), getCollection(3), getCollection(2));
    intended_size = 5;
    testCollection(new IteratorCollection(getCollection(8).iterator(), -1, 5), getCollection(5), getCollection(2));
    intended_size = 8;
    testCollection(new IteratorCollection(getCollection(8).iterator(), -1, 8), getCollection(8), getCollection(2));
    testCollection(new IteratorCollection(getCollection(8).iterator(), -1, 17), getCollection(8), getCollection(2));
    intended_size = 0;
    testCollection(new IteratorCollection(getCollection(8).iterator(), 3, 0), getCollection(0), getCollection(0));
    intended_size = old_size;
  }

}




