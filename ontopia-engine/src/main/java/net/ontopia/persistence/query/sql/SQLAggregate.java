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
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Represents an aggregate function in a SQL query.
 */

public class SQLAggregate implements SQLAggregateIF {

  protected int type;
  protected SQLValueIF value;
  protected String alias;
  
  public SQLAggregate(SQLValueIF value, int type) {
    Objects.requireNonNull(value, "Aggregate function variable cannot not be null.");
    this.value = value;
    this.type = type;
  }
  
  @Override
  public int getType() {
    return type;
  }

  @Override
  public SQLValueIF getValue() {
    return value;
  }

  @Override
  public void setValue(SQLValueIF value) {
    this.value = value;
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
  public SQLAggregateIF getReference() {
    throw new UnsupportedOperationException("SQLAggregateIF is not a reference, so this method should not be called.");
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    switch (type) {
    case COUNT:
      sb.append("count");
      break;
    default:
      throw new OntopiaRuntimeException("Unknown aggregate function type: " + type);
    }
    return sb.append('(')
        .append(value)
        .append(')')
        .toString();
  }
  
}





