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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.ontopia.persistence.query.jdo.JDOQuery;
import net.ontopia.utils.OntopiaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A storage access implementation accessing relational
 * databases using JDBC.
 */

public class RDBMSAccess implements StorageAccessIF {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(RDBMSAccess.class.getName());
  
  protected boolean debug = log.isDebugEnabled();
  
  protected String id;
  protected boolean readonly;
  protected RDBMSStorage storage;
  protected RDBMSMapping mapping;
  
  protected Connection connection;

  protected boolean closed;
  
  protected Map<Class<?>, ClassAccessIF> handlers;
  
  protected boolean batch_updates = false;
  protected Collection<FlushableIF> flushable;
  
  public RDBMSAccess(String id, RDBMSStorage storage, boolean readonly) {
    this.id = id;
    this.readonly = readonly;
    this.storage = storage;
    this.mapping = storage.getMapping();
    
    // Enable or disable batch updates
    if (Boolean.parseBoolean(getProperty("net.ontopia.topicmaps.impl.rdbms.BatchUpdates"))) {
      batch_updates = true;
    }
    
    handlers = new HashMap<Class<?>, ClassAccessIF>();
    flushable = new HashSet<FlushableIF>();
    
    // Request new connection from storage
    requestConnectionFromStorage();

    log.debug(getId() + ": Storage access created");    
  }
  
  @Override
  public String getId() {
    return id;
  }
  
  @Override
  public StorageIF getStorage() {
    return storage;
  }

  @Override
  public boolean isReadOnly() {
    return readonly;
  }
  
  @Override
  public String getProperty(String property) {
    return storage.getProperty(property);
  }
  
  // -----------------------------------------------------------------------------
  // RDBMS specific
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Requests a normal connection from the storage, to be used in a transaction.
   */
  private synchronized void requestConnectionFromStorage() {
    try {
      this.connection = storage.getConnectionFactory(readonly).requestConnection();
    } catch (SQLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * INTERNAL: Returns the JDBC database connection used. It is important that 
   * this connection is neither closed, nor commited or rolled back. 
   * If this access has already been closed, a non-transactional read connection is requested
   * from the storage. The connection is validated using validateConnection and renewed if not
   * validated.
   */
  public synchronized Connection getConnection() {
    if (this.connection == null) {
      this.connection = storage.getNonTransactionalReadConnection();
    } else {
      // validate connection is still valid
      if (!validateConnection(this.connection)) {
        // reopen
        if (closed) {
          this.connection = storage.getNonTransactionalReadConnection();
        } else {
          requestConnectionFromStorage();
        }
      }
    }
    storage.touch(this.connection);
    return this.connection;
  }
  
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return getConnection().prepareStatement(sql);
  }
  
  protected boolean isSQLException(Throwable e) {
    if (e == null) {
      return false;
    }
    if (e instanceof SQLException) {
      return true;
    } else if (e instanceof OntopiaRuntimeException) {
      return isSQLException(((OntopiaRuntimeException)e).getCause());
    } else {
      return false;
    }    
  }

  // -----------------------------------------------------------------------------
  // Handlers
  // -----------------------------------------------------------------------------
  
  /**
   * INTERNAL: Gets up the handler class that is used to manage
   * objects of the given class.
   */
  protected ClassAccessIF getHandler(Class<?> type) {
    // Each class have its own handler
    ClassAccessIF handler = handlers.get(type);
    // Create class handlers lazily
    if (handler == null) {
      // TODO: Update class descriptors to no longer depend on java.lang.Class.
      ClassInfoIF cinfo = mapping.getClassInfo(type);
      switch (cinfo.getStructure()) {
      case ClassInfoIF.STRUCTURE_OBJECT: {
        handler = (batch_updates ? new SQLBatchObjectAccess(this, cinfo) :
          new SQLObjectAccess(this, cinfo));
        handlers.put(type, handler);
        break;
      }
      case ClassInfoIF.STRUCTURE_COLLECTION: {
        handler = (batch_updates ? new SQLCollectionAccess(this, cinfo) : // FIXME: implement batch
          new SQLCollectionAccess(this, cinfo));
        handlers.put(type, handler);
        break;      
      }
      default:
        throw new OntopiaRuntimeException("Unsupported ClassInfoIF structure: " + cinfo.getStructure());
      }
    }
    return handler;
  }
  
  // -----------------------------------------------------------------------------
  // Connection validation
  // -----------------------------------------------------------------------------
  
  @Override
  public boolean validate() {
    if (closed) { return false; }
    Connection conn = getConnection();
    return !(closed || (conn == null ? false : !validateConnection(conn)));
  }
  
  protected boolean validateConnection(Connection conn) {
    // stop here if connection says that it's closed 
    try {
      if (conn.isClosed()) {
        return false;
      }
    } catch (SQLException e) {
      return false;
    }
    
    // get validation query
    String vquery = getProperty("net.ontopia.topicmaps.impl.rdbms.ConnectionPool.ValidationQuery");
    if (vquery == null) {
      vquery = "select seq_count from TM_ADMIN_SEQUENCE where 1 != 1";
    }
    
    // run validation query
    PreparedStatement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.prepareStatement(vquery);
      rs = stm.executeQuery();
      // do nothing with result set
      rs.next();
      rs.close();
      
    } catch (SQLException e) {
      return false;
    } finally {
      try {
        if (stm != null) {
          stm.close();
        }
      } catch (SQLException e) {
        return false;
      }
    }
    return true;
  }
  
