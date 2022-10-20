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

package net.ontopia.persistence.proxy;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Class that performs the task of accessing and
 * manipulating M:M reference fields in the database.
 */

public class SQLBatchManyToManyReference extends SQLManyToManyReference implements FlushableIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(SQLBatchManyToManyReference.class.getName());
  protected boolean debug = log.isDebugEnabled();

  protected PreparedStatement stm_add;
  protected PreparedStatement stm_remove;
  protected PreparedStatement stm_clear;
  
  public SQLBatchManyToManyReference(RDBMSAccess access, FieldInfoIF field) {
    super(access, field);
    close_stm = false;
  }
  
  //! protected void add(IdentityIF identity, Object[] values) throws Exception {
  //!   // Get batch statement
  //!   PreparedStatement stm = add_getStatement();
  //! 
  //!   // Loop over the values
  //!   for (int i=0; i < values.length; i++) {
  //!     
  //!     // Bind parameters
  //!     add_bindParameters(stm, identity, values[i]);
  //!     
  //!     // Add batch update
  //!     if (debug) log.debug("Adding batch: " + sql_add);
  //!     stm.addBatch();
  //!   }
  //! }
  
  @Override
  protected PreparedStatement add_getStatement() throws SQLException {
    if (stm_add == null) {
      // Create statement and set statement field
      stm_add = super.add_getStatement();
      // Register as flushable
      access.needsFlushing(this);
    }
    return stm_add;
  }
  
  //! protected void remove(IdentityIF identity, Object[] values) throws Exception {
  //!   // Get batch statement
  //!   PreparedStatement stm = remove_getStatement();
  //! 
  //!   // Loop over the values
  //!   for (int i=0; i < values.length; i++) {
  //!     
  //!     // Bind parameters
  //!     remove_bindParameters(stm, identity, values[i]);
  //!     
  //!     // Add batch update
  //!     if (debug) log.debug("Adding batch: " + sql_remove);
  //!     stm.addBatch();
  //!   }
  //! }
  
  @Override
  protected PreparedStatement remove_getStatement() throws SQLException {
    if (stm_remove == null) {
      // Create statement and set statement field
      stm_remove = super.remove_getStatement();
      // Register as flushable
      access.needsFlushing(this);
    }
    return stm_remove;
  }
  
  //! public void clear(IdentityIF identity) throws Exception {
  //!   // Get batch statement
  //!   PreparedStatement stm = clear_getStatement();
  //! 
  //!   // Bind parameters
  //!   clear_bindParameters(stm, identity);
  //!     
  //!   // Add batch update
  //!   if (debug) log.debug("Adding batch: " + sql_clear);
  //!   stm.addBatch();    
  //! }
  
  @Override
  protected PreparedStatement clear_getStatement() throws SQLException {
    if (stm_clear == null) {
      // Create statement and set statement field
      stm_clear = super.clear_getStatement();
      // Register as flushable
      access.needsFlushing(this);
    }
    return stm_clear;
  }
  
  @Override
  protected void executeUpdate(PreparedStatement stm, String sql) throws Exception {
    // Add batch update
    if (debug) {
      log.debug("Adding batch: " + sql);
    }
    stm.addBatch();
  }
  
  @Override
  public void flush() throws Exception {
    // Handle add batch
    if (stm_add != null) {
      try {
        // Execute batch statements
        stm_add.executeBatch();
      } finally {
        stm_add.close();
        stm_add = null;
      }
    }

    // Handle remove batch
    if (stm_remove != null) {
      try {
        // Execute batch statements
        stm_remove.executeBatch();
      } finally {
        stm_remove.close();
        stm_remove = null;
      }
    }

    // Handle clear batch
    if (stm_clear != null) {
      try {
        // Execute batch statements
        stm_clear.executeBatch();
      } finally {
        stm_clear.close();
        stm_clear = null;
      }
    }
  }
  
}






