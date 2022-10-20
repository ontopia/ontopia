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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Class that performs the task of accessing and
 * manipulating 1:1 fields in the database.
 */

public class SQLOneToOne implements FieldAccessIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(SQLOneToOne.class.getName());
  protected boolean debug = log.isDebugEnabled();

  protected RDBMSAccess access;  
  
  protected String sql_load;
  protected String sql_load_multiple;
  protected String sql_set;
  
  protected FieldInfoIF identity_field;
  protected FieldInfoIF[] select_fields;
  protected FieldInfoIF[] select_fields_ref;
  protected FieldInfoIF value_field;

  protected FieldInfoIF field;
  protected int select_value_index = -1;

  protected boolean close_stm = true;
  protected int batchSize = SQLObjectAccess.batchSize;

  public SQLOneToOne(RDBMSAccess access, FieldInfoIF field) {
    this.access = access;
    this.field = field;
    
    // Prepare fields
    identity_field = field.getParentClassInfo().getIdentityFieldInfo();
    select_fields = field.getParentClassInfo().getOne2OneFieldInfos();
    
    // Figure out the index of the value field in the select fields
    // list. We need this index later to figure out the return value.
    for (int i=0; i < select_fields.length; i++) {
      if (field == select_fields[i]) {
        select_value_index = i;
      }
    }
    if (select_value_index == -1) {
      throw new OntopiaRuntimeException("Could not find value field among 1:1 fields.");
    }
    
    // -----------------------------------------------------------------------------
    // Load
    // -----------------------------------------------------------------------------

    // Generate SQL statement
    FieldInfoIF[] fields = FieldUtils.joinFieldInfos(new FieldInfoIF[] { identity_field }, select_fields);
    sql_load = SQLGenerator.getSelectStatement(field.getParentClassInfo().getMasterTable(),
                                               fields, new FieldInfoIF[] { identity_field }, 0);
    if (debug) {
      log.debug("Compiled SQL (load 1:1) : " + sql_load);
    }

    if (field.isReferenceField()) {

      value_field = field.getValueClassInfo().getIdentityFieldInfo();
      select_fields_ref = field.getValueClassInfo().getOne2OneFieldInfos();

      String jointable = field.getTable();
      String datatable = field.getValueClassInfo().getMasterTable();
      String[] jointable_keys = field.getValueColumns();
      String[] datatable_keys = field.getValueClassInfo().getIdentityFieldInfo().getValueColumns();    
      String[] datatable_select_columns =
        FieldUtils.joinStrings(field.getValueClassInfo().getIdentityFieldInfo().getValueColumns(),
                               FieldUtils.getColumns(field.getValueClassInfo().getOne2OneFieldInfos()));
      String[] jointable_where_columns = field.getParentClassInfo().getIdentityFieldInfo().getValueColumns();

      sql_load_multiple = SQLGenerator.getSelectStatement(jointable, datatable,
                                                          jointable_keys, datatable_keys,
                                                          datatable_select_columns, jointable_where_columns, batchSize);

    } else {      
      sql_load_multiple = SQLGenerator.getSelectStatement(field.getParentClassInfo().getMasterTable(),
                                                          fields, new FieldInfoIF[] { identity_field }, batchSize);
    }

    if (debug) {
      log.debug("Compiled SQL (load 1:1*) : " + sql_load_multiple);
    }

    // Selecting all 1:1 fields
    // SELECT id, topicmap_id, subject_notation, subject_address FROM TM_TOPIC WHERE id = ?

    // -----------------------------------------------------------------------------
    // Set
    // -----------------------------------------------------------------------------

    // TODO: Set individual field values only when there are few of
    // them. Add a treshold property.
    
    // Generate SQL statement
    //! sql_set = SQLGenerator.getUpdateStatement(field.getTable(),
    //!                                           new FieldInfoIF[] { field },
    //!                                           new FieldInfoIF[] { identity_field });
    sql_set = SQLGenerator.getUpdateStatement(field.getTable(),
                                              select_fields,
                                              new FieldInfoIF[] { identity_field });
    if (debug) {
      log.debug("Compiled SQL (set 1:1) " + field.getName() + ": " + sql_set);
    }

    // UPDATE TM_TOPIC set subject_notation = ? where id = ?
  }
  
  @Override
  public Object load(AccessRegistrarIF registrar, IdentityIF identity) throws Exception {
    // Prepare result value
    Object result = null;

    // Get ticket
    TicketIF ticket = registrar.getTicket();
    Exception exception = null;
    
    // Prepare statement
    PreparedStatement stm = access.prepareStatement(sql_load);
    try {
      
      // Bind identity columns
      if (debug) {
        log.debug("Binding object identity: " + identity);
      }
      identity_field.bind(identity, stm, 1);
      
      // Execute statement
      if (debug) {
        log.debug("Executing: " + sql_load);
      }
      ResultSet rs = stm.executeQuery();
      try {
        // Exactly one row expected
        if (rs.next()) {
        
          // Register object identity with registrar
          registrar.registerIdentity(ticket, identity);
        
          // Load row data (skip identity column)
          int rsindex = 1 + identity_field.getColumnCount();
          for (int i=0; i < select_fields.length; i++) {
            // Let field info traverse the result set row
            FieldInfoIF finfo = select_fields[i];
            // Load field value
            Object value = finfo.load(registrar, ticket, rs, rsindex, false);
            if (i == select_value_index) {
              result = value;
            }

            if (value instanceof OnDemandValue) {
              OnDemandValue odv = (OnDemandValue)value;
              odv.setContext(identity, finfo);
            }
            
            // Update persistence cache
            registrar.registerField(ticket, identity, finfo.getIndex(), value);

            // Register value identity with registrar
            if (value != null && finfo.isReferenceField()) {
              registrar.registerIdentity(ticket, (IdentityIF)value);
            }
    
            // Increment column index
            rsindex += finfo.getColumnCount();    
          }
        } else {
          // No rows were found.
          exception = new IdentityNotFoundException(identity);
        }
      } finally {
        // Close result set
        rs.close();
      }
    } catch (Exception e) {
      exception = e;
    } finally {
      //! if (close_stm && stm != null) stm.close();
      if (stm != null) {
        stm.close();
      }
    }

    // Return loaded value
    if (exception != null) {
      throw exception;
    }
    return result;
  }

  @Override
  public Object loadMultiple(AccessRegistrarIF registrar, Collection<IdentityIF> identities,
                             IdentityIF current) throws Exception {
    // Prepare result value
    boolean found_current = false;
    Object result = null;

    // Get ticket
    TicketIF ticket = registrar.getTicket();
    Exception exception = null;
    
    // Prepare statement
    PreparedStatement stm = access.prepareStatement(sql_load_multiple);
    stm.setFetchSize(100);

    //! Statement stm = access.createStatement();
    //! System.out.println("F1:1: " + field + " " + field.getParentClassInfo() + " " + field.getValueClassInfo());
    //! String sql = SQLGenerator.processMultipleLoadParameters(identities, sql_load_multiple);
    SQLGenerator.bindMultipleParameters(identities.iterator(), identity_field, stm, batchSize);

    try {
      
      // Execute statement
      if (debug) {
        log.debug("Executing: " + sql_load_multiple);
      }
      //! ResultSet rs = stm.executeQuery(sql);
      ResultSet rs = stm.executeQuery();
      try {
        if (field.isReferenceField()) {
          // Zero or more rows expected
          while (rs.next()) {
            // Load object identity
            IdentityIF identity = (IdentityIF)identity_field.load(registrar, ticket, rs, 1, false);

            if (identity.equals(current)) {
              found_current = true;
            }
    
            // Set column count to identity field width
            int rsindex = 1 + identity_field.getColumnCount();
    
            // Load object identity
            IdentityIF value_id = (IdentityIF)value_field.load(registrar, ticket, rs, rsindex, false);
            if (identity.equals(current)) {
              result = value_id;
            }

            // Set column count to identity field width
            rsindex += value_field.getColumnCount();
    
            // Register object identity with registrar
            registrar.registerIdentity(ticket, value_id);          
            // Update persistence cache
            registrar.registerField(ticket, identity, field.getIndex(), value_id);

            // Load row data (skip identity column)
            for (int i=0; i < select_fields_ref.length; i++) {
              // Let field info traverse the result set row
              FieldInfoIF finfo = select_fields_ref[i];
              // Load field value
              Object value = finfo.load(registrar, ticket, rs, rsindex, false);

              if (value instanceof OnDemandValue) {
                OnDemandValue odv = (OnDemandValue)value;
                odv.setContext(value_id, finfo);
              }
          
              // Update persistence cache
              registrar.registerField(ticket, value_id, finfo.getIndex(), value);

              // Register value identity with registrar
              if (value != null && finfo.isReferenceField()) {
                registrar.registerIdentity(ticket, (IdentityIF)value);
              }
    
              // Increment column index
              rsindex += finfo.getColumnCount();    
            }
          }
        } else {
          // Zero or more rows expected
          while (rs.next()) {
            // Load object identity
            IdentityIF identity = (IdentityIF)identity_field.load(registrar, ticket, rs, 1, false);
        
            // Register object identity with registrar
            registrar.registerIdentity(ticket, identity);

            if (identity.equals(current)) {
              found_current = true;
            }

            // Load row data (skip identity column)
            int rsindex = 1 + identity_field.getColumnCount();
            for (int i=0; i < select_fields.length; i++) {
              // Let field info traverse the result set row
              FieldInfoIF finfo = select_fields[i];
              // Load field value
              Object value = finfo.load(registrar, ticket, rs, rsindex, false);
              if (i == select_value_index && identity.equals(current)) {
                result = value;
              }
          
              // Update persistence cache
              registrar.registerField(ticket, identity, finfo.getIndex(), value);

              // Register value identity with registrar
              if (value != null && finfo.isReferenceField()) {
                registrar.registerIdentity(ticket, (IdentityIF)value);
              }
    
              // Increment column index
              rsindex += finfo.getColumnCount();    
            }
          }
        }

        // current identity was not found
        if (current != null && !found_current) {
          exception = new IdentityNotFoundException(current);
        }
      } finally {     
        // close result set
        rs.close();
      }
    } catch (Exception e) {
      exception = e;
    } finally {
      stm.close();
    }
    
    // return loaded value
    if (exception != null) {
      throw exception;
    }
    return result;
  }

  @Override
  public void clear(IdentityIF identity) throws Exception {
    throw new OntopiaRuntimeException("The clear method is not supported (1:1 relation).");
  }

  // -----------------------------------------------------------------------------
  // Store dirty

  @Override
  public void storeDirty(ObjectAccessIF oaccess, Object object) throws Exception {

    // Update all 1:1 fields

    // Create statement
    PreparedStatement stm = set_getStatement();
    try {
      IdentityIF identity = oaccess.getIdentity(object);
      // Bind parameters
      int offset = 1;
      for (int i=0; i < select_fields.length; i++) {
        // Have to load field here if it is not already loaded.        
        Object value = oaccess.getValue(object, select_fields[i]);
        if (debug) {
          log.debug("Binding value: " + value);
        }
        select_fields[i].bind(value, stm, offset);
        
        offset += select_fields[i].getColumnCount();   
      }
      
      // Bind identity columns
      if (debug) {
        log.debug("Binding object identity: " + identity);
      }
      identity_field.bind(identity, stm, offset);
      
      // Execute statement
      executeUpdate(stm, sql_set);
    } finally {
      if (close_stm && stm != null) {
        stm.close();
      }
    }
    
    // Mark fields as not dirty when all fields have been stored.
    for (int i=0; i < select_fields.length; i++) {
      oaccess.setDirtyFlushed(object, select_fields[i].getIndex());
    }
        
  }
  
  protected PreparedStatement set_getStatement() throws SQLException {
    return access.prepareStatement(sql_set);
  }

  protected void executeUpdate(PreparedStatement stm, String sql) throws Exception {
    if (debug) {
      log.debug("Executing: " + sql);
    }
    stm.executeUpdate();
  }
  
}
