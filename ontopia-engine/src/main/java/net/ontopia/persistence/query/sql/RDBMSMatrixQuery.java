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
import java.util.Map;

import net.ontopia.persistence.proxy.TicketIF;

/**
 * INTERNAL: RDBMS query implementation that performs queries that
 * return an instance of QueryResultIF.
 */

public class RDBMSMatrixQuery implements DetachedQueryIF {

  protected SQLStatementIF stm;
  protected boolean lookup_identities;

  public RDBMSMatrixQuery(SQLStatementIF stm, boolean lookup_identities) {
    this.stm = stm;
    this.lookup_identities = lookup_identities;
  }

  @Override
  public Object executeQuery(Connection conn) throws Exception {
    TicketIF ticket = stm.getTicket();
    return new RDBMSQueryResult(stm, ticket, stm.executeQuery(conn), lookup_identities);
  }

  @Override
  public Object executeQuery(Connection conn, Object[] params) throws Exception {    
    TicketIF ticket = stm.getTicket();
    return new RDBMSQueryResult(stm, ticket, stm.executeQuery(conn, params), lookup_identities);
  } 

  @Override
  public Object executeQuery(Connection conn, Map params) throws Exception {    
    TicketIF ticket = stm.getTicket();
    return new RDBMSQueryResult(stm, ticket, stm.executeQuery(conn, params), lookup_identities);
  } 

  @Override
  public String toString() {    
    return "RMQ: " + stm;
  }
  
}
