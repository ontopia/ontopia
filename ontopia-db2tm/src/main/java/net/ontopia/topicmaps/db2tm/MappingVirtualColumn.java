/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import java.util.Map;
import java.util.HashMap;

/**
 * INTERNAL: Virtual column that used a hash table to map from old
 * value to new value. A default value can also be specified when no
 * entry exists.
 */
public class MappingVirtualColumn implements ValueIF {

  protected final Relation relation;
  protected final String colname;
    
  protected Map<String, String> table = new HashMap<String, String>();
  protected String defaultValue;
  protected boolean defaultSpecified;

  protected final boolean isVirtualColumn;
  protected final String inputColumn;
  protected int cix;

  MappingVirtualColumn(Relation relation, String colname, String inputColumn) {
    this.relation = relation;
    this.colname = colname;
    // NOTE: virtual columns can depend on each other
    this.inputColumn = inputColumn;
    this.isVirtualColumn = relation.isVirtualColumn(inputColumn);
    if (!this.isVirtualColumn) {
      this.cix = relation.getColumnIndex(inputColumn);
      if (this.cix < 0) {
        throw new DB2TMConfigException("Unknown mapping input column: " + inputColumn);
      }
    }
  }

  @Override
  public String getValue(String[] tuple) {
    String value = (isVirtualColumn ? relation.getVirtualColumn(inputColumn).getValue(tuple) : tuple[cix]);
    if (table.containsKey(value)) {
      return table.get(value);
    } else
      if (defaultSpecified) {
        return defaultValue;
    } else {
        throw new DB2TMInputException("No default value specified for mapping column '" + colname + "'", relation, tuple);
    }
  }

  public void addMapping(String from_value, String to_value) {
    table.put(from_value, to_value);
  }

  public void setDefault(String defaultValue) {
    this.defaultValue = defaultValue;
    this.defaultSpecified = true;
  }
  
}
