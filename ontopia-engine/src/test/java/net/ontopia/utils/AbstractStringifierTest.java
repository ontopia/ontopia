
package net.ontopia.utils;

import junit.framework.TestCase;

public abstract class AbstractStringifierTest extends TestCase {

  protected int intended_size = 8;
  
  public AbstractStringifierTest(String name) {
    super(name);
  }

  protected void setUp() {
  }

  protected void tearDown() {
  }

  protected void testStringifier(Object str, Object identical, Object different) {
    assertTrue("stringifier is not equal", str.equals(identical));
    assertTrue("stringifier is equal", !str.equals(different));
  }
  
}




