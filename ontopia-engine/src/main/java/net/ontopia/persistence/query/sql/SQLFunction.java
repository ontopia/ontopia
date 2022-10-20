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
 * INTERNAL: SQL condition: any function; fn(arg1, ... argN)<p>
 */

public class SQLFunction implements SQLValueIF {

  protected static final Class DEFAULT_VALUE_TYPE = String.class;  
  protected static final FieldHandlerIF DEFAULT_FIELD_HANDLER = new DefaultFieldHandler(java.sql.Types.VARCHAR);
  
  protected String name;
  protected SQLValueIF[] args;

  protected String alias; // column alias. e.g. A.foo as 'Foo Bar'  

  protected Class vtype;
  protected FieldHandlerIF fhandler;
  
  public SQLFunction(String name, SQLValueIF[] args) {
    // Check arities
    for (int i=0; i < args.length; i++) {
      if (args[i].getArity() != 1) {
        throw new IllegalArgumentException("Arity of function argument call must be 1: " + args[i]);
      }
    }

    this.name = name;
    this.args = args;
  }

  @Override
  public int getType() {
    return FUNCTION;
  }

  public String getName() {
    return name;
  }

  public SQLValueIF[] getArguments() {
    return args;
  }

  public void setArguments(SQLValueIF[] args) {
    this.args = args;
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
    return (vtype == null ? DEFAULT_VALUE_TYPE : vtype);
  }

  @Override
  public void setValueType(Class vtype) {
    this.vtype = vtype;
  }

  @Override
  public FieldHandlerIF getFieldHandler() {
    return (fhandler == null ? DEFAULT_FIELD_HANDLER : fhandler);
  }

  @Override
  public void setFieldHandler(FieldHandlerIF fhandler) {
    this.fhandler = fhandler;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name).append('(');
    for (int i=0; i < args.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(args[i]);
    }
    sb.append(')');
    return sb.toString();
  }
  
}
