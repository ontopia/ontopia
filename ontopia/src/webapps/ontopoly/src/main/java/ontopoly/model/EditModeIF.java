
package ontopoly.model;

import net.ontopia.topicmaps.core.TopicIF;

/**
 * Represents the edit mode of a field.
 */
public interface EditModeIF extends OntopolyTopicIF {

  public boolean isExistingValuesOnly();

  public boolean isNewValuesOnly();

  public boolean isOwnedValues();

  public boolean isNoEdit();

}
