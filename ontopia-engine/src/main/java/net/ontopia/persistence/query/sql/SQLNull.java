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
  
  @Override
  public int getType() {
    return NULL;
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
    return (fhandler == null ? new DefaultFieldHandler(java.sql.Types.NULL) : fhandler);
  }

  @Override
  public void setFieldHandler(FieldHandlerIF fhandler) {
    this.fhandler = fhandler;
  }

  @Override
  public int hashCode() {
    return 123; // Just some random number
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SQLNull) {
      return true;
    } else {
      return false;
    }
  }
  
  @Override
  public String toString() {
    return "null";
  }
  
}
