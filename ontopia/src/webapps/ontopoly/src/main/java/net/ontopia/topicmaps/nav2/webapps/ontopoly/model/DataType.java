
// $Id: DataType.java,v 1.3 2008/11/07 13:36:49 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.utils.OntopolyModelUtils;

/**
 * Represents a datatype which can be assigned to an occurrence type.
 */
public class DataType extends Topic {

  public DataType(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof DataType))
      return false;

    DataType other = (DataType) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Returns the datatype locator that this datatype represents.
   */
  public LocatorIF getLocator() {
    TopicIF topicIf = getTopicIF();   
    TopicIF typeIf = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "datatype-locator", false);
    if (typeIf != null) {
      OccurrenceIF occ = OntopolyModelUtils.findOccurrence(typeIf, topicIf);
      LocatorIF loc = (occ == null ? null : occ.getLocator());
      if (loc != null) return loc;
    }
    
    // the code below can be removed at some time in the future
    Collection subinds = getTopicIF().getSubjectIdentifiers();
    if (subinds.contains(DataTypes.TYPE_DATE))
      return DataTypes.TYPE_DATE;
    else if (subinds.contains(DataTypes.TYPE_DATETIME))
      return DataTypes.TYPE_DATETIME;
    else if (subinds.contains(DataTypes.TYPE_DECIMAL))
      return DataTypes.TYPE_DECIMAL;
    else if (subinds.contains(DataTypes.TYPE_STRING))
      return DataTypes.TYPE_STRING;
    else if (subinds.contains(DataTypes.TYPE_URI))
      return DataTypes.TYPE_URI;
    else if (subinds.contains(PSI.ON_DATATYPE_HTML))
      return PSI.ON_DATATYPE_HTML;
    else if (subinds.contains(PSI.ON_DATATYPE_IMAGE))
      return PSI.ON_DATATYPE_IMAGE;
    else
      throw new OntopolyModelRuntimeException("Unknown datatype for topic "+ getTopicIF());
  }

  /**
   * Tests whether this datatype is xsd:date.
   * 
   * @return true if the datatype is xsd:date.
   */
  public boolean isDate() {
    return getTopicIF().getSubjectIdentifiers().contains(DataTypes.TYPE_DATE);
  }

  /**
   * Tests whether this datatype is xsd:datetime.
   * 
   * @return true if the datatype is xsd:datetime.
   */
  public boolean isDateTime() {
    return getTopicIF().getSubjectIdentifiers().contains(DataTypes.TYPE_DATETIME);
  }

  /**
   * Tests whether this datatype is xsd:number.
   * 
   * @return true if the datatype is xsd:number.
   */
  public boolean isNumber() {
    return getTopicIF().getSubjectIdentifiers().contains(DataTypes.TYPE_DECIMAL);
  }

  /**
   * Tests whether this datatype is xsd:string.
   * 
   * @return true if the datatype is xsd:string.
   */
  public boolean isString() {
    return getTopicIF().getSubjectIdentifiers().contains(DataTypes.TYPE_STRING);
  }

  /**
   * Tests whether this datatype is xsd:anyUri.
   * 
   * @return true if the datatype is xsd:anyUri.
   */
  public boolean isURI() {
    return getTopicIF().getSubjectIdentifiers().contains(DataTypes.TYPE_URI);
  }

  /**
   * Tests whether this datatype is on:datatype-html.
   * 
   * @return true if the datatype is on:datatype-html.
   */
  public boolean isHTML() {
    return getTopicIF().getSubjectIdentifiers().contains(PSI.ON_DATATYPE_HTML);
  }

  /**
   * Tests whether this datatype is on:datatype-image.
   * 
   * @return true if the datatype is on:datatype-image.
   */
  public boolean isImage() {
    return getTopicIF().getSubjectIdentifiers().contains(PSI.ON_DATATYPE_IMAGE);
  }

  /**
   * Tests whether this datatype is xsd:base64Binary (binary content).
   * 
   * @return true if the datatype is xsd:base64Binary (binary content).
   */
  public boolean isBinary() {
    return getTopicIF().getSubjectIdentifiers().contains(DataTypes.TYPE_BINARY);
  }

  public static DataType getDefaultDataType(TopicMap tm) {
    return new DataType(tm.getTopicMapIF().getTopicBySubjectIdentifier(DataTypes.TYPE_STRING), tm);
  }

  public static List getDataTypes(TopicMap tm) {
    String query = "instance-of($d, on:datatype)?";

    Collection result = tm.getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    if (result.isEmpty())
      return Collections.EMPTY_LIST;

    List dataTypes = new ArrayList();
    Iterator it = result.iterator();
    while (it.hasNext()) {
      dataTypes.add(new DataType((TopicIF) it.next(), tm));
    }
    return dataTypes;
  }
}
