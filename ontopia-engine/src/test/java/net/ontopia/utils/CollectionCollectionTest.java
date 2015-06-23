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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestSuite;
import junit.framework.Test;
import org.junit.Assert;

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
  
  public void testEmpty() {
    Collection<Collection<Object>> singletonList = Collections.<Collection<Object>>singletonList(new ArrayList<Object>());
    CollectionCollection<Object> collectionCollection = new CollectionCollection<Object>(singletonList);
    
    // Failed due to #515
    Assert.assertFalse(collectionCollection.iterator().hasNext());
  }

}




