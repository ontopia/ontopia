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
import org.junit.Assert;

public abstract class AbstractCollectionTest {

  protected int intended_size = 8;
  
  protected Collection getCollection(int size) {
    Collection list = new ArrayList();
    int c = 65;
    for (int i = 0; i < size; i++) {
      list.add("" + (char)(c+i));
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


  protected void assertCollection(Collection coll, Collection identical, Collection smaller) {
    // Size checks
    if (coll.size() > 0) {
      Assert.assertTrue("coll is not empty[1]", coll.isEmpty() == false);
    } else {
      Assert.assertTrue("coll is empty[1]", coll.isEmpty());
    }
    
    Assert.assertTrue("coll size[1]", coll.size() == intended_size);
    
    if (coll.size() > 0) {
      Assert.assertTrue("coll is not empty[2]", coll.isEmpty() == false);
    } else {
      Assert.assertTrue("coll is empty[2]", coll.isEmpty());
    }
    
    Assert.assertTrue("coll size[2]", coll.size() == intended_size);

    Assert.assertTrue("coll toarray[1]", coll.toArray().length == intended_size);

    // Equality check on identical collection
    Assert.assertTrue("coll equals identical" + coll.size() +""+ identical.size(), coll.equals(identical));

    // Containment check on identical collection
    Assert.assertTrue("coll contains all", coll.containsAll(identical));

    // Containment
    if (coll.size() > 0) {
      Assert.assertTrue("coll contains A", coll.contains("A"));
    }
    if (coll.size() > 1) {
      Assert.assertTrue("coll contains B", coll.contains("B"));
    }

    // Test with smaller collection
    Assert.assertTrue("coll contains all smaller", coll.containsAll(smaller));

    if (coll.size() > 0) {
      Assert.assertTrue("smaller contains all coll", !smaller.containsAll(coll));
    } else {
      Assert.assertTrue("smaller contains all coll", smaller.containsAll(coll));
    }
    
    if (coll.size() > 0) {
      Assert.assertTrue("coll equals smaller", !coll.equals(smaller));
    } else {
      Assert.assertTrue("coll equals smaller", coll.equals(smaller));
    }
      
    Assert.assertTrue("coll equals identical", coll.equals(identical));
    
  }
  
}
