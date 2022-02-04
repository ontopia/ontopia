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

public interface VariantNameIF extends NameIF, ScopedIF, ReifiableIF {

  String EVENT_ADDED = "VariantNameIF.added";
  String EVENT_REMOVED = "VariantNameIF.removed";
  String EVENT_SET_VALUE = "VariantNameIF.setValue";
  String EVENT_SET_DATATYPE = "VariantNameIF.setDataType";
  String EVENT_ADD_THEME = "VariantNameIF.addTheme";
  String EVENT_REMOVE_THEME = "VariantNameIF.removeTheme";

  /**
   * PUBLIC: Gets the topic name to which this variant name belongs. The
   * topic name may be a direct parent or an ancestor.
   *
   *
   * @return The topic name of which this is a variant; an object implementing TopicNameIF.
   *
   */
  TopicNameIF getTopicName();

  /**
   * PUBLIC: Gets the data type of this variant.
   *
   * @since 4.0
   */    
  LocatorIF getDataType();

  /**
   * PUBLIC: Gets the string representation of this variant. This
   * method will return null if the length
   * of the value exceeds the supported maximum size.
   */
  @Override
  String getValue();

  /**
   * PUBLIC: Returns a Reader that allows you to stream the string
   * representation of this variant. Values of all sizes are supported by this
   * method.
   *
   * @since 4.0
   */
  Reader getReader();

  /**
   * PUBLIC: Same as <code>setValue(value,
   * DataTypes.TYPE_STRING)</code>. This method is here primarily for
   * backwards compatibility.
   */
  @Override
  default void setValue(String value) {
    setValue(value, DataTypes.TYPE_STRING);
  }
  
  /**
   * PUBLIC: Returns a LocatorIF representation of the variant
   * value. This method will return null if the value is not of type
   * xsd:anyURI (same as <code>DataType.TYPE_URI</code>). This method
   * is here primarily for backwards compatibility.
   */
  default LocatorIF getLocator() {
    if (!DataTypes.TYPE_URI.equals(getDataType())) { return null; }
    String value = getValue();
    return (value == null) ? null : URILocator.create(value);
  }
  
  /**
   * PUBLIC: Same as <code>setValue(locator.getAddress(),
   * DataTypes.TYPE_URI)</code>. This method is here primarily for
   * backwards compatibility.
   */
  default void setLocator(LocatorIF locator) {
      Objects.requireNonNull(locator, "Variant locator must not be null.");
      if (!"URI".equals(locator.getNotation())) { throw new ConstraintViolationException("Only locators with notation 'URI' are supported: " + locator); }
      setValue(locator.getAddress(), DataTypes.TYPE_URI);
  }

  /**
   * PUBLIC: Sets the value and the data type of this variant using
   * a string. The value must conform to the correct string
   * representation according to the datatype.
   *
   * @since 4.0
   */
  void setValue(String value, LocatorIF datatype);
  
  /**
   * PUBLIC: Sets the value and the data type of this variant using
   * a reader. The reader value must conform to the correct string
   * representation according to the datatype.
   *
   * @since 4.0
   */
  void setReader(Reader value, long length, LocatorIF datatype);
  
  /**
   * PUBLIC: Returns the length of the variant value. The number of characters in the string representation
   * is returned.
   *
   * @since 4.0
   */
  long getLength();
  
}





