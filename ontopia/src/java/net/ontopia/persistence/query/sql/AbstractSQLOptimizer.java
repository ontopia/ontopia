
// $Id: AbstractSQLOptimizer.java,v 1.4 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.sql;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Class used to build SQL queries from JDO queries.
 */

public abstract class AbstractSQLOptimizer {

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
    analyzeValue(expr.getLeft());
    analyzeValue(expr.getRight());
  }

  protected void analyzeExists(SQLExists expr) {
    analyzeExpression(expr.getExpression());
  }

  protected void analyzeFalse(SQLFalse expr) {
  }

  protected void analyzeIn(SQLIn expr) {
    analyzeValue(expr.getLeft());
    analyzeValue(expr.getRight());
  }

  protected void analyzeIsNull(SQLIsNull expr) {
    analyzeValue(expr.getValue());
  }

  protected void analyzeJoin(SQLJoin expr) {
    analyzeValue(expr.getLeft());
    analyzeValue(expr.getRight());
  }

  protected void analyzeLike(SQLLike expr) {
    analyzeValue(expr.getLeft());
    analyzeValue(expr.getRight());
  }

  protected void analyzeNot(SQLNot expr) {
    analyzeExpression(expr.getExpression());
  }

  protected void analyzeNotEquals(SQLNotEquals expr) {
    analyzeValue(expr.getLeft());
    analyzeValue(expr.getRight());
  }

  protected void analyzeOr(SQLOr expr) {
    analyzeExpressions(expr.getExpressions());
  }

  protected void analyzeSetOperation(SQLSetOperation expr) {
    // TODO: Analyze individual queries
  }

  protected void analyzeVerbatimExpression(SQLVerbatimExpression expr) {
  }

  protected void analyzeValueExpression(SQLValueExpression expr) {
    analyzeValue(expr.getValue());
  }
  
  protected void analyzeValues(SQLValueIF[] values) {
    // Loop over SQL values and analyze them individually
    for (int i=0; i < values.length; i++) {
      analyzeValue(values[i]);
    }
  }

  protected void analyzeValue(SQLValueIF sqlvalue) {
    if (sqlvalue == null) return;
    switch (sqlvalue.getType()) {
    case SQLValueIF.COLUMNS:
      analyzeColumns((SQLColumns)sqlvalue);
      break;
    case SQLValueIF.NULL:
      analyzeNull((SQLNull)sqlvalue);
      break;
    case SQLValueIF.PARAMETER:
      analyzeParameter((SQLParameter)sqlvalue);
      break;
    case SQLValueIF.PRIMITIVE:
      analyzePrimitive((SQLPrimitive)sqlvalue);
      break;
    case SQLValueIF.TUPLE:
      analyzeTuple((SQLTuple)sqlvalue);
      break;
    case SQLValueIF.VERBATIM:
      analyzeVerbatim((SQLVerbatim)sqlvalue);
      break;
    case SQLValueIF.FUNCTION:
      analyzeFunction((SQLFunction)sqlvalue);
      break;
    default:
      throw new OntopiaRuntimeException("Invalid SQLValueIF: '" + sqlvalue + "'");
    }
  }

  protected void analyzeColumns(SQLColumns value) {
  }

  protected void analyzeNull(SQLNull value) {
  }

  protected void analyzeParameter(SQLParameter value) {
  }

  protected void analyzePrimitive(SQLPrimitive value) {
  }

  protected void analyzeTuple(SQLTuple value) {
    analyzeValues(value.getValues());
  }

  protected void analyzeVerbatim(SQLVerbatim value) {
  }

  protected void analyzeFunction(SQLFunction value) {
    analyzeValues(value.getArguments());
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
      if (exprs[i] == null) continue;
      totcount++;
      int result = optimizeExpression(exprs[i]);
      // Remove removable expression
      if (result == 1) {
        exprs[i] = null;
        truecount++;
      }
      else if (result == -1)
        return -1;
    }
    // If all subexpressions are true so is the whole expression,
    // otherwise it is non-optimizable.
    if (totcount == truecount)
      return 1;
    else
      return 0;
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
      if (exprs[i] == null) continue;
      totcount++;
      int result = optimizeExpression(exprs[i]);
      // Remove removable expression
      if (result == 1)
        return 1;        
      else if (result == -1) {
        exprs[i] = null;
        falsecount++;
      }        
    }
    // If all subexpressions are false so is the whole expression,
    // otherwise it is non-optimizable.
    if (totcount == falsecount)
      return 1;
    else
      return 0;
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
