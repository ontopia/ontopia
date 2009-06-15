// $Id: SubstringGrabberTest.java,v 1.4 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils.test;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class SubstringGrabberTest extends AbstractGrabberTest {

  public SubstringGrabberTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(SubstringGrabberTest.class);
  }
  
  public void testSubstringGrabber() {
    String str = "Jazz is NOT dead, it JUST SMELLS funny";
      
    testGrabber(new SubstringGrabber(2, 5).grab(str), str.substring(2, 5), str.substring(1,4));
    testGrabber(new SubstringGrabber(0, 10).grab(str), str.substring(0, 10), str.substring(1, 10));
    testGrabber(new SubstringGrabber(2, str.length()).grab(str), str.substring(2, str.length()), str.substring(2, str.length() - 1));
  }

}




