/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
  private static final Logger log = LoggerFactory.getLogger(SQLStatement.class.getName());

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

  @Override
  public void setObjectAccess(ObjectAccessIF oaccess) {
    this.oaccess = oaccess;
  }

  @Override
  public void setAccessRegistrar(AccessRegistrarIF registrar) {
    this.registrar = registrar;
  }

  @Override
  public TicketIF getTicket() {
    return registrar.getTicket();
  }
  
  public void setFetchSize(int fetchSize) {
    this.fetchSize = fetchSize;
  }

  @Override
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
  
  @Override
  public ResultSet executeQuery(Connection conn) throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("Executing: " + sql);
    }
    PreparedStatement stm = conn.prepareStatement(sql);
    if (fetchSize > 0) {
      stm.setFetchSize(fetchSize);
    }
    return stm.executeQuery();
  }
  
  @Override
  public ResultSet executeQuery(Connection conn, Object[] params) throws Exception {
    return param_processor.executeQuery(conn, sql, params);
  }

  @Override
  public ResultSet executeQuery(Connection conn, Map params) throws Exception {
    return param_processor.executeQuery(conn, sql, params);
  }
  
  @Override
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
      if (identity == null) {
        return null;
      }

      // Register identity with access registrar, since field handler
      // don't do this themselves.
      if (registrar != null) {
        registrar.registerIdentity(ticket, identity);
      }
      
      // Note: an exception will be thrown if the object doesn't exist
      // in the database, fortunately it should always exist since we're
      // always going through the local transaction cache.
    
      // Get object with the identity from transaction.
      if (lookup_identity) {
        return oaccess.getObject(identity);
      } else {
        return identity;
      }
      
    } else {
      // Return loaded value as-is.
      return fhandler.load(registrar, ticket, rs, select_offsets[index], false);
    }
  }

  @Override
  public Object[] readValues(TicketIF ticket, ResultSet rs, Object[] values, boolean lookup_identities) throws Exception {
    for (int i=0; i < width; i++) {
      values[i] = readValue(ticket, rs, i, lookup_identities);
    }
    return values;
  }
  
  @Override
  public Object[] readValues(TicketIF ticket, ResultSet rs, boolean lookup_identities) throws Exception {
    Object[] values = new Object[width];
    for (int i=0; i < width; i++) {
      values[i] = readValue(ticket, rs, i, lookup_identities);
    }
    return values;
  }

  @Override
  public String toString() {
    return getSQL();
  }
  
}
