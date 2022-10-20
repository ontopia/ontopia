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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import net.ontopia.persistence.proxy.TicketIF;

/**
 * INTERNAL: RDBMS query implementation that performs queries that
 * return a single object instance.
 */

public class RDBMSObjectQuery implements DetachedQueryIF {

  protected SQLStatementIF stm;
  protected boolean lookup_identities;
  
  public RDBMSObjectQuery(SQLStatementIF stm, boolean lookup_identities) {
    this.stm = stm;
    this.lookup_identities = lookup_identities;
  }

  @Override
  public Object executeQuery(Connection conn) throws Exception {
    TicketIF ticket = stm.getTicket();
    return processResult(ticket, stm.executeQuery(conn));
  }

  @Override
  public Object executeQuery(Connection conn, Object[] params) throws Exception {
    TicketIF ticket = stm.getTicket();
    return processResult(ticket, stm.executeQuery(conn, params));
  }

  @Override
  public Object executeQuery(Connection conn, Map params) throws Exception {
    TicketIF ticket = stm.getTicket();
    return processResult(ticket, stm.executeQuery(conn, params));
  }

  protected Object processResult(TicketIF ticket, ResultSet rs) throws Exception {    
    try {               
      // Zero or one row expected
      if (rs.next()) {
        // Object was found
        return stm.readValue(ticket, rs, 0, lookup_identities);
      } else {
        // No match
        return null;
      }
      // FIXME: Should we complain when more than one object was
      // found?
      
    } finally {
      Statement _stm = rs.getStatement();
      // Close result set
      rs.close();
      rs = null;
      // Close statement
      if (_stm != null) {
        _stm.close();
      }
    }
  } 
  
}






