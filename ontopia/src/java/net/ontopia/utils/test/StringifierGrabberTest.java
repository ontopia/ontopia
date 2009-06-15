// $Id: StringifierGrabberTest.java,v 1.5 2002/08/01 13:04:37 grove Exp $

package net.ontopia.utils.test;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class StringifierGrabberTest extends AbstractGrabberTest {

  public StringifierGrabberTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(StringifierGrabberTest.class);
  }
  
  public void testStringifierGrabber() {
    testGrabber(new StringifierGrabber().grab(new Float(1234.56)), new Float(1234.56).toString(), new Float(1234).toString()); 
    testGrabber(new StringifierGrabber().grab(new Double(4321.56)), new Double(4321.56).toString(), new Double(4321.57).toString()); 
    testGrabber(new StringifierGrabber().grab(new Integer(2987)), new Integer(2987).toString(), new Integer(2986).toString()); 
    testGrabber(new StringifierGrabber().grab(new Integer(2987)), new Integer(2987).toString(), new Float(2987).toString()); 
    testGrabber(new StringifierGrabber().grab(Boolean.TRUE), Boolean.TRUE.toString(), Boolean.FALSE.toString()); 
  }

}




