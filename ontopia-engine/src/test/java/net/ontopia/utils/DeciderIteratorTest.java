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

import java.util.Iterator;
import junit.framework.Test;
import junit.framework.TestSuite;

public class DeciderIteratorTest extends AbstractIteratorTest {

  public DeciderIteratorTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(DeciderIteratorTest.class);
  }
  
  public void testDeciderIterator() {

    int size = 5;
    
    DeciderIF decider = new DeciderIF() {
      public boolean ok(Object object) {
        if (object.equals("B")) return false;
        return true;
      }
    };
    
    Iterator iter = new DeciderIterator(decider, getIterator(size));

    testIterator(iter, size - 1);
  }

}




