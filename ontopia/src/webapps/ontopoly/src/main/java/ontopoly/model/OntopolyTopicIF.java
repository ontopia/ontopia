
package ontopoly.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontopoly.utils.TopicComparator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.CopyUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Common super-interface for all topics, like instances,
 * association types, topic types, role types, etc.
 */
public interface OntopolyTopicIF {

  // ------------------------------------------------------------------------
  // ABSTRACT TOPIC
  
  /**
   * Gets the id of this topic. 
   * 
   * @return the id of this topic.
   */
  public String getId();

  /**
   * Gets the topicIF object of this topic. 
   * 
   * @return the topicIF object of this topic. 
   */
  public TopicIF getTopicIF();

  /**
   * Gets the topicMap this topic belongs to. 
   * 
   * @return the topicMap this topic belongs to. 
   */  
  public OntopolyTopicMapIF getTopicMap();

  /**
   * Gets the unscoped name of the topic.
   * 
   * @Return the unscoped name of the topic or null if no name has been set.
   */
  public String getName();

  /**
   * Tests whether this topic is a topic map.
   * 
   * @return true if this is a topic map.
   */
  public boolean isTopicMap();

  /**
   * Tests whether this topic is a topic type.
   * 
   * @return true if this is a topic type.
   */
  public boolean isTopicType();

  /**
   * Tests whether this topic is a name type.
   * 
   * @return true if this is a name type.
   */
  public boolean isNameType();

  /**
   * Tests whether this topic is an occurrence type.
   * 
   * @return true if this is an occurrence type.
   */
  public boolean isOccurrenceType();

  /**
   * Tests whether this topic is an association type.
   * 
   * @return true if this is an association type.
   */
  public boolean isAssociationType();

  /**
   * Tests whether this topic is a role type.
   * 
   * @return true if this is a role type.
   */
  public boolean isRoleType();

  /**
   * Tests whether this topic is a field definition or an association field.
   * 
   * @return true if this is a role type.
   */
  public boolean isFieldDefinition();

  /**
   * Tests whether this topic is a system topic type.
   * 
   * @return true if this is a system topic type.
   */
  public boolean isSystemTopic();

  public boolean isPrivateSystemTopic();

  public boolean isPublicSystemTopic();

  /**
   * Tests whether this topic is an instance of an ontology type,
   * i.e. an instance of topic type, name type, occurrence type,
   * association type or role type.
   * 
   * @return true if this is an instance of an ontology type.
   */
  public boolean isOntologyTopic();

  /**
   * Tests whether this topic is an ontology type, i.e. topic type,
   * name type, occurrence type, association type or role type.
   * 
   * @return true if this is an ontology type.
   */
  public boolean isOntologyType();

  // ------------------------------------------------------------------------
  // INSTANCE

  /**
   * Given the topic type, find the subtype that is the specific type
   * of this topic. The topic type given is usually a super type of
   * the specific type. If null is returned then the topic type is not
   * a super type of this topic.
   * @param topicType the super type of this topic
   * @return the most specific type of this type, or null if there is none.
   */
  public TopicTypeIF getMostSpecificTopicType(TopicTypeIF topicType);

  /**
   * Returns the topic types of which this topic is a direct instance.
   */
  public List<TopicTypeIF> getTopicTypes();

  /**
   * Adds the topic type to the list of topic types that topic is a
   * direct instance of.
   */
  public void addTopicType(TopicTypeIF type);

  /**
   * Removes the topic type from the list of topic types that topic is
   * a direct instance of.
   */
  public void removeTopicType(TopicTypeIF type);

  public List<FieldInstanceIF> getFieldInstances(TopicTypeIF topicType);

  public List<FieldInstanceIF> getFieldInstances(TopicTypeIF topicType,
                                                 FieldsViewIF fieldsView);

  /**
   * Removes the topic from the topic map.
   * @param listener listener that gets call back from the deleting
   * this topic, and any dependencies.
   */
  public void remove(LifeCycleListenerIF listener);

  public Collection<OntopolyTopicIF> getDependentObjects();

  public OntopolyTopicIF copyCharacteristics();
  
  public boolean isInstanceOf(OntopolyTopicIF type);
  
  public boolean isInstanceOf(LocatorIF psi);

  public FieldInstanceIF getFieldInstance(FieldAssignmentIF fieldAssignment);
}
