// $Id: VariantNameIF.java,v 1.15 2008/06/12 14:37:13 geir.gronmo Exp $

package net.ontopia.topicmaps.core;

import java.io.Reader;
import java.io.InputStream;

import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: Implemented by objects representing variant names for
 * topics.  Corresponds to 'variant' elements in the XTM 1.0 syntax; a
 * variant name may belong directly to a topic name, or indirectly via
 * another variant name.</p>
 *
 * A variant name either has a literal value (represented directly by
 * the <code>value</code> property, handled using TopicNameIF methods,
 * and correponding to the contents of the 'variantName /
 * resourceData' element in XTM 1.0) or a locator which identifies a
 * resource (corresponding to a 'variantName' element with a
 * 'resourceRef' child element in XTM 1.0). If a locator is present,
 * the value property is <code>null</code>, and the locator of the
 * resource appears in the locator property of the variantName.</p>
 */

public interface VariantNameIF extends ScopedIF, ReifiableIF {

  /**
   * PUBLIC: Gets the topic name to which this variant name belongs. The
   * topic name may be a direct parent or an ancestor.
   *
   *
   * @return The topic name of which this is a variant; an object implementing TopicNameIF.
   *
   */
  public TopicNameIF getTopicName();

  /**
   * PUBLIC: Gets the topic to which this variant name belongs.
   *
   * @return The topic named by this variant name; an object implementing TopicIF.
   *
   */
  public TopicIF getTopic();
  
  /**
   * PUBLIC: Gets the data type of this variant.
   *
   * @since 4.0
   */    
  public LocatorIF getDataType();

  /**
   * PUBLIC: Gets the string representation of this variant. This
   * method will return null if the length
   * of the value exceeds the supported maximum size.
   */
  public String getValue();

  /**
   * PUBLIC: Returns a Reader that allows you to stream the string
   * representation of this variant. Values of all sizes are supported by this
   * method.
   *
   * @since 4.0
   */
  public Reader getReader();

  /**
   * PUBLIC: Same as <code>setValue(value,
   * DataTypes.TYPE_STRING)</code>. This method is here primarily for
   * backwards compatibility.
   */
  public void setValue(String value);
  
  /**
   * PUBLIC: Returns a LocatorIF representation of the variant
   * value. This method will return null if the value is not of type
   * xsd:anyURI (same as <code>DataType.TYPE_URI</code>). This method
   * is here primarily for backwards compatibility.
   */
  public LocatorIF getLocator();
  
  /**
   * PUBLIC: Same as <code>setValue(locator.getAddress(),
   * DataTypes.TYPE_URI)</code>. This method is here primarily for
   * backwards compatibility.
   */
  public void setLocator(LocatorIF locator);

  /**
   * PUBLIC: Sets the value and the data type of this variant using
   * a string. The value must conform to the correct string
   * representation according to the datatype.
   *
   * @since 4.0
   */
  public void setValue(String value, LocatorIF datatype);
  
  /**
   * PUBLIC: Sets the value and the data type of this variant using
   * a reader. The reader value must conform to the correct string
   * representation according to the datatype.
   *
   * @since 4.0
   */
  public void setReader(Reader value, long length, LocatorIF datatype);
  
  /**
   * PUBLIC: Returns the length of the variant value. The number of characters in the string representation
   * is returned.
   *
   * @since 4.0
   */
  public long getLength();
  
}





