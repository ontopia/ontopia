// $Id: ObjectIdStringifier.java,v 1.7 2004/11/29 19:01:50 grove Exp $

package net.ontopia.topicmaps.utils;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Stringifier that returns the object id of a topic map object.
 */

public class ObjectIdStringifier implements StringifierIF {
  
  /**
   * INTERNAL: Stringifies an arbitrary topicmap object, using its objectId
   *
   * @param tmobject object, typecast internally to TMObjectIF; the
   * given topicmap object
   * @return string the topic map object id
   */
  public String toString(Object tmobject) {
    if (tmobject == null) return "null";
    return ((TMObjectIF)tmobject).getObjectId();
  }
  
}





