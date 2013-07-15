
package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.utils.StringifierIF;

/**
 * INTERNAL: Stringifier that returns the object id of a topic map object.
 */

public class ObjectIdStringifier implements StringifierIF<TMObjectIF> {
  
  /**
   * INTERNAL: Stringifies an arbitrary topicmap object, using its objectId
   *
   * @param tmobject object, typecast internally to TMObjectIF; the
   * given topicmap object
   * @return string the topic map object id
   */
  public String toString(TMObjectIF tmobject) {
    if (tmobject == null) return "null";
    return tmobject.getObjectId();
  }
  
}





