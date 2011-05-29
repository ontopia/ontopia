
package net.ontopia.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * INTERNAL: Grabber that makes the second grabber grab what the first
 * grabber grabs and so on. Any number of grabbers may be chained
 * together.</p>
 */

public class GrabberGrabber implements GrabberIF<Object, Object> {

  protected List<GrabberIF> grabbers = new ArrayList<GrabberIF>();
  
  public GrabberGrabber(GrabberIF... grabbers) {
    this.grabbers = new ArrayList<GrabberIF>(Arrays.asList(grabbers));
  }

  /**
   * Gets the chained grabbers.
   */  
  public List<GrabberIF> getGrabbers() {
    return grabbers;
  }

  /**
   * Sets the grabbers.
   */  
  public void setGrabbers(List<GrabberIF> grabbers) {
    this.grabbers = grabbers;
  }
  
  /**
   * Add grabber to the end of the grabber list.
   */  
  public void addGrabber(GrabberIF grabber) {
    grabbers.add(grabber);
  }
  
  public Object grab(Object object) {
    Object grabbed = object;
    // Loop over grabbers
    Iterator<GrabberIF> iter = grabbers.iterator();
    while (iter.hasNext()) {
      GrabberIF grabber = iter.next();
      grabbed = grabber.grab(grabbed);
    }
    return grabbed;
  }

}




