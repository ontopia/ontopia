
package net.ontopia.persistence.proxy;

  
/**
 * INTERNAL: Interface for representing two-dimensional (or
 * potentially even N-dimensional) query results.<p>
 *
 * Note: it is a goal that this interface is aligned with
 * net.ontopia.topicmaps.query.core.QueryResultIF.
 */

public interface QueryResultIF {
  
  /**
   * INTERNAL: Skip to the next row in the query result set. The
   * method returns false if the skip was not valid, i.e. we're at the
   * end of the result set.
   */
  public boolean next();

  /**
   * INTERNAL: Returns the number of fields that each row in the query
   * result set have.
   */
  public int getWidth();

  //! /**
  //!  * PUBLIC: Returns the index of the named column. Returns -1 if the
  //!  * column does not exist. The column index is zero-based.
  //!  */
  //! public int getIndex(String colname);

  /**
   * PUBLIC: Returns the names of the columns.
   */
  public String[] getColumnNames();

  /**
   * PUBLIC: Returns the name of the given column.  The column index
   * is zero-based.
   *
   * @throws IndexOutOfBoundsException if there is no such column.
   */
  public String getColumnName(int ix);
  
  /**
   * INTERNAL: Get the value of the field with the specified index
   * from the current result row. The index is zero-based.
   */
  public Object getValue(int index);
  
  //! /**
  //!  * PUBLIC: Returns the value in the given column in the current
  //!  * match.  Requires <code>next()</code> to have been called first.
  //!  * @throws IllegalArgumentException if there is no such column.
  //!  */
  //! public Object getValue(String colname);
  
  /**
   * INTERNAL: Get the values of all fields from the current result
   * row.
   */
  public Object[] getValues();
  
  /**
   * INTERNAL: Reads the values of all fields from the current result
   * row into the specified array.
   */
  public Object[] getValues(Object[] values);
  
  /**
   * INTERNAL: Closes the query result, which allows it to free its
   * resources.
   */
  public void close();
  
}
