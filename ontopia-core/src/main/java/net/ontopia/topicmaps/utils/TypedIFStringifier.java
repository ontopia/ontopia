// $Id: TypedIFStringifier.java,v 1.8 2004/11/29 19:01:50 grove Exp $

package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Stringifier that generates a string representation of the
 * type property of the object given to it.</p>
 *
 * This stringifier uses a TypedIFGrabber internally to grab the
 * object's type property. Instances of this class can be configured
 * with a stringifier used to stringify the resulting topic.</p>
 */

public class TypedIFStringifier implements StringifierIF {

  protected GrabberIF grabber = new TypedIFGrabber();
  protected StringifierIF topic_stringifier;
  
  public TypedIFStringifier(StringifierIF topic_stringifier) {
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
  public String toString(Object typed) {
    TopicIF type = (TopicIF)grabber.grab(typed);
    return topic_stringifier.toString(type);
  }
  
}





