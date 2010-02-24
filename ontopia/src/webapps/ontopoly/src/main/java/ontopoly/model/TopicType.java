
// $Id: TopicType.java,v 1.7 2009/04/30 09:53:41 geir.gronmo Exp $

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
public class TopicType extends AbstractTypingTopic {

  public TopicType(TopicIF currTopic, TopicMap tm) {
    super(currTopic, tm);
  }

  /**
   * Tests whether this topic type is abstract.
   * 
   * @return true if this topic type is abstract. 
   */
  public boolean isAbstract() {
    return isTrueAssociation("is-abstract", "topic-type");
  }

  /**
   * Tests whether this topic type has a large instance set.
   */
  public boolean isLargeInstanceSet() {
    return isTrueAssociation("has-large-instance-set", "topic-type");
  }

  private boolean isTrueAssociation(String atype, String rtype) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, atype);
    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, rtype);

    TopicIF topicIF = getTopicIF();
    AssociationIF assoc = OntopolyModelUtils.findUnaryAssociation(tm, aType, topicIF, rType);
    return (assoc != null);
  }

  /**
   * Gets the direct subtypes of this type.
   * 
   * @return A Collection of TopicType objects.
   */
  public Collection<TopicType> getDirectSubTypes() {
    String query = "xtm:superclass-subclass($SUB : xtm:subclass, %topic% : xtm:superclass)?";

    Map<String,TopicIF> params = Collections.singletonMap("topic", getTopicIF());

    QueryMapper<TopicType> qm = getTopicMap().newQueryMapper(TopicType.class);
    return qm.queryForList(query, params);
  }

  /**
   * Gets the all subtypes (direct and indirect) of this type.
   * 
   * @return A Collection of TopicType objects.
   */
  public Collection<TopicType> getAllSubTypes() {
    String query = "subclasses-of($SUP, $SUB) :- { "
        + "xtm:superclass-subclass($SUP : xtm:superclass, $SUB : xtm:subclass) | "
        + "xtm:superclass-subclass($SUP : xtm:superclass, $MID : xtm:subclass), "
        + "subclasses-of($MID, $SUB) }. " + "subclasses-of(%topic%, $SUB)?";

    Map<String,TopicIF> params = Collections.singletonMap("topic", getTopicIF());

    QueryMapper<TopicType> qm = getTopicMap().newQueryMapper(TopicType.class);
    return qm.queryForList(query, params);
  }

  /**
   * Returns the supertype of this type, or null if there is none.
   */
  public TopicType getSuperType() {
    String query = "xtm:superclass-subclass(%topic% : xtm:subclass, $SUP : xtm:superclass)?";

    Map<String,TopicIF> params = Collections.singletonMap("topic", getTopicIF());

    QueryMapper<TopicType> qm = getTopicMap().newQueryMapper(TopicType.class);
    return qm.queryForObject(query, params);
  }

  public FieldAssignment addField(FieldDefinition fieldDefinition) {
    TopicMap tm = getTopicMap();
    final TopicIF HAS_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-field");
    final TopicIF HAS_CARDINALITY = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-cardinality");
    final TopicIF FIELD_DEFINITION = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    final TopicIF FIELD_OWNER = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-owner");
    final TopicIF CARDINALITY = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "cardinality");

    TopicIF fieldDefinitionTopic = fieldDefinition.getTopicIF();
    TopicIF topicTypeTopic = getTopicIF();

    fieldOrderMaintainance(this);
    // on:has-field($TT : on:field-owner, $FD : on:field-definition)
    OntopolyModelUtils.makeBinaryAssociation(HAS_FIELD, 
      topicTypeTopic, FIELD_OWNER,
      fieldDefinitionTopic, FIELD_DEFINITION);

    // on:has-cardinality($TT : on:field-owner, $FD : on:field-definition, $C : on:cardinality)
    OntopolyModelUtils.makeTernaryAssociation(HAS_CARDINALITY, 
        topicTypeTopic, FIELD_OWNER, 
        fieldDefinitionTopic, FIELD_DEFINITION, 
        Cardinality.getDefaultCardinality(fieldDefinition).getTopicIF(), CARDINALITY);

    // Add field-order occurrence for this topictype and all it's subtypes.
    addFieldOrder(this, fieldDefinition);

    return new FieldAssignment(this, this, fieldDefinition);
  }

  public void removeField(FieldDefinition fieldDefinition) {
    TopicMap tm = getTopicMap();
    final TopicIF HAS_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-field");
    final TopicIF HAS_CARDINALITY = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-cardinality");
    final TopicIF FIELD_DEFINITION = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    final TopicIF CARDINALITY = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "cardinality");
    final TopicIF FIELD_OWNER = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-owner");

    TopicIF fieldDefinitionTopic = fieldDefinition.getTopicIF();
    TopicIF topicTypeTopic = getTopicIF();

    // find and remove has-cardinality association
    AssociationIF associationIF = OntopolyModelUtils.findTernaryAssociation(tm, HAS_CARDINALITY, 
      topicTypeTopic, FIELD_OWNER, 
      fieldDefinitionTopic, FIELD_DEFINITION, 
      Cardinality.getDefaultCardinality(fieldDefinition).getTopicIF(), CARDINALITY);
    if (associationIF != null)
      associationIF.remove();

    // find and remove has-field association
    associationIF = OntopolyModelUtils.findBinaryAssociation(tm, HAS_FIELD,
        topicTypeTopic, FIELD_OWNER, 
        fieldDefinitionTopic, FIELD_DEFINITION);
    if (associationIF != null)
      associationIF.remove();

    // See if one of the supertypes have also defined this field. If some of
    // the supertypes has defined
    // this field, don't remove the field-order occurrence.
    boolean removeFieldOrder = true;
    Iterator it = getFieldAssignments().iterator();
    while (it.hasNext()) {
      FieldAssignment fa = (FieldAssignment) it.next();
      if (fa.getFieldDefinition().equals(fieldDefinition)) {
        removeFieldOrder = false;
        break;
      }
    }

    if (removeFieldOrder) {
      // Remove field-order occurrence from this topictype and all it's
      // subtypes which have defined it.
      removeFieldOrder(this, fieldDefinition);
    }
  }

  public NameType createNameType() {
    TopicMap tm = getTopicMap();
    TopicMapBuilderIF builder = tm.getTopicMapIF().getBuilder();

    // create name field
    TopicIF nameFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-field");
    TopicIF nameFieldTopic = builder.makeTopic(nameFieldType);

    // create name type
    TopicIF nameTypeTopic = builder.makeTopic(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-type"));
    NameType nameType = new NameType(nameTypeTopic, tm);
    
    // on:has-name-type($TT : on:name-type, $FD : on:name-field)
    final TopicIF HAS_NAME_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-name-type");
    final TopicIF NAME_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-type");
    final TopicIF NAME_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-field");
    OntopolyModelUtils.makeBinaryAssociation(HAS_NAME_TYPE, 
        nameTypeTopic, NAME_TYPE,
        nameFieldTopic, NAME_FIELD);

    // TODO: add default cardinality

    // add field
    NameField nameField = new NameField(nameFieldTopic, tm, nameType);
    addField(nameField);
    return nameType;
  }

  public OccurrenceType createOccurrenceType() {
    TopicMap tm = getTopicMap();
    TopicMapBuilderIF builder = tm.getTopicMapIF().getBuilder();

    // create occurrence field
    TopicIF occurrenceFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-field");
    TopicIF occurrenceFieldTopic = builder.makeTopic(occurrenceFieldType);

    // create occurrence type
    TopicIF occurrenceTypeTopic = builder.makeTopic(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-type"));
    OccurrenceType occurrenceType = new OccurrenceType(occurrenceTypeTopic, tm);
    
    // on:has-occurrence-type($TT : on:occurrence-type, $FD : on:occurrence-field)
    final TopicIF HAS_OCCURRENCE_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-occurrence-type");
    final TopicIF OCCURRENCE_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-type");
    final TopicIF OCCURRENCE_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-field");
    OntopolyModelUtils.makeBinaryAssociation(HAS_OCCURRENCE_TYPE, 
        occurrenceTypeTopic, OCCURRENCE_TYPE,
        occurrenceFieldTopic, OCCURRENCE_FIELD);

    // TODO: add default datatype and cardinality

    // add field
    OccurrenceField occurrenceField = new OccurrenceField(occurrenceFieldTopic, tm);
    addField(occurrenceField);
    return occurrenceType;
  }

  public AssociationType createAssociationType() {
    TopicMap tm = getTopicMap();
    TopicMapBuilderIF builder = tm.getTopicMapIF().getBuilder();

    // create role field
    TopicIF roleFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "role-field");
    TopicIF roleFieldTopic = builder.makeTopic(roleFieldType);

    // create association field
    TopicIF associationFieldType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-field");
    TopicIF associationFieldTopic = builder.makeTopic(associationFieldType);

    // create association type
    TopicIF associationTypeTopic = builder.makeTopic(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-type"));
    AssociationType associationType = new AssociationType(associationTypeTopic, tm);
    
    // on:has-association-type($TT : on:association-type, $FD : on:association-field)
    final TopicIF HAS_ASSOCIATION_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-association-type");
    final TopicIF ASSOCIATION_TYPE = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-type");
    final TopicIF ASSOCIATION_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-field");
    OntopolyModelUtils.makeBinaryAssociation(HAS_ASSOCIATION_TYPE, 
        associationType.getTopicIF(), ASSOCIATION_TYPE,
        associationFieldTopic, ASSOCIATION_FIELD);
    
    // on:has-association-field($AF : on:association-field, $FD : on:role-field)
    final TopicIF HAS_ASSOCIATION_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-association-field");
    final TopicIF ROLE_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "role-field");
    OntopolyModelUtils.makeBinaryAssociation(HAS_ASSOCIATION_FIELD, 
        roleFieldTopic, ROLE_FIELD,
        associationFieldTopic, ASSOCIATION_FIELD);

    // TODO: add default cardinality

    // add field
    RoleField roleField = new RoleField(roleFieldTopic, tm);
    addField(roleField);

    // create second role field
    TopicIF roleFieldTopic2 = builder.makeTopic(roleFieldType);
    
    // on:has-association-field($AF : on:association-field, $FD : on:role-field)
    OntopolyModelUtils.makeBinaryAssociation(HAS_ASSOCIATION_FIELD, 
        roleFieldTopic2, ROLE_FIELD,
        associationFieldTopic, ASSOCIATION_FIELD);

    return associationType;
  }

  // Assures that every fieldAssignment assigned to this topic type has a
  // fieldOrder
  private static void fieldOrderMaintainance(TopicType tt) {
    final TopicIF FIELD_ORDER = OntopolyModelUtils.getTopicIF(tt.getTopicMap(), PSI.ON, "field-order");
    TopicIF topicIF = tt.getTopicIF();

    List fieldAssignments = tt.getFieldAssignments();
    Iterator it = fieldAssignments.iterator();
    while (it.hasNext()) {
      FieldAssignment fa = (FieldAssignment) it.next();

      FieldDefinition fieldDefinition = fa.getFieldDefinition();
      Collection<TopicIF> scope = Collections.singleton(fieldDefinition.getTopicIF());

      OccurrenceIF occurrenceIF = OntopolyModelUtils.findOccurrence(
          FIELD_ORDER, topicIF, DataTypes.TYPE_STRING, scope);

      if (occurrenceIF == null) {
        String fieldOrderAsString;
        int fieldOrder = fa.getOrder(tt);
        if (fieldOrder != Integer.MAX_VALUE)
          fieldOrderAsString = StringUtils.pad(fieldOrder + 1, '0', 9);
        else
          fieldOrderAsString = tt.getNextUnusedFieldOrder();

        // create field-order occurrence
        OntopolyModelUtils.makeOccurrence(FIELD_ORDER, topicIF,
            fieldOrderAsString, DataTypes.TYPE_STRING, scope);
      }
    }
  }

  private static void addFieldOrder(TopicType tt, FieldDefinition fieldDefinition) {

    final TopicIF FIELD_ORDER = OntopolyModelUtils.getTopicIF(tt.getTopicMap(), PSI.ON, "field-order");

    TopicIF topicTypeTopic = tt.getTopicIF();
    TopicIF fieldDefinitionTopic = fieldDefinition.getTopicIF();
    Collection<TopicIF> scope = Collections.singleton(fieldDefinitionTopic);

    // see if field-order occurrence already exist for the same field
    OccurrenceIF occurrenceIF = OntopolyModelUtils.findOccurrence(FIELD_ORDER,
        topicTypeTopic, DataTypes.TYPE_STRING, scope);
    if (occurrenceIF != null)
      return;

    // create field-order occurrence
    OntopolyModelUtils.makeOccurrence(FIELD_ORDER, topicTypeTopic, 
        tt.getNextUnusedFieldOrder(), DataTypes.TYPE_STRING, scope);

    // Go through all of TopicType tt's subtypes depth-first.
    Iterator it = tt.getDirectSubTypes().iterator();
    while (it.hasNext()) {
      addFieldOrder((TopicType) it.next(), fieldDefinition);
    }
  }

  private static void removeFieldOrder(TopicType tt, FieldDefinition fieldDefinition) {
    // See if the same field is defined on this topic type.
    TopicMap tm = tt.getTopicMap();
    final TopicIF HAS_FIELD = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-field");
    final TopicIF FIELD_DEFINITION = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    final TopicIF FIELD_OWNER = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-owner");

    TopicIF topicTypeTopic = tt.getTopicIF();
    TopicIF fieldDefinitionTopic = fieldDefinition.getTopicIF();

    AssociationIF associationIF = OntopolyModelUtils.findBinaryAssociation(
      tt.getTopicMap(), HAS_FIELD, 
      topicTypeTopic, FIELD_OWNER,
      fieldDefinitionTopic, FIELD_DEFINITION);

    // The field is defined on this topic type too, hence the field-order
    // occurrence can't be removed.
    if (associationIF != null)
      return;

    // find field-order occurrence
    Collection<TopicIF> scope = Collections.singleton(fieldDefinitionTopic);
    OccurrenceIF occurrenceIF = OntopolyModelUtils.findOccurrence(
        OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-order"), 
        tt.getTopicIF(), DataTypes.TYPE_STRING, scope);

    // remove field-order occurrence if it exist
    if (occurrenceIF != null) {
      occurrenceIF.remove();

      // Go through all of TopicType tt's subtypes depth-first.
      Iterator it = tt.getDirectSubTypes().iterator();
      while (it.hasNext()) {
        removeFieldOrder((TopicType) it.next(), fieldDefinition);
      }
    }
  }

  public List<FieldsView> getFieldViews(boolean includeHiddenViews, boolean includeEmbeddedViews) {
    String query = 
      "subclasses-of($SUP, $SUB) :- { " +
      "  xtm:superclass-subclass($SUP : xtm:superclass, $SUB : xtm:subclass) | " +
      "  xtm:superclass-subclass($SUP : xtm:superclass, $MID : xtm:subclass), subclasses-of($MID, $SUB) " +
      "}. " +
      "select $FIELDSVIEW from " +
      "{ $TT = %tt% | subclasses-of($TT, %tt%) }, " +
      "on:has-field($TT : on:field-owner, $FD : on:field-definition), " +
      "{ on:field-in-view($FD : on:field-definition, $FV : on:fields-view)" +
      (includeHiddenViews ? "" : ", not(on:is-hidden-view($FV : on:fields-view))") +
      (includeEmbeddedViews ? "" : ", not(on:is-embedded-view($FV : on:fields-view))") +
      " }, coalesce($FIELDSVIEW, $FV, on:default-fields-view) order by $FIELDSVIEW?";
                                                        
    Map<String,TopicIF> params = Collections.singletonMap("tt", getTopicIF());
    
    QueryMapper<FieldsView> qm = getTopicMap().newQueryMapperNoWrap();
    return qm.queryForList(query,
        new RowMapperIF<FieldsView>() {
          public FieldsView mapRow(QueryResultIF result, int rowno) {
            TopicIF viewTopic = (TopicIF)result.getValue(0);
            if (viewTopic == null)
              return FieldsView.getDefaultFieldsView(getTopicMap());
            else
              return new FieldsView(viewTopic, getTopicMap());
          }
        }, params);
  }

  /**
   * Returns the FieldAssignments for this topic type. These are sorted by the
   * field order field on the field types. In addition, fields are inherited
   * from all ancestor types.
   * 
   * <p>
   * Note that if isSystemTopic(), the list of fields will always contain the
   * default name type with the "exactly one" cardinality at the very top
   */
  public List<FieldAssignment> getFieldAssignments() {
    return getFieldAssignments(null);
  }

  public List<FieldAssignment> getFieldAssignments(FieldsView view) {
    String viewClause = "";
    if (view != null) {
      if (view.isDefaultView())
        viewClause = "{ on:field-in-view($FD : on:field-definition, on:default-fields-view : on:fields-view) | not(on:field-in-view($FD : on:field-definition, $XV : on:fields-view), $XV /= on:default-fields-view) }, ";
      else
        viewClause = "on:field-in-view($FD : on:field-definition, %view% : on:fields-view), ";
    }

    String query = 
      "subclasses-of($SUP, $SUB) :- { " +
      "  xtm:superclass-subclass($SUP : xtm:superclass, $SUB : xtm:subclass) | " +
      "  xtm:superclass-subclass($SUP : xtm:superclass, $MID : xtm:subclass), subclasses-of($MID, $SUB) " +
      "}. " +
      "field-order($T, $FA, $FO) :- " +
      "  { occurrence($T, $O), type($O, on:field-order),  " +
      "    scope($O, $FA), value($O, $FO) || " +
      "    xtm:superclass-subclass($T : xtm:subclass, $TT : xtm:superclass), " +
      "    field-order($TT, $FA, $FO) }. " +
      "select $TT, $FD, $FT, $FO from " +
      "{ $TT = %tt% | subclasses-of($TT, %tt%) }, " +
      "on:has-field($TT : on:field-owner, $FD : on:field-definition), " +
      viewClause +
      "direct-instance-of($FD, $FT), xtm:superclass-subclass($FT : xtm:subclass, on:field-definition : xtm:superclass), " +
      "{ field-order(%tt%, $FD, $FO) }?";
    Map<String,TopicIF> params;
    if (view == null)
      params = Collections.singletonMap("tt", getTopicIF());
    else {
      params = new HashMap<String,TopicIF>(2);
      params.put("tt", getTopicIF());
      params.put("view", view.getTopicIF());
    }
    QueryMapper<FieldAssignment> qm = getTopicMap().newQueryMapperNoWrap();
    
    List<FieldAssignment> fieldAssignments = qm.queryForList(query,
        new RowMapperIF<FieldAssignment>() {
          public FieldAssignment mapRow(QueryResultIF result, int rowno) {
            TopicIF topicType = (TopicIF)result.getValue(0);
            TopicIF fieldDefinitionTopic = (TopicIF)result.getValue(1);
            TopicIF fieldDefinitionType = (TopicIF)result.getValue(2);
            
            // OPTIMIZATION: retrieving field order here so we can pass it to the constructor
            String foValue = (String)result.getValue(3);
            int fieldOrder = (foValue != null ? Integer.parseInt(foValue) : Integer.MAX_VALUE);

            TopicMap tm = getTopicMap();
            TopicType tt = new TopicType(topicType, tm);
            FieldDefinition fd = findFieldDefinitionImpl(tm, fieldDefinitionTopic, fieldDefinitionType);

            return new FieldAssignment(TopicType.this, tt, fd, fieldOrder);
          }
        }, params);
    Collections.sort(fieldAssignments, FieldAssignmentOrderComparator.INSTANCE);
    return fieldAssignments;
  }

  static FieldDefinition findFieldDefinitionImpl(TopicMap tm, TopicIF fieldDefinitionTopic, TopicIF fieldDefinitionType) {
    Collection identities = fieldDefinitionType.getSubjectIdentifiers();
    if (identities.contains(PSI.ON_OCCURRENCE_FIELD))
      return new OccurrenceField(fieldDefinitionTopic, tm);
    else if (identities.contains(PSI.ON_ROLE_FIELD))
      return new RoleField(fieldDefinitionTopic, tm);
    else if (identities.contains(PSI.ON_NAME_FIELD))
      return new NameField(fieldDefinitionTopic, tm);
    else if (identities.contains(PSI.ON_IDENTITY_FIELD))
      return new IdentityField(fieldDefinitionTopic, tm);
    else
      throw new OntopolyModelRuntimeException(
          "This topic's subjectIndicator address didn't match any FieldDefinition implementations: "
              + identities);
  }

  private String getNextUnusedFieldOrder() {
    int fieldOrder = 0;

    // find field-order occurrence
    Collection fieldOrderOccurrences = OntopolyModelUtils.findOccurrences(
        OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "field-order"),
        getTopicIF(), DataTypes.TYPE_STRING);

    Iterator it = fieldOrderOccurrences.iterator();
    while (it.hasNext()) {
      OccurrenceIF occurrenceIF = (OccurrenceIF) it.next();
      int temp = Integer.parseInt(occurrenceIF.getValue());
      if (temp > fieldOrder)
        fieldOrder = temp;
    }

    return StringUtils.pad(fieldOrder + 1, '0', 9);
  }

  /**
   * Returns the set of all instances of this topic type.
   * 
   * @return A collection of Topic objects.
   */
  public Collection<Topic> getInstances() {
    String query = "instance-of($instance, %topic%)?";

    Map<String,TopicIF> params = Collections.singletonMap("topic", getTopicIF());

    QueryMapper<Topic> qm = getTopicMap().newQueryMapper(Topic.class);
    return qm.queryForList(query, params);
  }

  /**
   * Create a new topic instance of this topic type.
   */
  public Topic createInstance(String name) {
    TopicMap tm = getTopicMap();
    
    // delegate to specific create method if known type
    Collection subinds = getTopicIF().getSubjectIdentifiers();
    if (subinds.contains(PSI.ON_TOPIC_TYPE))
      return tm.createTopicType(name);
    else if (subinds.contains(PSI.ON_ASSOCIATION_TYPE))
      return tm.createAssociationType(name);
    else if (subinds.contains(PSI.ON_ROLE_TYPE))
      return tm.createRoleType(name);
    else if (subinds.contains(PSI.ON_NAME_TYPE))
      return tm.createNameType(name);
    else if (subinds.contains(PSI.ON_OCCURRENCE_TYPE))
      return tm.createOccurrenceType(name);
      
    // use default create method
    TopicIF topic = tm.createNamedTopic(name, getTopicIF());
    return new Topic(topic, tm);
  }

  @Override
  public LocatorIF getLocatorIF() {
    return PSI.ON_TOPIC_TYPE;
  }

  /**
   * Returns the topics that matches the given search term. Only topics of
   * allowed player types are returned.
   * 
   * @return a collection of Topic objects
   */
  public List<Topic> searchAll(String searchTerm) {
    String query = "select $topic, $score from "
        + "value-like($tn, %searchTerm%, $score), topic-name($topic, $tn), instance-of($topic, %topicType%) "
        + "order by $score desc, $topic?";

    Map<String,Object> params = new HashMap<String,Object>();
    params.put("searchTerm", searchTerm);
    params.put("topicType", getTopicIF());
    
    QueryMapper<Topic> qm = getTopicMap().newQueryMapper(Topic.class);
    Collection<Topic> rows = qm.queryForList(query, params);
    
    Iterator<Topic> it = rows.iterator();
    List<Topic> results = new ArrayList<Topic>(rows.size());
    try {
    Set<Topic> duplicateChecks = new HashSet<Topic>(rows.size());
    while (it.hasNext()) {
      Topic topic = it.next();
      if (duplicateChecks.contains(topic))
        continue; // avoid duplicates
      results.add(topic);
      duplicateChecks.add(topic);
    }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return results;
  }

  public Collection<? extends FieldDefinition> getDeclaredByFields() {
    return Collections.emptyList();
  }

}
