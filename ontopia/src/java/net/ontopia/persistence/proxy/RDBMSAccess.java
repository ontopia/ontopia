
// $Id: RDBMSAccess.java,v 1.84 2007/06/12 13:10:04 geir.gronmo Exp $

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
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.TraceUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A storage access implementation accessing relational
 * databases using JDBC.
 */

public class RDBMSAccess implements StorageAccessIF {
  
  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(RDBMSAccess.class.getName());
  
  protected boolean debug = log.isDebugEnabled();
  
  protected String id;
  protected boolean readonly;
  protected RDBMSStorage storage;
  protected RDBMSMapping mapping;
  
  protected Connection conn_;
  protected ThreadLocal conn_tl = new ThreadLocal();

  protected boolean closed;
  
  protected Map handlers;
  
  protected boolean batch_updates = false;
  protected Collection flushable;
  
  public RDBMSAccess(String id, RDBMSStorage storage, boolean readonly) {
    this.id = id;
    this.readonly = readonly;
    this.storage = storage;
    this.mapping = storage.getMapping();
    
    // Enable or disable batch updates
    if (PropertyUtils.isTrue(getProperty("net.ontopia.topicmaps.impl.rdbms.BatchUpdates")))
      batch_updates = true;
    
    handlers = new HashMap();
    flushable = new HashSet();
    
    log.debug(getId() + ": Storage access created");    
  }
  
  public String getId() {
    return id;
  }
  
  public StorageIF getStorage() {
    return storage;
  }

  public boolean isReadOnly() {
    return readonly;
  }
  
  public String getProperty(String property) {
    return storage.getProperty(property);
  }
  
  // -----------------------------------------------------------------------------
  // RDBMS specific
  // -----------------------------------------------------------------------------

  protected Connection getConn() {
    if (readonly)
      return (Connection)conn_tl.get();
    else
      return conn_;
  }
  protected void setConn(Connection conn) {
    if (readonly)
      this.conn_tl.set(conn);
    else
      this.conn_ = conn;
  }

  /**
   * INTERNAL: Returns the JDBC database connection used. It is important that 
   * this connection is neither closed, nor commited or rolled back. 
   */
  public synchronized Connection getConnection() {    
    Connection conn = getConn();
    if (conn == null) {
      try {
        // Request new connection object from storage
        conn = storage.getConnectionFactory(readonly).requestConnection();
        setConn(conn);
        return conn;
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    } else
      return conn;
  }
  
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return getConnection().prepareStatement(sql);
  }
  
