// $Id: GrabberIterator.java,v 1.5 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: An iterator that uses a grabber to grab object from another
 * iterator.</p>
 */

public class GrabberIterator implements Iterator {

  protected Iterator iter;
  protected GrabberIF grabber;
  
  public GrabberIterator(Iterator iter, GrabberIF grabber) {
    this.iter = iter;
    this.grabber = grabber;
  }
  
  public boolean hasNext() {
    return iter.hasNext();
  }

  public Object next() {
    return grabber.grab(iter.next());
  }

  public void remove() {
    iter.remove();
  }
  
}




