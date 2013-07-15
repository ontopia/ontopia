
package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.utils.GrabberIF;
import net.ontopia.utils.StringifierIF;

/**
 * INTERNAL: Stringifier that generates a string representation of the
 * type property of the object given to it.</p>
 *
 * This stringifier uses a TypedIFGrabber internally to grab the
 * object's type property. Instances of this class can be configured
 * with a stringifier used to stringify the resulting topic.</p>
 */

public class TypedIFStringifier implements StringifierIF<TypedIF> {

  protected GrabberIF<TypedIF, TopicIF> grabber = new TypedIFGrabber();
  protected StringifierIF<? super TopicIF> topic_stringifier;
  
  public TypedIFStringifier(StringifierIF<? super TopicIF> topic_stringifier) {
    this.topic_stringifier = topic_stringifier;
  }

  /**
   * Returns a string representation of the type property of the typed
   * object.
   *
   * @param typed An object implementing TypedIF.
   * @return The string that results from applying the configured
   * stringifier to the type extracted from the typed object.
   */
  public String toString(TypedIF typed) {
    TopicIF type = grabber.grab(typed);
    return topic_stringifier.toString(type);
  }
  
}





