// $Id: CrossTopicMapException.java,v 1.2 2009/05/07 12:25:48 geir.gronmo Exp $

package net.ontopia.topicmaps.core;


/**
 * PUBLIC: Thrown when a topic map object is attempted used in more
 * than a single topic maps.</p>
 *
 * Extends ConstraintViolationException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 *
 * @since 4.0.0
 */

public class CrossTopicMapException extends ConstraintViolationException {

	public CrossTopicMapException(TMObjectIF tmobject, TMObjectIF target) {
		super("Cannot assign " + tmobject + " to object " + target + " as they belong to two different topic maps: " + tmobject.getTopicMap() + " and " + target.getTopicMap()); 
	}

	public CrossTopicMapException(TMObjectIF tmobject, TopicMapIF target) {
		super("Cannot assign " + tmobject + " to topic map " + target + " as the object belongs to another topic map: " + tmobject.getTopicMap()); 
	}

  public CrossTopicMapException(Throwable e) {
    super(e);
  }

  public CrossTopicMapException(String message) {
    super(message);
  }

  public CrossTopicMapException(String message, Throwable cause) {
    super(message, cause);
  }
  
	public static void check(TMObjectIF tmobject, TMObjectIF target) {
		TopicMapIF topicmap1 = tmobject.getTopicMap();
		if (topicmap1 == null)
			throw new ObjectRemovedException(tmobject);
		TopicMapIF topicmap2 = target.getTopicMap();
		if (topicmap2 == null)
			throw new ObjectRemovedException(target);
		if (!topicmap1.equals(topicmap2))
			throw new CrossTopicMapException(tmobject, target);
	}

	public static void check(TMObjectIF tmobject, TopicMapIF target) {
		TopicMapIF topicmap = tmobject.getTopicMap();
		if (topicmap == null)
			throw new ObjectRemovedException(tmobject);
		if (!topicmap.equals(target))
			throw new CrossTopicMapException(tmobject, target);
	}

}





