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
 * INTERNAL: Class used to build SQL queries from JDO queries.
 */

public abstract class BooleanSQLOptimizer {

  public abstract SQLQuery optimize(SQLQuery query);

  protected void optimizeQuery(SQLQuery query) {
    optimizeSelect(query);
    optimizeFilter(query);
    optimizeOrderBy(query);
  }

  protected void optimizeSelect(SQLQuery query) {
    //! Iterator iter = query.getSelect().iterator();
    //! while (iter.hasNext()) {
    //!   Object selected = iter.next();
    //!   if (selected instanceof SQLAggregateIF)
    //!     optimizeValue(((SQLAggregateIF)selected).getValue());
    //!   else
    //!     optimzeValue((SQLValueIF)selected);
    //! }
  }

  protected void optimizeFilter(SQLQuery query) {
    optimizeExpression(query.getFilter());
  }

  protected void optimizeOrderBy(SQLQuery query) {
    //! Iterator iter = query.getOrderBy().iterator();
    //! while (iter.hasNext()) {
    //!   SQLOrderBy order = (SQLOrderBy)iter.next();
    //!   
    //!   if (order.isAggregate())
    //!     optimizeValue(order.getAggregate().getValue());
    //!   else
    //!     optimizeValue(order.getValue());
    //! }
  }

  protected int optimizeExpression(SQLExpressionIF sqlexpr) {
    switch (sqlexpr.getType()) {
    case SQLExpressionIF.AND:
      return optimizeAnd((SQLAnd)sqlexpr);
    case SQLExpressionIF.EQUALS:
      return optimizeEquals((SQLEquals)sqlexpr);
    case SQLExpressionIF.EXISTS:
      return optimizeExists((SQLExists)sqlexpr);
    case SQLExpressionIF.FALSE:
      return optimizeFalse((SQLFalse)sqlexpr);
    case SQLExpressionIF.IN:
      return optimizeIn((SQLIn)sqlexpr);
    case SQLExpressionIF.IS_NULL:
      return optimizeIsNull((SQLIsNull)sqlexpr);
    case SQLExpressionIF.JOIN:
      return optimizeJoin((SQLJoin)sqlexpr);
    case SQLExpressionIF.LIKE:
      return optimizeLike((SQLLike)sqlexpr);
    case SQLExpressionIF.NOT:
      return optimizeNot((SQLNot)sqlexpr);
    case SQLExpressionIF.NOT_EQUALS:
      return optimizeNotEquals((SQLNotEquals)sqlexpr);
    case SQLExpressionIF.OR:
      return optimizeOr((SQLOr)sqlexpr);
    case SQLExpressionIF.SET_OPERATION:
      return optimizeSetOperation((SQLSetOperation)sqlexpr);
    case SQLExpressionIF.VERBATIM:
      return optimizeVerbatimExpression((SQLVerbatimExpression)sqlexpr);
    case SQLExpressionIF.VALUE_EXPRESSION:
      return optimizeValueExpression((SQLValueExpression)sqlexpr);
    default:
      throw new OntopiaRuntimeException("Invalid SQLExpressionIF: '" + sqlexpr + "'");
    }
  }

  protected int optimizeAnd(SQLAnd expr) {
    int totcount = 0;
    int truecount = 0;
    // NOTE: is true if all subexpressions are true
    SQLExpressionIF[] exprs = expr.getExpressions();
    for (int i=0; i < exprs.length; i++) {
      if (exprs[i] == null) {
        continue;
      }
      totcount++;
      int result = optimizeExpression(exprs[i]);
      // Remove removable expression
      if (result == 1) {
        exprs[i] = null;
        truecount++;
      }
      else if (result == -1) {
        return -1;
      }
    }
    // If all subexpressions are true so is the whole expression,
    // otherwise it is non-optimizable.
    if (totcount == truecount) {
      return 1;
    } else {
      return 0;
    }
  }

  protected int optimizeEquals(SQLEquals expr) {
    return 0;
  }

  protected int optimizeExists(SQLExists expr) {
    return optimizeExpression(expr.getExpression());
  }

  protected int optimizeFalse(SQLFalse expr) {
    return 0;
  }

