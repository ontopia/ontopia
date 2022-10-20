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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Class that performs the task of accessing and
 * manipulating 1:M reference fields in the database.
 */

public class SQLOneToManyReference implements FieldAccessIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(SQLOneToManyReference.class.getName());
  protected boolean debug = log.isDebugEnabled();

  protected RDBMSAccess access;  
  protected FieldInfoIF field;
  
  protected String sql_load;
  protected String sql_load_multiple;
  protected String sql_add;
  protected String sql_remove;
  protected String sql_clear;
  
  protected FieldInfoIF[] select_fields;
  protected FieldInfoIF identity_field;
  protected FieldInfoIF value_field;

  protected boolean close_stm = true;
  protected int batchSize = SQLObjectAccess.batchSize;
  
  public SQLOneToManyReference(RDBMSAccess access, FieldInfoIF field) {
    this.access = access;
    this.field = field;
    
    // Prepare fields    
    identity_field = field.getParentClassInfo().getIdentityFieldInfo();
    value_field = field.getValueClassInfo().getIdentityFieldInfo();
    select_fields = field.getValueClassInfo().getOne2OneFieldInfos();
  
    // -----------------------------------------------------------------------------
    // Load
    // -----------------------------------------------------------------------------

    // Generate SQL statement
    FieldInfoIF[] fields = FieldUtils.joinFieldInfos(new FieldInfoIF[] { value_field }, select_fields);    
    sql_load = SQLGenerator.getSelectStatement(field.getTable(), FieldUtils.getColumns(fields),
                                               field.getJoinKeys(), 0);      
    if (debug) {
      log.debug("Compiled SQL (load 1:M reference) " + field.getName() + ": " + sql_load);
    }

    sql_load_multiple = SQLGenerator.getSelectStatement(field.getTable(), 
              FieldUtils.joinStrings(field.getJoinKeys(), 
                         FieldUtils.getColumns(fields)),
              field.getJoinKeys(), batchSize);
    if (debug) {
      log.debug("Compiled SQL (load 1:M reference*) " + field.getName() + ": " + sql_load_multiple);
    }
  
    // -----------------------------------------------------------------------------
    // Add
    // -----------------------------------------------------------------------------

    // Generate SQL statement
    sql_add = SQLGenerator.getUpdateStatement(field.getJoinTable(),
                                              field.getJoinKeys(),
                                              field.getValueClassInfo().getIdentityFieldInfo().getValueColumns());
    if (debug) {
      log.debug("Compiled SQL (add 1:M reference) " + field.getName() + ": " + sql_add);
    }

    // update TM_BASE_NAME set topic_id = ? where id = ?
  
    // -----------------------------------------------------------------------------
    // Remove
    // -----------------------------------------------------------------------------

    // Generate SQL statement
    sql_remove = SQLGenerator.getUpdateStatement(field.getJoinTable(),
                                                 field.getJoinKeys(),
                                                 field.getValueClassInfo().getIdentityFieldInfo().getValueColumns());
    if (debug) {
      log.debug("Compiled SQL (remove 1:M reference) " + field.getName() + ": " + sql_remove);
    }

    // update TM_BASE_NAME set topic_id = NULL where id = ?

    // Note: add and remove statements are identical

    // -----------------------------------------------------------------------------
    // Clear
    // -----------------------------------------------------------------------------

    // Generate SQL statement
    sql_clear = SQLGenerator.getUpdateStatement(field.getJoinTable(),
                                                field.getJoinKeys(),
                                                field.getJoinKeys());
    if (debug) {
      log.debug("Compiled SQL (clear 1:M reference) " + field.getName() + ": " + sql_clear);
    }

    // update TM_BASE_NAME set topic_id = NULL where topic_id = ?
    
  }
  
  @Override
  public Object load(AccessRegistrarIF registrar, IdentityIF identity) throws Exception {
    // Prepare result collection
    Collection<IdentityIF> result = new HashSet<IdentityIF>();

    // Get ticket
    TicketIF ticket = registrar.getTicket();
    
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
        // Zero or more rows expected
        while (rs.next()) {
        
          // Load object identity
          IdentityIF value_id = (IdentityIF)value_field.load(registrar, ticket, rs, 1, false);

          // Set column count to identity field width
          int rsindex = 1 + identity_field.getColumnCount();
        
          // Register object identity with registrar
          registrar.registerIdentity(ticket, value_id);
        
          // Load row data
          for (int i=0; i < select_fields.length; i++) {
            // Let field info traverse the result set row
            FieldInfoIF finfo = select_fields[i];
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

          // FIXME: Initialize 1:M and M:M fields
        
          // Add row object to result collection
          result.add(value_id);
        }
      } finally {
        // Close result set
        rs.close();
      }
    } finally {
      //! if (close_stm && stm != null) stm.close();
      if (stm != null) {
        stm.close();
      }
    }
    
    // Update persistence cache
    registrar.registerField(ticket, identity, field.getIndex(), result);

    // Return loaded collection
    return result;
  }
  
  @Override
  public Object loadMultiple(AccessRegistrarIF registrar, Collection<IdentityIF> identities, 
           IdentityIF current) throws Exception {
    // Prepare result collection
    Map<IdentityIF, Collection<IdentityIF>> results = new HashMap<IdentityIF, Collection<IdentityIF>>(identities.size());
    for (IdentityIF identity : identities) {
      results.put(identity, new HashSet<IdentityIF>());
    }

    // Get ticket
    TicketIF ticket = registrar.getTicket();
    
    // Prepare statement
    PreparedStatement stm = access.prepareStatement(sql_load_multiple);
    stm.setFetchSize(100);

    //! Statement stm = access.createStatement();
    //! System.out.println("F1:MR: " + field + " " + field.getParentClassInfo() + " " + field.getValueClassInfo());
    //! String sql = SQLGenerator.processMultipleLoadParameters(identities, sql_load_multiple);
    SQLGenerator.bindMultipleParameters(identities.iterator(), identity_field, stm, batchSize);
    try {
      
      // Execute statement
      if (debug) {
        log.debug("Executing: " + sql_load_multiple);
      }
      //! ResultSet rs = stm.executeQuery(sql);
      ResultSet rs = stm.executeQuery();

      // Zero or more rows expected
      while (rs.next()) {
        // Load object identity
        IdentityIF identity = (IdentityIF)identity_field.load(registrar, ticket, rs, 1, false);

        // Set column count to identity field width
        int rsindex = 1 + identity_field.getColumnCount();
        
        // Load object identity
        IdentityIF value_id = (IdentityIF)value_field.load(registrar, ticket, rs, rsindex, false);

        // Set column count to identity field width
        rsindex += value_field.getColumnCount();
        
        // Register object identity with registrar
        registrar.registerIdentity(ticket, value_id);
        
        // Load row data
        for (int i=0; i < select_fields.length; i++) {
          // Let field info traverse the result set row
          FieldInfoIF finfo = select_fields[i];
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
        // Add row object to result collection
        Collection<IdentityIF> result = results.get(identity);
        result.add(value_id);
      }
      // Close result set
      rs.close();
      
    } finally {
      if (stm != null) {
        stm.close();
      }
    }

    // Update persistence cache
    for (IdentityIF rkey : results.keySet()) {
      registrar.registerField(ticket, rkey, field.getIndex(), results.get(rkey));
    }

    // Return loaded collection
    return results.get(current);
  }
  
  protected void add(IdentityIF identity, Collection<?> values) throws Exception {
    // Prepare statement
    PreparedStatement stm = add_getStatement();
    try {
      
      // Loop over the values
      Iterator<?> iter = values.iterator();
      while (iter.hasNext()) {
        
        // Bind parameters
        add_bindParameters(stm, identity, iter.next());
        
        // Execute statement
        executeUpdate(stm, sql_add);
      }
    } finally {
      if (close_stm && stm != null) {
        stm.close();
      }
    }
  }
  
  protected PreparedStatement add_getStatement() throws SQLException {
    return access.prepareStatement(sql_add);
  }
  
  protected void add_bindParameters(PreparedStatement stm, IdentityIF identity, Object value) throws Exception {
    // Bind identity columns
    if (debug) {
      log.debug("Binding object identity: " + identity);
    }
    identity_field.bind(identity, stm, 1);
    
    // Bind value columns
    if (debug) {
      log.debug("Binding reference identity: " + value);
    }
    field.bind(value, stm, 1 + identity_field.getColumnCount());    
  }
  
  protected void remove(IdentityIF identity, Collection<?> values) throws Exception {
    // Prepare statement
    PreparedStatement stm = remove_getStatement();
    try {
      
      // Loop over the values
      Iterator<?> iter = values.iterator();
      while (iter.hasNext()) {
        
        // Bind parameters
        remove_bindParameters(stm, identity, iter.next());
        
        // Execute statement
        executeUpdate(stm, sql_remove);
      }
    } finally {
      if (close_stm && stm != null) {
        stm.close();
      }
    }
  }
  
  protected PreparedStatement remove_getStatement() throws SQLException {
    return access.prepareStatement(sql_remove);
  }
  
  protected void remove_bindParameters(PreparedStatement stm, IdentityIF identity, Object value) throws Exception {
    // Bind identity columns
    if (debug) {
      log.debug("Binding null identity: " + identity);
    }
    identity_field.bind(null, stm, 1);
    // replace keys with NULLs!
    
    // Bind value columns
    if (debug) {
      log.debug("Binding value: " + value);
    }
    field.bind(value, stm, 1 + identity_field.getColumnCount());
  }
  
  @Override
  public void clear(IdentityIF identity) throws Exception {
    // Prepare statement
    PreparedStatement stm = clear_getStatement();
    try {
        
      // Bind parameters
      clear_bindParameters(stm, identity);
      
      // Execute statement
      executeUpdate(stm, sql_clear);
    } finally {
      if (close_stm && stm != null) {
        stm.close();
      }
    }
  }
  
  protected PreparedStatement clear_getStatement() throws SQLException {
    return access.prepareStatement(sql_clear);
  }
  
  protected void clear_bindParameters(PreparedStatement stm, IdentityIF identity) throws Exception {      
    // Bind null identity columns
    if (debug) {
      log.debug("Binding null identity: " + identity);
    }
    identity_field.bind(null, stm, 1);
    // replace keys with NULLs!
    
    // Bind identity columns
    if (debug) {
      log.debug("Binding identity: " + identity);
    }
    identity_field.bind(identity, stm, 1 + identity_field.getColumnCount());
  }

  // -----------------------------------------------------------------------------
  // Store dirty

  @Override
  public void storeDirty(ObjectAccessIF oaccess, Object object) throws Exception {
    // Get field value
    TrackableCollectionIF value = (TrackableCollectionIF)oaccess.getValue(object, field);

    // Compare added values
    Collection<?> added = value.getAdded();
    Collection<?> removed = value.getRemoved();
    
    // Add added values
    if (added != null && !added.isEmpty()) {
      add(oaccess.getIdentity(object), added);
    }
    
    // Remove removed values
    if (removed != null && !removed.isEmpty()) {
      remove(oaccess.getIdentity(object), removed);
    }
    
    // Reset trackable set
    value.resetTracking();    

    // Mark dirty field as flushed
    oaccess.setDirtyFlushed(object, field.getIndex());
  }

  protected void executeUpdate(PreparedStatement stm, String sql) throws Exception {
    if (debug) {
      log.debug("Executing: " + sql);
    }
    stm.executeUpdate();
  }
  
}
