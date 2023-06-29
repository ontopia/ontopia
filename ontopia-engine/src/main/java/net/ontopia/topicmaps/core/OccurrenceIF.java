/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.core;

import java.io.Reader;
import java.util.Objects;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

/**
 * PUBLIC: Implemented by objects representing occurrences in the
 * topic map model.  An occurrence is a relationship between a topic,
 * and an information resource which is relevant to that topic.</p>
 *
 * NOTE Comments partly revised only.</p>
 */

public interface OccurrenceIF extends ScopedIF, TypedIF, ReifiableIF {

  String EVENT_ADDED = "OccurrenceIF.added";
  String EVENT_REMOVED = "OccurrenceIF.removed";
  String EVENT_SET_TYPE = "OccurrenceIF.setType";
  String EVENT_SET_VALUE = "OccurrenceIF.setValue";
  String EVENT_SET_DATATYPE = "OccurrenceIF.setDataType";
  String EVENT_ADD_THEME = "OccurrenceIF.addTheme";
  String EVENT_REMOVE_THEME = "OccurrenceIF.removeTheme";

  /**
   * PUBLIC: Gets the topic for this occurrence.
   *
   * @return The topic to which this occurrence belongs; an object implementing TopicIF.
   */
  TopicIF getTopic();
  
  /**
   * PUBLIC: Gets the data type of this occurrence.
   *
   * @since 4.0
   */    
  LocatorIF getDataType();

  /**
   * PUBLIC: Gets the string representation of this occurrence. This
   * method will return null if the length
   * of the value exceeds the supported maximum size.
   */
  String getValue();

  /**
   * PUBLIC: Returns a Reader that allows you to stream the string
   * representation of this occurrence. Values of all sizes are supported by this
   * method.
   *
   * @since 4.0
   */
  Reader getReader();
  
  //! /**
  //!  * PUBLIC: Returns an InputStream that allows you to stream the
  //!  * string representation of this occurrence. This method will return
  //!  * null if the value is not binary. Values of all sizes are
  //!  * supported by this method.
  //!  *
  //!  * @since 4.0
  //!  */
  //! public InputStream getInputStream();

  /**
   * PUBLIC: Same as <code>setValue(value,
   * DataTypes.TYPE_STRING)</code>. This method is here primarily for
   * backwards compatibility.
   */
  default void setValue(String value) {
    setValue(value, DataTypes.TYPE_STRING);
  }
  
  /**
   * PUBLIC: Returns a LocatorIF representation of the occurrence
   * value. This method will return null if the value is not of type
   * xsd:anyURI (same as <code>DataType.TYPE_URI</code>). This method
   * is here primarily for backwards compatibility.
   */
  default LocatorIF getLocator() {
    if (!DataTypes.TYPE_URI.equals(getDataType())) { return null; }
    String value = getValue();
    return (value == null ? null : URILocator.create(value));
  }
  
  /**
   * PUBLIC: Same as <code>setValue(locator.getAddress(),
   * DataTypes.TYPE_URI)</code>. This method is here primarily for
   * backwards compatibility.
   */
  default void setLocator(LocatorIF locator) {
    Objects.requireNonNull(locator, "Occurrence locator must not be null.");
    if (!"URI".equals(locator.getNotation())) { throw new ConstraintViolationException("Only locators with notation 'URI' are supported: " + locator); }
    setValue(locator.getAddress(), DataTypes.TYPE_URI);
  }

  // public Object getObject();
  // public void setObject(Object value);
  // public void setObject(Object value, LocatorIF datatype);
  
  /**
   * PUBLIC: Sets the value and the data type of this occurrence using
   * a string. The value must conform to the correct string
   * representation according to the datatype.
   *
   * @since 4.0
   */
  void setValue(String value, LocatorIF datatype);
  
  /**
   * PUBLIC: Sets the value and the data type of this occurrence using
   * a reader. The reader value must conform to the correct string
   * representation according to the datatype.
   *
   * @since 4.0
   */
  void setReader(Reader value, long length, LocatorIF datatype);
  
  //! /**
  //!  * PUBLIC: Sets the [binary] value and the data type of this
  //!  * occurrence using an InputStream. Use this method to set binary
  //!  * values.
  //!  *
  //!  * @since 4.0
  //!  */
  //! public void setInputStream(InputStream value, long length, LocatorIF datatype);
  //! 
  //! /**
  //!  * PUBLIC: Returns true if the occurrence value is binary.
  //!  *
  //!  * @since 4.0
  //!  */
  //! public boolean isBinary();
  
  /**
   * PUBLIC: Returns the length of the occurrence value. The number of characters in the string representation
   * is returned.
   *
   * @since 4.0
   */
  long getLength();

  // public Object getHashCode();
  
}
