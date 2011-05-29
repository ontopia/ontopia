
package net.ontopia.topicmaps.core;

import java.util.Collection;
  
/**
 * PUBLIC: Implemented by topic map objects that can be
 * reified. Reification means making a topic for another object (that
 * is not itself a topic) so that you can say something about it. This
 * interface is implemented by AssociationIF, AssociationRoleIF,
 * TopicNameIF, VariantNameIF, OccurrenceIF and TopicMapIF.</p>
   *
   * @since 4.0
 */

public interface ReifiableIF extends TMObjectIF {
  
  /**
   * PUBLIC: Returns the topic that reifies this object.
   */
  public TopicIF getReifier();
  
  /**
   * PUBLIC: Sets the reifier of this object.
   */
  public void setReifier(TopicIF reifier);

}
