
package net.ontopia.utils;

import junit.framework.TestCase;

public abstract class AbstractGrabberTest extends TestCase {

  protected int intended_size = 8;
  
  public AbstractGrabberTest(String name) {
    super(name);
  }

  protected void setUp() {
  }

  protected void tearDown() {
  }

  protected void testGrabber(Object grb, Object identical, Object different) {
    assertTrue("grabber is not equal", grb.equals(identical));
    assertTrue("grabber is equal", !grb.equals(different));
  }
  
}




