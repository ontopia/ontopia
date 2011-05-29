
package net.ontopia.persistence.query.sql;

/**
 * INTERNAL: SQL condition: equals (=)
 */

public class SQLValueExpression implements SQLExpressionIF {

  protected SQLValueIF value;

  public SQLValueExpression(SQLValueIF value) {
    this.value = value;
  }

  public int getType() {
    return VALUE_EXPRESSION;
  }

  public SQLValueIF getValue() {
    return value;
  }

  public void setValue(SQLValueIF value) {
    this.value = value;
  }

  public String toString() {
    return "expr(" + getValue() + ')';
  }
  
}





