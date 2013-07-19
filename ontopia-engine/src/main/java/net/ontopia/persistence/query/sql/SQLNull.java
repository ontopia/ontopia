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
 * INTERNAL: SQL value: null. Represents the SQL null value.<p>
 */

public class SQLNull implements SQLValueIF {

  protected String alias;

  protected Class vtype;
  protected FieldHandlerIF fhandler;
  
  public SQLNull() {
  }

  public int getType() {
    return NULL;
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

  public Class getValueType() {    
    return vtype;
  }

  public void setValueType(Class vtype) {
    this.vtype = vtype;
  }

  public FieldHandlerIF getFieldHandler() {
    return (fhandler == null ? new DefaultFieldHandler(java.sql.Types.NULL) : fhandler);
  }

  public void setFieldHandler(FieldHandlerIF fhandler) {
    this.fhandler = fhandler;
  }

  public int hashCode() {
    return 123; // Just some random number
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof SQLNull)
      return true;
    else
      return false;
  }
  
  public String toString() {
    return "null";
  }
  
}
