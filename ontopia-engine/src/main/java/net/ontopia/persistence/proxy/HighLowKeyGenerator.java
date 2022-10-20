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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import net.ontopia.utils.OntopiaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A key generator using the HIGH/LOW key generator
 * algorithm. It maintains the current counters in a counter
 * table. The key generator is able to preallocate a number of
 * identities which it hand out without having to go the database for
 * every new identity needed. It is used by the RDBMS proxy
 * implementation.
 */

public final class HighLowKeyGenerator implements KeyGeneratorIF {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(HighLowKeyGenerator.class.getName());
  
  protected ConnectionFactoryIF connfactory;
  
  protected String table;
  protected String keycol;
  protected String valcol;
  protected int grabsize;
  protected String global_entry;
  protected String database;
  protected Map<String, String> properties;
  
  protected long value;
  protected long max_value;
  
  public HighLowKeyGenerator(ConnectionFactoryIF connfactory,
      String table, String keycol, String valcol,
      String global_entry, int grabsize, String database, Map<String, String> properties) {
    
    this.connfactory = connfactory;
    
    // NOTE: should probably just use a Properties/Map object.
    this.table = table;
    this.keycol = keycol;
    this.valcol = valcol;
    this.global_entry = global_entry;
    this.grabsize = grabsize;
    this.database = database;
    this.properties = properties;
    
    // Initialize counters so that they get set when first accessed
    value = -1;
    max_value = -1;
  }
  
  @Override
  public synchronized IdentityIF generateKey(Class<?> type) {
    
    // If we've used up the reserved interval fetch a new one from the database.
    if (value >= max_value) {
      return new LongIdentity(type, incrementInDatabase(type));
    } else {
      // Increment and return
      return new LongIdentity(type, ++value);
    }
  }
  
  /**
   * INTERNAL: Sends a request to the database to retrieve the current
   * counter value. The counter value row is then locked, the counter
   * incremented and the new value stored.
   */
  protected long incrementInDatabase(Object type) {
    
    // Read current counter value from key generator table.
    long current_value;
    long new_value;
    
    String entry;
    if (global_entry != null) {
      entry = global_entry;
    } else {
      //! entry = type.getName();
      throw new UnsupportedOperationException("Named key generators are not yet supported.");
    }
    
    
    // Get key generator row locking keyword (e.g. 'for update')
    String lkw = properties.get("net.ontopia.topicmaps.impl.rdbms.HighLowKeyGenerator.SelectSuffix");    
    
    // The row should normally be should locked while updating, but
    // not all databases support this.
    
    String sql_select;
    if (lkw == null && ("sqlserver".equals(database))) {
      sql_select = "select " + valcol + " from " + table + " with (XLOCK) where " + keycol + " = ?";
      
    } else {
      if (lkw == null) {      
        if ("sapdb".equals(database)) {
          lkw = "with lock";
        } else {
          lkw = "for update";
        }
      }
      sql_select = "select " + valcol + " from " + table + " where " + keycol + " = ? " + lkw;
    }
    
    if (log.isDebugEnabled()) {
      log.debug("KeyGenerator: retrieving: " + sql_select);
    }
    
    // Request new database connection
    Connection conn = null;
    
    try {
      conn = connfactory.requestConnection();
      
      PreparedStatement stm1 = conn.prepareStatement(sql_select);
      try {
        stm1.setString(1, entry);
        ResultSet rs = stm1.executeQuery();
        
        if (!rs.next()) {
          throw new OntopiaRuntimeException("HIGH/LOW key generator table '" + table + 
          "' not initialized (no rows).");
        }
        
        // Get value from result set and close it.
        current_value = rs.getLong(1);
        
        rs.close();        
      } finally {
        stm1.close();        
      }
      
      // Increment current value in the database
      new_value = current_value + grabsize;
      
      String sql_update = "update " + table + " set " + valcol + " = ? where " + keycol + " = ?";
      if (log.isDebugEnabled()) {
        log.debug("KeyGenerator: incrementing: " + sql_update);
      }

      PreparedStatement stm2 = conn.prepareStatement(sql_update);
      try {
        stm2.setLong(1, new_value);
        stm2.setString(2, entry);
        stm2.executeUpdate();      
      } finally {
        stm2.close();
      }
      
      // commit transaction
      conn.commit();
      
    } catch (SQLException e) {
      try {
        if (conn != null) {
          conn.rollback();
        }
      } catch (SQLException e2) {
        // Ignore, we're already in trouble.
      }
      throw new OntopiaRuntimeException(e);
    } finally {
      if (conn != null) {
        try {
          // Close/release connection
          conn.close();
        } catch (Exception e) {
          throw new OntopiaRuntimeException(e);
        }
      }
    }
    
    // Set counters
    value = current_value + 1;
    max_value = new_value;
    
    return value;
  }
  
}
