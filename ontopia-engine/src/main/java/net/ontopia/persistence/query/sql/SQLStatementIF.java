
package net.ontopia.persistence.query.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;

import net.ontopia.persistence.proxy.AccessRegistrarIF;
import net.ontopia.persistence.proxy.ObjectAccessIF;
import net.ontopia.persistence.proxy.TicketIF;

/**
 * INTERNAL: Represents a concrete SQL query.
 */

public interface SQLStatementIF {
  
  public void setObjectAccess(ObjectAccessIF oaccess);
  
  public void setAccessRegistrar(AccessRegistrarIF registrar);

  public TicketIF getTicket();
  
  /**
   * INTERNAL: Returns the number of fields that will be selected by
   * the statement.
   */  
  public int getWidth();

  /**
   * INTERNAL: Execute the statement without any parameters and return
   * a JDBC result set.
   */  
  public ResultSet executeQuery(Connection conn) throws Exception;

  /**
   * INTERNAL: Execute the statement with parameters and return a JDBC
   * result set.
   */  
  public ResultSet executeQuery(Connection conn, Object[] params) throws Exception;

  /**
   * INTERNAL: Execute the statement with parameters and return a JDBC
   * result set.
   */  
  public ResultSet executeQuery(Connection conn, Map params) throws Exception;
  
  /**
   * INTERNAL: Read the field value of the specified index from the
   * current row in the result set.<p>
   *
   * If the field is an identity field or a reference field, the
   * object identity will be extracted and the identity will be used
   * to look up the object in the transaction.<p>
   */
  public Object readValue(TicketIF ticket, ResultSet rs, int index, boolean lookup_identities) throws Exception;

  /**
   * INTERNAL: Reads all the field values from the current row into
   * the specified value array. Note that this array must have a width
   * that is equal or greater than the width of the result.<p>
   */  
  public Object[] readValues(TicketIF ticket, ResultSet rs, Object[] values, boolean lookup_identities) throws Exception;
  
  /**
   * INTERNAL: Reads all the field values from the current row in the
   * result set.<p>
   */  
  public Object[] readValues(TicketIF ticket, ResultSet rs, boolean lookup_identities) throws Exception;
  
}