  // -----------------------------------------------------------------------------
  // Transactions
  // -----------------------------------------------------------------------------
  
  @Override
  public void commit() {
    if (this.connection != null) {
      try {
        this.connection.commit();
        log.debug(getId() + ": Storage access rw committed.");      
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    } else {
      log.debug(getId() + ": Storage access committed (no connection).");      
    }
  }
  
  @Override
  public void abort() {
    if (this.connection != null) {
      try {
        this.connection.rollback();
        log.debug(getId() + ": Storage rw access aborted.");      
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      } finally {
        close();
      }
    } else {
      log.debug(getId() + ": Storage access aborted (no connection).");      
    }
  }
  
  @Override
  public synchronized void close() {
    try {
      // Close/release connections
      if (this.connection != null) {
        try {
          this.connection.close();
        } catch (SQLException e) {
          // ignore
        } finally {
          this.connection = null;
        }
        log.debug(getId() + ": Storage access rw closed.");
      } else {
        log.debug(getId() + ": Storage access closed (no connection).");          
      }
    } finally {
      closed = true;
    }
  }
  
  @Override
  public void flush() {
    // Return if nothing to flush
    if (flushable.isEmpty()) {
      return;
    }
    
    try {
      log.trace(Thread.currentThread() + " RDBMSAccess.flush enter");
      // Flush flushable handlers
      for (FlushableIF object : flushable) {
        object.flush();
      }
      flushable.clear();
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      log.trace(Thread.currentThread() + " RDBMSAccess.flush leave");
    }
  }
  
  @Override
  public boolean loadObject(AccessRegistrarIF registrar, IdentityIF identity) {
    try {
      if (debug) {
        log.debug("Loading object: " + identity);
      }
      try {
        return getHandler(identity.getType()).load(registrar, identity);
      } catch (IdentityNotFoundException e) {
        throw e;
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public Object loadField(AccessRegistrarIF registrar, IdentityIF identity, int field) {
    try {
      if (debug) {
        log.debug("Loading field: " + field + " identity: " + identity);
      }
      try {
        return getHandler(identity.getType()).loadField(registrar, identity, field);
      } catch (IdentityNotFoundException e) {
        throw e;
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public Object loadFieldMultiple(AccessRegistrarIF registrar, Collection<IdentityIF> identities, 
      IdentityIF current, Class<?> type, int field) {
    try {
      if (debug) {
        if (current == null) {
          log.debug("Loading field: " + field + " batch: " + (identities == null ? 0 : identities.size()));
        } else {
          log.debug("Loading field: " + field + " identity: " + current + " and " + 
              (identities == null ? 0 : identities.size()) + " others");
        }
      }
      try {
        return getHandler(type).loadFieldMultiple(registrar, identities, current, field);
      } catch (IdentityNotFoundException e) {
        throw e;
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public void createObject(ObjectAccessIF oaccess, Object object) {
    try {
      if (debug) {
        log.debug(getId() + ": Creating object " + oaccess.getIdentity(object));
      }
      getHandler(oaccess.getType(object)).create(oaccess, object);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public void deleteObject(ObjectAccessIF oaccess, Object object) {
    try {
      if (debug) {
        log.debug(getId() + ": Deleting object " + oaccess.getIdentity(object));
      }
      getHandler(oaccess.getType(object)).delete(oaccess, object);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public void storeDirty(ObjectAccessIF oaccess, Object object) {
    try {
      if (debug) {
        log.debug(getId() + ": Storing dirty object " + oaccess.getIdentity(object));
      }
      getHandler(oaccess.getType(object)).storeDirty(oaccess, object);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  protected void needsFlushing(FlushableIF handler) {
    flushable.add(handler);
  }
  
  // -----------------------------------------------------------------------------
  // Queries
  // -----------------------------------------------------------------------------
  
  @Override
  public QueryIF createQuery(String name, ObjectAccessIF oaccess, AccessRegistrarIF registrar) {
    return storage.createQuery(name, this, oaccess, registrar);
  }
  
  @Override
  public QueryIF createQuery(JDOQuery jdoquery, ObjectAccessIF oaccess, AccessRegistrarIF registrar, boolean lookup_identities) {
    return storage.createQuery(jdoquery, this, oaccess, registrar, lookup_identities);
  }
  
  // -----------------------------------------------------------------------------
  // Identity generator
  // -----------------------------------------------------------------------------
  
  @Override
  public IdentityIF generateIdentity(Class<?> type) {
    return storage.generateIdentity(type);
  }
  
}
