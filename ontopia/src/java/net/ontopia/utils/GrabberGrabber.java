// $Id: GrabberGrabber.java,v 1.5 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Grabber that makes the second grabber grab what the first
 * grabber grabs and so on. Any number of grabbers may be chained
 * together.</p>
 */

public class GrabberGrabber implements GrabberIF {

  protected List grabbers = new ArrayList();
  
  public GrabberGrabber(GrabberIF grabber1, GrabberIF grabber2) {
    grabbers.add(grabber1);
    grabbers.add(grabber2);
  }

  public GrabberGrabber(GrabberIF grabber1, GrabberIF grabber2, GrabberIF grabber3) {
    grabbers.add(grabber1);
    grabbers.add(grabber2);
    grabbers.add(grabber3);
  }

  public GrabberGrabber(GrabberIF grabber1, GrabberIF grabber2, GrabberIF grabber3, GrabberIF grabber4) {
    grabbers.add(grabber1);
    grabbers.add(grabber2);
    grabbers.add(grabber3);
    grabbers.add(grabber4);
  }

  /**
   * Gets the chained grabbers.
   */  
  public List getGrabbers() {
    return grabbers;
  }

  /**
   * Sets the grabbers.
   */  
  public void setGrabbers(List grabbers) {
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
    Iterator iter = grabbers.iterator();
    while (iter.hasNext()) {
      GrabberIF grabber = (GrabberIF)iter.next();
      grabbed = grabber.grab(grabbed);
    }
    return grabbed;
  }

}




