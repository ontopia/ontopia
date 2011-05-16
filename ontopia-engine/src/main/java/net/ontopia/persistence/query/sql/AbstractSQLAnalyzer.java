
// $Id: AbstractSQLAnalyzer.java,v 1.4 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.sql;

import java.util.Iterator;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used to build SQL queries from JDO queries.
 */

public abstract class AbstractSQLAnalyzer {

  public void analyze(SQLQuery query) {
    analyzeSelect(query);
    analyzeFilter(query);
    analyzeOrderBy(query);
  }

  protected void analyzeSelect(SQLQuery query) {
    Iterator iter = query.getSelect().iterator();
    while (iter.hasNext()) {
      Object selected = iter.next();
      if (selected instanceof SQLAggregateIF)
        analyzeAggregate(null, (SQLAggregateIF)selected);
      else
        analyzeValue(null, (SQLValueIF)selected);
    }
  }

  protected void analyzeFilter(SQLQuery query) {
    analyzeExpression(query.getFilter());
  }

  protected void analyzeOrderBy(SQLQuery query) {
    Iterator iter = query.getOrderBy().iterator();
    while (iter.hasNext()) {
      SQLOrderBy order = (SQLOrderBy)iter.next();
      
      if (order.isAggregate())
        analyzeAggregate(null, order.getAggregate());
      else
        analyzeValue(null, order.getValue());
    }
  }

  protected void analyzeExpressions(SQLExpressionIF[] exprs) {
    // Loop over SQL expressions and analyze them individually
    for (int i=0; i < exprs.length; i++) {
      analyzeExpression(exprs[i]);
    }
  }

  protected void analyzeExpression(SQLExpressionIF sqlexpr) {
    if (sqlexpr == null) return;
    switch (sqlexpr.getType()) {
    case SQLExpressionIF.AND:
      analyzeAnd((SQLAnd)sqlexpr);
      break;
    case SQLExpressionIF.EQUALS:
      analyzeEquals((SQLEquals)sqlexpr);
      break;
    case SQLExpressionIF.EXISTS:
      analyzeExists((SQLExists)sqlexpr);
      break;
    case SQLExpressionIF.FALSE:
      analyzeFalse((SQLFalse)sqlexpr);
      break;
    case SQLExpressionIF.IN:
      analyzeIn((SQLIn)sqlexpr);
      break;
    case SQLExpressionIF.IS_NULL:
      analyzeIsNull((SQLIsNull)sqlexpr);
      break;
    case SQLExpressionIF.JOIN:
      analyzeJoin((SQLJoin)sqlexpr);
      break;
    case SQLExpressionIF.LIKE:
      analyzeLike((SQLLike)sqlexpr);
      break;
    case SQLExpressionIF.NOT:
      analyzeNot((SQLNot)sqlexpr);
      break;
    case SQLExpressionIF.NOT_EQUALS:
      analyzeNotEquals((SQLNotEquals)sqlexpr);
      break;
    case SQLExpressionIF.OR:
      analyzeOr((SQLOr)sqlexpr);
      break;
    case SQLExpressionIF.SET_OPERATION:
      analyzeSetOperation((SQLSetOperation)sqlexpr);
      break;
    case SQLExpressionIF.VERBATIM:
      analyzeVerbatimExpression((SQLVerbatimExpression)sqlexpr);
      break;
    case SQLExpressionIF.VALUE_EXPRESSION:
      analyzeValueExpression((SQLValueExpression)sqlexpr);
      break;
    default:
      throw new OntopiaRuntimeException("Invalid SQLExpressionIF: '" + sqlexpr + "'");
    }
  }

  protected void analyzeAnd(SQLAnd expr) {
    analyzeExpressions(expr.getExpressions());
  }

  protected void analyzeEquals(SQLEquals expr) {
    analyzeValue(expr, expr.getLeft());
    analyzeValue(expr, expr.getRight());
  }

