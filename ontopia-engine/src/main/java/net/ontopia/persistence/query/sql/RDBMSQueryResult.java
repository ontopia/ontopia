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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import net.ontopia.persistence.proxy.QueryResultIF;
import net.ontopia.persistence.proxy.TicketIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: QueryResultIF implementation that wraps a ResultSet.
 */

public class RDBMSQueryResult implements QueryResultIF {

  protected SQLStatementIF stm;
  protected TicketIF ticket;
  protected ResultSet rs;
  protected boolean lookup_identities;
    
  public RDBMSQueryResult(SQLStatementIF stm, TicketIF ticket, ResultSet rs) {
    this(stm, ticket, rs, true);
  }

  public RDBMSQueryResult(SQLStatementIF stm, TicketIF ticket, ResultSet rs, boolean lookup_identities) {
    this.stm = stm;
    this.ticket = ticket;
    this.rs = rs;
    this.lookup_identities = lookup_identities;
  }
  
  @Override
  public int getWidth() {
    return stm.getWidth();
  }

  @Override
  public String[] getColumnNames() {
    try {
      synchronized (this) {
        ResultSetMetaData md = rs.getMetaData();
        String[] colnames = new String[getWidth()];
        for (int i=0; i < colnames.length; i++) {
          colnames[i] = md.getColumnName(i+1);
        }
        return colnames;
      }
    } catch (Exception t) {
      throw new OntopiaRuntimeException(t);
    }
  }

  @Override
  public String getColumnName(int ix) {
    try {
      synchronized (this) {
        ResultSetMetaData md = rs.getMetaData();
        //! return md.getColumnName(ix+1);
        return md.getColumnLabel(ix+1);
      }
    } catch (Exception t) {
      throw new OntopiaRuntimeException(t);
    }
  }
  
  @Override
  public Object getValue(int index) {
    try {
      synchronized (this) {
        return stm.readValue(ticket, rs, index, lookup_identities);
      }
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public Object[] getValues() {
    try {
      synchronized (this) {
        return stm.readValues(ticket, rs, lookup_identities);
      }
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public Object[] getValues(Object[] values) {
    try {
      synchronized (this) {
        return stm.readValues(ticket, rs, values, lookup_identities);
      }
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public boolean next() {
    if (rs == null) {
      return false;
    }
    try {
      synchronized (this) {
        // Skip to next result row
        boolean next = rs.next();
        // If row is not valid, we need to clean up
        if (!next) {
          close();
        }
        return next;      
      }
    } catch (SQLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public void close() {    
    if (rs == null) {
      return;
    }
    try {
      synchronized (this) {
        Statement _stm = rs.getStatement();
        rs.close();
        rs = null;
        if (_stm != null) {
          _stm.close();
        }
      }
    } catch (SQLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  protected void finalize() throws Throwable {
    if (rs != null) {
      close();
    }
  }
  
}
