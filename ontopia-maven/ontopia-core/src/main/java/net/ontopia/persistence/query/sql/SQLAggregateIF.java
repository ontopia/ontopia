// $Id: SQLAggregateIF.java,v 1.5 2004/05/21 12:29:18 grove Exp $

package net.ontopia.persistence.query.sql;

/**
 * INTERNAL: Represents an aggregate function in a SQL query.
 */

public interface SQLAggregateIF {
  
  /**
   * INTERNAL: Constant referring to the COUNT aggregate function.
   */
  public static final int COUNT = 1;
  
  /**
   * INTERNAL: Returns the aggregate function type indicated by one of
   * the constants in the {@link SQLAggregateIF} interface.
   */
  public int getType();

  /**
   * INTERNAL: Returns the SQLValueIF that the aggregate function is
   * to be evaluated against.
   */
  public SQLValueIF getValue();

  /**
   * INTERNAL: Sets the SQLValueIF that the aggregate function is
   * to be evaluated against.
   */
  public void setValue(SQLValueIF value);

  /**
   * INTERNAL: The <i>column</i> alias to use if this value is
   * included in the projection. The SQL select syntax is typically
   * like "select value as <calias> from foo".
   */
  public String getAlias();

  /**
   * INTERNAL: Sets the column alias.
   */
  public void setAlias(String alias);

  /**
   * INTERNAL: Returns true if this aggregate is a reference to
   * another.
   */
  public boolean isReference();

  /**
   * INTERNAL: Returns the referenced aggregate if one exists.
   */
  public SQLAggregateIF getReference();
  
}
