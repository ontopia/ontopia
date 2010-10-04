
package ontopoly.model;

import net.ontopia.topicmaps.core.TopicIF;

/**
 * Represents the edit mode of a field.
 */
public interface CreateActionIF extends OntopolyTopicIF {

  public boolean isNone();

  public boolean isPopup();

  public boolean isNavigate();

}
