
package net.ontopia.persistence.query.sql;

/**
 * INTERNAL: Interface for generating SQL statements.
 */

public interface SQLGeneratorIF {

  /**
   * INTERNAL: Create a concrete SQL statement from the given abstract
   * SQL query.
   */
  public SQLStatementIF createSQLStatement(SQLQuery sqlquery);

  /**
   * INTERNAL: Returns true if generator supports the functionality of
   * OFFSET/LIMIT.
   */
  public boolean supportsLimitOffset();
  
}





