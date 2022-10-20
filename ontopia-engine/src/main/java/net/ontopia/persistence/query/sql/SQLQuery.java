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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: Represents an abstract SQL query. Holds SQL query
 * information in a platform independent form.
 */

public class SQLQuery {

  // List of tuples/values to be selected
  protected List select = new ArrayList();

  protected boolean distinct = false;
  protected int limit = -1;
  protected int offset = -1;
  
  // The SQL filter (roughly the same as the where clause)
  protected SQLExpressionIF filter;
  
  // List of tuples/values to be ordered by
  protected List orderby;

  public boolean isSetQuery() {
    return (getFilter() instanceof SQLSetOperation);
  }
  
  public boolean getDistinct() {
    return distinct;
  }

  public void setDistinct(boolean distinct) {
    this.distinct = distinct;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }
  
  public List getSelect() {
    return select;
  }
  
  public void addSelect(SQLValueIF value) {
    select.add(value);
  }
  
  public void addSelect(SQLAggregateIF aggregate) {
    select.add(aggregate);
  }

  public int getWidth() {
    int width = 0;
    int length = select.size();
    for (int i=0; i < length; i++) {
      Object value = select.get(i);
      SQLValueIF sqlvalue;
      if (value instanceof SQLAggregateIF) {
        sqlvalue = ((SQLAggregateIF)value).getValue();
      } else {
        sqlvalue = (SQLValueIF)value;
      }
      width = width + sqlvalue.getArity();
    }
    return width;
  }
  
  public List getOrderBy() {
    if (orderby == null) {
      return Collections.EMPTY_LIST;
    } else {
      return orderby;
    }
  }

  public void addOrderBy(SQLOrderBy sob) {
    if (orderby == null) {
      orderby = new ArrayList();
    }
    orderby.add(sob);
  }

  public void addAscending(SQLValueIF value) {
    addOrderBy(new SQLOrderBy(value, SQLOrderBy.ASCENDING));
  }
  
  public void addDescending(SQLValueIF value) {
    addOrderBy(new SQLOrderBy(value, SQLOrderBy.DESCENDING));
  }
  
  public void addAscending(SQLAggregateIF aggregate) {
    addOrderBy(new SQLOrderBy(aggregate, SQLOrderBy.ASCENDING));
  }
  
  public void addDescending(SQLAggregateIF aggregate) {
    addOrderBy(new SQLOrderBy(aggregate, SQLOrderBy.DESCENDING));
  }

  public SQLExpressionIF getFilter() {
    return filter;
  }

  public void setFilter(SQLExpressionIF filter) {
    this.filter = filter;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("select ");
    if (getDistinct()) {
      sb.append("distinct ");
    }
    if (select == null || select.isEmpty()) {
      sb.append('*');
    } else {
      sb.append(StringUtils.join(select, ", "));
    }
    if (getFilter() != null) {
      sb.append(" from ");
      sb.append(getFilter());
    }
    List _orderby = getOrderBy();
    if (!_orderby.isEmpty()) {
      sb.append(" order by ");
      sb.append(StringUtils.join(_orderby, ", "));
    }
    return sb.toString();
  }
  
}
