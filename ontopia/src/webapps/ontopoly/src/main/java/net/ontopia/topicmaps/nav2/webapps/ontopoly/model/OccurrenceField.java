
// $Id: OccurrenceField.java,v 1.9 2009/04/30 09:53:41 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.utils.OntopolyModelUtils;
import net.ontopia.utils.CollectionUtils;

/**
 * Represents an occurrence type.
 */
public class OccurrenceField extends FieldDefinition {

	private OccurrenceType occurrenceType;

  public OccurrenceField(TopicIF topic, TopicMap tm) {
		this(topic, tm, null);
  }

  public OccurrenceField(TopicIF topic, TopicMap tm, OccurrenceType occurrenceType) {
		super(topic, tm);
		this.occurrenceType = occurrenceType;
	}

  public int getFieldType() {
    return FIELD_TYPE_OCCURRENCE;
  }

  public String getFieldName() {
    Collection names = getTopicIF().getTopicNames();
    Iterator it = names.iterator();
    while (it.hasNext()) {
      TopicNameIF name = (TopicNameIF) it.next();
      if (name.getType() == null && name.getScope().isEmpty())
        return name.getValue();
    }		
    OccurrenceType otype = getOccurrenceType();
    return (otype == null ? null : otype.getName());
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof OccurrenceField))
      return false;
		
    OccurrenceField other = (OccurrenceField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Gets the occurrence type to which this field is assigned.
   * 
   * @return the occurrence type.
   */
  public OccurrenceType getOccurrenceType() {
    if (occurrenceType == null) {
      TopicMap tm = getTopicMap();
      TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-occurrence-type");
      TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-field");
      TopicIF player1 = getTopicIF();
      TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "occurrence-type");
      Collection players = OntopolyModelUtils.findBinaryPlayers(tm, aType, player1, rType1, rType2);
      TopicIF occurrenceTypeIf = (TopicIF)CollectionUtils.getFirst(players);
      this.occurrenceType = (occurrenceTypeIf == null ? null : new OccurrenceType(occurrenceTypeIf, getTopicMap()));      
		}
    return occurrenceType;
	}

  /**
   * Returns the data type of the occurrence type.
   */
  public DataType getDataType() {
    String query = "select $datatype from on:has-datatype(%FD% : on:field-definition, $datatype : on:datatype)?";
    Map<String,TopicIF> params = Collections.singletonMap("FD", getTopicIF());

    QueryMapper<TopicIF> qm = getTopicMap().newQueryMapperNoWrap();
    
    TopicIF dataType = qm.queryForObject(query, params);
    return dataType == null ? DataType.getDefaultDataType(getTopicMap()) : new DataType(dataType, getTopicMap());
  }

//  /**
//   * Sets the data type of the occurrence type.
//   */
//  public void setDataType(DataType dataType) {
//    TopicMap tm = getTopicMap();
//    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-datatype");
//    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "field-definition");
//    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "datatype");
//
//    Collection hasDatatypeAssocs = OntopolyModelUtils.findBinaryAssociations(
//        tm, aType, getTopicIF(), rType1, rType2);
//
//    Iterator it = hasDatatypeAssocs.iterator();
//    while (it.hasNext()) {
//      ((AssociationIF) it.next()).remove();
//    }
//
//    OntopolyModelUtils.makeBinaryAssociation(aType, getTopicIF(), rType1,
//        dataType.getTopicIF(), rType2);
//  }

  /**
   * Returns the assigned height of the occurrence text field.
   */
  public int getHeight() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "height");
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 1 : Integer.parseInt(occ.getValue()));
  }

  /**
   * Returns the assigned width of the occurrence text field.
   */
  public int getWidth() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "width");
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 50 : Integer.parseInt(occ.getValue()));
  }

  public Collection getValues(Topic topic) {
    TopicIF topicIf = topic.getTopicIF();
    OccurrenceType otype = getOccurrenceType();
    if (otype == null) return Collections.EMPTY_SET;
		TopicIF typeIf = otype.getTopicIF();
    // FIXME: need to figure out how to do datatypes properly
    //! LocatorIF datatype = getDataType().getLocator();
    // HACK: we're ignoring the datatype when looking up existing ones
    // return OntopolyModelUtils.findOccurrences(getTopicIF(), topicIf,
    // datatype, Collections.EMPTY_SET);
    return OntopolyModelUtils.findOccurrences(typeIf, topicIf);
  }

  public void addValue(FieldInstance fieldInstance, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = fieldInstance.getInstance().getTopicIF();
    String value = (String) _value;
    LocatorIF datatype = getDataType().getLocator();
    OccurrenceType otype = getOccurrenceType();
    if (otype == null) return;
		TopicIF typeIf = otype.getTopicIF();
		
    // HACK: we're ignoring the datatype when looking up existing ones
    // Collection occs = OntopolyModelUtils.findOccurrences(getTopicIF(),
    // topicIf, value, datatype, Collections.EMPTY_SET);
    Collection occs = OntopolyModelUtils.findOccurrences(typeIf, topicIf,
        value, Collections.EMPTY_SET);
    if (occs.isEmpty()) {
      // create new
      OntopolyModelUtils.makeOccurrence(typeIf, topicIf, value, datatype, Collections.EMPTY_SET);
    } else {
      // remove all except the first one
      Iterator iter = occs.iterator();
      iter.next(); // skip first
      while (iter.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) iter.next();
        occ.remove();
      }
    }
    
    listener.onAfterAdd(fieldInstance, value);
  }

  public void removeValue(FieldInstance fieldInstance, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = fieldInstance.getInstance().getTopicIF();
    String value = (_value instanceof OccurrenceIF ? ((OccurrenceIF) _value)
        .getValue() : (String) _value);
//    LocatorIF datatype = getDataType().getLocator();
    OccurrenceType otype = getOccurrenceType();
    if (otype == null) return;
		TopicIF typeIf = otype.getTopicIF();

    listener.onBeforeRemove(fieldInstance, value);
		
    // HACK: we're ignoring the datatype when looking up existing ones
    // Collection occs = OntopolyModelUtils.findOccurrences(typeIf,
    // topicIf, value, datatype, Collections.EMPTY_SET);
    Collection occs = OntopolyModelUtils.findOccurrences(typeIf, topicIf,
        value, Collections.EMPTY_SET);
    if (!occs.isEmpty()) {
      // remove all the matching
      Iterator iter = occs.iterator();
      while (iter.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) iter.next();
        occ.remove();
      }
    }
  }

}
