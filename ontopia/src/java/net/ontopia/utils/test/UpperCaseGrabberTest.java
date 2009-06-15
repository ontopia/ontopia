// $Id: UpperCaseGrabberTest.java,v 1.4 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils.test;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class UpperCaseGrabberTest extends AbstractGrabberTest {

  public UpperCaseGrabberTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(UpperCaseGrabberTest.class);
  }
  
  public void testUpperCaseGrabber() {
    String str = "Don't you eat that YELLOW snow!";
      
    testGrabber(new UpperCaseGrabber().grab(str), str.toUpperCase(), str);
  }

}




