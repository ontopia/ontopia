// $Id: SQLTable.java,v 1.8 2003/08/05 15:45:54 grove Exp $

package net.ontopia.persistence.query.sql;

/**
 * INTERNAL: Represents a reference to a table in a relation
 * database. The reference consist of the name of the table and the
 * alias which it has been assigned in the query.
 */

public class SQLTable {

  protected String name;
  protected String alias;
  
  public SQLTable(String name, String alias) {
    if (name == null) throw new NullPointerException("Table name cannot be null (alias=" + alias + ").");
    // FIXME: Perhaps we should open for no table alias?
    if (alias == null) throw new NullPointerException("Table alias cannot be null (name=" + name + ")."); 
    this.name = name;
    this.alias = alias;
  }

  public String getName() {
    return name;
  }

  public String getAlias() {
    return alias;
  }

  public int hashCode() {
    return name.hashCode() + alias.hashCode();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof SQLTable)) return false;
    SQLTable table = (SQLTable)obj;
    if (alias == null ? table.getAlias() != null : !alias.equals(table.getAlias())) return false;
    if (name == null ? table.getName() != null : !name.equals(table.getName())) return false;
    return true;
  }
  
  public String toString() {
    return getName() + " " + getAlias();
  }
  
}





