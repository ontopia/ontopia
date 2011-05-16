
// $Id: NoEscapeStringifier.java,v 1.4 2007/07/13 12:35:06 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.utils;

import net.ontopia.utils.StringifierIF;
import net.ontopia.topicmaps.core.OccurrenceIF;

/**
 * INTERNAL: Stringifier that stringifies occurrences to their internal
 * string value and all other objects using obj.toString(). Contents
 * are output without being HTML-escaped.
 *
 * @since 2.0
 */
public class NoEscapeStringifier implements StringifierIF {
  
  public String toString(Object object) {
    if (object instanceof OccurrenceIF) 
      return ((OccurrenceIF) object).getValue();
    else 
      return (object == null ? null : object.toString());
  }
  
}
