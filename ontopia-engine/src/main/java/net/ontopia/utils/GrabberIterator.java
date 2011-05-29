
package net.ontopia.utils;

import java.util.Iterator;

/**
 * INTERNAL: An iterator that uses a grabber to grab object from another
 * iterator.</p>
 */

public class GrabberIterator<O, G> implements Iterator<G> {

  protected Iterator<O> iter;
  protected GrabberIF<O, G> grabber;
  
  public GrabberIterator(Iterator<O> iter, GrabberIF<O, G> grabber) {
    this.iter = iter;
    this.grabber = grabber;
  }
  
  public boolean hasNext() {
    return iter.hasNext();
  }

  public G next() {
    return grabber.grab(iter.next());
  }

  public void remove() {
    iter.remove();
  }
  
}




