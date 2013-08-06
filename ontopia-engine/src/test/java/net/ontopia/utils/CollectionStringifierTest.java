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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import junit.framework.Test;
import junit.framework.TestSuite;

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




