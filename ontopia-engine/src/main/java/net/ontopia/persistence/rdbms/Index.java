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

/** 
 * INTERNAL: Represents the definition of a relational table index.
 */

public class Index {

  protected String name;
  protected String shortname;
  protected String[] columns;

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
   * INTERNAL: Gets the indexed columns.
   */
  public String[] getColumns() {
    return columns;
  }

  /**
   * INTERNAL: Sets the indexed column.
   */
  public void setColumns(String[] columns) {
    this.columns = columns;
  }
  
}