  protected int optimizeIn(SQLIn expr) {
    //! optimizeValue(expr.getLeft());
    //! optimizeValue(expr.getRight());
    return 0;
  }

  protected int optimizeIsNull(SQLIsNull expr) {
    //! optimizeValue(expr.getValue());
    return 0;
  }

  protected int optimizeJoin(SQLJoin expr) {
    // ISSUE: Same optimization as for SQLEqual?
    //! optimizeValue(expr.getLeft());
    //! optimizeValue(expr.getRight());
    return 0;
  }

  protected int optimizeLike(SQLLike expr) {
    //! optimizeValue(expr.getLeft());
    //! optimizeValue(expr.getRight());
    return 0;
  }

  protected int optimizeNot(SQLNot expr) {
    return optimizeExpression(expr.getExpression());
  }

  protected int optimizeNotEquals(SQLNotEquals expr) {
    return 0;
  }

  protected int optimizeOr(SQLOr expr) {
    int totcount = 0;
    int falsecount = 0;
    // NOTE: is true if any of the subexpressions are true
    SQLExpressionIF[] exprs = expr.getExpressions();
    for (int i=0; i < exprs.length; i++) {
      if (exprs[i] == null) {
        continue;
      }
      totcount++;
      int result = optimizeExpression(exprs[i]);
      // Remove removable expression
      if (result == 1) {
        return 1;
      } else if (result == -1) {
        exprs[i] = null;
        falsecount++;
      }        
    }
    // If all subexpressions are false so is the whole expression,
    // otherwise it is non-optimizable.
    if (totcount == falsecount) {
      return 1;
    } else {
      return 0;
    }
  }

  protected int optimizeSetOperation(SQLSetOperation expr) {
    // TODO: Optimize individual queries
    return 0;
  }

  protected int optimizeVerbatimExpression(SQLVerbatimExpression expr) {
    return 0;
  }

  protected int optimizeValueExpression(SQLValueExpression expr) {
    return 0;
  }
  
  //! protected int optimizeValues(SQLValueIF[] values) {
  //!   // Loop over SQL values and optimize them individually
  //!   for (int i=0; i < values.length; i++) {
  //!     optimizeValue(values[i]);
  //!   }
  //! }
  //! 
  //! protected int optimizeValue(SQLValueIF sqlvalue) {
  //!   switch (sqlvalue.getType()) {
  //!   case SQLValueIF.COLUMNS:
  //!     return optimizeColumns((SQLColumns)sqlvalue);
  //!   case SQLValueIF.NULL:
  //!     return optimizeNull((SQLNull)sqlvalue);
  //!   case SQLValueIF.PARAMETER:
  //!     return optimizeParameter((SQLParameter)sqlvalue);
  //!   case SQLValueIF.PRIMITIVE:
  //!     return optimizePrimitive((SQLPrimitive)sqlvalue);
  //!   case SQLValueIF.TUPLE:
  //!     return optimizeTuple((SQLTuple)sqlvalue);
  //!   case SQLValueIF.VERBATIM:
  //!     return optimizeVerbatim((SQLVerbatim)sqlvalue);
  //!   case SQLValueIF.FUNCTION:
  //!     return optimizeFunction((SQLFunction)sqlvalue);
  //!   default:
  //!     throw new OntopiaRuntimeException("Invalid SQLValueIF: '" + sqlvalue + "'");
  //!   }
  //! }
  //! 
  //! protected int optimizeColumns(SQLColumns value) {
  //! }
  //! 
  //! protected int optimizeNull(SQLNull value) {
  //! }
  //! 
  //! protected int optimizeParameter(SQLParameter value) {
  //! }
  //! 
  //! protected int optimizePrimitive(SQLPrimitive value) {
  //! }
  //! 
  //! protected int optimizeTuple(SQLTuple value) {
  //! }
  //! 
  //! protected int optimizeVerbatim(SQLVerbatim value) {
  //!   SQLTable[] tables = value.getTables();
  //!   for (int i=0; i < tables.length; i++) {
  //!     addTable(tables[i]);
  //!   }
  //! }
  //! 
  //! protected int optimizeFunction(SQLFunction value) {
  //!   ...
  //! }

}
