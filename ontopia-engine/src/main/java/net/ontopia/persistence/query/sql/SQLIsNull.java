
package net.ontopia.persistence.query.sql;

/**
 * INTERNAL: SQL condition: is null<p>
 *
 * A NULL condition tests for nulls.<p>
 */

public class SQLIsNull implements SQLExpressionIF {

  protected SQLValueIF value;
  
  public SQLIsNull(SQLValueIF value) {
    this.value = value;
  }

  public int getType() {
    return IS_NULL;
  }

  public SQLValueIF getValue() {
    return value;
  }

  public void setValue(SQLValueIF value) {
    this.value = value;
  }

  public String toString() {
    return getValue() + " is null";
  }
  
}





