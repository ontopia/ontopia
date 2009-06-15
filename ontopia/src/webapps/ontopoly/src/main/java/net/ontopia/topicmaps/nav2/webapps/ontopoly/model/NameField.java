
// $Id: NameField.java,v 1.5 2009/05/06 14:19:11 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.utils.OntopolyModelUtils;
import net.ontopia.utils.CollectionUtils;

/**
 * Represents a name type.
 */
public class NameField extends FieldDefinition {

	private NameType nameType;

  public NameField(TopicIF topic, TopicMap tm) {
		this(topic, tm, null);
  }

  public NameField(TopicIF topic, TopicMap tm, NameType nameType) {
		super(topic, tm);
		this.nameType = nameType;
	}

  public int getFieldType() {
    return FIELD_TYPE_NAME;
  }

  public String getFieldName() {
    Collection names = getTopicIF().getTopicNames();
    Iterator it = names.iterator();
    while (it.hasNext()) {
      TopicNameIF name = (TopicNameIF) it.next();
      if (name.getType() == null && name.getScope().isEmpty())
        return name.getValue();
    }		
    NameType ntype = getNameType();
    return (ntype == null ? null : ntype.getName());
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof NameField))
      return false;
		
    NameField other = (NameField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Gets the name type.
   * 
   * @return the name type.
   */
  public NameType getNameType() {
    if (nameType == null) {
      TopicMap tm = getTopicMap();
      TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-name-type");
      TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-field");
      TopicIF player1 = getTopicIF();
      TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "name-type");
      Collection players = OntopolyModelUtils.findBinaryPlayers(tm, aType, player1, rType1, rType2);
      TopicIF nameTypeIf = (TopicIF)CollectionUtils.getFirst(players);
      this.nameType = (nameTypeIf == null ? null : new NameType(nameTypeIf, getTopicMap()));      
      
//			String query = "select $NT from on:has-name-type(%THIS% : on:name-field, $NT : on:name-type)?";
//			Map params = Collections.singletonMap("THIS", getTopicIF());
//			TopicMap tm = getTopicMap();
//			TopicIF ntype = (TopicIF)tm.getQueryWrapper().queryForObject(query, params);
//      if (ntype == null) return null;
//			this.nameType = new NameType(ntype, tm);
		}
    return nameType;
	}

  /**
   * Returns the names, which have this NameType object as type, associated with
   * the topic.
   * 
   * @param topic the topic from which the values is retrieved.
   * @return a collection of TopicNameIFs.
   */
  public Collection getValues(Topic topic) {
    TopicIF topicIf = topic.getTopicIF();
    NameType ntype = getNameType();
    if (ntype == null) return Collections.EMPTY_SET;
		TopicIF typeIf = ntype.getTopicIF();
    return OntopolyModelUtils.findTopicNames(typeIf, topicIf);
  }

  /**
   * Adds a name to a topic. The name has this NameType object as type. If no
   * identical names are associated with the topic, a new one is added, but if
   * some names already exist, all except the first one is deleted.
   * 
   * @param fieldInstance
   *            field instance to which the value is going to be added.
   * @param _value
   *            value which is going to be added to the topic.
   */
  public void addValue(FieldInstance fieldInstance, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = fieldInstance.getInstance().getTopicIF();
    String value = (String) _value;
    NameType ntype = getNameType();
    if (ntype == null) return;
		TopicIF typeIf = ntype.getTopicIF();

    Collection names = OntopolyModelUtils.findTopicNames(typeIf, topicIf,
        value, Collections.EMPTY_SET);
    if (names.isEmpty()) {
      // create new
      OntopolyModelUtils.makeTopicName(typeIf, topicIf, value,
          Collections.EMPTY_SET);
    } else {
      // remove all except the first one
      Iterator iter = names.iterator();
      iter.next();
      while (iter.hasNext()) {
        TopicNameIF name = (TopicNameIF) iter.next();
        name.remove();
      }
    }
    
    listener.onAfterAdd(fieldInstance, value);
  }

  /**
   * Removes a name from a topic. The name has this NameType object as type. If
   * names with the value, _value, are associated with the topic, topic, they
   * will be deleted.
   * 
   * @param fieldInstance
   *            field instance from which the value is going to be removed.
   * @param _value
   *            value which is going to be removed from the topic.
   */
  public void removeValue(FieldInstance fieldInstance, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = fieldInstance.getInstance().getTopicIF();
    String value = (_value instanceof TopicNameIF ? ((TopicNameIF) _value)
        .getValue() : (String) _value);
    NameType ntype = getNameType();
    if (ntype == null) return;
		TopicIF typeIf = ntype.getTopicIF();

		listener.onBeforeRemove(fieldInstance, value);
		
    Collection names = OntopolyModelUtils.findTopicNames(typeIf, topicIf,
        value, Collections.EMPTY_SET);
    if (!names.isEmpty()) {
      // remove all matching
      Iterator iter = names.iterator();
      while (iter.hasNext()) {
        TopicNameIF name = (TopicNameIF) iter.next();
        name.remove();
      }
    }
  }

  /**
   * Returns the assigned height of the name text field.
   */
  public int getHeight() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "height");
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 1 : Integer.parseInt(occ.getValue()));
  }

  /**
   * Returns the assigned width of the name text field.
   */
  public int getWidth() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "width");
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 50 : Integer.parseInt(occ.getValue()));
  }

}
