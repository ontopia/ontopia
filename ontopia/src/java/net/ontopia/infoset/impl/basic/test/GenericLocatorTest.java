
package net.ontopia.infoset.impl.basic.test;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;

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
    assertTrue("locator does not equal itself",
	   locator.equals(locator));
    assertTrue("locator equals null",
	   !locator.equals(null));
    
    LocatorIF locator2 = createLocator("generic", ADDRESS);
    assertTrue("comparison of notation name not case-insensitive",
	   locator.equals(locator2));
  }
}
