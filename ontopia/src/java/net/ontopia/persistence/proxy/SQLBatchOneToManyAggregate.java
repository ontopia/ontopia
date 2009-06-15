// $Id: SQLBatchOneToManyAggregate.java,v 1.9 2005/10/24 11:12:03 grove Exp $

package net.ontopia.persistence.proxy;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Class that performs the task of accessing and
 * manipulating 1:M aggregate fields in the database.
 */

public class SQLBatchOneToManyAggregate extends SQLOneToManyAggregate implements FlushableIF {

  // Define a logging category.
  static Logger log = Logger.getLogger(SQLBatchOneToManyAggregate.class.getName());
  protected boolean debug = log.isDebugEnabled();

  protected PreparedStatement stm_add;
  protected PreparedStatement stm_remove;
  protected PreparedStatement stm_clear;
  
  public SQLBatchOneToManyAggregate(RDBMSAccess access, FieldInfoIF field) {
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
  
  protected PreparedStatement clear_getStatement() throws SQLException {
    if (stm_clear == null) {
      // Create statement and set statement field
      stm_clear = super.clear_getStatement();
      // Register as flushable
      access.needsFlushing(this);
    }
    return stm_clear;
  }
  
  protected void executeUpdate(PreparedStatement stm, String sql) throws Exception {
    // Add batch update
    if (debug) log.debug("Adding batch: " + sql);
    stm.addBatch();
  }
  
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






