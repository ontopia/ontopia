
package ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.utils.CollectionUtils;

/**
 * Represents a name type.
 */
public interface NameFieldIF extends FieldDefinitionIF {

  /**
   * Gets the name type.
   * 
   * @return the name type.
   */
  public NameTypeIF getNameType();

  /**
   * Returns the assigned height of the name text field.
   */
  public int getHeight();

  /**
   * Returns the assigned width of the name text field.
   */
  public int getWidth();

}
