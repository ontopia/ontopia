
package net.ontopia.topicmaps.db2tm;

import java.util.Map;
import java.util.HashMap;

/**
 * INTERNAL: Virtual column that used a hash table to map from old
 * value to new value. A default value can also be specified when no
 * entry exists.
 */
public class MappingVirtualColumn implements ValueIF {

  protected Relation relation;
  protected String colname;
    
  protected Map table = new HashMap();
  protected String defaultValue;
  protected boolean defaultSpecified;

  protected boolean isVirtualColumn;
  protected String inputColumn;
  protected int cix;

  MappingVirtualColumn(Relation relation, String colname, String inputColumn) {
    this.relation = relation;
    this.colname = colname;
    // NOTE: virtual columns can depend on each other
    this.inputColumn = inputColumn;
    this.isVirtualColumn = relation.isVirtualColumn(inputColumn);
    if (!this.isVirtualColumn) {
      this.cix = relation.getColumnIndex(inputColumn);
      if (this.cix < 0)
        throw new DB2TMConfigException("Unknown mapping input column: " + inputColumn);
    }
  }

  public String getValue(String[] tuple) {
    String value = (isVirtualColumn ? relation.getVirtualColumn(inputColumn).getValue(tuple) : tuple[cix]);
    if (table.containsKey(value))
      return (String)table.get(value);
    else
      if (defaultSpecified)
        return defaultValue;
      else
        throw new DB2TMInputException("No default value specified for mapping column '" + colname + "'", relation, tuple);
  }

  public void addMapping(String from_value, String to_value) {
    table.put(from_value, to_value);
  }

  public void setDefault(String defaultValue) {
    this.defaultValue = defaultValue;
    this.defaultSpecified = true;
  }
  
}
