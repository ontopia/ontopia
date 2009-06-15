// $Id: GrabberComparator.java,v 1.5 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Comparator that compares grabbed objects using a
 * comparator.
 */

public class GrabberComparator implements Comparator {

  protected GrabberIF grabber1;
  protected GrabberIF grabber2;

  protected Comparator comparator;
   
  public GrabberComparator(GrabberIF grabber, Comparator comparator) {
    this.grabber1 = grabber;
    this.comparator = comparator;
  }
 
  public GrabberComparator(GrabberIF grabber1, GrabberIF grabber2, Comparator comparator) {
    this.grabber1 = grabber1;
    this.grabber2 = grabber2;
    this.comparator = comparator;
  }
 
  public int compare(Object object1, Object object2) {
    // Grab objects
    Object grabbed1 = grabber1.grab(object1);
    Object grabbed2;
    if (grabber2 == null)
      grabbed2 = grabber1.grab(object2);
    else 
      grabbed2 = grabber2.grab(object2);

    // Compare grabbed objects
    return comparator.compare(grabbed1, grabbed2);    
  }
  
}




