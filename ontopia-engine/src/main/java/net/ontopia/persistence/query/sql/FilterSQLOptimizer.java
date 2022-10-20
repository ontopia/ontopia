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

import java.util.List;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used to build SQL queries from JDO queries.
 */

public abstract class FilterSQLOptimizer {

  public SQLQuery optimize(SQLQuery query) {
    filterSelect(query);
    filterFilter(query);
    filterOrderBy(query);
    return query;
  }

  protected void filterSelect(SQLQuery query) {
    List select = query.getSelect();
    for (int i=0;  i < select.size(); i++) {
      Object selected = select.get(i);
      if (selected instanceof SQLAggregateIF) {
	select.set(i, filterAggregate((SQLAggregateIF)selected));
      } else {
	// replace value
	select.set(i, filterValue((SQLValueIF)selected));
      }
    }
  }

  protected void filterFilter(SQLQuery query) {
    filterExpression(query.getFilter());
  }

  protected void filterOrderBy(SQLQuery query) {
    List orderby = query.getOrderBy();
    for (int i=0;  i < orderby.size(); i++) {
      SQLOrderBy ordered = (SQLOrderBy)orderby.get(i);
      if (ordered.isAggregate()) {
	ordered.setAggregate(filterAggregate(ordered.getAggregate()));
	//! orderby.set(i, ordered));
      } else {
	// replace value
	ordered.setValue(filterValue(ordered.getValue()));
	//! orderby.set(i, ordered);
      }
    }
  }

  protected SQLExpressionIF[] filterExpressions(SQLExpressionIF[] exprs) {
    // Loop over SQL expressions and filter them individually
    for (int i=0; i < exprs.length; i++) {
      exprs[i] = filterExpression(exprs[i]);
    }
    return exprs;
  }

  protected SQLExpressionIF filterExpression(SQLExpressionIF sqlexpr) {
    if (sqlexpr == null) {
      return null;
    }
    switch (sqlexpr.getType()) {
    case SQLExpressionIF.AND:
      return filterAnd((SQLAnd)sqlexpr);
    case SQLExpressionIF.EQUALS:
      return filterEquals((SQLEquals)sqlexpr);
    case SQLExpressionIF.EXISTS:
      return filterExists((SQLExists)sqlexpr);
    case SQLExpressionIF.FALSE:
      return filterFalse((SQLFalse)sqlexpr);
    case SQLExpressionIF.IN:
      return filterIn((SQLIn)sqlexpr);
    case SQLExpressionIF.IS_NULL:
      return filterIsNull((SQLIsNull)sqlexpr);
    case SQLExpressionIF.JOIN:
      return filterJoin((SQLJoin)sqlexpr);
    case SQLExpressionIF.LIKE:
      return filterLike((SQLLike)sqlexpr);
    case SQLExpressionIF.NOT:
      return filterNot((SQLNot)sqlexpr);
    case SQLExpressionIF.NOT_EQUALS:
      return filterNotEquals((SQLNotEquals)sqlexpr);
    case SQLExpressionIF.OR:
      return filterOr((SQLOr)sqlexpr);
    case SQLExpressionIF.SET_OPERATION:
      return filterSetOperation((SQLSetOperation)sqlexpr);
    case SQLExpressionIF.VERBATIM:
      return filterVerbatimExpression((SQLVerbatimExpression)sqlexpr);
    case SQLExpressionIF.VALUE_EXPRESSION:
      return filterValueExpression((SQLValueExpression)sqlexpr);
    default:
      throw new OntopiaRuntimeException("Invalid SQLExpressionIF: '" + sqlexpr + "'");
    }
  }

  protected SQLExpressionIF filterAnd(SQLAnd expr) {
    expr.setExpressions(filterExpressions(expr.getExpressions()));
    return expr;
  }
  
  protected SQLExpressionIF filterEquals(SQLEquals expr) {
    expr.setLeft(filterValue(expr.getLeft()));
    expr.setRight(filterValue(expr.getRight()));
    return expr;
  }

  protected SQLExpressionIF filterExists(SQLExists expr) {
    expr.setExpression(filterExpression(expr.getExpression()));
    return expr;
  }

