
package ontopoly.model;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontopoly.sysmodel.OntopolyRepository;
import ontopoly.sysmodel.TopicMapReference;
import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.utils.StringifierIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.xml.XTMTopicMapReference;

/**
 * INTERNAL: Represents an Ontopoly topic map.
 */
public interface OntopolyTopicMapIF {
  public static final int TYPE_TOPIC = 1;
  public static final int TYPE_ASSOCIATION_TYPE = 2;
  public static final int TYPE_ROLE_TYPE = 4;
  public static final int TYPE_NAME_TYPE = 8;
  public static final int TYPE_OCCURRENCE_TYPE = 16;
  public static final int TYPE_TOPIC_TYPE = 32;

  //public DeclarationContextIF getDeclarationContext();
  //public QueryProcessorIF getQueryProcessor();
  
  public boolean containsOntology();

  public boolean isDeleteable();

  public TopicMapIF getTopicMapIF();

  //public <T> QueryMapper<T> newQueryMapperNoWrap();

  //public <T> QueryMapper<T> newQueryMapper(final Class<T> type);

  public OntopolyRepository getOntopolyRepository();

  //public TopicIF getTopicIFById(String id);

  public OntopolyTopicIF getTopicById(String id);

  public OntopolyTopicIF getReifier();

  /**
   * Returns the name of the topic map, or null if it has none.
   */
  public String getName();

  /**
   * Returns the version of the Ontopoly meta-ontology used in this topic map.
   * FIXME: This method is sort of dubious here.
   */
  public float getOntologyVersion();

  /**
   * Returns the Id of the topic map reference in the topic map registry.
   */
  public String getId();

  /**
   * Returns a list of the topic types that is not a system topic type.
   */
  public List<TopicTypeIF> getTopicTypes();

  /**
   * Returns a list of the topic types that is not a system topic type.
   */
  public List<TopicTypeIF> getTopicTypesWithLargeInstanceSets();

  public List<OccurrenceTypeIF> getOccurrenceTypes();

  public List<OccurrenceFieldIF> getOccurrenceFields();

  public List<AssociationTypeIF> getAssociationTypes();

  public List<RoleTypeIF> getRoleTypes(boolean includeSystemTopics);

  public List<RoleFieldIF> getRoleFields();

  public List<NameTypeIF> getNameTypes();

  public List<NameFieldIF> getNameFields();

  public List<IdentityTypeIF> getIdentityTypes();

  public List<IdentityFieldIF> getIdentityFields();

  public boolean isSaveable();

  public void save();

  public TopicTypeIF createTopicType(String name);
  
  public IdentityFieldIF getIdentityField(IdentityTypeIF identityType);

  public NameTypeIF createNameType(String name);

  public NameFieldIF getNameField(NameTypeIF nameType);

  public OccurrenceTypeIF createOccurrenceType(String name);

  public OccurrenceFieldIF getOccurrenceField(OccurrenceTypeIF occurrenceType);

  public AssociationTypeIF createAssociationType(String name);

  public AssociationFieldIF getAssociationField(AssociationTypeIF atype);

  public RoleTypeIF createRoleType(String name);

  public RoleFieldIF getRoleField(AssociationTypeIF atype, RoleTypeIF rtype);

  /**
   * Returns the topics that matches the given search term. Only topics of
   * allowed player types are returned.
   * 
   * @return a collection of Topic objects
   */
  public List<OntopolyTopicIF> searchAll(String searchTerm);

  // ===== NEW METHODS

  public FieldDefinitionIF findFieldDefinition(String fieldId, int fieldType);

  // FIXME: strictly speaking a duplicate
  public OntopolyTopicIF findTopic(String topicId);
  
  public TopicTypeIF findTopicType(String topicId);

  public NameTypeIF findNameType(String topicId);

  public OccurrenceTypeIF findOccurrenceType(String topicId);
  
  public AssociationTypeIF findAssociationType(String topicId);
  
  public RoleTypeIF findRoleType(String topicId);

  public FieldsViewIF findFieldsView(String topicId);

  public RoleFieldIF findRoleField(String topicId);

  public OntopolyTopicIF findTypingTopic(String topicId, int kind);

  public FieldsViewIF getDefaultFieldsView();

  public TopicTypeIF getDefaultTopicType();

  public List<CardinalityIF> getCardinalityTypes();
}
