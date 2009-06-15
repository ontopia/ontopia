
// $Id: TrackableSetTest.java,v 1.5 2005/07/12 09:37:41 grove Exp $

package net.ontopia.persistence.proxy.test;

import java.util.Collections;

import net.ontopia.persistence.proxy.TrackableCollectionIF;
import net.ontopia.persistence.proxy.TrackableSet;

/**
 * INTERNAL: Test cases for testing the TrackableCollectionIF
 * interface implemented by the TrackableSet class. Actual test
 * methods can be found in TrackableCollectionTest.
 */
public class TrackableSetTest extends TrackableCollectionTest {
  
  public TrackableSetTest(String name) {
    super(name);
  }

  protected TrackableCollectionIF createTrackableCollection() {
    return new TrackableSet(null, Collections.EMPTY_SET);
  }
  
}
