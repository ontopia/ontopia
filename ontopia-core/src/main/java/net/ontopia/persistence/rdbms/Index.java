// $Id: Index.java,v 1.2 2007/07/17 12:34:43 geir.gronmo Exp $

package net.ontopia.persistence.rdbms;

/** 
 * INTERNAL: Represents the definition of a relational table index.
 */

public class Index {

  protected String name;
  protected String shortname;
  protected String[] columns;

  /**
   * INTERNAL: Gets the name of the column.
   */
  public String getName() {
    return name;
  }

  /**
   * INTERNAL: Sets the name of the column.
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
   * INTERNAL: Gets the indexed columns.
   */
  public String[] getColumns() {
    return columns;
  }

  /**
   * INTERNAL: Sets the indexed column.
   */
  public void setColumns(String[] columns) {
    this.columns = columns;
  }
  
}





