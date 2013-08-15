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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * INTERNAL: Class that holds a database schema definition.
 */

public class Project {

  protected String name;
  protected Map tables;
  protected Map datatypes;
  protected Map c_actions;
  protected Map d_actions;
  
  public Project() {
    tables = new HashMap();
    datatypes = new HashMap();
    c_actions = new HashMap();
    d_actions = new HashMap();
  }

  /**
   * INTERNAL: Gets the name of the database schema.
   */
  public String getName() {
    return name;
  }

  /**
   * INTERNAL: Gets the name of the database schema.
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * INTERNAL: Gets a table definition by name.
   */
  public Table getTableByName(String name) {
    return (Table)tables.get(name);
  }
  
  /**
   * INTERNAL: Gets all the tables in the database schema.
   */
  public Collection getTables() {
    return tables.values();
  }

  /**
   * INTERNAL: Adds the table to the database schema.
   */
  public void addTable(Table table) {
    tables.put(table.getName(), table);
  }

  /**
   * INTERNAL: Removes the table from the database schema.
   */
  public void removeTable(Table table) {
    tables.remove(table.getName());
  }

  /**
   * INTERNAL: Gets all datatype platforms.
   */
  public Collection getDataTypePlatforms() {
    return datatypes.keySet();
  }
  
  /**
   * INTERNAL: Gets a datatype definition by name.
   */
  public DataType getDataTypeByName(String name, String platform) {
    return getDataTypeByName(name, new String[] { platform });
  }
  
  public DataType getDataTypeByName(String name, String[] platforms) {
    for (int i=0; i < platforms.length; i++) {
      Map types = (Map)datatypes.get(platforms[i]);
      if (types == null ||
          !types.containsKey(name)) continue;
      return (DataType)types.get(name);
    }
    return null;
  }
  
  /**
   * INTERNAL: Gets all the datatypes for the given platforms.
   */
  public Collection getDataTypes(String platform) {
    return getDataTypes(new String[] { platform });
  }
  
  public Collection getDataTypes(String[] platforms) {
    Map types = new HashMap();
    for (int i=platforms.length-1; i >= 0; i--) {
      Map _types = (Map)datatypes.get(platforms[i]);
      if (_types != null)
        types.putAll(_types);
    }
    return types.values();
  }

  /**
   * INTERNAL: Add the platform specific datatype.
   */
  public void addDataType(DataType datatype, String platform) {
    if (!datatypes.containsKey(platform)) {
      Map types = new HashMap();
      types.put(datatype.getName(), datatype);
      datatypes.put(platform, types);
    } else {
      Map types = (Map)datatypes.get(platform);
      types.put(datatype.getName(), datatype);
    }
  }

  /**
   * INTERNAL: Remove the platform specific datatype.
   */
  public void removeDataType(DataType datatype, String platform) {
    if (datatypes.containsKey(platform)) {
      Map types = (Map)datatypes.get(platform);      
      types.remove(datatype.getName());
      if (types.isEmpty())
        datatypes.remove(platform);
    }
  }

  /**
   * INTERNAL: Gets the actions to be performed in the database as
   * part of the schema create. Actions for the first matching
   * platform is returned.
   */
  public List getCreateActions(String[] platforms) {
		List actions = new ArrayList();
    for (int i=platforms.length-1; i >= 0; i--) {
      if (c_actions.containsKey(platforms[i]))
				actions.addAll((List)c_actions.get(platforms[i]));
    }
    return actions;
  }

  /**
   * INTERNAL: Sets the actions to be performed in the database as
   * part of the schema create.
   */
  public void addCreateAction(String platform, String action) {
    List actions = (List)this.c_actions.get(platform);
    if (actions == null) {
      actions = new ArrayList();
      this.c_actions.put(platform, actions);
    }
    actions.add(action);
  }

  /**
   * INTERNAL: Gets the actions to be performed in the database as
   * part of the schema drop. Actions for the first matching
   * platform is returned.
   */
  public List getDropActions(String[] platforms) {
		List actions = new ArrayList();
    for (int i=platforms.length-1; i >= 0; i--) {
      if (d_actions.containsKey(platforms[i]))
        actions.addAll((List)d_actions.get(platforms[i]));
    }
    return actions;
  }

  /**
   * INTERNAL: Sets the actions to be performed in the database as
   * part of the schema drop.
   */
  public void addDropAction(String platform, String action) {
    List actions = (List)this.d_actions.get(platform);
    if (actions == null) {
      actions = new ArrayList();
      this.d_actions.put(platform, actions);
    }
    actions.add(action);
  }
  
}
