
package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.utils.GrabberIF;

/**
 * INTERNAL: Grabber that grabs the type property of the TypedIF object
 * given to it.</p>
 */

public class TypedIFGrabber implements GrabberIF<TypedIF, TopicIF> {


  /**
   * INTERNAL: Grabs the topic which is the type of the given typedIF
   *
   * @param typed the given object; internally typecast to TypedIF
   * @return object which is the type; an object implementing TopicIF
   */  

  public TopicIF grab(TypedIF typed) {
    return typed.getType();
  }

}





