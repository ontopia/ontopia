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

import junit.framework.TestCase;

public abstract class AbstractComparatorTest extends TestCase {

  protected int intended_size = 8;
  
  public AbstractComparatorTest(String name) {
    super(name);
  }

  protected void setUp() {
  }

  protected void tearDown() {
  }

  protected void testComparator(int comp, int identical, int different) {
    assertTrue("comparator is not equal", comp == identical);
    assertTrue("comparator is equal", comp != different);
  }
  
}




