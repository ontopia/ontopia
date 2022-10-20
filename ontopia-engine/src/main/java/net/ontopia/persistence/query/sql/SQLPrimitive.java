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

import java.sql.Types;

import net.ontopia.persistence.proxy.DefaultFieldHandler;
import net.ontopia.persistence.proxy.FieldHandlerIF;
import net.ontopia.persistence.proxy.SQLTypes;

/**
 * INTERNAL: SQL value: primitive. Represents a primitive value of one
 * of the standard SQL types. A primitive always has an arity of 1.<p>
 
 * <b>Warning:</b> Null should be represented using the SQLNull
 * class.<p>
 *
 * @see java.sql.Types
 */

public class SQLPrimitive implements SQLValueIF {
    
  protected Object value;
  protected int sql_type;
  protected String alias;

  protected Class vtype;
  protected FieldHandlerIF fhandler;

  public SQLPrimitive(Object value, int sql_type) {
    if (value == null) {
      throw new IllegalArgumentException("Primitive value cannot be null (SQL type: " + sql_type  + ").");
    }
    
    this.sql_type = sql_type;
    this.value = value;
  }

  @Override
  public int getType() {
    return PRIMITIVE;
  }
  
  @Override
  public int getArity() {
    return 1;
  }

  @Override
  public int getValueArity() {
    return 1;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public boolean isReference() {
    return false;
  }
  
  @Override
  public SQLValueIF getReference() {
    throw new UnsupportedOperationException("SQLValueIF is not a reference, so this method should not be called.");
  }
  
  public int getSQLType() {
    return sql_type;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public Class getValueType() {    
    return (vtype == null ? SQLTypes.getType(sql_type) : vtype);
  }

  @Override
  public void setValueType(Class vtype) {
    this.vtype = vtype;
  }

  @Override
  public FieldHandlerIF getFieldHandler() {
    return (fhandler == null ? new DefaultFieldHandler(sql_type) : fhandler);
  }

  @Override
  public void setFieldHandler(FieldHandlerIF fhandler) {
    this.fhandler = fhandler;
  }

  @Override
  public String toString() {
    switch (getSQLType()) {
    case Types.VARCHAR:
      return "'" + getValue() + "'";
    default:
      return getValue().toString();
    }
  }
  
}
