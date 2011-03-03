// $Id: GrabberGrabber.java,v 1.5 2004/11/29 19:10:44 grove Exp $

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

  protected List<GrabberIF<Object, Object>> grabbers = new ArrayList<GrabberIF<Object, Object>>();
  
  public GrabberGrabber(GrabberIF<Object, Object>... grabbers) {
    this.grabbers = new ArrayList<GrabberIF<Object, Object>>(Arrays.asList(grabbers));
  }

  /**
   * Gets the chained grabbers.
   */  
  public List<GrabberIF<Object, Object>> getGrabbers() {
    return grabbers;
  }

  /**
   * Sets the grabbers.
   */  
  public void setGrabbers(List<GrabberIF<Object, Object>> grabbers) {
    this.grabbers = grabbers;
  }
  
  /**
   * Add grabber to the end of the grabber list.
   */  
  public void addGrabber(GrabberIF<Object, Object> grabber) {
    grabbers.add(grabber);
  }
  
  public Object grab(Object object) {
    Object grabbed = object;
    // Loop over grabbers
    Iterator<GrabberIF<Object, Object>> iter = grabbers.iterator();
    while (iter.hasNext()) {
      GrabberIF<Object, Object> grabber = iter.next();
      grabbed = grabber.grab(grabbed);
    }
    return grabbed;
  }

}




