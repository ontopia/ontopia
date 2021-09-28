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

package net.ontopia.persistence.query.sql;

import java.util.Arrays;

import net.ontopia.persistence.proxy.DefaultFieldHandler;
import net.ontopia.persistence.proxy.FieldHandlerIF;
import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: Represents a set of columns from a given table. The
 * columns are grouped for a reason. Columns are often used as join
 * criteria.
 */

public class SQLColumns implements SQLValueIF {

  protected static final Class DEFAULT_VALUE_TYPE = java.lang.String.class;  
  protected static final FieldHandlerIF DEFAULT_FIELD_HANDLER = new DefaultFieldHandler(java.sql.Types.VARCHAR);
    
  protected SQLTable table;
  protected String[] cols;

  protected String alias; // column alias. e.g. A.foo as 'Foo Bar'  

  protected Class vtype;
  protected FieldHandlerIF fhandler;
 
  public SQLColumns(SQLTable table, String col) {
    this(table, new String[] { col });
  }
  
  public SQLColumns(SQLTable table, String[] cols) {
    if (table == null) throw new IllegalArgumentException("Table cannot be null.");
    if (cols == null || cols.length == 0)
      throw new IllegalArgumentException("List of columns cannot be null or empty.");
    this.table = table;
    this.cols = cols;
  }

  //! public SQLColumns(SQLTable table, String col, String alias) {
  //!   this(table, new String[] { col });
  //!   this.alias = alias;
  //! }
  //! 
  //! public SQLColumns(SQLTable table, String[] cols, String alias) {
  //!   this(table, cols);
  //!   this.alias = alias;
  //! }

  public int getType() {
    return COLUMNS;
  }

  public int getArity() {
    return cols.length;
  }

  public int getValueArity() {
    return 1;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public boolean isReference() {
    return false;
  }
  
  public SQLValueIF getReference() {
    throw new UnsupportedOperationException("SQLValueIF is not a reference, so this method should not be called.");
  }
  
  public SQLTable getTable() {
    return table;
  }

  public String[] getColumns() {
    return cols;
  }

  public Class getValueType() {    
    return (vtype == null ? DEFAULT_VALUE_TYPE : vtype);
  }

  public void setValueType(Class vtype) {
    this.vtype = vtype;
  }

  /**
   * INTERNAL: Returns the field handler for the columns. Default
   * field handler is DefaultFieldHandler with type
   * java.sql.Types.VARCHAR when not specified.
   */
  public FieldHandlerIF getFieldHandler() {
    return (fhandler == null ? DEFAULT_FIELD_HANDLER : fhandler);
  }

  public void setFieldHandler(FieldHandlerIF fhandler) {
    this.fhandler = fhandler;
  }

  public int hashCode() {
    int hashCode = table.hashCode();
    for (int ix = 0; ix < cols.length; ix++) {
      if (cols[ix] != null)
        hashCode = (hashCode + cols[ix].hashCode()) & 0x7FFFFFFF;
    }
    return hashCode;
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof SQLColumns) {
      SQLColumns other = (SQLColumns)obj;
      if (table.equals(other.getTable())) {
        if (Arrays.equals(cols, other.getColumns()))
          return true;
      }
    }
    return false;
  }
  
  public String toString() {
    if (getArity() == 1)
      return getTable().getAlias() + "." + cols[0];
    else {
      StringBuilder sb = new StringBuilder();
      sb.append("columns:");
      sb.append(getTable().getAlias());
      sb.append("(");
      sb.append(StringUtils.join(cols, ", "));
      sb.append(")");
      return sb.toString();
    }
  }
    
}
