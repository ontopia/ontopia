// $Id: SQLStatement.java,v 1.36 2008/04/10 08:04:25 geir.gronmo Exp $

package net.ontopia.persistence.query.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import net.ontopia.persistence.proxy.AccessRegistrarIF;
import net.ontopia.persistence.proxy.FieldHandlerIF;
import net.ontopia.persistence.proxy.FieldUtils;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.ObjectAccessIF;
import net.ontopia.persistence.proxy.TicketIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * INTERNAL: The default SQL statement implementation.
 */

public class SQLStatement implements SQLStatementIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(SQLStatement.class.getName());

  // TODO: Would be useful to get at parameter information from outside.
  
  protected ObjectAccessIF oaccess;
  protected AccessRegistrarIF registrar;
  
  protected String sql;
  protected int fetchSize;
  protected int width;
  protected int[] select_offsets;
  protected FieldHandlerIF[] select_fields;
  protected ParameterProcessorIF param_processor;
  
  public SQLStatement(String sql, FieldHandlerIF[] select_fields,
                      ParameterProcessorIF param_processor) {
    this.sql = sql;
    this.width = select_fields.length;
    
    // Field handlers
    this.select_fields = select_fields;
    this.param_processor = param_processor;
    
    // Figure out select offsets
    this.select_offsets = FieldUtils.getResultSetOffsets(select_fields);
  }

  public void setObjectAccess(ObjectAccessIF oaccess) {
    this.oaccess = oaccess;
  }

  public void setAccessRegistrar(AccessRegistrarIF registrar) {
    this.registrar = registrar;
  }

  public TicketIF getTicket() {
    return registrar.getTicket();
  }
  
  public void setFetchSize(int fetchSize) {
    this.fetchSize = fetchSize;
  }

  public int getWidth() {
    return width;
  }

  public String getSQL() {
    return sql;
  }

  //! public int getSelectOffsets() {
  //!   return select_offsets;
  //! }
  //! 
  //! public FieldHandlerIF[] getSelectFields() {
  //!   return select_fields;
  //! }
  
  public ResultSet executeQuery(Connection conn) throws Exception {
    if (log.isDebugEnabled()) log.debug("Executing: " + sql);
    PreparedStatement stm = conn.prepareStatement(sql);
    if (fetchSize > 0) stm.setFetchSize(fetchSize);
    return stm.executeQuery();
  }
  
  public ResultSet executeQuery(Connection conn, Object[] params) throws Exception {
    return param_processor.executeQuery(conn, sql, params);
  }

  public ResultSet executeQuery(Connection conn, Map params) throws Exception {
    return param_processor.executeQuery(conn, sql, params);
  }
  
  public Object readValue(TicketIF ticket, ResultSet rs, int index, boolean lookup_identity) throws Exception {
              
    // FIXME: Should possibly skip the inline data retrieval if
    // instance already exist.

    // FIXME: The transaction object lookup could perhaps be delegated
    // to the field handler. That might lead to performance
    // improvements.
    
    // Get appropriate field handler
    FieldHandlerIF fhandler = select_fields[index];
    
    // Load field value
    if (fhandler.isIdentityField()) {
      // Expect object identity
      IdentityIF identity = (IdentityIF)fhandler.load(registrar, ticket, rs, select_offsets[index], false);

      // If value was null return immediately
      if (identity == null) return null;

      // Register identity with access registrar, since field handler
      // don't do this themselves.
      if (registrar != null) registrar.registerIdentity(ticket, identity);
      
      // Note: an exception will be thrown if the object doesn't exist
      // in the database, fortunately it should always exist since we're
      // always going through the local transaction cache.
    
      // Get object with the identity from transaction.
      if (lookup_identity)
        return oaccess.getObject(identity);
      else
        return identity;
      
    } else {
      // Return loaded value as-is.
      return fhandler.load(registrar, ticket, rs, select_offsets[index], false);
    }
  }

  public Object[] readValues(TicketIF ticket, ResultSet rs, Object[] values, boolean lookup_identities) throws Exception {
    for (int i=0; i < width; i++) {
      values[i] = readValue(ticket, rs, i, lookup_identities);
    }
    return values;
  }
  
  public Object[] readValues(TicketIF ticket, ResultSet rs, boolean lookup_identities) throws Exception {
    Object[] values = new Object[width];
    for (int i=0; i < width; i++) {
      values[i] = readValue(ticket, rs, i, lookup_identities);
    }
    return values;
  }

  public String toString() {
    return getSQL();
  }
  
}
