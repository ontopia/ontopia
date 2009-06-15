
// $Id: SQLServerSQLGenerator.java,v 1.1 2006/07/06 12:43:01 grove Exp $

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
