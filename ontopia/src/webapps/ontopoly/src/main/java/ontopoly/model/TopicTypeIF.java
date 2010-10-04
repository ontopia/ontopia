
package ontopoly.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ontopoly.utils.FieldAssignmentOrderComparator;
import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: Represents a topic type.
 */
public interface TopicTypeIF extends TypingTopicIF {

  /**
   * Tests whether this topic type is abstract.
   * 
   * @return true if this topic type is abstract. 
   */
  public boolean isAbstract();

  /**
   * Tests whether this topic type has a large instance set.
   */
  public boolean isLargeInstanceSet();

  /**
   * Tests whether this topic type has a hierarchy-forming association
   * type attached to it.
   */
  public boolean hasHierarchy();

  /**
   * Gets the direct subtypes of this type.
   * 
   * @return A Collection of TopicTypeIF objects.
   */
  public Collection<TopicTypeIF> getDirectSubTypes();

  /**
   * Gets the all subtypes (direct and indirect) of this type.
   * 
   * @return A Collection of TopicType objects.
   */
  public Collection<TopicTypeIF> getAllSubTypes();

  /**
   * Returns the supertype of this type, or null if there is none.
   */
  public TopicTypeIF getSuperType();

  public FieldAssignmentIF addField(FieldDefinitionIF fieldDefinition);

  public void removeField(FieldDefinitionIF fieldDefinition);

  public NameTypeIF createNameType();

  public OccurrenceTypeIF createOccurrenceType();

  public AssociationTypeIF createAssociationType();

  public List<FieldsViewIF> getFieldViews(boolean includeHiddenViews,
                                          boolean includeEmbeddedViews);

  /**
   * Returns the FieldAssignments for this topic type. These are sorted by the
   * field order field on the field types. In addition, fields are inherited
   * from all ancestor types.
   * 
   * <p>
   * Note that if isSystemTopic(), the list of fields will always contain the
   * default name type with the "exactly one" cardinality at the very top
   */
  public List<FieldAssignmentIF> getFieldAssignments();

  public List<FieldAssignmentIF> getFieldAssignments(FieldsViewIF view);

  /**
   * Returns the set of all instances of this topic type.
   * 
   * @return A collection of Topic objects.
   */
  public List<OntopolyTopicIF> getInstances();

  /**
   * Create a new topic instance of this topic type.
   */
  public OntopolyTopicIF createInstance(String name);

  /**
   * Returns the topics that matches the given search term. Only topics of
   * allowed player types are returned.
   * 
   * @return a collection of Topic objects
   */
  public List<OntopolyTopicIF> searchAll(String searchTerm);

  public Collection<? extends FieldDefinitionIF> getDeclaredByFields();

  public FieldAssignmentIF getFieldAssignment(TopicTypeIF declaredTopicType,
                                              FieldDefinitionIF fieldDefinition);
}
