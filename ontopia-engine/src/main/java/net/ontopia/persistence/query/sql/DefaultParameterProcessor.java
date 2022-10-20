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
import java.sql.SQLException;
import java.util.Map;

import net.ontopia.persistence.proxy.FieldHandlerIF;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Parameter processor that binds parameters to SQL
 * statements without any special preprocessing.
 */

public class DefaultParameterProcessor implements ParameterProcessorIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(DefaultParameterProcessor.class.getName());

  protected FieldHandlerIF[] param_fields;
  protected String[] param_names;
  protected int fetchSize;

  public DefaultParameterProcessor(FieldHandlerIF[] param_fields, String[] param_names) {
    this.param_fields = param_fields;
    this.param_names = param_names;
  }

  public int getFetchSize() {
    return fetchSize;
  }
  
  public void setFetchSize(int fetchSize) {
    this.fetchSize = fetchSize;
  }

  @Override
  public ResultSet executeQuery(Connection conn, String sql, Map params) throws SQLException {
    if (param_names == null) {
      throw new OntopiaRuntimeException("Cannot use named parameters when query not defined with parameter names.");
    }
    // Map parameters into parameter array
    Object[] _params = new Object[param_names.length];
    for (int i=0; i < _params.length; i++) {
      _params[i] = params.get(param_names[i]);
    }
    // Delegate execution to array method
    return executeQuery(conn, sql, _params);
  }
  
  @Override
  public ResultSet executeQuery(Connection conn, String sql, Object[] params) throws SQLException {
    
    if (log.isDebugEnabled()) {
      log.debug("Executing: " + sql);
    }    
    // FIXME: Should we keep a reference to the prepared statement?
    PreparedStatement stm = conn.prepareStatement(sql);
    if (fetchSize > 0) {
      stm.setFetchSize(fetchSize);
    }

    // Bind query parameters
    int offset = 1;
    for (int i=0; i < params.length; i++) {
      param_fields[i].bind(params[i], stm, offset);
      offset += param_fields[i].getColumnCount();
    }
    return stm.executeQuery();
  }
  
}
