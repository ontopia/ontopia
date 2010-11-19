
package ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.CollectionUtils;

/**
 * Represents a field type, which may be a name type, an occurrence type, an
 * identity field, or a combination of an association role and an association
 * type.
 */
public abstract class FieldDefinition extends Topic {
  public static final int FIELD_TYPE_ROLE = 1;
  public static final int FIELD_TYPE_OCCURRENCE = 2;
  public static final int FIELD_TYPE_NAME = 4;
  public static final int FIELD_TYPE_IDENTITY = 8;
  public static final int FIELD_TYPE_QUERY = 16;

  private Cardinality cachedCardinality;

  protected FieldDefinition(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }

  /**
   * @return an int that identifies this fieldType
   */
  public abstract int getFieldType();

  /**
   * Returns the name of this field definition.
   */
  public abstract String getFieldName();

  private Collection<TopicIF> getViewModes(FieldsView view) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "use-view-mode");
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player1 = getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "fields-view");
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode");
    return OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
  }
  
  public boolean isReadOnly(FieldsView view) {
    Collection<TopicIF> viewModes = getViewModes(view);
    return viewModes.contains(OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "view-mode-readonly"));
  }

  public boolean isHidden(FieldsView view) {
    Collection<TopicIF> viewModes = getViewModes(view);
    return viewModes.contains(OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "view-mode-hidden"));
  }

  public boolean isTraversable(FieldsView view) {
    Collection<TopicIF> viewModes = getViewModes(view);
    return !viewModes.contains(OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "view-mode-not-traversable"));
  }

  public boolean isEmbedded(FieldsView view) {
    Collection<TopicIF> viewModes = getViewModes(view);
    return viewModes.contains(OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "view-mode-embedded"));
  }

  public FieldsView getValueView(FieldsView view) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "use-value-view");
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player1 = getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "parent-view");
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "child-view");
    Collection<TopicIF> players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
    TopicIF viewIf = (TopicIF) CollectionUtils.getFirst(players);
    // ISSUE: should we use view given in parameter as default instead?
    if (viewIf == null) {
      if (view.isEmbeddedView())
        return FieldsView.getDefaultFieldsView(tm);
      else
        return view;
    } else {
      return new FieldsView(viewIf, tm);
    }
  }

  /**
   * Returns the cardinality of the field on this topic type.
   */
  public Cardinality getCardinality() {
    if (cachedCardinality != null) return cachedCardinality;

    String query = 
      "select $C from on:has-cardinality(%FD% : on:field-definition, $C : on:cardinality) limit 1?";

    Map<String,TopicIF> params = Collections.singletonMap("FD", getTopicIF());

    QueryMapper<TopicIF> qm = getTopicMap().newQueryMapperNoWrap();    
    
    TopicIF card = qm.queryForObject(query, params);
    Cardinality cardinality = (card == null ? Cardinality.getDefaultCardinality(this) : new Cardinality(card, getTopicMap()));
    cachedCardinality = cardinality;
    return cardinality;
  }

  /**
   * Sets the cardinality of the field on this topic type.
   */
  public void setCardinality(Cardinality cardinality) {
    // NOTE: used by FieldsEditor
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-cardinality");
    TopicIF type2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF type3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "cardinality");
    TopicIF player2 = getTopicIF();
    TopicIF player3 = cardinality.getTopicIF();

    Collection<AssociationIF> associationIFs = OntopolyModelUtils.findBinaryAssociations(
        tm, aType, player2, type2, type3);
    Iterator<AssociationIF> it = associationIFs.iterator();

    while (it.hasNext()) {
      ((AssociationIF) it.next()).remove();
    }
    OntopolyModelUtils.makeBinaryAssociation(aType, player2,
        type2, player3, type3);

    cachedCardinality = cardinality;
  }

  /**
   * Returns the topic types to which this field is assigned.
   * 
   * @return a list of TopicType objects
   */
  public List<TopicType> getUsedBy() {
    String query = "select $type from on:has-field($type : on:field-owner, %FD% : on:field-definition)?";
    Map<String,TopicIF> params = Collections.singletonMap("FD", getTopicIF());

    QueryMapper<TopicType> qm = getTopicMap().newQueryMapper(TopicType.class);
    return qm.queryForList(query, params);
  }

  /**
   * Returns the field's occurrence type. If the field is not an
   * occurrence field it returns null.
   */
  public OccurrenceType getOccurrenceType() {
    String query =
      "on:has-occurrence-type(%OF% : on:occurrence-field, $OT : on:occurrence-type)?";
    Map<String,TopicIF> params = Collections.singletonMap("OF", getTopicIF());
    QueryMapper<OccurrenceType> qm = getTopicMap().newQueryMapper(OccurrenceType.class);
    return qm.queryForObject(query, params);
  }

  public abstract Collection<? extends Object> getValues(Topic topic);

  public abstract void addValue(FieldInstance fieldInstance, Object _value, LifeCycleListener listener);

  public abstract void removeValue(FieldInstance fieldInstance, Object _value, LifeCycleListener listener);
  
  public boolean equals(Object obj) {
    if (!(obj instanceof FieldDefinition))
      return false;

    FieldDefinition other = (FieldDefinition)obj;
    return getTopicIF().equals(other.getTopicIF());
  }

  public int hashCode() {
    return getTopicIF().hashCode();
  }

  private static int getFieldType(TopicIF fieldTopic) {
    for (TopicIF topicType : fieldTopic.getTypes()) {
      Collection<LocatorIF> psis = topicType.getSubjectIdentifiers();
      if (psis.contains(PSI.ON_NAME_FIELD))
        return FieldDefinition.FIELD_TYPE_NAME;
      else if (psis.contains(PSI.ON_IDENTITY_FIELD))
        return FieldDefinition.FIELD_TYPE_IDENTITY;
      else if (psis.contains(PSI.ON_OCCURRENCE_FIELD))
        return FieldDefinition.FIELD_TYPE_OCCURRENCE;
      else if (psis.contains(PSI.ON_ROLE_FIELD))
        return FieldDefinition.FIELD_TYPE_ROLE;
      else if (psis.contains(PSI.ON_QUERY_FIELD))
        return FieldDefinition.FIELD_TYPE_QUERY;
    }
    throw new RuntimeException("Not a field definition: " + fieldTopic);
  }
  
  public static FieldDefinition getFieldDefinition(String fieldId, TopicMap tm) {
    TopicIF fieldTopic = tm.getTopicIFById(fieldId);
    int fieldType = getFieldType(fieldTopic);
    return getFieldDefinition(fieldId, fieldType, tm);
  }

  public static FieldDefinition getFieldDefinition(String fieldId, int fieldType, TopicMap tm) {    
    TopicIF fieldTopic = tm.getTopicIFById(fieldId);
    return getFieldDefinition(fieldTopic, fieldType, tm);
  }
  
  private static FieldDefinition getFieldDefinition(TopicIF fieldTopic, int fieldType, TopicMap tm) {    
    
    switch (fieldType) {
    case FieldDefinition.FIELD_TYPE_ROLE:
      return new RoleField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      return new OccurrenceField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_NAME:
      return new NameField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_IDENTITY:
      return new IdentityField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_QUERY:
      return new QueryField(fieldTopic, tm);
    default:
      throw new RuntimeException("Unknown field type: " + fieldType);
    }    
  }

}
