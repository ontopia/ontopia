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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * INTERNAL: Represents the definition of a relational database table.
 */

public class Table {

  protected String name;
  protected String shortname;

  protected Map<String, Column> colsmap = new HashMap<String, Column>();
  protected List<Column> columns = new ArrayList<Column>();
  protected Map<String, Index> idxsmap = new HashMap<String, Index>();
  protected List<Index> indexes = new ArrayList<Index>();
  protected String[] pkeys;

  protected Map<String, String> properties;

  /**
   * INTERNAL: Gets the name of the table.
   */
  public String getName() {
    return name;
  }

  /**
   * INTERNAL: Sets the name of the table.
   */
  public void setName(String name) {
    this.name = name;
  }  

  /**
   * INTERNAL: Gets the short name of the table.
   */
  public String getShortName() {
    return shortname;
  }

  /**
   * INTERNAL: Sets the short name of the table.
   */
  public void setShortName(String shortname) {
    this.shortname = shortname;
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
   * INTERNAL: Gets a column by name.
   */
  public Column getColumnByName(String name) {
    return colsmap.get(name);
  }
  
  /**
   * INTERNAL: Gets all the columns in the table.
   */
  public List<Column> getColumns() {
    return columns;
  }
  
  //! /**
  //!  * INTERNAL: Gets all the names of the columns in the table.
  //!  */
  //! public String[] getColumnNames() {
  //!   int length = columns.size();
  //!   String[] result = new String[length];
  //!   for (int i=0; i < length; i++) {
  //!     result[i] = ((Column)columns.get(i)).getName();
  //!   }
  //!   return result;
  //! }

  /**
   * INTERNAL: Adds the column to the table definition.
   */
  public void addColumn(Column column) {
    columns.add(column);
    colsmap.put(column.getName(), column);
  }

  /**
   * INTERNAL: Removes the column from the table definition.
   */
  public void removeColumn(Column column) {
    columns.remove(column);
    colsmap.remove(column.getName());
  }
  
  /**
   * INTERNAL: Gets all the indexes in the table.
   */
  public List<Index> getIndexes() {
    return indexes;
  }
  
  //! /**
  //!  * INTERNAL: Gets all the names of the indexes in the table.
  //!  */
  //! public String[] getIndexNames() {
  //!   int length = indexes.size();
  //!   String[] result = new String[length];
  //!   for (int i=0; i < length; i++) {
  //!     result[i] = ((Index)indexes.get(i)).getName();
  //!   }
  //!   return result;
  //! }

  /**
   * INTERNAL: Adds the index to the table definition.
   */
  public void addIndex(Index index) {
    indexes.add(index);
    idxsmap.put(index.getName(), index);
  }

  /**
   * INTERNAL: Removes the index from the table definition.
   */
  public void removeIndex(Index index) {
    indexes.remove(index);
    idxsmap.remove(index.getName());
  }

  /**
   * INTERNAL: Gets the primary key columns.
   */
  public String[] getPrimaryKeys() {
    return pkeys;
  }

  /**
   * INTERNAL: Sets the primary key columns.
   */
  public void setPrimaryKeys(String[] pkeys) {
    this.pkeys = pkeys;
  }

}





