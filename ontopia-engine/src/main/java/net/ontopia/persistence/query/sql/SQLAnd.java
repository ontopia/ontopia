
package net.ontopia.persistence.query.sql;

import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: SQL logical expression: and
 */

public class SQLAnd implements SQLExpressionIF {

  protected SQLExpressionIF[] expressions;

  public SQLAnd(SQLExpressionIF expr1, SQLExpressionIF expr2) {
    this.expressions = new SQLExpressionIF[] {expr1, expr2};
  }

  public SQLAnd(SQLExpressionIF expr1, SQLExpressionIF expr2, SQLExpressionIF expr3) {
    this.expressions = new SQLExpressionIF[] {expr1, expr2, expr3};
  }
  
  public SQLAnd(SQLExpressionIF[] expressions) {
    if (expressions == null) throw new NullPointerException("And expressions cannot be null.");
    this.expressions = expressions;
  }

  public int getType() {
    return AND;
  }

  public SQLExpressionIF[] getExpressions() {
    return expressions;
  }

  public void setExpressions(SQLExpressionIF[] expressions) {
    this.expressions = expressions;
  }

  public String toString() {
    return "(" + StringUtils.join(expressions, " and ") + ")";
  }
  
}
