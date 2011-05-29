
package net.ontopia.topicmaps.core;


/**
 * PUBLIC: Thrown when a deleted topic map object is attempted
 * reassigned to a property in a topic map.</p>
 *
 * Extends ConstraintViolationException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 *
 * @since 4.0.0
 */

public class ObjectRemovedException extends ConstraintViolationException {

	public ObjectRemovedException(TMObjectIF tmobject) {
		super("Cannot reassign object " + tmobject + " as it has already been removed from the topic map."); 
	}

}





