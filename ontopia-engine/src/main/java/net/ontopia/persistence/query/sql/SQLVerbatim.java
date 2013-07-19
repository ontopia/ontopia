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

import net.ontopia.persistence.proxy.DefaultFieldHandler;
import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: Represents a verbatim SQL expression value.
 */

public class SQLVerbatim implements SQLValueIF {

  protected static final Class DEFAULT_VALUE_TYPE = java.lang.String.class;  
  protected static final FieldHandlerIF DEFAULT_FIELD_HANDLER = new DefaultFieldHandler(java.sql.Types.VARCHAR);
    
  protected Object value;
  protected String alias; // column alias. e.g. A.foo as 'Foo Bar'
  protected SQLTable[] tables;
  
  protected Class vtype;
  protected FieldHandlerIF fhandler;

  public SQLVerbatim(Object value) {
    this.value = value;
  }
  
  public SQLVerbatim(Object value, SQLTable[] tables) {
    this.value = value;
    this.tables = tables;
  }

  public int getType() {
    return VERBATIM;
  }

  public int getArity() {
    return 1;
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

  public Object getValue() {
    return value;
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

  /**
   * INTERNAL: Returns the tables that are involved in the verbatim
   * expression. This information is neccessary so that the FROM
   * clause can be correctly generated.
   */
  public SQLTable[] getTables() {    
    return tables;
  }

  public void setTables(SQLTable[] tables) {
    this.tables = tables;
  }
  
  public String toString() {
    return "verbatim: " + getValue();
  }
    
}
