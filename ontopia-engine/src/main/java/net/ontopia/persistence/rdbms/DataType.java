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

package net.ontopia.persistence.rdbms;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** 
 * INTERNAL: Represents the definition of a relational database datatype.
 */

public class DataType {

  protected String name;
  protected String type;
  protected String size;
  protected boolean variable;

  protected Map<String, String> properties;
  
  /**
   * INTERNAL: Gets the name of the datatype.
   */
  public String getName() {
    return name;
  }

  /**
   * INTERNAL: Sets the name of the datatype.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * INTERNAL: Gets the table properties.
   */
  public Collection<String> getProperties() {
    return (properties == null ? Collections.<String>emptySet() : properties.keySet());
  }

  /**
   * INTERNAL: Gets the property value
   */
  public String getProperty(String property) {
    if (properties == null) {
      return null;
    } else {
      return properties.get(property);
    }
  }

  /**
   * INTERNAL: Adds table property.
   */
  public void addProperty(String property, String value) {
    if (properties == null) {
      properties = new HashMap<String, String>();
    }
    properties.put(property, value);
  }
  
  /**
   * INTERNAL: Removes table property.
   */
  public void removeProperty(String property, String value) {
    if (properties == null) {
      return;
    }
    properties.remove(property);
    if (properties.isEmpty()) {
      properties = null;
    }
  }

  /**
   * INTERNAL: Gets the datatype type.
   */
  public String getType() {
    return type;
  }

  /**
   * INTERNAL: Sets the datatype type.
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * INTERNAL: Gets the datatype size.
   */
  public String getSize() {
    return size;
  }

  /**
   * INTERNAL: Sets the datatype size.
   */
  public void setSize(String size) {
    this.size = size;
  }

  /**
   * INTERNAL: Returns true if the database is a variable sized
   * datatype (i.e. not constant).
   */
  public boolean isVariable() {
    return variable;
  }

  /**
   * INTERNAL: Sets whether the datatype is a variable sized datatype
   * or not.
   */
  public void setVariable(boolean variable) {
    this.variable = variable;
  }
  
}





