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

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Represents an aggregate function in a SQL query.
 */

public class SQLAggregate implements SQLAggregateIF {

  protected int type;
  protected SQLValueIF value;
  protected String alias;
  
  public SQLAggregate(SQLValueIF value, int type) {
    if (value == null)
      throw new NullPointerException("Aggregate function variable cannot not be null.");
    this.value = value;
    this.type = type;
  }
  
  public int getType() {
    return type;
  }

  public SQLValueIF getValue() {
    return value;
  }

  public void setValue(SQLValueIF value) {
    this.value = value;
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
  
  public SQLAggregateIF getReference() {
    throw new UnsupportedOperationException("SQLAggregateIF is not a reference, so this method should not be called.");
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    switch (type) {
    case COUNT:
      sb.append("count");
      break;
    default:
      throw new OntopiaRuntimeException("Unknown aggregate function type: " + type);
    }
    sb.append("(");
    sb.append(value);
    sb.append(")");
    return sb.toString();
  }
  
}





