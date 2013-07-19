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
    if (refvalue == null) throw new IllegalArgumentException("Referenced SQLValueIF cannot be null.");
    this.refvalue = refvalue;
  }
  
  public int getType() {
    return refvalue.getType();
  }

  public int getArity() {
    return refvalue.getArity();
  }

  public int getValueArity() {
    return refvalue.getArity();
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public boolean isReference() {
    return true;
  }
  
  public SQLValueIF getReference() {
    return refvalue;
  }

  public Class getValueType() {    
    return refvalue.getValueType();
  }

  public void setValueType(Class vtype) {
    refvalue.setValueType(vtype);
  }

  public FieldHandlerIF getFieldHandler() {
    return refvalue.getFieldHandler();
  }

  public void setFieldHandler(FieldHandlerIF fhandler) {
    refvalue.setFieldHandler(fhandler);
  }
  
  public int hashCode() {
    return refvalue.hashCode();
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof SQLValueReference) {
      SQLValueReference other = (SQLValueReference)obj;
      if (refvalue.equals(other.getReference()))
        return true;
    }
    return false;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("ref:");
    sb.append("(");
    sb.append(refvalue);
    sb.append(")");
    return sb.toString();
  }
    
}
