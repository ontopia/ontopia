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
 * manipulating 1:1 fields in the database.
 */

public class SQLBatchOneToOne extends SQLOneToOne implements FlushableIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(SQLBatchOneToOne.class.getName());
  protected boolean debug = log.isDebugEnabled();

  protected PreparedStatement stm_set;
  
  public SQLBatchOneToOne(RDBMSAccess access, FieldInfoIF field) {
    super(access, field);
    close_stm = false;
  }

  @Override
  protected PreparedStatement set_getStatement() throws SQLException {
    if (stm_set == null) {
      // Create statement and set statement field
      stm_set = super.set_getStatement();
      // Register as flushable
      access.needsFlushing(this);
    }
    return stm_set;
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
    // Do nothing if no statement
    if (stm_set == null) {
      return;
    }

    try {
      // Execute batch statements
      stm_set.executeBatch();
    } finally {
      stm_set.close();
      stm_set = null;
    }
  }
  
}






