
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

  public boolean isReadOnly(FieldsView view) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "use-view-mode");
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player1 = getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "fields-view");
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode");
    Collection<TopicIF> players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
    return players.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode-readonly"));
  }

  public boolean isHidden(FieldsView view) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "use-view-mode");
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player1 = getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "fields-view");
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode");
    Collection<TopicIF> players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
    return players.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode-hidden"));
  }

  public boolean isNotTraversable(FieldsView view) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "use-view-mode");
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player1 = getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "fields-view");
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode");
    Collection<TopicIF> players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
    return players.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode-not-traversable"));
  }

  public boolean isEmbedded(FieldsView view) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "use-view-mode");
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player1 = getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "fields-view");
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode");
    Collection<TopicIF> players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
    return players.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode-embedded"));
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

}
