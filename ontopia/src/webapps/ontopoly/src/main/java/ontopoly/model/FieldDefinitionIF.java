
package ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.CollectionUtils;

/**
 * Represents a field type, which may be a name type, an occurrence type, an
 * identity field, or a combination of an association role and an association
 * type.
 */
public interface FieldDefinitionIF extends OntopolyTopicIF {
  public static final int FIELD_TYPE_ROLE       = 1;
  public static final int FIELD_TYPE_OCCURRENCE = 2;
  public static final int FIELD_TYPE_NAME       = 4;
  public static final int FIELD_TYPE_IDENTITY   = 8;
  public static final int FIELD_TYPE_QUERY      = 16;

  /**
   * @return an int that identifies this fieldType
   */
  public int getFieldType();

  /**
   * Returns the name of this field definition.
   */
  public String getFieldName();

  public boolean isReadOnly(FieldsViewIF view);

  public boolean isHidden(FieldsViewIF view);

  public boolean isNotTraversable(FieldsViewIF view);

  public boolean isEmbedded(FieldsViewIF view);

  public FieldsViewIF getValueView(FieldsViewIF view);

  /**
   * Returns the cardinality of the field on this topic type.
   */
  public CardinalityIF getCardinality();

  /**
   * Sets the cardinality of the field on this topic type.
   */
  public void setCardinality(CardinalityIF cardinality);

  /**
   * Returns the topic types to which this field is assigned.
   * 
   * @return a list of TopicType objects
   */
  public List<TopicTypeIF> getUsedBy();

  public Collection getValues(OntopolyTopicIF topic);

  public void addValue(FieldInstanceIF fieldInstance,
                       Object _value, LifeCycleListenerIF listener);

  public void removeValue(FieldInstanceIF fieldInstance, Object _value,
                          LifeCycleListenerIF listener);

}
