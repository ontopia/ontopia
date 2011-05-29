
package net.ontopia.topicmaps.core.index;

import java.util.Collection;
import net.ontopia.topicmaps.core.*;

/**
 * PUBLIC: Implemented by objects holding information about topics
 * used as themes in scopes, and about topic map objects that have
 * scope. The intention is to provide quick lookup of such
 * information.</p>
 */

public interface ScopeIndexIF extends IndexIF {
    
  /**
   * PUBLIC: Gets all topic names that have the given topic in their direct
   * scope.
   *
   * @param theme The given topic.
   *
   * @return A collection of TopicNameIF objects; the topic names that have the 
   *           given topic in their direct scope. 
   */
  public Collection<TopicNameIF> getTopicNames(TopicIF theme);
  
  /**
   * PUBLIC: Gets all variant names that have the given topic in their direct
   * scope. Note that 'variant' does not have a 'scope' child element,
   * but only a 'parameters' child element, which is considered by the
   * engine to be the same as a scope in practice.
   *
   * @param theme The given topic.
   *
   * @return A collection of VariantNameIF objects; the  variant names that have the 
   *           given topic in their parameters.
   */
  public Collection<VariantNameIF> getVariants(TopicIF theme);
  
  /**
   * PUBLIC: Gets all occurrences that have the given topic in their direct scope.
   *
   * @param theme The given topic.
   *
   * @return A collection of OccurrenceIF objects; the occurrences that have the 
   *           given topic in their direct scope.
   */
  public Collection<OccurrenceIF> getOccurrences(TopicIF theme);
  
  /**
   * PUBLIC: Gets all associations that have the given topic in their direct scope.
   *
   * @param theme The given topic.
   *
   * @return A collection of AssociationIF objects; the associations that have the 
   *           given topic in their direct scope.
   */
  public Collection<AssociationIF> getAssociations(TopicIF theme);
    
  /**
   * PUBLIC: Gets the set of all topics that are used in the direct scope
   * of at least one topic name.
   *
   * @return A collection of TopicIF objects.
   */
  public Collection<TopicIF> getTopicNameThemes();

  /**
   * PUBLIC: Gets the set of all topics that are used in the direct scope
   * of at least one variant name.
   *
   * @return A collection of TopicIF objects.
   */
  public Collection<TopicIF> getVariantThemes();

  /**
   * PUBLIC: Gets the set of all topics that are used in the direct scope
   * of at least one occurrence.
   *
   * @return A collection of TopicIF objects.
   */
  public Collection<TopicIF> getOccurrenceThemes();
  
  /**
   * PUBLIC: Gets the set of all topics that are used in the direct scope
   * of at least one association.
   *
   * @return A collection of TopicIF objects.
   */
  public Collection<TopicIF> getAssociationThemes();

  /**
   * PUBLIC: Returns true if the topic has been used in the direct scope of at
   * least one topic name.
   */
  public boolean usedAsTopicNameTheme(TopicIF topic);

  /**
   * PUBLIC: Returns true if the topic has been used in the direct scope of at
   * least one variant name.
   */
  public boolean usedAsVariantTheme(TopicIF topic);

  /**
   * PUBLIC: Returns true if the topic has been used in the direct scope of at
   * least one occurrence.
   */
  public boolean usedAsOccurrenceTheme(TopicIF topic);

  /**
   * PUBLIC: Returns true if the topic has been used in the direct scope of at
   * least one association.
   */
  public boolean usedAsAssociationTheme(TopicIF topic);
  
  /**
   * PUBLIC: Returns true if the topic has been used in a direct scope
   * somewhere in the topic map.
   */
  public boolean usedAsTheme(TopicIF topic);
  
}





