
package net.ontopia.persistence.query.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.ontopia.persistence.proxy.TicketIF;

/**
 * INTERNAL: RDBMS query implementation that performs queries that
 * return a collection of object instances, ie. an instance of
 * java.util.Collection.
 */

public class RDBMSCollectionQuery implements DetachedQueryIF {
  
  protected SQLStatementIF stm;
  protected boolean lookup_identities;
  
  public RDBMSCollectionQuery(SQLStatementIF stm, boolean lookup_identities) {
    this.stm = stm;
    this.lookup_identities = lookup_identities;
  }

  protected Collection createCollection() {
    //! return new LinkedList();
    return new ArrayList();
  }

  public Object executeQuery(Connection conn) throws Exception {
    TicketIF ticket = stm.getTicket();
    return processResult(ticket, stm.executeQuery(conn));
  }
  
  public Object executeQuery(Connection conn, Object[] params) throws Exception {
    TicketIF ticket = stm.getTicket();
    return processResult(ticket, stm.executeQuery(conn, params));
  }

  public Object executeQuery(Connection conn, Map params) throws Exception {
    TicketIF ticket = stm.getTicket();
    return processResult(ticket, stm.executeQuery(conn, params));
  }

  protected Object processResult(TicketIF ticket, ResultSet rs) throws Exception {
    Collection result;
    try {
      // Prepare result collection
      result = createCollection();

      // Zero or more rows expected
      while (rs.next()) {       
        // Add row object to result collection
        result.add(stm.readValue(ticket, rs, 0, lookup_identities));
      }      
    } finally {
      Statement _stm = rs.getStatement();
      // Close result set
      rs.close();
      rs = null;
      // Close statement
      if (_stm != null) _stm.close();
    }

    return result;
  } 
  
}





