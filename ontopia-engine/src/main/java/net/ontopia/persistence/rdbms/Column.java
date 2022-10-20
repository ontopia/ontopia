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
 * INTERNAL: Represents the definition of a relational database column.
 */

public class Column {

  protected String name;
  protected String type;
  protected String size;
  protected String default_value;
  protected String reftable;
  protected String refcol;
  protected boolean nullable;

  protected Map<String, String> properties;

  /**
   * INTERNAL: Gets the name of the column.
   */
  public String getName() {
    return name;
  }

  /**
   * INTERNAL: Sets the name of the column.
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
   * INTERNAL: Gets the column datatype.
   */
  public String getType() {
    return type;
  }

  /**
   * INTERNAL: Sets the column datatype.
   */
  public void setType(String type) {
    this.type = type;
  }
  
  /**
   * INTERNAL: Gets the size of the column.
   */
  public String getSize() {
    return size;
  }

  /**
   * INTERNAL: Sets the size of the column.
   */
  public void setSize(String size) {
    this.size = size;
  }
  
  /**
   * INTERNAL: Gets the column default value.
   */
  public String getDefault() {
    return default_value;
  }

  /**
   * INTERNAL: Sets the column default value.
   */
  public void setDefault(String default_value) {
    this.default_value = default_value;
  }

  /**
   * INTERNAL: Returns true if the column references another column
   * (is a foreign key column).
   */
  public boolean isReference() {
    return (reftable != null);
  }
      
  /**
   * INTERNAL: Returns the name of the table in which the column it
   * references is stored.
   */
  public String getReferencedTable() {
    return reftable;
  }

  /**
   * INTERNAL: Sets the name of the table in which the column it
   * references is stored.
   */
  public void setReferencedTable(String table) {
    reftable = table;
  }
  
  /**
   * INTERNAL: Gets the name of the column that this column references.
   */
  public String getReferencedColumn() {
    // TODO: This should really be a list of columns!
    return refcol;
  }

  /**
   * INTERNAL: Sets the name of the column that this column references.
   */
  public void setReferencedColumn(String column) {
    refcol = column;
  }
  
  /**
   * INTERNAL: Returns true if the column can contain nulls.
   */
  public boolean isNullable() {
    return nullable;
  }

  /**
   * INTERNAL: Sets whether the column can contain nulls or not.
   */
  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }
  
}





