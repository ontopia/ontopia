// $Id: AbstractIteratorTest.java,v 1.6 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils.test;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.test.*;

public abstract class AbstractIteratorTest extends AbstractOntopiaTestCase {

  public AbstractIteratorTest(String name) {
    super(name);
  }

  protected Iterator getIterator(int size) {
    return getCollection(size).iterator();
  }
  
  protected Collection getCollection(int size) {
    Collection list = new ArrayList();
    int c = 65;
    for (int i = 0; i < size; i++) {
      list.add("" + (char)(c+i));
    }
    return list;
  }
  
  protected void testIterator(Iterator iterator, int size) {
    int count = 0;
    
    while (iterator.hasNext()) {
      count++;
      Object element = iterator.next();
      // System.out.println("Object #" + count + ":" + iterator.next());
    }
    assertTrue("Number of elements equal", count == size);
  }

}




