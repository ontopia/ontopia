
// $Id: LowerCaseGrabber.java,v 1.5 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

/**
 * INTERNAL: Grabber that lowercases the String object given to it.
 */

public class LowerCaseGrabber implements GrabberIF {
  
  public Object grab(Object object) {
    return object.toString().toLowerCase();
  }

}




