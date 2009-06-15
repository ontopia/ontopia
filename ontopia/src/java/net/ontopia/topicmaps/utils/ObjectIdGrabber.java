// $Id: ObjectIdGrabber.java,v 1.7 2004/11/29 19:01:50 grove Exp $

package net.ontopia.topicmaps.utils;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Grabber that grabs the object id of the TMObjectIF given to
 * it. This class is very similar to the ObjectIdGrabber, except that
 * this class implements GrabberIF instead of StringifierIF.</p>
 */

public class ObjectIdGrabber implements GrabberIF {
  
  /**
   * INTERNAL: Grabs the objectId of the given TMObjectIF
   *
   * @param object the given object; internally typecast to TMObjectIF
   * @return object which is the objectId of the given TMObjectIF
   */ 

  public Object grab(Object object) {
    return ((TMObjectIF)object).getObjectId();

  }

}





