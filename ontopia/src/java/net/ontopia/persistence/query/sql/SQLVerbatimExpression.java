
// $Id: SQLVerbatimExpression.java,v 1.2 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.sql;

import net.ontopia.persistence.proxy.DefaultFieldHandler;
import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: Represents a verbatim SQL expression.
 */

public class SQLVerbatimExpression implements SQLExpressionIF {

  protected static final Class DEFAULT_VALUE_TYPE = java.lang.String.class;  
  protected static final FieldHandlerIF DEFAULT_FIELD_HANDLER = new DefaultFieldHandler(java.sql.Types.VARCHAR);
    
  protected Object value;
  protected String alias; // column alias. e.g. A.foo as 'Foo Bar'
  protected SQLTable[] tables;
  
  protected Class vtype;
  protected FieldHandlerIF fhandler;

  public SQLVerbatimExpression(Object value) {
    this.value = value;
  }
  
  public SQLVerbatimExpression(Object value, SQLTable[] tables) {
    this.value = value;
    this.tables = tables;
  }

  public int getType() {
    return VERBATIM;
  }

  public Object getValue() {
    return value;
  }

  /**
   * INTERNAL: Returns the tables that are involved in the verbatim
   * expression. This information is neccessary so that the FROM
   * clause can be correctly generated.
   */
  public SQLTable[] getTables() {    
    return tables;
  }

  public void setTables(SQLTable[] tables) {
    this.tables = tables;
  }
  
  public String toString() {
    return "verbatim: " + getValue();
  }
    
}
