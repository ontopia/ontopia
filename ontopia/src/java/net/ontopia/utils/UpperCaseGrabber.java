
// $Id: UpperCaseGrabber.java,v 1.5 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

/**
 * INTERNAL: Grabber that uppercases the String object given to it.
 */

public class UpperCaseGrabber implements GrabberIF {
  
  public Object grab(Object object) {
    return object.toString().toUpperCase();
  }

}




