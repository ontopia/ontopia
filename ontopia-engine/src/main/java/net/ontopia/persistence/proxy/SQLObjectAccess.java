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

import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Class that performs the task of accessing and
 * manipulating "identifiable object type" instances in the database.
 */

public class SQLObjectAccess implements ClassAccessIF {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(SQLObjectAccess.class.getName());

  public static int batchSize = 50;

  protected boolean debug = log.isDebugEnabled();

  protected RDBMSAccess access;  
  protected ClassInfoIF cinfo;
  
  protected String sql_load;
  protected String sql_create;
  protected String sql_delete;
  
  protected FieldInfoIF identity_field;
  protected FieldInfoIF[] value_fields;

  protected FieldInfoIF[] o2o_fields;
  protected FieldInfoIF[] o2a_fields;
  protected FieldInfoIF[] m2m_fields;  

  protected FieldAccessIF[] faccesses;

  public SQLObjectAccess(RDBMSAccess access, ClassInfoIF cinfo) {
    // TODO: The load method is identical to the one in
    // SQLOneToOne.java. Should make sure that they are being shared.
    this.access = access;
    this.cinfo = cinfo;

    // Get 1:M aggregate and M:M reference field infos
    this.o2a_fields = FieldUtils.filterAggregate(cinfo.getOne2ManyFieldInfos());
    this.m2m_fields = cinfo.getMany2ManyFieldInfos();

    // Prepare fields
    identity_field = cinfo.getIdentityFieldInfo();
    value_fields = cinfo.getValueFieldInfos();
    o2o_fields = cinfo.getOne2OneFieldInfos();
    
    // -----------------------------------------------------------------------------
    // Load
    // -----------------------------------------------------------------------------

    // Generate SQL statement
    FieldInfoIF[] fields = FieldUtils.joinFieldInfos(new FieldInfoIF[] { identity_field }, o2o_fields);
    sql_load = SQLGenerator.getSelectStatement(cinfo.getMasterTable(),
                                               fields, new FieldInfoIF[] { identity_field }, 0);

    if (debug) {
      // log.debug("Load SQL (1:1) " + field.getName() + ": " + sql_load);
      log.debug("Load SQL (1:1) : " + sql_load);
    }

    // Selecting all 1:1 fields
    // SELECT id, topicmap_id, subject_notation, subject_address FROM TM_TOPIC WHERE id = ?

    // -----------------------------------------------------------------------------
    // Create
    // -----------------------------------------------------------------------------

    // Generate SQL statement
    //! FieldInfoIF[] fields = FieldUtils.joinFieldInfos(new FieldInfoIF[] { identity_field }, o2o_fields);
    sql_create = SQLGenerator.getInsertStatement(cinfo.getMasterTable(), fields);

    if (debug) {
      log.debug("Create SQL (" + cinfo.getDescriptorClass().getName() + "): " + sql_create);
    }

    // INSERT INTO TM_TOPIC (id, topicmap_id, subject_notation, subject_address) VALUES (?, ?, ?, ?)

    // -----------------------------------------------------------------------------
    // Delete
    // -----------------------------------------------------------------------------
    
    // Generate SQL statement
    sql_delete = SQLGenerator.getDeleteStatement(cinfo.getMasterTable(), new FieldInfoIF[] { identity_field });

    if (debug) {
      log.debug("Delete SQL (" + cinfo.getDescriptorClass().getName() + "): " + sql_delete);
    }

    // DELETE FROM TM_TOPIC WHERE id = ?

    // -----------------------------------------------------------------------------
    // Field accesses handlers
    // -----------------------------------------------------------------------------

    faccesses = new FieldAccessIF[value_fields.length];

    // Loop over the value fields and create field access handlers
    for (int i=0; i < value_fields.length; i++) {
      faccesses[i] = getFieldAccess(i);
    }    
  }

