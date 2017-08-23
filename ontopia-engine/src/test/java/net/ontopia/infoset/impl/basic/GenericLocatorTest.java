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

public class GenericLocatorTest extends AbstractLocatorTest {

  protected static final String NOTATION = "GENERIC";
  protected static final String ADDRESS = "Some obscure address";
  
  public GenericLocatorTest(String name) {
    super(name);
  }

  protected LocatorIF createLocator() {
    return createLocator(NOTATION, ADDRESS);
  }

  protected LocatorIF createLocator(String notation, String address) {
    return new GenericLocator(notation, address);
  }
    
  // --- tests

  public void testProperties() {
    LocatorIF locator = createLocator(NOTATION, ADDRESS);
    assertTrue("notation property not correctly set",
	   NOTATION.equals(locator.getNotation()));
    assertTrue("address property not correctly set",
	   ADDRESS.equals(locator.getAddress()));
  }

  public void testEquals() {
    LocatorIF locator = createLocator(NOTATION, ADDRESS);
    assertEquals("locator does not equal itself", locator, locator);
    assertFalse("locator equals null", locator.equals(null));
    
    LocatorIF locator2 = createLocator("generic", ADDRESS);
    assertEquals("comparison of notation name not case-insensitive", locator2, locator);
  }
}
