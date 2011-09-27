
package net.ontopia.topicmaps.db2tm;

import java.util.Map;
import java.util.HashMap;

/**
 * INTERNAL: Data carrier holding the information about a change log
 * table from the mapping file.
 */
public class Changelog {
  protected Relation relation;
  
  protected String table; // table name
  protected String[] pkey; // primary key
  protected String order_column; // ordering column
  protected String local_order_column; // local ordering column

  protected String condition; // added to where clause for filtering
  
  protected Map<String, ExpressionVirtualColumn> virtualColumns = new HashMap();
  
  Changelog(Relation relation) {
    this.relation = relation;
  }

  /**
   * INTERNAL: Returns the relation to which the changelog belongs.
   */
  public Relation getRelation() {
    return relation;
  }

  /**
   * INTERNAL: Returns the name of the changelog table.
   */
  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String[] getPrimaryKey() {
    return pkey;
  }

  public void setPrimaryKey(String[] pkey) {
    this.pkey = pkey;
  }

  public String getOrderColumn() {
    return order_column;
  }

  public void setOrderColumn(String order_column) {
    this.order_column = order_column;
  }

  public String getLocalOrderColumn() {
    return local_order_column;
  }

  public void setLocalOrderColumn(String local_order_column) {
    this.local_order_column = local_order_column;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getCondition() {
    return condition;
  }

  public void addVirtualColumn(ExpressionVirtualColumn column) {
    virtualColumns.put(column.getColumnName(), column);
  }

  public boolean isExpressionColumn(String colname) {
    return virtualColumns.containsKey(colname);
  }

  public String getColumnExpression(String colname) {
    return virtualColumns.get(colname).getSQLExpression();
  }

  public String toString() {
    return "Changelog(" + getTable() + ")";
  }
  
  void compile() {
  }

}
