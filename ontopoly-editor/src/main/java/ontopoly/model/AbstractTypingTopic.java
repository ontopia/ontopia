
package ontopoly.model;

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.utils.OntopolyModelUtils;

/**
 * INTERNAL: Common superclass for all typing topics, like association types,
 * topic types, role types, etc. FIXME: Should there be another subtype for
 * isHidden, isReadOnly etc?
 */
public abstract class AbstractTypingTopic extends Topic {

  /**
   * Constructor. 
   * 
   * @param topicIF the TopicIF object associated with this topic.
   * @param tm the TopicMap this topic belongs to.
   */ 
  public AbstractTypingTopic(TopicIF topicIF, TopicMap tm) {
    super(topicIF, tm);
  }

  /**
   * Gets the LocatorIF for this typing topic. The locator is the PSI used by the ontology topic map model.p
   * 
   * @return the LocatorIF for this typing topic.
   */
  public abstract LocatorIF getLocatorIF();

  /**
   * Returns true if this typing topic is read-only. If the topic type is read-only it cannot be edited or deleted.
   * 
   * @return true if read only is turned on.
   */
  public boolean isReadOnly() {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON_IS_READONLY_TYPE);
    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON_ONTOLOGY_TYPE);
    TopicIF player = getTopicIF();
    return OntopolyModelUtils.isUnaryPlayer(tm, aType, player, rType);
  }

  /**
   * Returns true if this typing topic is hidden. NOTE: this feature is not yet supported.
   * 
   * @return true if hidden is turned on.
   */
  public boolean isHidden() {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON_IS_HIDDEN_TYPE);
    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON_ONTOLOGY_TYPE);
    TopicIF player = getTopicIF();
    return OntopolyModelUtils.isUnaryPlayer(tm, aType, player, rType);
  }

  /**
   * Returns the field definitions that are declared for this typing topic.
   * 
   * @return a list of FieldDefinitions.
   */
  public abstract Collection<? extends FieldDefinition> getDeclaredByFields();

}
