
// $Id: TypedIF.java,v 1.14 2008/01/09 11:51:41 geir.gronmo Exp $

package net.ontopia.topicmaps.core;
  
/**
 * PUBLIC: Implemented by topic map objects that are instances of a
 * single type, such as occurrences, associations, association roles,
 * and topic names.</p>
 *
 * Types are always represented by topics.</p>
 */

public interface TypedIF extends TMObjectIF {

  /**
   * PUBLIC: Gets the type that this object is an instance of.
   *
   * @return The type of this object; an object implementing TopicIF.
   */
  public TopicIF getType();

  /**
   * PUBLIC: Sets the type that this object is an instance of.
   *
   * @param type The type for this object; an object implementing TopicIF.
   */
  public void setType(TopicIF type);
  
}
