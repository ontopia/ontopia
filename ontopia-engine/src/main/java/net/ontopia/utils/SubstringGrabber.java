
package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Grabber that grabs a substring from the String object given
 * to it.
 */

public class SubstringGrabber implements GrabberIF {

  protected int begin_index;
  protected int end_index;
  
  public SubstringGrabber(int begin_index, int end_index) {
    this.begin_index = begin_index;
    this.end_index = end_index;
  }
  
  public Object grab(Object object) {
    if (object.toString().length() == 0) return "";                                      
    return object.toString().substring(begin_index, end_index);
  }
  
}




