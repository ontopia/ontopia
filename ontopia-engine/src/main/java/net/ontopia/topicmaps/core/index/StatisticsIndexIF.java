
package net.ontopia.topicmaps.core.index;

/**
 * PUBLIC: Interface implemented by objects providing quick lookup
 * facilities to find topics used as types, and the instances of those
 * types, within a topic map.</p>
 */
public interface StatisticsIndexIF {

  /**
   * Returns the number of topics in the topicmap.
   * @return the number of topics in the topicmap.
   */
  int getTopicCount();
  
  /**
   * Returns the number of topics in the topicmap that have a type.
   * @return the number of topics in the topicmap that have a type.
   */
  int getTypedTopicCount();
  
  /**
   * Returns the number of topics without a type.
   * @return the number of topics without a type.
   */
  int getUntypedTopicCount();
  
  /**
   * Returns the number of topics that are used as topic type.
   * @return the number of topics that are used as topic type.
   */
  int getTopicTypeCount();
  
  /**
   * Return the number of associations in the topicmap.
   * @return the number of associations in the topicmap.
   */
  int getAssociationCount();
  
  /**
   * Returns the number of topics used as association type.
   * @return the number of topics used as association type.
   */
  int getAssociationTypeCount();
  
  /**
   * Returns the number of roles in the topicmap.
   * @return the number of roles in the topicmap.
   */
  int getRoleCount();
  
  /**
   * Returns the number of topics used as association role type.
   * @return the number of topics used as association role type.
   */
  int getRoleTypeCount();
  
  /**
   * Returns the number of occurrences in the topicmap.
   * @return the number of occurrences in the topicmap.
   */
  int getOccurrenceCount();
  
  /**
   * Returns the number of topics used as occurrence type.
   * @return the number of topics used as occurrence type.
   */
  int getOccurrenceTypeCount();
  
  /**
   * Returns the number of topic names in the topicmap.
   * @return the number of topic names in the topicmap.
   */
  int getTopicNameCount();
  
  /**
   * Returns the number of topics without a name.
   * @return the number of topics without a name.
   */
  int getNoNameTopicCount();
  
  /**
   * Returns the number of topics used as topic name type.
   * @return the number of topics used as topic name type.
   */
  int getTopicNameTypeCount();
  
  /**
   * Returns the number of variant names in the topicmap.
   * @return the number of variant names in the topicmap.
   */
  int getVariantCount();

  /**
   * Returns the number of subject identifiers in the topicmap.
   * @return the number of subject identifiers in the topicmap.
   */
  int getSubjectIdentifierCount();

  /**
   * Returns the number of subject locators in the topicmap.
   * @return the number of subject locators in the topicmap.
   */
  int getSubjectLocatorCount();
  
  /**
   * Returns the number of item identifiers in the topicmap.
   * @return the number of item identifiers in the topicmap.
   */
  int getItemIdentifierCount();
  
}