  protected void analyzeExists(SQLExists expr) {
    analyzeExpression(expr.getExpression());
  }

  protected void analyzeFalse(SQLFalse expr) {
  }

  protected void analyzeIn(SQLIn expr) {
    analyzeValue(expr, expr.getLeft());
    analyzeValue(expr, expr.getRight());
  }

  protected void analyzeIsNull(SQLIsNull expr) {
    analyzeValue(expr, expr.getValue());
  }

  protected void analyzeJoin(SQLJoin expr) {
    analyzeValue(expr, expr.getLeft());
    analyzeValue(expr, expr.getRight());
  }

  protected void analyzeLike(SQLLike expr) {
    analyzeValue(expr, expr.getLeft());
    analyzeValue(expr, expr.getRight());
  }

  protected void analyzeNot(SQLNot expr) {
    analyzeExpression(expr.getExpression());
  }

  protected void analyzeNotEquals(SQLNotEquals expr) {
    analyzeValue(expr, expr.getLeft());
    analyzeValue(expr, expr.getRight());
  }

  protected void analyzeOr(SQLOr expr) {
    analyzeExpressions(expr.getExpressions());
  }

  protected void analyzeSetOperation(SQLSetOperation expr) {
    // TODO: Analyze individual queries
    throw new UnsupportedOperationException();
  }

  protected void analyzeVerbatimExpression(SQLVerbatimExpression expr) {
  }

  protected void analyzeValueExpression(SQLValueExpression expr) {
    analyzeValue(expr, expr.getValue());
  }
  
  protected void analyzeValues(SQLExpressionIF expr, SQLValueIF[] values) {
    // Loop over SQL values and analyze them individually
    for (int i=0; i < values.length; i++) {
      analyzeValue(expr, values[i]);
    }
  }

  protected void analyzeAggregate(SQLExpressionIF expr, SQLAggregateIF sqlagg) {
    analyzeValue(expr, sqlagg.getValue());
  }

  protected void analyzeValue(SQLExpressionIF expr, SQLValueIF sqlvalue) {
    if (sqlvalue == null) return;
    switch (sqlvalue.getType()) {
    case SQLValueIF.COLUMNS:
      analyzeColumns(expr, (SQLColumns)sqlvalue);
      break;
    case SQLValueIF.NULL:
      analyzeNull(expr, (SQLNull)sqlvalue);
      break;
    case SQLValueIF.PARAMETER:
      analyzeParameter(expr, (SQLParameter)sqlvalue);
      break;
    case SQLValueIF.PRIMITIVE:
      analyzePrimitive(expr, (SQLPrimitive)sqlvalue);
      break;
    case SQLValueIF.TUPLE:
      analyzeTuple(expr, (SQLTuple)sqlvalue);
      break;
    case SQLValueIF.VERBATIM:
      analyzeVerbatim(expr, (SQLVerbatim)sqlvalue);
      break;
    case SQLValueIF.FUNCTION:
      analyzeFunction(expr, (SQLFunction)sqlvalue);
      break;
    default:
      throw new OntopiaRuntimeException("Invalid SQLValueIF: '" + sqlvalue + "'");
    }
  }

  protected void analyzeColumns(SQLExpressionIF expr, SQLColumns value) {
  }

  protected void analyzeNull(SQLExpressionIF expr, SQLNull value) {
  }

  protected void analyzeParameter(SQLExpressionIF expr, SQLParameter value) {
  }

  protected void analyzePrimitive(SQLExpressionIF expr, SQLPrimitive value) {
  }

  protected void analyzeTuple(SQLExpressionIF expr, SQLTuple value) {
    analyzeValues(expr,value.getValues());
  }

  protected void analyzeVerbatim(SQLExpressionIF expr, SQLVerbatim value) {
  }

  protected void analyzeFunction(SQLExpressionIF expr, SQLFunction value) {
    analyzeValues(expr, value.getArguments());
  }

}
