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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.persistence.proxy.FieldHandlerIF;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Parameter processor that embeds collection parameters
 * inside SQL statements.
 */

public class CollectionParameterProcessor implements ParameterProcessorIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(CollectionParameterProcessor.class.getName());

  protected FieldHandlerIF[] param_fields;
  protected String[] param_names;
  protected int[] coll_indexes;
  protected int[] param_offsets;

  public CollectionParameterProcessor(FieldHandlerIF[] param_fields, String[] param_names,
                                      int[] coll_indexes, int[] param_offsets) {
    this.param_fields = param_fields;
    this.param_names = param_names;
    this.coll_indexes = coll_indexes;
    this.param_offsets = param_offsets;
  }

  public String process(String sql, Object[] params) {
    // Rewrite SQL statement so that includes the actual number of parameters.
    StringBuilder sb = new StringBuilder();
    int prev = 0;
    for (int i=0; i < coll_indexes.length; i++) {
      int coll_index = coll_indexes[i];
      int param_offset = param_offsets[coll_index];
      Collection coll_param = (Collection)params[coll_index];
      //! System.out.println("COLLECTION PARAM: " + coll_index + ":" + param_offset + " => " + coll_param);
      sb.append(sql.substring(prev, param_offset));

      //! // Embed parameter values inside statement
      //! List values = readFieldValues(coll_param, coll_index);
      //! // FIXME: Handle all SQL types correctly, not just numbers. The solution
      //! // is probably to delegate the string generation to the SQLGenerator.
      //! StringUtils.join(values, ", ", sb);

      // Insert parameter marks in statements
      int psize = coll_param.size() - 1;
      sb.append('?'); // size must be greater than 1
      for (int p=0; p < psize; p++) {
        sb.append(", ");
        sb.append('?');
      }      
      prev = param_offset + 1;
    }
    sb.append(sql.substring(prev, sql.length()));
    return sb.toString();
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
    
    // Embed collection parameters
    String processed_sql = process(sql, params);

    // Prepare statement
    if (log.isDebugEnabled()) {
      log.debug("Executing: " + processed_sql);
    }    
    PreparedStatement stm = conn.prepareStatement(processed_sql);

    // Pull out next collection index
    int coll_idx = 0;
    int next_coll = coll_indexes[coll_idx];
    
    int offset = 1;
    for (int i=0; i < params.length; i++) {
      if (i == next_coll) {
        // Bind individual collection elements
        Collection coll_param = (Collection)params[next_coll];
        Iterator iter = coll_param.iterator();
        while (iter.hasNext()) {
          param_fields[i].bind(iter.next(), stm, offset);
          offset +=  param_fields[i].getColumnCount();
        }
        // Skip to next collection index
        if (coll_idx < coll_indexes.length - 1) {
          coll_idx++;
          next_coll = coll_indexes[coll_idx];
        }
      } else {
        // Bind object parameter
        param_fields[i].bind(params[i], stm, offset);
        offset +=  param_fields[i].getColumnCount();
      }
    }
    return stm.executeQuery();
  }
  
  //! public List readFieldValues(Collection objects, int parameter) {
  //!   FieldHandlerIF param_field = param_fields[parameter];
  //!   List field_values = new ArrayList();
  //!   Iterator iter = objects.iterator();
  //!   while (iter.hasNext()) {
  //!     param_field.retrieveFieldValues(iter.next(), field_values);
  //!   }
  //!   return field_values;
  //! }
  
}
