
package net.ontopia.persistence.query.sql;

import java.util.Map;

/**
 * INTERNAL: MySQL SQL statement generator.
 */

public class MySQLGenerator extends GenericSQLGenerator {

  MySQLGenerator(Map properties) {
    super(properties);
  }

  // TODO: string = operations are case-insensitive. they should not
  // be. cast expression to 'binary L = R'.

  public void fromSubSelectAlias(StringBuffer sql, BuildInfo info) {
    // sub-SELECT in FROM must have an alias.
    // For example, FROM (SELECT ...) [AS] foo
    sql.append(" as FOOBAR");
  }

  protected StringBuffer createOffsetLimitClause(int offset, int limit, BuildInfo info) {    
    // NOTE: offset supported in versions > 4.0

    // LIMIT x OFFSET y clause
    if (limit > 0 && offset > 0) {
      StringBuffer sb = new StringBuffer();
      sb.append(" limit ").append(offset).append(", ").append(limit);
      return sb;
    } else if (limit > 0) {
      StringBuffer sb = new StringBuffer();
      sb.append(" limit ").append(limit);
      return sb;
    // else if (offset > 0) // NOTE: does not work with MySQL
    //   sql_order_by.append(" limit ").append(limit);
    } else {
      return null;
    }
  }
  
}
