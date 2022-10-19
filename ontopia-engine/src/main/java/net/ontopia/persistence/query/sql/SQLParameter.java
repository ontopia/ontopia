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

import java.util.Objects;
import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: SQL value: parameter. Represents a parameter in a
 * query. A parameter has an arity of one or more.
 */

public class SQLParameter implements SQLValueIF {

  protected String name;
  protected int arity;
  protected String alias;
  
  protected Class vtype;
  protected FieldHandlerIF fhandler;
  
  public SQLParameter(String name, int arity) {
    Objects.requireNonNull(name, "A SQL parameter must have a name.");
    if (arity < 1) {
      throw new IllegalArgumentException("The arity of a SQL parameter must be 1 or more; " + arity + " specified.");
    }
    this.name = name;
    this.arity = arity;
  }

  @Override
  public int getType() {
    return PARAMETER;
  }

  @Override
  public int getArity() {
    return arity;
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
  
  public String getName() {
    return name;
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
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SQLParameter) {
      SQLParameter other = (SQLParameter)obj;    
      return (name.equals(other.getName()));
    }
    return false;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i=0; i < arity; i++) {
      if (i > 1) sb.append(", ");
      sb.append('?').append(getName());
    }
    return sb.toString();
  }
  
}
