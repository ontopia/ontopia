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
import java.io.InputStream;

import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: Implemented by objects representing occurrences in the
 * topic map model.  An occurrence is a relationship between a topic,
 * and an information resource which is relevant to that topic.</p>
 *
 * NOTE Comments partly revised only.</p>
 */

public interface OccurrenceIF extends ScopedIF, TypedIF, ReifiableIF {

  public static final String EVENT_ADDED = "OccurrenceIF.added";
  public static final String EVENT_REMOVED = "OccurrenceIF.removed";
  public static final String EVENT_SET_TYPE = "OccurrenceIF.setType";
  public static final String EVENT_SET_VALUE = "OccurrenceIF.setValue";
  public static final String EVENT_SET_DATATYPE = "OccurrenceIF.setDataType";
  public static final String EVENT_ADD_THEME = "OccurrenceIF.addTheme";
  public static final String EVENT_REMOVE_THEME = "OccurrenceIF.removeTheme";

  /**
   * PUBLIC: Gets the topic for this occurrence.
   *
   * @return The topic to which this occurrence belongs; an object implementing TopicIF.
   */
  public TopicIF getTopic();
  
  /**
   * PUBLIC: Gets the data type of this occurrence.
   *
   * @since 4.0
   */    
  public LocatorIF getDataType();

  /**
   * PUBLIC: Gets the string representation of this occurrence. This
   * method will return null if the length
   * of the value exceeds the supported maximum size.
   */
  public String getValue();

  /**
   * PUBLIC: Returns a Reader that allows you to stream the string
   * representation of this occurrence. Values of all sizes are supported by this
   * method.
   *
   * @since 4.0
   */
  public Reader getReader();
  
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
  public void setValue(String value);
  
  /**
   * PUBLIC: Returns a LocatorIF representation of the occurrence
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
  public void setValue(String value, LocatorIF datatype);
  
  /**
   * PUBLIC: Sets the value and the data type of this occurrence using
   * a reader. The reader value must conform to the correct string
   * representation according to the datatype.
   *
   * @since 4.0
   */
  public void setReader(Reader value, long length, LocatorIF datatype);
  
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
  public long getLength();

  // public Object getHashCode();
  
}
