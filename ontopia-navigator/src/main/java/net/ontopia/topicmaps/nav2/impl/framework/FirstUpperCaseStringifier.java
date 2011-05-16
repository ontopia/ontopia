
// $Id: FirstUpperCaseStringifier.java,v 1.8 2008/06/12 14:37:18 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.impl.framework;

import net.ontopia.utils.StringifierIF; 
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * INTERNAL: ...
 */
public class FirstUpperCaseStringifier implements StringifierIF {

  public FirstUpperCaseStringifier() {
  }
  
  public String toString(Object obj) {
    if (obj instanceof TopicNameIF) {
      TopicNameIF basename = (TopicNameIF) obj;
      String name_value = basename.getValue();
      if (name_value != null && name_value.length() > 0) {
        // return name_value.substring(0, 1).toUpperCase();
        return String.valueOf(Character.toUpperCase(name_value.charAt(0)));
      } else {
        return "";    
      }
    } else
      throw new IllegalArgumentException("FirstUpperCaseStringifier: Expected" +
                                         " instance of TopicNameIF.");
  }
  
}
