// $Id: PostgreSQLGenerator.java,v 1.8 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.sql;

import java.util.Map;

/**
 * INTERNAL: PostgreSQL SQL statement generator.
 */

public class PostgreSQLGenerator extends GenericSQLGenerator {

  PostgreSQLGenerator(Map properties) {
    super(properties);
  }

  public void fromSubSelectAlias(StringBuffer sql, BuildInfo info) {
    // sub-SELECT in FROM must have an alias.
    // For example, FROM (SELECT ...) [AS] foo
    sql.append(" as FOOBAR");
  }
  
}
