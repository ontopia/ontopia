
package net.ontopia.topicmaps.core;

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: This interface is implemented by objects representing
 * topics in the topic map model.</p>
 *
 * Note that topic identity is a difficult area, since it depends on
 * the availability of information from outside the topic map. In a
 * nutshell, topics represent addressable subjects, but addressable
 * subjects may be the same even if their locators are very
 * different.</p>
 *
 * In general, the determination of equality between addressable
 * subjects cannot be fully guaranteed, and applications using this
 * API will need to be aware of their limitations and capabilities in
 * this respect - and that other topic map applications they
 * communicate with may have lesser or greater powers to determine
 * subject identity.</p>
 */

public interface TopicIF extends TMObjectIF {

  public static final String EVENT_ADDED = "TopicIF.added";
  public static final String EVENT_MODIFIED = "TopicIF.modified";
  public static final String EVENT_REMOVED = "TopicIF.removed";
  public static final String EVENT_ADD_TYPE = "TopicIF.addType";
  public static final String EVENT_REMOVE_TYPE = "TopicIF.removeType";
  public static final String EVENT_ADD_SUBJECTLOCATOR = "TopicIF.addSubjectLocator";
  public static final String EVENT_REMOVE_SUBJECTLOCATOR = "TopicIF.removeSubjectLocator";
  public static final String EVENT_ADD_SUBJECTIDENTIFIER = "TopicIF.addSubjectIdentifier";
  public static final String EVENT_REMOVE_SUBJECTIDENTIFIER = "TopicIF.removeSubjectIdentifier";
  public static final String EVENT_ADD_TOPICNAME = "TopicIF.addTopicName";
  public static final String EVENT_REMOVE_TOPICNAME = "TopicIF.removeTopicName";
  public static final String EVENT_ADD_OCCURRENCE = "TopicIF.addOccurrence";
  public static final String EVENT_REMOVE_OCCURRENCE = "TopicIF.removeOccurrence";
  
  /**
   * PUBLIC: Gets the subject locators of this topic. These are
   * locators for resources that directly address the subject of this
   * topic. Such a resource is also called an addressable subject. The
   * subject locators are not guaranteed to have any specific order
   * within the returned collection.
   *
   * @return A collection of LocatorIF objects serving as subject locators.
   * @since 4.0
   */
  public Collection<LocatorIF> getSubjectLocators();

  /**
   * PUBLIC: Adds the given subject locator to the set of subject locators
   * for this topic.
   *
   * @exception ConstraintViolationException Thrown if the topic map
   *            already has a topic with this subject locator.
   *
   * @param subject_locator A locator for the subject locator to be added;
   *                        an object implementing LocatorIF.
   * @since 4.0
   */
  public void addSubjectLocator(LocatorIF subject_locator)
      throws ConstraintViolationException;

  /**
   * PUBLIC: Removes the given subject locator from the set of
   * subject locators for this topic. If the topic does not have the
   * given subject locator then this method has no effect.
   *
   * @param subject_locator A locator for the subject locator to be removed;
   *                        an object implementing LocatorIF.
   * @since 4.0
   */
  public void removeSubjectLocator(LocatorIF subject_locator);
  
  /**
   * PUBLIC: Gets the subject identitifers of this topic. These are
   * locators for resources that indirectly address or otherwise
   * indicate the subject of this topic. A subject identifier is
   * intended as a surrogate for a subject which cannot be addressed
   * directly. The subject identifiers are not guaranteed to have any
   * specific order within the returned collection.
   *
   * @return A collection of LocatorIF objects serving as subject identifiers.
   * @since 4.0
   */
  public Collection<LocatorIF> getSubjectIdentifiers();

  /**
   * PUBLIC: Adds the given subject identifier to the set of subject identifiers
   * for this topic.
   *
   * @exception ConstraintViolationException Thrown if the topic map
   *            already has a topic with this addressable subject.
   *
   * @param subject_identifier A locator for the subject identifier to be added;
   *                           an object implementing LocatorIF.
   * @since 4.0
   */
  public void addSubjectIdentifier(LocatorIF subject_identifier)
      throws ConstraintViolationException;

  /**
   * PUBLIC: Removes the given subject identifier from the set of
   * subject identifiers for this topic. If the topic does not have the
   * given subject identifier then this method has no effect.
   *
   * @param subject_identifier A locator for the subject identifier to be removed;
   *                           an object implementing LocatorIF.
   * @since 4.0
   */
  public void removeSubjectIdentifier(LocatorIF subject_identifier);

  /**
   * PUBLIC: Gets the types that this topic is an instance of. There
   * is no guarantee as to which order these will be returned in.
   *
   * @return A collection of TopicIF objects.
   */
  public Collection<TopicIF> getTypes();

  /**
   * PUBLIC: Adds a type to this topic.
   *
   * @param type The additional type; an object implementing TopicIF.
   */
  public void addType(TopicIF type);

  /**
   * PUBLIC: Removes a type from this topic. If the given topic is not
   * present amongst the types, then the method has no effect.
   *
   * @param type The type to be removed; an object implementing TopicIF.
   */
  public void removeType(TopicIF type);

  /**
   * PUBLIC: Gets the names of this topic.
   *
   * @return A collection of TopicNameIF objects.
   */
  public Collection<TopicNameIF> getTopicNames();
  
  /**
   * PUBLIC: Gets the occurrences of this topic. The occurrences are
   * not guaranteed to have any specific order.
   *
   * @return A collection of OccurrenceIF objects.
   */
  public Collection<OccurrenceIF> getOccurrences();
  
  /**
   * PUBLIC: Gets the occurrences of this topic with a specified type. 
   * The occurrences are not guaranteed to have any specific order.
   *
   * @return A collection of OccurrenceIF objects typed by specified 
   * type.
   */
  public Collection<OccurrenceIF> getOccurrencesByType(TopicIF type);

  /**
   * PUBLIC: Gets the association roles played by this topic. There
   * is no guarantee as to the order these are returned in.
   *
   * @return A collection of AssociationRoleIF objects.
   */
  public Collection<AssociationRoleIF> getRoles();

  /**
   * PUBLIC: Gets the association roles of the specifed type played
   * by this topic. There is no guarantee as to the order these are
   * returned in.
   *
   * @return A collection of AssociationRoleIF objects.
   * @since 2.2
   */
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype);

  /**
   * PUBLIC: Gets the association roles of the specifed type played
   * by this topic. The association roles must be part of an association
   * of the specified type. There is no guarantee as to the order
   * these are returned in.
   *
   * @return A collection of AssociationRoleIF objects.
   * @since 2.2
   */
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype,
                                                      TopicIF assoc_type);

  /**
   * EXPERIMENTAL: Merges the characteristics of one topic into
   * another topic.  The source topic stripped of characteristics, all
   * of which are moved to the target topic. Duplicate characteristics
   * are suppressed. The topics must be in the same topic map, and the
   * source topic is removed from the topic map.
   *
   * @param topic topicIF; the source topic. This is empty after the
   *            operation and is removed from the topic map.
   * @exception throws ConstraintViolationException if the two topics
   * have different values for the 'subject' property, since if they
   * do they cannot represent the same subject. If this exception is
   * thrown both topics remain untouched.
   *
   * @since 2.0.4
   */
  public void merge(TopicIF topic);

  /**
   * PUBLIC: Returns the topic map object that this topic reifies.
   *
   * @since 4.0
   */
  public ReifiableIF getReified();

}
