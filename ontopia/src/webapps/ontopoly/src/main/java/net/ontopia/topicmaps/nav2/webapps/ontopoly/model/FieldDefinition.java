// $Id: FieldDefinition.java,v 1.9 2009/05/05 12:36:58 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.utils.OntopolyModelUtils;
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
   * @return a int that identify this fieldType
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
    Collection players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
    return players.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode-readonly"));
//    String query = "on:use-view-mode(%FD% : on:field-definition, %V% : on:fields-view, on:view-mode-readonly : on:view-mode)?";
//    Map params = new HashMap(2);
//    params.put("FD", getTopicIF());
//    params.put("V", view.getTopicIF());
//    return getTopicMap().getQueryWrapper().isTrue(query, params);
  }

  public boolean isHidden(FieldsView view) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "use-view-mode");
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player1 = getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "fields-view");
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode");
    Collection players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
    return players.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode-hidden"));
//    String query = "on:use-view-mode(%FD% : on:field-definition, %V% : on:fields-view, on:view-mode-hidden : on:view-mode)?";
//    Map params = new HashMap(2);
//    params.put("FD", getTopicIF());
//    params.put("V", view.getTopicIF());
//    return getTopicMap().getQueryWrapper().isTrue(query, params);
  }

  public boolean isNotTraversable(FieldsView view) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "use-view-mode");
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player1 = getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "fields-view");
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode");
    Collection players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
    return players.contains(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode-not-traversable"));
//    String query = "on:use-view-mode(%FD% : on:field-definition, %V% : on:fields-view, on:view-mode-not-traversable : on:view-mode)?";
//    Map params = new HashMap(2);
//    params.put("FD", getTopicIF());
//    params.put("V", view.getTopicIF());
//    return getTopicMap().getQueryWrapper().isTrue(query, params);
  }

  public boolean isEmbedded(FieldsView view) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "use-view-mode");
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
    TopicIF player1 = getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "fields-view");
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "view-mode");
    Collection players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
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
    Collection players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
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
//    // NOTE: returns null if no value view given
//    String query = "select $CV from on:use-value-view(%FD% : on:field-definition, %PV% : on:parent-view, $CV : on:child-view)?";
//    Map params = new HashMap(2);
//    params.put("FD", getTopicIF());
//    params.put("PV", view.getTopicIF());
//    TopicIF embeddedView = getTopicMap().getQueryWrapper().queryForTopic(query, params);
//    return embeddedView == null ? null : new FieldsView(embeddedView, getTopicMap());
  }

  /**
   * Returns the cardinality of the field on this topic type.
   */
  public Cardinality getCardinality() {
		if (cachedCardinality != null) return cachedCardinality;

    String query = 
			"select $C from on:has-cardinality(%FD% : on:field-definition, $C : on:cardinality) limit 1?";

    Map params = Collections.singletonMap("FD", getTopicIF());

    TopicMap tm = getTopicMap();
    TopicIF card = (TopicIF)tm.getQueryWrapper().queryForObject(query, params);
		Cardinality cardinality = (card == null ? Cardinality.getDefaultCardinality(tm) : new Cardinality(card, tm));
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

    Collection associationIFs = OntopolyModelUtils.findBinaryAssociations(
        tm, aType, player2, type2, type3);
    Iterator it = associationIFs.iterator();

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
  public List getUsedBy() {
    String query = "select $type from on:has-field($type : on:field-owner, %FD% : on:field-definition)?";
    Map params = Collections.singletonMap("FD", getTopicIF());

    TopicMap tm = getTopicMap();
    Collection queryCollection = tm.getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn(), params);

    Iterator it = queryCollection.iterator();
    List topicTypes = new ArrayList();
    TopicIF currTopicIF;
    while (it.hasNext()) {
      currTopicIF = (TopicIF) it.next();
      topicTypes.add(new TopicType(currTopicIF, tm));
    }

    return topicTypes;
  }

  public abstract Collection getValues(Topic topic);

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