  protected synchronized void resetConnection() {
    // NOTE: method used to reset connection for read-only access, so
    // that a second attempt can be made.
    Connection conn = getConn();
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        // ignore
      } finally {
        setConn(null);
      }
    }
  }
  
  protected boolean isSQLException(Throwable e) {
    if (e == null) return false;
    if (e instanceof SQLException)
      return true;
    else if (e instanceof OntopiaRuntimeException)
      return isSQLException(((OntopiaRuntimeException)e).getCause());
    else
      return false;    
  }
  
  // -----------------------------------------------------------------------------
  // Handlers
  // -----------------------------------------------------------------------------
  
  /**
   * INTERNAL: Gets up the handler class that is used to manage
   * objects of the given class.
   */
  protected ClassAccessIF getHandler(Object type) {
    // Each class have its own handler
    ClassAccessIF handler = (ClassAccessIF)handlers.get(type);
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
  
  public boolean validate() {
    Connection conn = getConn();
    return !(closed || (conn == null ? false : !validateConnection(conn)));
  }
  
  protected boolean validateConnection(Connection conn) {
    // stop here if connection says that it's closed 
    try {
      if (conn.isClosed()) return false;
    } catch (SQLException e) {
      return false;
    }
    
    // get validation query
    String vquery = getProperty("net.ontopia.topicmaps.impl.rdbms.ConnectionPool.ValidationQuery");
    if (vquery == null)
      vquery = "select seq_count from TM_ADMIN_SEQUENCE where 1 != 1";
    
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
        if (stm != null) stm.close();
      } catch (SQLException e) {
        return false;
      }
    }
    return true;
  }
  
  // -----------------------------------------------------------------------------
  // Transactions
  // -----------------------------------------------------------------------------
  
  public void commit() {
    Connection conn = getConn();
    if (conn != null) {
      try {
        conn.commit();
        log.debug(getId() + ": Storage access rw committed.");      
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
    } else {
      log.debug(getId() + ": Storage access committed (no connection).");      
    }
  }
  
  public void abort() {
    Connection conn = getConn();
    if (conn != null) {
      try {
        conn.rollback();
        log.debug(getId() + ": Storage rw access aborted.");      
      } catch (SQLException e) {
        throw new OntopiaRuntimeException(e);
      }
      try {
        conn.close();
      } catch (SQLException e) {
        // ignore
      } finally {
        setConn(null);
      }
    } else {
      log.debug(getId() + ": Storage access aborted (no connection).");      
    }
  }
  
  public void close() {
    try {
      // Close/release connections
      Connection conn = getConn();
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          // ignore
        } finally {
          setConn(null);
        }
        log.debug(getId() + ": Storage access rw closed.");
      } else {
        log.debug(getId() + ": Storage access closed (no connection).");          
      }
    } finally {
      closed = true;
    }
  }
  
  public void flush() {
    // Return if nothing to flush
    if (flushable.isEmpty()) return;
    
    try {
      TraceUtils.enter("RDBMSAccess.flush");
      // Flush flushable handlers
      Object[] objects = flushable.toArray();
      for (int i=0; i < objects.length; i++) {
        ((FlushableIF)objects[i]).flush();
      }      
      flushable.clear();
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      TraceUtils.leave("RDBMSAccess.flush");
    }
  }
  
  public boolean loadObject(AccessRegistrarIF registrar, IdentityIF identity) {
    try {
      if (debug)
        log.debug("Loading object: " + identity);
      try {
        return getHandler(identity.getType()).load(registrar, identity);
      } catch (IdentityNotFoundException e) {
        throw e;
      } catch (Exception e) {
        if (readonly && isSQLException(e)) {
          // if read-only, reset connection and try again
          resetConnection();
          log.warn(getId() + ": Connection seems to be down. Resetting read-only connection before second attempt (loadObject).", e);
          return getHandler(identity.getType()).load(registrar, identity);
        } else throw e;
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public Object loadField(AccessRegistrarIF registrar, IdentityIF identity, int field) {
    try {
      if (debug)
        log.debug("Loading field: " + field + " identity: " + identity);
      try {
        return getHandler(identity.getType()).loadField(registrar, identity, field);
      } catch (IdentityNotFoundException e) {
        throw e;
      } catch (Exception e) {
        if (readonly && isSQLException(e)) {
          // if read-only, reset connection and try again
          resetConnection();
          log.warn(getId() + ": Connection seems to be down. Resetting read-only connection before second attempt (loadField).", e);
          return getHandler(identity.getType()).loadField(registrar, identity, field);  
        } else throw e;
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public Object loadFieldMultiple(AccessRegistrarIF registrar, Collection identities, 
      IdentityIF current, Object type, int field) {
    try {
      if (debug) {
        if (current == null)
          log.debug("Loading field: " + field + " batch: " + (identities == null ? 0 : identities.size()));
        else
          log.debug("Loading field: " + field + " identity: " + current + " and " + 
              (identities == null ? 0 : identities.size()) + " others");
      }
      try {
        return getHandler(type).loadFieldMultiple(registrar, identities, current, field);
      } catch (IdentityNotFoundException e) {
        throw e;
      } catch (Exception e) {
        if (readonly && isSQLException(e)) {
          // if read-only, reset connection and try again
          resetConnection();
          log.warn(getId() + ": Connection seems to be down. Resetting read-only connection before second attempt (loadFieldMultiple).", e);
          return getHandler(type).loadFieldMultiple(registrar, identities, current, field);
        } else throw e;
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void createObject(ObjectAccessIF oaccess, Object object) {
    try {
      if (debug)
        log.debug(getId() + ": Creating object " + oaccess.getIdentity(object));
      getHandler(oaccess.getType(object)).create(oaccess, object);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void deleteObject(ObjectAccessIF oaccess, Object object) {
    try {
      if (debug)
        log.debug(getId() + ": Deleting object " + oaccess.getIdentity(object));
      getHandler(oaccess.getType(object)).delete(oaccess, object);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void storeDirty(ObjectAccessIF oaccess, Object object) {
    try {
      if (debug)
        log.debug(getId() + ": Storing dirty object " + oaccess.getIdentity(object));
      getHandler(oaccess.getType(object)).storeDirty(oaccess, object);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  void needsFlushing(FlushableIF handler) {
    flushable.add(handler);
  }
  
  // -----------------------------------------------------------------------------
  // Queries
  // -----------------------------------------------------------------------------
  
  public QueryIF createQuery(String name, ObjectAccessIF oaccess, AccessRegistrarIF registrar) {
    return storage.createQuery(name, this, oaccess, registrar);
  }
  
  public QueryIF createQuery(JDOQuery jdoquery, ObjectAccessIF oaccess, AccessRegistrarIF registrar, boolean lookup_identities) {
    return storage.createQuery(jdoquery, this, oaccess, registrar, lookup_identities);
  }
  
  // -----------------------------------------------------------------------------
  // Identity generator
  // -----------------------------------------------------------------------------
  
  public IdentityIF generateIdentity(Object type) {
    return storage.generateIdentity(type);
  }
  
}
