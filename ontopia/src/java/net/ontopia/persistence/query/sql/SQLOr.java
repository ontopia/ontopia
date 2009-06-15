// $Id: SQLOr.java,v 1.11 2004/05/21 12:29:18 grove Exp $

package net.ontopia.persistence.query.sql;

import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: SQL logical expression: or
 */

public class SQLOr implements SQLExpressionIF {

  protected SQLExpressionIF[] expressions;

  public SQLOr(SQLExpressionIF[] expressions) {
    this.expressions = expressions;
  }

  public SQLOr(SQLExpressionIF expr1, SQLExpressionIF expr2) {
    this.expressions = new SQLExpressionIF[] {expr1, expr2};
  }

  public SQLOr(SQLExpressionIF expr1, SQLExpressionIF expr2, SQLExpressionIF expr3) {
    this.expressions = new SQLExpressionIF[] {expr1, expr2, expr3};
  }

  public int getType() {
    return OR;
  }

  public SQLExpressionIF[] getExpressions() {
    return expressions;
  }

  public void setExpressions(SQLExpressionIF[] expressions) {
    this.expressions = expressions;
  }

  public String toString() {
    return "(" + StringUtils.join(getExpressions(), " or ") + ")";
  }
  
}





