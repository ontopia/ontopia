
// $Id: SQLBatchObjectAccess.java,v 1.5 2005/10/24 11:12:03 grove Exp $

package net.ontopia.persistence.proxy;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.ontopia.utils.OntopiaRuntimeException;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Class that performs the task of creating new objects in
 * the database.
 */

public class SQLBatchObjectAccess extends SQLObjectAccess implements FlushableIF {

  // Define a logging category.
  static Logger log = Logger.getLogger(SQLBatchObjectAccess.class.getName());
  protected boolean debug = log.isDebugEnabled();

  protected PreparedStatement stm_create;
  protected PreparedStatement stm_delete;
  
  public SQLBatchObjectAccess(RDBMSAccess access, ClassInfoIF cinfo) {
    super(access, cinfo);
  }
  
  protected FieldAccessIF getFieldAccess(int field) {
    // Create a field access instance for the given field
    FieldInfoIF finfo = value_fields[field];
    switch (finfo.getCardinality()) {
    case FieldInfoIF.ONE_TO_ONE:
      return new SQLBatchOneToOne(access, finfo);
    case FieldInfoIF.ONE_TO_MANY:
      if (finfo.isAggregateField())
        return new SQLBatchOneToManyAggregate(access, finfo);
      else
        return new SQLBatchOneToManyReference(access, finfo);
    case FieldInfoIF.MANY_TO_MANY:
      return new SQLBatchManyToManyReference(access, finfo);
    default:
      throw new OntopiaRuntimeException("Unknown field cardinality: " + finfo.getCardinality());
    }
  }
  
  public void flush() throws Exception {
    // Do nothing if no statement
    if (stm_create != null) {
      try {
        // if (debug) log.debug("Flushing batch: " + sql_create);
        // Execute batch statements
        stm_create.executeBatch();
      } finally {
        stm_create.close();
        stm_create = null;
      }
    }
    // Do nothing if no statement
    if (stm_delete != null) {
      try {
        // Execute batch statements
        stm_delete.executeBatch();
      } finally {
        stm_delete.close();
        stm_delete = null;
      }
    }
  }
  
  // -----------------------------------------------------------------------------
  // Create

  /**
   * INTERNAL: Creates the new object identity in the database.
   */
  public void create(ObjectAccessIF oaccess, Object object) throws Exception {
    // Get batch statement
    PreparedStatement stm = get_createStatement();

    // Bind parameters
    bindParametersCreate(stm, oaccess, object);

    // Add batch update
    if (debug) log.debug("Adding batch: " + sql_create);
    stm.addBatch();      
  }

  protected PreparedStatement get_createStatement() throws SQLException {
    if (stm_create == null) {
      // Create statement and set statement field
      stm_create = access.prepareStatement(sql_create);
      // Register as flushable
      access.needsFlushing(this);
    }
    return stm_create;
  }
  
  // -----------------------------------------------------------------------------
  // Delete
  /**
   * INTERNAL: Deletes the object identity in the database.
   */
  public void delete(IdentityIF identity) throws Exception {

    // Clear 1:M and M:M fields
    clearFields(identity);
    
    // Prepare statement
    PreparedStatement stm = get_deleteStatement();

    // Bind parameters
    bindParametersDelete(stm, identity);

    // Add batch update
    if (debug) log.debug("Adding batch: " + sql_delete);
    stm.addBatch();
  }

  protected PreparedStatement get_deleteStatement() throws SQLException {
    if (stm_delete == null) {
      // Create statement and set statement field
      stm_delete = access.prepareStatement(sql_delete);
      // Register as flushable
      access.needsFlushing(this);
    }
    return stm_delete;
  }
  
}
