
// $Id: SameGrabber.java,v 1.4 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Grabber that grabs the object given to it, i.e. returns
 * the object that was given to it.</p>
 */

public class SameGrabber implements GrabberIF {
  
  public Object grab(Object object) {
    return object;
  }

}




