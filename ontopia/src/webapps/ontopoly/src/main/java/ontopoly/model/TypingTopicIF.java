
package ontopoly.model;

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.utils.OntopolyModelUtils;

/**
 * INTERNAL: Super-interface for all typing topics, like association
 * types, topic types, role types, etc.
 */
public interface TypingTopicIF extends OntopolyTopicIF {

  /**
   * Gets the LocatorIF for this typing topic. The locator is the PSI
   * used by the ontology topic map model.
   * 
   * @return the LocatorIF for this typing topic.
   */
  public LocatorIF getLocatorIF();

  /**
   * Returns true if this typing topic is read-only. If the topic type
   * is read-only it cannot be edited or deleted.
   * 
   * @return true if read only is turned on.
   */
  public boolean isReadOnly();

  /**
   * Returns true if this typing topic is hidden. NOTE: this feature
   * is not yet supported.
   * 
   * @return true if hidden is turned on.
   */
  public boolean isHidden();

  /**
   * Returns the field definitions that are declared for this typing topic.
   * 
   * @return a list of FieldDefinitions.
   */
  public Collection<? extends FieldDefinitionIF> getDeclaredByFields();

}
