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

import net.ontopia.infoset.core.LocatorIF;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractLocatorTest {
  
  protected abstract LocatorIF createLocator();
  protected abstract LocatorIF createLocator(String notation, String address);
    
  // --- tests
  
  @Test
  public void testEqualsNullArgument() {
    LocatorIF locator = createLocator();
    Assert.assertFalse("checking equality with null object", locator.equals(null));
  }
  
  @Test
  public void testIdenticalEquality() {
    LocatorIF locator1 = createLocator();
    LocatorIF locator2 = createLocator();
    
    Assert.assertEquals("equal locators are not equal [1]", locator2, locator1);
    Assert.assertEquals("equal locators are not equal [2]", locator1, locator2);
  }
}
