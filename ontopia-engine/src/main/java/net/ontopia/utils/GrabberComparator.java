
package net.ontopia.utils;

import java.util.Comparator;

/**
 * INTERNAL: Comparator that compares grabbed objects using a
 * comparator.
 */

public class GrabberComparator<T, G> implements Comparator<T> {

  protected GrabberIF<T, G> grabber1;
  protected GrabberIF<T, G> grabber2;

  protected Comparator<G> comparator;
   
  public GrabberComparator(GrabberIF<T, G> grabber, Comparator<G> comparator) {
    this.grabber1 = grabber;
    this.comparator = comparator;
  }
 
  public GrabberComparator(GrabberIF<T, G> grabber1, GrabberIF<T, G> grabber2, Comparator<G> comparator) {
    this.grabber1 = grabber1;
    this.grabber2 = grabber2;
    this.comparator = comparator;
  }
 
  public int compare(T object1, T object2) {
    // Grab objects
    G grabbed1 = grabber1.grab(object1);
    G grabbed2;
    if (grabber2 == null)
      grabbed2 = grabber1.grab(object2);
    else 
      grabbed2 = grabber2.grab(object2);

    // Compare grabbed objects
    return comparator.compare(grabbed1, grabbed2);    
  }
  
}




