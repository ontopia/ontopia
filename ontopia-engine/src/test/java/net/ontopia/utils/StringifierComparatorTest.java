
package net.ontopia.utils;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class StringifierComparatorTest extends AbstractComparatorTest {

  public StringifierComparatorTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(StringifierComparatorTest.class);
  }
  
  public void testStringifierComparator() {
    UpperCaseGrabber upg = new UpperCaseGrabber();
    GrabberStringifier grb = new GrabberStringifier(upg);
    testComparator(new StringifierComparator(grb).compare(upg.grab("foobar"), "FOOBAR"), 0, 1);
  }

}




