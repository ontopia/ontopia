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
 * INTERNAL: Data carrier holding the information about a change log
 * table from the mapping file.
 */
public class Changelog {
  protected final Relation relation;
  
  protected String table; // table name
  protected String[] pkey; // primary key
  protected String order_column; // ordering column
  protected String local_order_column; // local ordering column

  protected String condition; // added to where clause for filtering
  
  protected Map<String, ExpressionVirtualColumn> virtualColumns = new HashMap<String, ExpressionVirtualColumn>();
  
  Changelog(Relation relation) {
    this.relation = relation;
  }

  /**
   * INTERNAL: Returns the relation to which the changelog belongs.
   */
  public Relation getRelation() {
    return relation;
  }

  /**
   * INTERNAL: Returns the name of the changelog table.
   */
  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String[] getPrimaryKey() {
    return pkey;
  }

  public void setPrimaryKey(String[] pkey) {
    this.pkey = pkey;
  }

  public String getOrderColumn() {
    return order_column;
  }

  public void setOrderColumn(String order_column) {
    this.order_column = order_column;
  }

  public String getLocalOrderColumn() {
    return local_order_column;
  }

  public void setLocalOrderColumn(String local_order_column) {
    this.local_order_column = local_order_column;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getCondition() {
    return condition;
  }

  public void addVirtualColumn(ExpressionVirtualColumn column) {
    virtualColumns.put(column.getColumnName(), column);
  }

  public boolean isExpressionColumn(String colname) {
    return virtualColumns.containsKey(colname);
  }

  public String getColumnExpression(String colname) {
    return virtualColumns.get(colname).getSQLExpression();
  }

  public String toString() {
    return "Changelog(" + getTable() + ")";
  }
  
  void compile() {
  }

}
