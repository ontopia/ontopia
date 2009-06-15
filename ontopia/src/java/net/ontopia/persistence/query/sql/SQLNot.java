// $Id: SQLNot.java,v 1.10 2004/05/21 12:29:18 grove Exp $

package net.ontopia.persistence.query.sql;

/**
 * INTERNAL: SQL logical expression: not
 */

public class SQLNot implements SQLExpressionIF {

  protected SQLExpressionIF expression;

  public SQLNot(SQLExpressionIF expression) {
    this.expression = expression;
  }

  public int getType() {
    return NOT;
  }

  public SQLExpressionIF getExpression() {
    return expression;
  }

  public void setExpression(SQLExpressionIF expression) {
    this.expression = expression;
  }
  
  public String toString() {
    return "not (" + getExpression().toString() + ")";
  }
  
}





