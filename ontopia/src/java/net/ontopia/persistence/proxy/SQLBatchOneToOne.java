// $Id: SQLBatchOneToOne.java,v 1.10 2005/10/24 11:12:03 grove Exp $

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
  static Logger log = LoggerFactory.getLogger(SQLBatchOneToOne.class.getName());
  protected boolean debug = log.isDebugEnabled();

  protected PreparedStatement stm_set;
  
  public SQLBatchOneToOne(RDBMSAccess access, FieldInfoIF field) {
    super(access, field);
    close_stm = false;
  }

  protected PreparedStatement set_getStatement() throws SQLException {
    if (stm_set == null) {
      // Create statement and set statement field
      stm_set = super.set_getStatement();
      // Register as flushable
      access.needsFlushing(this);
    }
    return stm_set;
  }
  
  protected void executeUpdate(PreparedStatement stm, String sql) throws Exception {
    // Add batch update
    if (debug) log.debug("Adding batch: " + sql);
    stm.addBatch();
  }
  
  public void flush() throws Exception {
    // Do nothing if no statement
    if (stm_set == null) return;

    try {
      // Execute batch statements
      stm_set.executeBatch();
    } finally {
      stm_set.close();
      stm_set = null;
    }
  }
  
}






