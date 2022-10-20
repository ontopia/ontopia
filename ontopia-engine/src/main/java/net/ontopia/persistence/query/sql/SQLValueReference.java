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

import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: A reference to another SQLValueIF. Note that the
 * SQLValueIF must be referenceable (e.g. have an alias).
 */

public class SQLValueReference implements SQLValueIF {

  protected SQLValueIF refvalue;
  protected String alias;
  
  public SQLValueReference(SQLValueIF refvalue) {
    if (refvalue == null) {
      throw new IllegalArgumentException("Referenced SQLValueIF cannot be null.");
    }
    this.refvalue = refvalue;
  }
  
  @Override
  public int getType() {
    return refvalue.getType();
  }

  @Override
  public int getArity() {
    return refvalue.getArity();
  }

  @Override
  public int getValueArity() {
    return refvalue.getArity();
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
    return true;
  }
  
  @Override
  public SQLValueIF getReference() {
    return refvalue;
  }

  @Override
  public Class getValueType() {    
    return refvalue.getValueType();
  }

  @Override
  public void setValueType(Class vtype) {
    refvalue.setValueType(vtype);
  }

  @Override
  public FieldHandlerIF getFieldHandler() {
    return refvalue.getFieldHandler();
  }

  @Override
  public void setFieldHandler(FieldHandlerIF fhandler) {
    refvalue.setFieldHandler(fhandler);
  }
  
  @Override
  public int hashCode() {
    return refvalue.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SQLValueReference) {
      SQLValueReference other = (SQLValueReference)obj;
      if (refvalue.equals(other.getReference())) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public String toString() {
    return new StringBuilder("ref:")
        .append('(')
        .append(refvalue)
        .append(')')
        .toString();
  }
    
}
