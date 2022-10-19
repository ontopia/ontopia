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

public class GenericLocatorTest extends AbstractLocatorTest {

  protected static final String NOTATION = "GENERIC";
  protected static final String ADDRESS = "Some obscure address";
  
  @Override
  protected LocatorIF createLocator() {
    return createLocator(NOTATION, ADDRESS);
  }

  @Override
  protected LocatorIF createLocator(String notation, String address) {
    return new GenericLocator(notation, address);
  }
    
  // --- tests

  @Test
  public void testProperties() {
    LocatorIF locator = createLocator(NOTATION, ADDRESS);
    Assert.assertTrue("notation property not correctly set",
	   NOTATION.equals(locator.getNotation()));
    Assert.assertTrue("address property not correctly set",
	   ADDRESS.equals(locator.getAddress()));
  }

  @Test
  public void testEquals() {
    LocatorIF locator = createLocator(NOTATION, ADDRESS);
    Assert.assertEquals("locator does not equal itself", locator, locator);
    Assert.assertFalse("locator equals null", locator.equals(null));
    
    LocatorIF locator2 = createLocator("generic", ADDRESS);
    Assert.assertEquals("comparison of notation name not case-insensitive", locator2, locator);
  }
}
