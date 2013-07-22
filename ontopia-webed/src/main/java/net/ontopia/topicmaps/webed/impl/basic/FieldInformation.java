/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.basic;

/**
 * INTERNAL: Default implementation of the FieldInformationIF
 * interface.
 */
public class FieldInformation implements FieldInformationIF {

  protected String name;
  protected String type;
  protected String maxlength;
  protected String columns;
  protected String rows;
  
  public FieldInformation(String name, String type,
                          String maxlength, String columns, String rows) {
    this.name = name;
    this.type = type;
    this.maxlength = maxlength;
    this.columns = columns;
    if (rows == null)
      rows = "1";
    this.rows = rows;
  }
  
  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getMaxLength() {
    return maxlength;
  }

  public String getColumns() {
    return columns;
  }

  public String getRows() {
    return rows;
  }

  // --- overwrite methods from java.lang.Object

  public int hashCode() {
    StringBuffer sb = new StringBuffer(32);
    sb.append(name).append(type).append(maxlength).append(columns)
      .append(rows);
    return sb.toString().hashCode();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof FieldInformation))
      return false;
    FieldInformation compObj = (FieldInformation) obj;
    return (compObj.getName().equals(name)
            && compObj.getType().equals(type)
            && compObj.getMaxLength().equals(maxlength)
            && compObj.getColumns().equals(columns)
            && compObj.getRows().equals(rows));
  }

  public String toString() {
    StringBuffer sb = new StringBuffer(48);
    sb.append("[FieldInformation: ").append(name).append(", ")
      .append(type).append(", ")
      .append(maxlength).append(", ")
      .append(columns).append(", ")
      .append(rows).append("]");
    return sb.toString();
    
  }
  
}
