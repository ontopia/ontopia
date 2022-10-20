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
import java.util.List;

import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: SQL value: tuple. Represents a list of nested SQL
 * values. The arity is the same as the total arity of its nested
 * values.<p>
 */

public class SQLTuple implements SQLValueIF {
    
  protected SQLValueIF[] values;
  protected int arity;
  protected int value_arity;
  protected String alias;

  protected Class vtype;
  protected FieldHandlerIF fhandler;
  
  public SQLTuple(List<SQLValueIF> values) {
    this(values.toArray(new SQLValueIF[values.size()]));
  }
  
  public SQLTuple(SQLValueIF[] values) {
    if (values == null) {
      throw new IllegalArgumentException("Tuples values cannot be null.");
    }    
    this.values = values;
    // Compute arity
    // TODO: Should this rather be done on demand instead?
    for (int i=0; i < values.length; i++) {
      arity = arity + values[i].getArity();
      value_arity = value_arity + values[i].getValueArity();
    }
  }

  @Override
  public int getType() {
    return TUPLE;
  }
  
  @Override
  public int getArity() {
    return arity;
  }

  @Override
  public int getValueArity() {
    return value_arity;
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

  public SQLValueIF[] getValues() {
    return values;
  }

  public void setValues(SQLValueIF[] values) {
    this.values = values;
  }

  @Override
  public Class getValueType() {    
    return vtype;
  }

  @Override
  public void setValueType(Class vtype) {
    this.vtype = vtype;
  }

  @Override
  public FieldHandlerIF getFieldHandler() {
    return fhandler;
  }

  @Override
  public void setFieldHandler(FieldHandlerIF fhandler) {
    this.fhandler = fhandler;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SQLTuple) {
      SQLTuple other = (SQLTuple)obj;
      if (Arrays.equals(values, other.getValues())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {    
    StringBuilder sb = new StringBuilder();
    sb.append("tuple:").append(arity).append(":(");
    for (int i=0; i < values.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(values[i]);
    }
    sb.append(')');
    return sb.toString();
  }
  
}
