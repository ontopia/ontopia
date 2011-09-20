
package net.ontopia.topicmaps.db2tm;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import net.ontopia.utils.CompactHashSet;

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
  
  protected String action; // action  FIXME: wtf is this?
  protected String action_column; // action column

  protected String condition; // added to where clause for filtering

  protected String state_machine; // which state machine to use
  
  protected Map actionMapping = new HashMap();
  protected Set ignoreActions = new CompactHashSet();
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

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getActionColumn() {
    return action_column;
  }

  public void setActionColumn(String action_column) {
    this.action_column = action_column;
  }

  public Map getActionMapping() {
    return actionMapping;
  }

  public String getCondition() {
    return condition;
  }

  public String getStateMachine() {
    return state_machine;
  }

  public void setStateMachine(String state_machine) {
    this.state_machine = state_machine;
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

  public int getAction(String value) {
    Integer a = (Integer)actionMapping.get(value);
    if (a == null)
      throw new DB2TMInputException("Unknown action value '" + value + "' in change log table '" + getTable() + "'");
    else
      return a.intValue();
  }

  public Collection getIgnoreActions() {
    return ignoreActions;
  }
  
  public void addActionMapping(String value, String action) {
    if (action.equals("create"))
      actionMapping.put(value, new Integer(ChangelogReaderIF.CHANGE_TYPE_CREATE));
    else if (action.equals("update"))
      actionMapping.put(value, new Integer(ChangelogReaderIF.CHANGE_TYPE_UPDATE));
    else if (action.equals("delete"))
      actionMapping.put(value, new Integer(ChangelogReaderIF.CHANGE_TYPE_DELETE));
    else if (action.equals("ignore")) {
      actionMapping.put(value, new Integer(ChangelogReaderIF.CHANGE_TYPE_IGNORE));
      ignoreActions.add(value);
    } else
      throw new DB2TMConfigException("Unknown action value '" + value + "' in change log table '" + getTable() + "'");
  }

  public void removeActionMapping(String value) {
    actionMapping.remove(value);
  }

  public String toString() {
    return "Changelog(" + getTable() + ")";
  }
  
  void compile() {
  }

}
