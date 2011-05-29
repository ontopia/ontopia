
package net.ontopia.utils;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class CachedGrabberTest extends AbstractGrabberTest {

  public CachedGrabberTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(CachedGrabberTest.class);
  }
  
  public void testCachedGrabber() {
    CachedGrabber grb = new CachedGrabber(new SubstringGrabber(5, 15));
    
    String str = "Jazz is not dead, it just smells funny!";
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
    str.replace(' ', '-');
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
    grb.refresh();
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
    str.replace('-', '_');
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));

    grb.setGrabber(new UpperCaseGrabber());
    testGrabber(grb.grab(str), grb.grab(str), grb.getGrabber().grab(str));
    grb.refresh();
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
  }

}




