
package net.ontopia.persistence.query.sql;

import java.util.List;
import java.util.Map;

/**
 * INTERNAL: Microsoft SQL Server SQL statement generator.
 */

public class SQLServerSQLGenerator extends GenericSQLGenerator {

  SQLServerSQLGenerator(Map properties) {
    super(properties);
  }

  protected void whereSQLFalse(SQLFalse expr, StringBuffer sql, BuildInfo info) {
    sql.append("1 = 2");
  }
  
}
