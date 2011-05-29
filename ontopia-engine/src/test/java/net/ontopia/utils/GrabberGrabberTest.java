
package net.ontopia.utils;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class GrabberGrabberTest extends AbstractGrabberTest {

  public GrabberGrabberTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(GrabberGrabberTest.class);
  }
  
  public void testGrabberGrabber() {
    LowerCaseGrabber grbLow = new LowerCaseGrabber();
    UpperCaseGrabber grbUp = new UpperCaseGrabber();
    SubstringGrabber grbSub1 = new SubstringGrabber(4, 15);
    SubstringGrabber grbSub2 = new SubstringGrabber(2,7);

    GrabberGrabber grbGrb1 = new GrabberGrabber(grbLow, grbUp);
    GrabberGrabber grbGrb2 = new GrabberGrabber(grbLow, grbUp);
    GrabberGrabber grbGrb3 = new GrabberGrabber(grbUp, grbLow);
    testGrabber(grbGrb1.getGrabbers(), grbGrb2.getGrabbers(), grbGrb3.getGrabbers()); 
    GrabberGrabber grbGrb4 = new GrabberGrabber(grbSub1, grbSub2, grbUp); 
    GrabberGrabber grbGrb5 = new GrabberGrabber(grbSub1, grbSub2, grbUp); 
    GrabberGrabber grbGrb6 = new GrabberGrabber(grbSub1, grbSub2, grbLow); 
    testGrabber(grbGrb4.getGrabbers(), grbGrb5.getGrabbers(), grbGrb6.getGrabbers()); 
    GrabberGrabber grbGrb7 = new GrabberGrabber(grbSub1, grbSub2, grbUp, grbLow); 
    GrabberGrabber grbGrb8 = new GrabberGrabber(grbSub1, grbSub2, grbUp, grbLow); 
    GrabberGrabber grbGrb9 = new GrabberGrabber(grbSub1, grbSub2, grbLow, grbUp);
    testGrabber(grbGrb7.getGrabbers(), grbGrb8.getGrabbers(), grbGrb9.getGrabbers()); 

    grbGrb1.addGrabber(grbSub1);
    grbGrb2.addGrabber(grbSub1);
    grbGrb3.addGrabber(grbSub1);
    
    grbGrb4.setGrabbers(grbGrb7.getGrabbers());
    grbGrb6.setGrabbers(grbGrb9.getGrabbers());
    testGrabber(grbGrb4.getGrabbers(), grbGrb7.getGrabbers(), grbGrb6.getGrabbers()); 

    String str = "JaZz Is NoT dEaD, iT jUsT sMeLlS fUnNy!";
    testGrabber(grbGrb4.grab(str), grbGrb7.grab(str), grbGrb6.grab(str));
  }

}




