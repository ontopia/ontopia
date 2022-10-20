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
 * INTERNAL: SQL order by statement. The order-by instance wraps a
 * SQLValueIF or SQLAggregateIF instance and specifies whether the
 * ordering should be ascending or descending.
 */

public class SQLOrderBy {

  public static final int ASCENDING = 1;
  public static final int DESCENDING = 2;

  protected SQLAggregateIF aggregate;
  protected SQLValueIF value;
  protected int order;
  
  public SQLOrderBy(SQLValueIF value, int order) {
    this.value = value;
    this.order = order;
  }
  
  public SQLOrderBy(SQLAggregateIF aggregate, int order) {
    this.aggregate = aggregate;
    this.order = order;
  }
  
  public int getOrder() {
    return order;
  }

  public boolean isAggregate() {
    return (aggregate != null);
  }

  public SQLAggregateIF getAggregate() {
    return aggregate;
  }

  public void setAggregate(SQLAggregateIF aggregate) {
    this.aggregate = aggregate;
  }
  
  public SQLValueIF getValue() {
    return value;
  }

  public void setValue(SQLValueIF value) {
    this.value = value;
  }

  @Override
  public String toString() {
    if (aggregate == null) {
      return value + (order == ASCENDING ? " asc" : " desc");
    } else {
      return aggregate + (order == ASCENDING ? " asc" : " desc");
    }
  }
  
}
