// $Id: GrabberCollectionTest.java,v 1.7 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils;

import junit.framework.*;
import java.util.*;

public class GrabberCollectionTest extends AbstractCollectionTest {

  public GrabberCollectionTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(GrabberCollectionTest.class);
  }
  
  protected void testCollection(Collection coll, Collection identical, Collection smaller) {
    super.testCollection(coll, identical, smaller);

    String[] strings = new String[intended_size];
    assertTrue("coll toarray[2]", coll.toArray(strings).length == intended_size);
  }

  protected Collection getCollectionLow(int size) {
    Collection list = new ArrayList();
    int c = 97;
    for (int i = 0; i < size; i++) {
      list.add("" + (char)(c+i));
    }
    return list;
  }

  protected DeciderIF getDeciders(int size) {
    Collection list = new ArrayList();
    HashSet ds = new HashSet();
    int c = 65;
    for (int i = 0; i < size; i++) {
      ds.add(new EqualsDecider("" + (char)(c+i)));
    }
    return new OrDecider(ds);
  }  

  public void testGrabberCollection() {
    UpperCaseGrabber grbU = new UpperCaseGrabber();
    testCollection(new GrabberCollection(getCollectionLow(intended_size), grbU), getCollection(intended_size), getCollection(intended_size - 5));
    testCollection(new GrabberCollection(getCollectionLow(intended_size), grbU, getDeciders(intended_size)), getCollection(intended_size), getCollection(intended_size - 5));
    intended_size = 10;
  }

}




