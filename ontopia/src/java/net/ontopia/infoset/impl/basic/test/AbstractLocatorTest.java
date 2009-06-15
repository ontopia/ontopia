
// $Id: AbstractLocatorTest.java,v 1.5 2003/02/13 13:56:02 larsga Exp $	

package net.ontopia.infoset.impl.basic.test;

import net.ontopia.test.*;
import net.ontopia.infoset.core.LocatorIF;

public abstract class AbstractLocatorTest extends AbstractOntopiaTestCase {
  
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
    
    assertTrue("equal locators are not equal [1]", locator1.equals(locator2));
    assertTrue("equal locators are not equal [2]", locator2.equals(locator1));    
  }
  
}
