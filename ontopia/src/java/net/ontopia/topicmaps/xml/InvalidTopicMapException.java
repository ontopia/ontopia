
// $Id: InvalidTopicMapException.java,v 1.5 2008/11/03 12:26:17 lars.garshol Exp $

package net.ontopia.topicmaps.xml;

import net.ontopia.topicmaps.core.ConstraintViolationException;

/**
 * PUBLIC: Thrown when an invalid topic map is processed. 
 */
public class InvalidTopicMapException extends ConstraintViolationException {

  public InvalidTopicMapException(Throwable e) {
    super(e);
  }

  public InvalidTopicMapException(String message) {
    super(message);
  }
  
}
