
package ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontopoly.utils.OntopolyModelUtils;
import ontopoly.utils.Ordering;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;

/**
 * Represents a field as assigned to a topic type. The field itself is a
 * FieldDefinition, and the topic type a TopicType. This object primarily
 * holds the cardinality and order in the list of fields.
 */
public interface FieldAssignmentIF {

  /**
   * Returns the topic type.
   */
  public TopicTypeIF getTopicType();

  /**
   * Returns the topic type.
   */
  public TopicTypeIF getDeclaredTopicType();

  /**
   * Returns the field type.
   */
  public FieldDefinitionIF getFieldDefinition();

  public CardinalityIF getCardinality();

  /**
   * Returns the ordering key of the field on this topic type.
   */
  public int getOrder();

  /**
   * Returns the ordering key of the field on the topic type sent in as an
   * argument.
   */
  public int getOrder(TopicTypeIF t);

  /**
   * Change field order so that this field is ordered directly after
   * the other field.
   * @param other the field to order after.
   */
  public void moveAfter(FieldAssignmentIF other);

}