  protected SQLExpressionIF filterFalse(SQLFalse expr) {
    return expr;
  }

  protected SQLExpressionIF filterIn(SQLIn expr) {
    expr.setLeft(filterValue(expr.getLeft()));
    expr.setRight(filterValue(expr.getRight()));
    return expr;
  }

  protected SQLExpressionIF filterIsNull(SQLIsNull expr) {
    expr.setValue(filterValue(expr.getValue()));
    return expr;
  }

  protected SQLExpressionIF filterJoin(SQLJoin expr) {
    expr.setLeft((SQLColumns)filterValue(expr.getLeft()));
    expr.setRight((SQLColumns)filterValue(expr.getRight()));
    return expr;
  }

  protected SQLExpressionIF filterLike(SQLLike expr) {
    expr.setLeft(filterValue(expr.getLeft()));
    expr.setRight(filterValue(expr.getRight()));
    return expr;
  }

  protected SQLExpressionIF filterNot(SQLNot expr) {
    expr.setExpression(filterExpression(expr.getExpression()));
    return expr;
  }

  protected SQLExpressionIF filterNotEquals(SQLNotEquals expr) {
    expr.setLeft(filterValue(expr.getLeft()));
    expr.setRight(filterValue(expr.getRight()));
    return expr;
  }

  protected SQLExpressionIF filterOr(SQLOr expr) {
    expr.setExpressions(filterExpressions(expr.getExpressions()));
    return expr;
  }

  protected SQLExpressionIF filterSetOperation(SQLSetOperation expr) {
    // TODO: Filter individual queries
    throw new UnsupportedOperationException();
  }

  protected SQLExpressionIF filterVerbatimExpression(SQLVerbatimExpression expr) {
    return expr;
  }

  protected SQLExpressionIF filterValueExpression(SQLValueExpression expr) {
    expr.setValue(filterValue(expr.getValue()));
    return expr;
  }
  
  protected SQLValueIF[] filterValues(SQLValueIF[] values) {
    // Loop over SQL values filter them individually
    for (int i=0; i < values.length; i++) {
      values[i] = filterValue(values[i]);
    }
    return values;
  }

  protected SQLAggregateIF filterAggregate(SQLAggregateIF sqlagg) {
    sqlagg.setValue(filterValue(sqlagg.getValue()));
    return sqlagg;
  }

  protected SQLValueIF filterValue(SQLValueIF sqlvalue) {
    if (sqlvalue == null) {
      return null;
    }
    switch (sqlvalue.getType()) {
    case SQLValueIF.COLUMNS:
      return filterColumns((SQLColumns)sqlvalue);
    case SQLValueIF.NULL:
      return filterNull((SQLNull)sqlvalue);
    case SQLValueIF.PARAMETER:
      return filterParameter((SQLParameter)sqlvalue);
    case SQLValueIF.PRIMITIVE:
      return filterPrimitive((SQLPrimitive)sqlvalue);
    case SQLValueIF.TUPLE:
      return filterTuple((SQLTuple)sqlvalue);
    case SQLValueIF.VERBATIM:
      return filterVerbatim((SQLVerbatim)sqlvalue);
    case SQLValueIF.FUNCTION:
      return filterFunction((SQLFunction)sqlvalue);
    default:
      throw new OntopiaRuntimeException("Invalid SQLValueIF: '" + sqlvalue + "'");
    }
  }

  protected SQLValueIF filterColumns(SQLColumns value) {
    return value;
  }

  protected SQLValueIF filterNull(SQLNull value) {
    return value;
  }

  protected SQLValueIF filterParameter(SQLParameter value) {
    return value;
  }

  protected SQLValueIF filterPrimitive(SQLPrimitive value) {
    return value;
  }

  protected SQLValueIF filterTuple(SQLTuple value) {
    value.setValues(filterValues(value.getValues()));
    return value;
  }

  protected SQLValueIF filterVerbatim(SQLVerbatim value) {
    return value;
  }

  protected SQLValueIF filterFunction(SQLFunction value) {
    value.setArguments(filterValues(value.getArguments()));
    return value;
  }

}
