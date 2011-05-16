// $Id: Table.java,v 1.11 2007/07/17 12:34:43 geir.gronmo Exp $

package net.ontopia.persistence.rdbms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * INTERNAL: Represents the definition of a relational database table.
 */

public class Table {

  protected String name;
  protected String shortname;

  protected Map colsmap = new HashMap();
  protected List columns = new ArrayList();
  protected Map idxsmap = new HashMap();
  protected List indexes = new ArrayList();
  protected String[] pkeys;

  protected Map properties;

  /**
   * INTERNAL: Gets the name of the table.
   */
  public String getName() {
    return name;
  }

  /**
   * INTERNAL: Sets the name of the table.
   */
  public void setName(String name) {
    this.name = name;
  }  

  /**
   * INTERNAL: Gets the short name of the table.
   */
  public String getShortName() {
    return shortname;
  }

  /**
   * INTERNAL: Sets the short name of the table.
   */
  public void setShortName(String shortname) {
    this.shortname = shortname;
  }  

  /**
   * INTERNAL: Gets the table properties.
   */
  public Collection getProperties() {
    return (properties == null ? Collections.EMPTY_SET : properties.keySet());
  }

  /**
   * INTERNAL: Gets the property value
   */
  public String getProperty(String property) {
    if (properties == null)
      return null;
    else
      return (String)properties.get(property);
  }

  /**
   * INTERNAL: Adds table property.
   */
  public void addProperty(String property, String value) {
    if (properties == null)
      properties = new HashMap();
    properties.put(property, value);
  }
  
  /**
   * INTERNAL: Removes table property.
   */
  public void removeProperty(String property, String value) {
    if (properties == null) return;
    properties.remove(property);
    if (properties.isEmpty())
      properties = null;
  }

  /**
   * INTERNAL: Gets a column by name.
   */
  public Column getColumnByName(String name) {
    return (Column)colsmap.get(name);
  }
  
  /**
   * INTERNAL: Gets all the columns in the table.
   */
  public List getColumns() {
    return columns;
  }
  
  //! /**
  //!  * INTERNAL: Gets all the names of the columns in the table.
  //!  */
  //! public String[] getColumnNames() {
  //!   int length = columns.size();
  //!   String[] result = new String[length];
  //!   for (int i=0; i < length; i++) {
  //!     result[i] = ((Column)columns.get(i)).getName();
  //!   }
  //!   return result;
  //! }

  /**
   * INTERNAL: Adds the column to the table definition.
   */
  public void addColumn(Column column) {
    columns.add(column);
    colsmap.put(column.getName(), column);
  }

  /**
   * INTERNAL: Removes the column from the table definition.
   */
  public void removeColumn(Column column) {
    columns.remove(column);
    colsmap.remove(column.getName());
  }
  
  /**
   * INTERNAL: Gets all the indexes in the table.
   */
  public List getIndexes() {
    return indexes;
  }
  
  //! /**
  //!  * INTERNAL: Gets all the names of the indexes in the table.
  //!  */
  //! public String[] getIndexNames() {
  //!   int length = indexes.size();
  //!   String[] result = new String[length];
  //!   for (int i=0; i < length; i++) {
  //!     result[i] = ((Index)indexes.get(i)).getName();
  //!   }
  //!   return result;
  //! }

  /**
   * INTERNAL: Adds the index to the table definition.
   */
  public void addIndex(Index index) {
    indexes.add(index);
    idxsmap.put(index.getName(), index);
  }

  /**
   * INTERNAL: Removes the index from the table definition.
   */
  public void removeIndex(Index index) {
    indexes.remove(index);
    idxsmap.remove(index.getName());
  }

  /**
   * INTERNAL: Gets the primary key columns.
   */
  public String[] getPrimaryKeys() {
    return pkeys;
  }

  /**
   * INTERNAL: Sets the primary key columns.
   */
  public void setPrimaryKeys(String[] pkeys) {
    this.pkeys = pkeys;
  }

}