  protected FieldAccessIF getFieldAccess(int field) {
    // Create a field access instance for the given field
    FieldInfoIF finfo = value_fields[field];
    switch (finfo.getCardinality()) {
    case FieldInfoIF.ONE_TO_ONE:
      return new SQLOneToOne(access, finfo);
    case FieldInfoIF.ONE_TO_MANY:
      if (finfo.isAggregateField()) {
        return new SQLOneToManyAggregate(access, finfo);
    } else {
        return new SQLOneToManyReference(access, finfo);
    }
    case FieldInfoIF.MANY_TO_MANY:
      return new SQLManyToManyReference(access, finfo);
    default:
      throw new OntopiaRuntimeException("Unknown field cardinality: " + finfo.getCardinality());
    }
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
      identity_field.bind(identity, stm, 1);
      
      // Execute statement
      if (debug) {
        log.debug("Executing: " + sql_load);
      }
      ResultSet rs = stm.executeQuery();
      
      // Exactly one row expected
      if (rs.next()) {
        
        // Register object identity with registrar
        registrar.registerIdentity(ticket, identity);
        
        // Load row data (skip identity column)
        int rsindex = 1 + identity_field.getColumnCount();
        for (int i=0; i < o2o_fields.length; i++) {
          // Let field info traverse the result set row
          FieldInfoIF finfo = o2o_fields[i];
          // Load field value
          Object value = finfo.load(registrar, ticket, rs, rsindex, false);

          if (value instanceof OnDemandValue) {
            OnDemandValue odv = (OnDemandValue)value;
            odv.setContext(identity, finfo);
          }

          // Update cache
          registrar.registerField(ticket, identity, finfo.getIndex(), value);
          // Increment column index
          rsindex += finfo.getColumnCount();    
        }
        // Close result set
        rs.close();
        return true;
      } else {
        // Close result set
        rs.close();
        // No rows were found.
        return false;
      }
      
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
    try {
      //! System.out.println("LFF:" + field + " " + identity);
      return faccesses[field].load(registrar, identity);
    } catch (IdentityNotFoundException e) {
      throw e;
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public Object loadFieldMultiple(AccessRegistrarIF registrar, Collection<IdentityIF> identities, 
                                  IdentityIF current, int field) {
    try {
      if (identities.size() > batchSize) {
        // split identities into smaller batches that can be handled
        // by underlying data store
        Object result = null;
        Iterator<IdentityIF> iter = identities.iterator();
        while (iter.hasNext()) {
          Collection<IdentityIF> batch = CollectionUtils.nextBatch(iter, batchSize);
          result = faccesses[field].loadMultiple(registrar, batch, current);
        }
        return result;
      } else {
        return faccesses[field].loadMultiple(registrar, identities, current);
      }
    } catch (IdentityNotFoundException e) {
      throw e;
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // -----------------------------------------------------------------------------
  // Create

  @Override
  public void create(ObjectAccessIF oaccess, Object object) throws Exception {
    // Prepare statement
    PreparedStatement stm = access.prepareStatement(sql_create);
    try {

      // Bind parameters
      bindParametersCreate(stm, oaccess, object);
            
      // Execute statement
      if (debug) {
        log.debug("Executing: " + sql_create);
      }
      stm.executeUpdate();
    } finally {
      if (stm != null) {
        stm.close();
      }
    }
      
  }
  
  protected void bindParametersCreate(PreparedStatement stm, ObjectAccessIF oaccess, Object object) throws Exception {

    // Note: The fields map is modified by removing keys of fields
    // that are being inserted at this point. This hack and might be
    // optimized in the future.

    // Bind identity columns
    Object identity = oaccess.getIdentity(object);
    if (debug) {
      log.debug("Binding object identity: " + identity);
    }    
    identity_field.bind(identity, stm, 1);
    
    // Bind value columns
    int stmindex = 1 + identity_field.getColumnCount();
    for (int i=0; i < o2o_fields.length; i++) {
      // Let field infos bind themselves
      FieldInfoIF finfo = o2o_fields[i];
      // Get field value
      Object value = oaccess.getValue(object, finfo);
      // Bind field value
      finfo.bind(value, stm, stmindex);
      // Mark dirty field as flushed
      oaccess.setDirtyFlushed(object, finfo.getIndex());
      // Increment column index
      stmindex += finfo.getColumnCount();
    }
    // TODO: Reset all dirty flags in one go: ObjectAccessIF.setDirtyFlushed(false).
  }

  // -----------------------------------------------------------------------------
  // Delete
  
  @Override
  public void delete(ObjectAccessIF oaccess, Object object) throws Exception {
    IdentityIF identity = oaccess.getIdentity(object);

    // Clear 1:M and M:M fields
    clearFields(identity);
    
    // Prepare statement
    PreparedStatement stm = access.prepareStatement(sql_delete);
    try {

      // Bind parameters
      bindParametersDelete(stm, identity);
      
      // Execute statement
      if (debug) {
        log.debug("Executing: " + sql_delete);
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

  protected void clearFields(IdentityIF identity) throws Exception {
    // Loop over all 1:M aggregate fields and remove their entries (or all in one go)
    for (int i=0; i < o2a_fields.length; i++) {
      log.debug("Deleting 1:M (aggregate) " + o2a_fields[i].getName());
      // FIXME: not neccessary if collection is empty
      faccesses[o2a_fields[i].getIndex()].clear(identity);
    }
    
    // Loop over all M:M fields and remove their entries (or all in one go)
    for (int i=0; i < m2m_fields.length; i++) {
      log.debug("Deleting M:M " + m2m_fields[i].getName());
      // FIXME: not neccessary if collection is empty
      faccesses[m2m_fields[i].getIndex()].clear(identity);
    }    
  }

  // -----------------------------------------------------------------------------
  // Store dirty

  @Override
  public void storeDirty(ObjectAccessIF oaccess, Object object) throws Exception {

    // Loop over dirty fields
    int fcount = value_fields.length;
    int i = 0; // Index of next dirty field
    while (i < fcount) {
      i = oaccess.nextDirty(object, i, fcount);
      if (i == -1) {
        break;
      }

      //! System.out.println(">-  " + i + " " + oaccess.getIdentity(object));

      if (value_fields[i].isReadOnly()) {
        // Mark dirty field as flushed
        oaccess.setDirtyFlushed(object, i);
        
      } else {
        // Update field
        faccesses[i].storeDirty(oaccess, object);
      }
      
      // Prepare for next index
      i++;
    }
    // WARNING: If the FieldAccessIF instances does not reset the
    // dirty flag we may end up looping forever.
  }

}
