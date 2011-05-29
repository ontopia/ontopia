
package net.ontopia.utils;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class LowerCaseGrabberTest extends AbstractGrabberTest {

  public LowerCaseGrabberTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(LowerCaseGrabberTest.class);
  }
  
  public void testLowerCaseGrabber() {
    String str = "Jazz is NOT dead, it JUST SMELLS funny";
      
    testGrabber(new LowerCaseGrabber().grab(str), str.toLowerCase(), str);
  }

}




