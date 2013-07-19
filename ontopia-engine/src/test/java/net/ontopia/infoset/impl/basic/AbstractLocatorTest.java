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

package net.ontopia.infoset.impl.basic;

import junit.framework.TestCase;
import net.ontopia.infoset.core.LocatorIF;

public abstract class AbstractLocatorTest extends TestCase {
  
  public AbstractLocatorTest(String name) {
    super(name);
  }

  protected abstract LocatorIF createLocator();
  protected abstract LocatorIF createLocator(String notation, String address);
    
  // --- tests
  
  public void testEqualsNullArgument() {
    LocatorIF locator = createLocator();
    assertTrue("checking equality with null object", locator.equals(null) == false);
  }
  
  public void testIdenticalEquality() {
    LocatorIF locator1 = createLocator();
    LocatorIF locator2 = createLocator();
    
    assertEquals("equal locators are not equal [1]", locator2, locator1);
    assertEquals("equal locators are not equal [2]", locator1, locator2);
  }
  
}
