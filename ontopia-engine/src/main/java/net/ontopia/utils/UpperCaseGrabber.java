
package net.ontopia.utils;

/**
 * INTERNAL: Grabber that uppercases the String object given to it.
 */

public class UpperCaseGrabber implements GrabberIF {
  
  public Object grab(Object object) {
    return object.toString().toUpperCase();
  }

}




