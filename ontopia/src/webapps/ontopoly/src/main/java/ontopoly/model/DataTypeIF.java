
package ontopoly.model;

import java.util.Collection;
import java.util.List;

import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * Represents a datatype which can be assigned to an occurrence type.
 */
public interface DataTypeIF extends OntopolyTopicIF {

  /**
   * Tests whether this datatype is xsd:date.
   * 
   * @return true if the datatype is xsd:date.
   */
  public boolean isDate();

  /**
   * Tests whether this datatype is xsd:datetime.
   * 
   * @return true if the datatype is xsd:datetime.
   */
  public boolean isDateTime();

  /**
   * Tests whether this datatype is xsd:number.
   * 
   * @return true if the datatype is xsd:number.
   */
  public boolean isNumber();

  /**
   * Tests whether this datatype is xsd:string.
   * 
   * @return true if the datatype is xsd:string.
   */
  public boolean isString();

  /**
   * Tests whether this datatype is xsd:anyUri.
   * 
   * @return true if the datatype is xsd:anyUri.
   */
  public boolean isURI();

  /**
   * Tests whether this datatype is on:datatype-html.
   * 
   * @return true if the datatype is on:datatype-html.
   */
  public boolean isHTML();

  /**
   * Tests whether this datatype is on:datatype-image.
   * 
   * @return true if the datatype is on:datatype-image.
   */
  public boolean isImage();

  /**
   * Tests whether this datatype is xsd:base64Binary (binary content).
   * 
   * @return true if the datatype is xsd:base64Binary (binary content).
   */
  public boolean isBinary();
  
}
