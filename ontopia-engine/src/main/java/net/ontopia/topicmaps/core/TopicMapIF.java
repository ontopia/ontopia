
package net.ontopia.topicmaps.core;

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: Represents an entire topic map.</p>
 */
public interface TopicMapIF extends TMObjectIF, ReifiableIF  {

  /**
   * PUBLIC: Gets the store to which the topic map's transaction is
   * connected.
   *
   * @since 2.0
   */
  public TopicMapStoreIF getStore();

  /**
   * PUBLIC: Gets a topic map builder for use with this transaction.
   *
   * @return An object implementing TopicMapBuilderIF
   * @since 4.0
   */
  public TopicMapBuilderIF getBuilder();

  /**
   * PUBLIC: Gets an index by name. An index is usually named by the
   * IndexIF subinterface that it implements. All indexes are kept
   * up-to-date at all times.<p>
   *
   * @exception OntopiaUnsupportedException Thrown if the index is either
   *            unknown or not supported.
   
   * @param name A string; the index name, i.e.g the interface that it
   * implements.
   *
   * @return An instance implementing the index interface.
   *
   * @since 4.0
   */
  public Object getIndex(String name);
  
  /**
   * PUBLIC: Gets all topics in this topic map. No specific order is
   * guaranteed.
   *
   * @return A collection of TopicIF objects.
   */
  public Collection<TopicIF> getTopics();

  /**
   * PUBLIC: Gets all associations in this topic map. No specific order is
   * guaranteed.
   *
   * @return A collection of AssociationIF objects.
   */
  public Collection<AssociationIF> getAssociations();
  
  /**
   * PUBLIC: Gets the topic map object that has the given object
   * id, from this topic map.
   * If there is no object with that object id in this topic map,
   * then null is returned.
   *
   * @param object_id A string; the object id of the object to get.
   *
   * @return A topic map object; an object implementing TMObjectIF.
   */
  public TMObjectIF getObjectById(String object_id);

  /**
   * PUBLIC: Gets the topic map object that has the given item
   * identifier (given as a LocatorIF object), from this topic map.
   * If there is no object with the given locator in this
   * topic map, null is returned.
   *
   * @param locator The given locator; an object implementing LocatorIF.
   *
   * @return A topic map object; an object implementing TMObjectIF.
   */
  public TMObjectIF getObjectByItemIdentifier(LocatorIF locator);

  /**
   * PUBLIC: Gets the topic in this topic map that represents the
   * given addressable subject (locator given as a LocatorIF object).
   * If there is no topic that represents the given addressable
   * subject in this topic map, null is returned.
   *
   * @param locator The given locator; an object implementing LocatorIF.
   *
   * @return A topic; an object implementing TopicIF.
   */
  public TopicIF getTopicBySubjectLocator(LocatorIF locator);

  /**
   * PUBLIC: Gets the topic that has the specified subject identifier,
   * given as a locator.  If there is no topic that has that subject
   * identifier in this topic map, null is returned.
   *
   * @param locator The given locator; an object implementing LocatorIF.
   *
   * @return A topic; an object implementing TopicIF.
   */
  public TopicIF getTopicBySubjectIdentifier(LocatorIF locator);

  /**
   * PUBLIC: Clears the topic map by removing all topics and associations.
   *
   * @since 4.0
   */
  public void clear();
  
}
