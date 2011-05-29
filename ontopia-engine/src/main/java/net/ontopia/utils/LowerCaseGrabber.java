
package net.ontopia.utils;

/**
 * INTERNAL: Grabber that lowercases the String object given to it.
 */

public class LowerCaseGrabber implements GrabberIF {
  
  public Object grab(Object object) {
    return object.toString().toLowerCase();
  }

}




