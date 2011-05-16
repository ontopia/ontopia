// $Id: SQLExists.java,v 1.12 2004/05/21 12:29:18 grove Exp $

package net.ontopia.persistence.query.sql;

/**
 * INTERNAL: SQL condition: exists. Evaluates to true if the left
 * value contains the right value.<p>
 *
 * An EXISTS condition tests for existence of rows in a subquery.<p>
 */

public class SQLExists implements SQLExpressionIF {

  protected SQLExpressionIF expression;

  public SQLExists(SQLExpressionIF expression) {
    if (expression == null)
      throw new NullPointerException("Expression must not be null.");
    this.expression = expression;
  }

  public int getType() {
    return EXISTS;
  }

  public SQLExpressionIF getExpression() {
    return expression;
  }

  public void setExpression(SQLExpressionIF expression) {
    this.expression = expression;
  }

  public String toString() {
    return "exists (" + getExpression() + ")";
  }
  
}





