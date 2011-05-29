
package net.ontopia.topicmaps.utils;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Grabber that grabs the type property of the TypedIF object
 * given to it.</p>
 */

public class TypedIFGrabber implements GrabberIF {


  /**
   * INTERNAL: Grabs the topic which is the type of the given typedIF
   *
   * @param typed the given object; internally typecast to TypedIF
   * @return object which is the type; an object implementing TopicIF
   */  

  public Object grab(Object typed) {
    return ((TypedIF)typed).getType();
  }

}





