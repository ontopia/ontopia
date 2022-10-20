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


/**
 * INTERNAL: Represents a set of columns from a given table. The
 * columns are grouped for a reason. Columns are often used as join
 * criteria.
 */

public class SQLAggregateReference implements SQLAggregateIF {

  protected String alias;
  protected SQLAggregateIF refagg;
 
  public SQLAggregateReference(SQLAggregateIF refagg) {
    if (refagg == null) {
      throw new IllegalArgumentException("Aggregate cannot be null.");
    }
    this.refagg = refagg;
  }
  
  @Override
  public int getType() {
    return refagg.getType();
  }

  @Override
  public SQLValueIF getValue() {
    return refagg.getValue();
  }

  @Override
  public void setValue(SQLValueIF value) {
    refagg.setValue(value);
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
  public SQLAggregateIF getReference() {
    return refagg;
  }

  @Override
  public int hashCode() {
    return refagg.hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SQLAggregateReference) {
      SQLAggregateReference other = (SQLAggregateReference)obj;
      if (refagg.equals(other.getReference())) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public String toString() {
    return new StringBuilder("ref:")
        .append('(')
        .append(refagg)
        .append(')')
        .toString();
  }
    
}
