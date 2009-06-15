// $Id: CollectionStringifierTest.java,v 1.5 2006/02/09 18:58:17 grove Exp $

package net.ontopia.utils.test;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class CollectionStringifierTest extends AbstractStringifierTest {

  public CollectionStringifierTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(CollectionStringifierTest.class);
  }  

  protected Collection getCollectionI(int size) {
    Collection list = new ArrayList();
    int c = 65;
    for (int i = 0; i < size; i++) {
      list.add(new Integer((c+i) * (c+2*i)));
    }
    return list;
  }

  public void printCollection(Collection c){
    Iterator i = c.iterator();
    System.out.println();
    while(i.hasNext()){
      System.out.print(i.next());
    }
    System.out.println();
  }



  protected Collection getCollectionS(int size) {
    Collection list = new ArrayList();
    int c = 65;
    for (int i = 0; i < size; i++) {
      list.add("" + ((c+i) * (c+2*i)));
    }
    return list;
  }
  
  protected Collection getCollectionSa(int size) {
    Collection list = new ArrayList();
    int c = 65;
    for (int i = 0; i < size; i++) {
      list.add("" + (char)(c+i));
    }
    return list;
  }

  protected Collection getCollectionSb(int size) {
    Collection list = new ArrayList();
    int c = 97;
    for (int i = 0; i < size; i++) {
      list.add("" + (char)(c+i));
    }
    return list;
  }

  public void testCollectionStringifier() {
    DefaultStringifier ds = new DefaultStringifier();
    LexicalComparator lc = LexicalComparator.CASE_INSENSITIVE;
    CollectionStringifier cs1 = new CollectionStringifier(ds);
    CollectionStringifier cs2 = new CollectionStringifier(ds, lc);

    testStringifier(cs1.toString(getCollectionI(8)), cs1.toString(getCollectionS(8)), ds.toString(getCollectionS(8)));
    testStringifier(cs2.toString(getCollectionI(8)), cs1.toString(getCollectionS(8)), cs2.toString(getCollectionS(7)));
    testStringifier(cs2.toString(getCollectionSa(8)), cs1.toString(getCollectionSa(8)), cs2.toString(getCollectionSb(8)));

  }

}




