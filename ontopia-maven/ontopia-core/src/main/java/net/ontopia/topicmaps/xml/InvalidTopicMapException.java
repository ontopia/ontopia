
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
