
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





