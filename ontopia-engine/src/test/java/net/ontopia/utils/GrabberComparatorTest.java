
package net.ontopia.utils;

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class GrabberComparatorTest extends AbstractComparatorTest {

  public GrabberComparatorTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(GrabberComparatorTest.class);
  }
  
  public void testGrabberComparator() {
    UpperCaseGrabber upg = new UpperCaseGrabber();
    LowerCaseGrabber log = new LowerCaseGrabber();
    Comparator sc = LexicalComparator.CASE_SENSITIVE;
    Comparator isc = new GrabberComparator(new UpperCaseGrabber(), sc);

    testComparator(new GrabberComparator(upg, sc).compare("foobar", "FOOBAR"), 0, 1);
    testComparator(new GrabberComparator(upg, isc).compare("foobar", "FoOBAR"), 0, 1);
    testComparator(new GrabberComparator(upg, log, sc).compare("foobar", "FoOBAR"), 
                   new GrabberComparator(log, upg, sc).compare("foobar", "FoOBAR") * -1, 
                   new GrabberComparator(upg, isc).compare("foobar", "FoOBAR "));
  }

}




