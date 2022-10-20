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
import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;

import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Class that performs the task of accessing and
 * manipulating "collection" type instances in the database.<p>
 *
 * NOTE: Collection type instances must have an identity field and
 * exactly one value field.<p>
 */

public class SQLCollectionAccess implements ClassAccessIF {
  private static final String EXECUTING = "Executing: ";

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(SQLCollectionAccess.class.getName());
  protected boolean debug = log.isDebugEnabled();

  protected RDBMSAccess access;  
  protected ClassInfoIF cinfo;
  
  protected String sql_load;
  protected String sql_add;
  protected String sql_remove;
  protected String sql_delete;
  
  protected FieldInfoIF identity_field;
  protected FieldInfoIF value_field;

  public SQLCollectionAccess(RDBMSAccess access, ClassInfoIF cinfo) {
    // TODO: The load method is identical to the one in
    // SQLOneToOne.java. Should make sure that they are being shared.
    this.access = access;
    this.cinfo = cinfo;

    // Prepare fields
    identity_field = cinfo.getIdentityFieldInfo();
    FieldInfoIF[] value_fields = cinfo.getOne2OneFieldInfos();
    if (value_fields.length != 1) {
      throw new OntopiaRuntimeException("Number of value fields for type " + cinfo.getName() +
                                        " must be 1 not " + value_fields.length + ".");
    }
    value_field = value_fields[0];
    
    // -----------------------------------------------------------------------------
    // Load
    // -----------------------------------------------------------------------------

    // Generate SQL statement
    FieldInfoIF[] fields = new FieldInfoIF[] { identity_field,  value_field };
    sql_load = SQLGenerator.getSelectStatement(cinfo.getMasterTable(),
                                               fields, new FieldInfoIF[] { identity_field }, 0);

    if (debug) {
      // log.debug("Load SQL (1:1) " + field.getName() + ": " + sql_load);
      log.debug("Load SQL (1:1) : " + sql_load);
    }

    // SELECT id, topic_id FROM TM_TOPIC_SET WHERE id = ?

    // -----------------------------------------------------------------------------
    // Add
    // -----------------------------------------------------------------------------

    // Generate SQL statement
    sql_add = SQLGenerator.getInsertStatement(cinfo.getMasterTable(), fields);

    if (debug) {
      log.debug("Create SQL (" + cinfo.getDescriptorClass().getName() + "): " + sql_add);
    }

    // INSERT INTO TM_TOPIC_SET (id, topic_id) VALUES (?, ?)

    // -----------------------------------------------------------------------------
    // Remove
    // -----------------------------------------------------------------------------

    // Generate SQL statement
    sql_remove = SQLGenerator.getDeleteStatement(cinfo.getMasterTable(), fields);

    if (debug) {
      log.debug("Create SQL (" + cinfo.getDescriptorClass().getName() + "): " + sql_remove);
    }

    // DELETE FROM TM_TOPIC_SET WHERE id = ? and topic_id = ?

    // -----------------------------------------------------------------------------
    // Delete
    // -----------------------------------------------------------------------------
    
    // Generate SQL statement
    sql_delete = SQLGenerator.getDeleteStatement(cinfo.getMasterTable(), new FieldInfoIF[] { identity_field });

    if (debug) {
      log.debug("Delete SQL (" + cinfo.getDescriptorClass().getName() + "): " + sql_delete);
    }

    // DELETE FROM TM_TOPIC_SET WHERE id = ?

  }
  
  // -----------------------------------------------------------------------------
  // Load
  
  @Override
  public boolean load(AccessRegistrarIF registrar, IdentityIF identity) throws Exception {
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
        log.debug(EXECUTING + sql_load);
      }
      ResultSet rs = stm.executeQuery();

      // Initialize collection value
      Collection values = new HashSet();      

      // Load row data (skip identity column)
      int rsindex = 1 + identity_field.getColumnCount();

      // Load collection values
      while (rs.next()) {
        values.add(value_field.load(registrar, ticket, rs, rsindex, false));
      }
      
      // Register object identity with registrar
      registrar.registerIdentity(ticket, identity);
      // Update cache
      registrar.registerField(ticket, identity, value_field.getIndex(), values);
      //! System.out.println("COLLECTION LOADED: " + identity + " " + value_field.getIndex() + " : " + values);

      // Close result set
      rs.close();

