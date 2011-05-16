
package net.ontopia.topicmaps.db2tm;

/**
 * INTERNAL: Represents an &lt;expression-column> element in the
 * mapping file. Effectively a virtual column which uses a SQL
 * expression to produce a value inside the SQL query.
 */
public class ExpressionVirtualColumn {
  private String colname;
  private String sqlexpression;

  public ExpressionVirtualColumn(String colname) {
    this.colname = colname;
  }

  public String getColumnName() {
    return colname;
  }

  public String getSQLExpression() {
    return sqlexpression;
  }

  public void setSQLExpression(String sqlexpression) {
    this.sqlexpression = sqlexpression;
  }
}