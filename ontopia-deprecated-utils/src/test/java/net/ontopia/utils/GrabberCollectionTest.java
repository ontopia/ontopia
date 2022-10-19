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
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;

public class GrabberCollectionTest extends AbstractCollectionTest {

  @Override
  protected void assertCollection(Collection coll, Collection identical, Collection smaller) {
    super.assertCollection(coll, identical, smaller);

    String[] strings = new String[intended_size];
    Assert.assertTrue("coll toarray[2]", coll.toArray(strings).length == intended_size);
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
    HashSet ds = new HashSet();
    int c = 65;
    for (int i = 0; i < size; i++) {
      ds.add(new EqualsDecider("" + (char)(c+i)));
    }
    return new OrDecider(ds);
  }  

  @Test
  public void testGrabberCollection() {
    UpperCaseGrabber grbU = new UpperCaseGrabber();
    assertCollection(new GrabberCollection(getCollectionLow(intended_size), grbU), getCollection(intended_size), getCollection(intended_size - 5));
    assertCollection(new GrabberCollection(getCollectionLow(intended_size), grbU, getDeciders(intended_size)), getCollection(intended_size), getCollection(intended_size - 5));
    intended_size = 10;
  }

}
