// $Id: ParameterProcessorIF.java,v 1.5 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * INTERNAL: Parameter processor makes sure that query parameters gets
 * properly bound to statements.
 */

public interface ParameterProcessorIF {

  public ResultSet executeQuery(Connection conn, String sql, Object[] params)
    throws SQLException;

  public ResultSet executeQuery(Connection conn, String sql, Map params)
    throws SQLException;
  
}
