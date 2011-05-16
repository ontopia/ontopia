
// $Id: AbstractLocatorTest.java,v 1.5 2003/02/13 13:56:02 larsga Exp $	

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
