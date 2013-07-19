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