      return true;      
    } finally {
      if (stm != null) {
        stm.close();
      }
    }
  }

  // -----------------------------------------------------------------------------
  // Load field
  
  @Override
  public Object loadField(AccessRegistrarIF registrar, IdentityIF identity, int field) {
    throw new UnsupportedOperationException("Persistent collections have no fields.");
  }

  @Override
  public Object loadFieldMultiple(AccessRegistrarIF registrar, Collection identities, 
          IdentityIF current, int field) {
    throw new UnsupportedOperationException("Persistent collections have no fields.");
  }
  
  // -----------------------------------------------------------------------------
  // Create

  @Override
  public void create(ObjectAccessIF oaccess, Object object) throws Exception {
    // Make sure trackable is in initialized state
    TrackableCollectionIF trackcoll = (TrackableCollectionIF)object;
    trackcoll.resetTracking();    
    // Store all collection elements
    if (!trackcoll.isEmpty()) {
      storeAdded(oaccess, oaccess.getIdentity(object), trackcoll);
    }
  }

  // -----------------------------------------------------------------------------
  // Delete
  
  @Override
  public void delete(ObjectAccessIF oaccess, Object object) throws Exception {
    // NOTE: Deletes all collection elements.
    
    // Prepare statement
    PreparedStatement stm = access.prepareStatement(sql_delete);
    try {

      // Bind parameters
      bindParametersDelete(stm, oaccess.getIdentity(object));
      
      // Execute statement
      if (debug) {
        log.debug(EXECUTING + sql_delete);
      }
      stm.executeUpdate();
    } finally {
      if (stm != null) {
        stm.close();
      }
    }
  }
  
  protected void bindParametersDelete(PreparedStatement stm, IdentityIF identity) throws Exception {            
    // Bind identity columns
    if (debug) {
      log.debug("Binding object identity: " + identity);
    }
    identity_field.bind(identity, stm, 1);
  }

  // -----------------------------------------------------------------------------
  // Store dirty

  @Override
  public void storeDirty(ObjectAccessIF oaccess, Object object) throws Exception {    
    // Store changes
    TrackableCollectionIF trackcoll = (TrackableCollectionIF)object;
    
    // Store added collection elements
    Collection added = trackcoll.getAdded();
    if (added != null && !added.isEmpty()) {
      storeAdded(oaccess, oaccess.getIdentity(object), added);
    }

    // Store removed collection elements
    Collection removed = trackcoll.getRemoved();    
    if (removed != null && !removed.isEmpty()) {
      storeRemoved(oaccess, oaccess.getIdentity(object), removed);
    }

    // Reset trackable collection
    trackcoll.resetTracking();    
  }

  // -----------------------------------------------------------------------------
  // Add collection elements

  protected void storeAdded(ObjectAccessIF oaccess, IdentityIF identity, Collection elements) throws Exception {
    // Prepare statement
    PreparedStatement stm = access.prepareStatement(sql_add);

    //! System.out.println("STORING COLLECTION ELEMENTS: " + identity + " -> " + elements);
    
    // Store elements individually
    try {
      int size = elements.size();
      Iterator iter = elements.iterator();
      for (int i=0; i < size; i++) {
        // Bind parameters
        bindParametersAddRemove(stm, oaccess, identity, iter.next());
        
        // Execute statement
        if (debug) {
          log.debug(EXECUTING + sql_add);
        }
        stm.executeUpdate();
      }   
    } finally {
      if (stm != null) {
        stm.close();
      }
    }      
  }
  
  protected void bindParametersAddRemove(PreparedStatement stm, ObjectAccessIF oaccess,
                                         IdentityIF identity, Object element) throws Exception {

    // Note: The fields map is modified by removing keys of fields
    // that are being inserted at this point. This hack and might be
    // optimized in the future.

    // Bind identity columns
    if (debug) {
      log.debug("Binding object identity: " + identity);
    }    
    identity_field.bind(identity, stm, 1);
    
    // Bind value columns
    int stmindex = 1 + identity_field.getColumnCount();

    // Get field value
    if (element != null && value_field.isReferenceField()) {
      element = oaccess.getIdentity(element);
    }
    // Bind field value
    value_field.bind(element, stm, stmindex);

    //! System.out.println("BINDING: " + identity + " -> " + element);
    
    // TODO: Reset all dirty flags in one go: ObjectAccessIF.setDirty(false).
  }

  // -----------------------------------------------------------------------------
  // Remove collection elements

  protected void storeRemoved(ObjectAccessIF oaccess, IdentityIF identity, Collection elements) throws Exception {
    // Prepare statement
    PreparedStatement stm = access.prepareStatement(sql_remove);

    // Store elements individually
    try {
      int size = elements.size();
      Iterator iter = elements.iterator();
      for (int i=0; i < size; i++) {
        // Bind parameters
        bindParametersAddRemove(stm, oaccess, identity, iter.next());
        
        // Execute statement
        if (debug) {
          log.debug(EXECUTING + sql_remove);
        }
        stm.executeUpdate();
      }   
    } finally {
      if (stm != null) {
        stm.close();
      }
    }      
  }
  
}
