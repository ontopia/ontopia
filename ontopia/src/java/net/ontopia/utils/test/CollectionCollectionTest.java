// $Id: CollectionCollectionTest.java,v 1.4 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils.test;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class CollectionCollectionTest extends AbstractCollectionTest {

  public CollectionCollectionTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(CollectionCollectionTest.class);
  }
  
  public void testCollection(Collection coll, Collection identical, Collection smaller) {
    super.testCollection(coll, identical, smaller);
  }

  protected Collection getCollections(int numcol, int size) {
    Collection list = (Collection) new ArrayList();
    for (int j = 0; j < numcol; j++) {
      list.add(getCollection(size));
    }
    return list;
  }

  protected Collection getCollectionList(int numcol, int size) {
    Collection list = new ArrayList();
    for (int j = 0; j < numcol; j++) {
      list.addAll(getCollection(size));
    }
    return list;
  }

  public void testCollectionCollection() {
    intended_size = 40;
    testCollection(new CollectionCollection(getCollections(5, 8)), getCollectionList(5, 8), getCollectionList(6, 3));
    testCollection(new CollectionCollection(getCollections(5, 8)), getCollectionList(5, 8), getCollectionList(2, 7));
  }

}




