// $Id: IdentityField.java,v 1.4 2009/05/06 14:19:11 geir.gronmo Exp $

package ontopoly.model;

import java.util.Collection;
import java.util.Iterator;

import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.utils.CollectionUtils;

/**
 * Represents both subject locator and subject identifier fields.
 */
public class IdentityField extends FieldDefinition {

	private IdentityType identityType;

  /**
   * Creates a new IdentityField object.
   */
  public IdentityField(TopicIF topic, TopicMap tm) {
		this(topic, tm, null);
  }

  public IdentityField(TopicIF topic, TopicMap tm, IdentityType identityType) {
		super(topic, tm);
		this.identityType = identityType;
  }

  public int getFieldType() {
    return FIELD_TYPE_IDENTITY;
  }

  /**
   * Returns the name of the IdentityField object.
   */
  public String getFieldName() {
    Collection names = getTopicIF().getTopicNames();
    Iterator it = names.iterator();
    while (it.hasNext()) {
      TopicNameIF name = (TopicNameIF) it.next();
      if (name.getType() == null && name.getScope().isEmpty())
        return name.getValue();
    }
    IdentityType itype = getIdentityType();
    return (itype == null ? null : itype.getName());
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof IdentityField))
      return false;
		
    IdentityField other = (IdentityField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Gets the identity type.
   * 
   * @return the identity type.
   */
  public IdentityType getIdentityType() {
    if (identityType == null) {
      TopicMap tm = getTopicMap();
      TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-identity-type");
      TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "identity-field");
      TopicIF player1 = getTopicIF();
      TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "identity-type");
      Collection players = OntopolyModelUtils.findBinaryPlayers(tm, aType, player1, rType1, rType2);
      TopicIF identityTypeIf = (TopicIF)CollectionUtils.getFirst(players);
      this.identityType = (identityTypeIf == null ? null : new IdentityType(identityTypeIf, getTopicMap()));      

//			String query = "select $IT from on:has-identity-type(%THIS% : on:identity-field, $IT : on:identity-type)?";
//			Map params = Collections.singletonMap("THIS", getTopicIF());
//			TopicMap tm = getTopicMap();
//			TopicIF itype = (TopicIF)tm.getQueryWrapper().queryForObject(query, params);
//      if (itype == null) return null;
//			this.identityType = new IdentityType(itype, tm);
		}
    return identityType;
	}

  /**
   * True if this is the subject locator field type.
   */
  public boolean isSubjectLocator() {
    IdentityType itype = getIdentityType();
    if (itype == null) return false;
		TopicIF itypeIF = itype.getTopicIF();
    return itypeIF.getSubjectIdentifiers().contains(PSI.ON_SUBJECT_LOCATOR);
  }

  /**
   * True if this is the subject identifier field type.
   */
  public boolean isSubjectIdentifier() {
    IdentityType itype = getIdentityType();
    if (itype == null) return false;
		TopicIF itypeIF = itype.getTopicIF();
    return itypeIF.getSubjectIdentifiers().contains(PSI.ON_SUBJECT_IDENTIFIER);
  }

  /**
   * True if this is the item identifier field type.
   */
  public boolean isItemIdentifier() {
    IdentityType itype = getIdentityType();
    if (itype == null) return false;
		TopicIF itypeIF = itype.getTopicIF();
    return itypeIF.getSubjectIdentifiers().contains(PSI.ON_ITEM_IDENTIFIER);
  }

  /**
   * Returns either the subject locator or every subject identifier associated
   * with the topic.
   * 
   * @param topic
   *            topic from which the values is retrieved.
   * @return A collection of LocatorIF objects.
   */
  public Collection getValues(Topic topic) {
    TopicIF topicIf = topic.getTopicIF();
    if (isSubjectLocator())
      return topicIf.getSubjectLocators();
    else if (isItemIdentifier())
      return topicIf.getItemIdentifiers();
    else
      return topicIf.getSubjectIdentifiers();
  }

  /**
   * Replaces a subject locator of or adds a subject identifier to a topic.
   * 
   * @param fieldInstance
   *            field instance to which the value is going to be added.
   * @param _value
   *            value which is going to be added to the topic.
   */
  public void addValue(FieldInstance fieldInstance, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = fieldInstance.getInstance().getTopicIF();
    LocatorIF value = (_value instanceof LocatorIF ? (LocatorIF) _value : 
											 URILocator.create((String) _value));
		if (value != null) {
			if (isSubjectLocator())
				topicIf.addSubjectLocator(value);
			else if (isItemIdentifier())
				topicIf.addItemIdentifier(value);
			else
				topicIf.addSubjectIdentifier(value);
		}
		
		listener.onAfterAdd(fieldInstance, value);
  }

  /**
   * Removes the subject locator or a subject identifier from a topic.
   * 
   * @param fieldInstance
   *            field instance from which the value is going to be removed.
   * @param _value
   *            value which is going to be removed from the topic.
   */
  public void removeValue(FieldInstance fieldInstance, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = fieldInstance.getInstance().getTopicIF();
    LocatorIF value = (_value instanceof LocatorIF ? (LocatorIF) _value : 
											 URILocator.create((String) _value));
		if (value != null) {

		  listener.onBeforeRemove(fieldInstance, value);
		  
			if (isSubjectLocator())
				topicIf.removeSubjectLocator(value);
			else if (isItemIdentifier())
				topicIf.removeItemIdentifier(value);
			else
				topicIf.removeSubjectIdentifier(value);
    }
  }

  /**
   * Returns the assigned height of the identity text field.
   */
  public int getHeight() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "height");
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 1 : Integer.parseInt(occ.getValue()));
  }

  /**
   * Returns the assigned width of the identity text field.
   */
  public int getWidth() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "width");
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 50 : Integer.parseInt(occ.getValue()));
  }

}
