
// $Id: TologConnection.java,v 1.3 2007/11/13 13:31:40 geir.gronmo Exp $

package net.ontopia.topicmaps.query.jdbc;

import java.sql.*;
import java.util.Map;
import java.util.Collections;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: 
 */

public class TologConnection implements Connection {

  boolean readOnly;
  
  TopicMapStoreIF store;
  QueryProcessorIF qp;

  String url;
  String userName;
  
  public TologConnection(String url) throws SQLException {
    this.url = url;

    // tolog:opera.ltm
    // tolog:tm-sources.xml:opera.ltm

    int init_ix = "tolog:".length();
    int tm_ix = url.indexOf(":", init_ix);

    String topicmapId = null;;
    String repositoryId = null;
    
    if (tm_ix > init_ix) {
      topicmapId = url.substring(init_ix, tm_ix);
      if (tm_ix < url.length() && url.charAt(tm_ix) == ':')
        repositoryId = url.substring(tm_ix+1);
    } else {
      topicmapId = url.substring(init_ix);
    }
        
    if (repositoryId == null)
      this.store = TopicMaps.createStore(topicmapId, readOnly);
    else
      this.store = TopicMaps.createStore(topicmapId, readOnly, repositoryId);

    this.qp = QueryUtils.getQueryProcessor(store.getTopicMap());
  }

  public TologConnection(String topicmapId, String repositoryId) {
    this.store = TopicMaps.createStore(topicmapId, readOnly, repositoryId);
    this.qp = QueryUtils.getQueryProcessor(store.getTopicMap());
  }

  public int getTransactionIsolation()
    throws SQLException {
    return Connection.TRANSACTION_READ_COMMITTED;
  }

  public void clearWarnings()
    throws SQLException {
    // todo
  }

  public void close()
    throws SQLException {
    store.close();
  }

  public void commit()
    throws SQLException {
    store.commit();
  }

  public void rollback() 
    throws SQLException {
    store.abort();
  }
  
  public boolean getAutoCommit()
    throws SQLException {
    return false;
  }

  public boolean isClosed()
    throws SQLException {
    return store.isOpen();
  }

  public boolean isReadOnly()
    throws SQLException {
    return readOnly;
  }

  public void setTransactionIsolation(int level)
    throws SQLException {
    // ignore
  }

  public void setAutoCommit(boolean autoCommit)
    throws SQLException {
    // ignore
  }

  public void setReadOnly(boolean readOnly)
    throws SQLException {
    this.readOnly = readOnly;
  }

  public String getCatalog()
    throws SQLException {
    return null;
  }

  public void setCatalog(String catalog)
    throws SQLException {
    // ignroe
  }

  public DatabaseMetaData getMetaData()
    throws SQLException {
    // todo
    return new TologDatabaseMetaData(this);
  }

  public SQLWarning getWarnings()
    throws SQLException {
    // todo
    return null;
  }

  public Statement createStatement()
    throws SQLException {
    return new TologStatement(this);
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency)
    throws SQLException {
    return new TologStatement(this, resultSetType, resultSetConcurrency);
  }


  public Map getTypeMap()
    throws SQLException {
    // todo
    return Collections.EMPTY_MAP;
  }

  public void setTypeMap(Map typeMap)
    throws SQLException {
    // ignore
  }

  public String nativeSQL(String sql)
    throws SQLException {
    return sql;
  }

  public CallableStatement prepareCall(String sql)
    throws SQLException {
    // todo
    return null;
  }

  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
    throws SQLException {
    // todo
    return null;
  }

  public PreparedStatement prepareStatement(String sql)
    throws SQLException {
    return new TologPreparedStatement(this, sql);
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
    throws SQLException {
    return new TologPreparedStatement(this, sql, resultSetType, resultSetConcurrency);
  }

  // J2EE 1.4

  //! public Savepoint setSavepoint()
  //!   throws SQLException {
  //!   return conn.setSavepoint();
  //! }
  //! 
  //! public void releaseSavepoint(Savepoint savepoint)
  //!   throws SQLException {
  //!   conn.releaseSavepoint(savepoint);
  //! }
  //! 
  //! public void rollback(Savepoint savepoint)
  //!   throws SQLException {
  //!   conn.rollback(savepoint);
  //! }
  //! 
  //! public Savepoint setSavepoint(String savepoint)
  //!   throws SQLException {
  //!   return conn.setSavepoint(savepoint);
  //! }

  //! public int getHoldability()
  //!   throws SQLException {
  //!   return conn.getHoldability();
  //! }
  //! 
  //! public void setHoldability(int holdability)
  //!   throws SQLException {
  //!   conn.setHoldability(holdability);
  //! }
  //! 
  //! public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
  //!   throws SQLException {
  //!   return new TologStatement(this, conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
  //! }
  //! 
  //! public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
  //!   throws SQLException {
  //!   return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
  //! }
  //! 
  //! public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
  //!   throws SQLException {
  //!   return new TologPreparedStatement(this, sql, conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
  //! }

  //! public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
  //!   throws SQLException {
  //!   return new TologPreparedStatement(this, sql, conn.prepareStatement(sql, autoGeneratedKeys));
  //! }

  //! public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
  //!   throws SQLException {
  //!   return new TologPreparedStatement(this, sql, conn.prepareStatement(sql, columnIndexes));
  //! }

  //! public PreparedStatement prepareStatement(String sql, String[] columnNames)
  //!   throws SQLException {
  //!   return new TologPreparedStatement(this, sql, conn.prepareStatement(sql, columnNames));
  //! }

  // --- Helpers

  TologResultSet executeQuery(TologStatement stm, String query) throws SQLException {
    try {
      return new TologResultSet(stm, qp.execute(query));
    } catch (InvalidQueryException e) {
      throw new SQLException(e.getMessage());
    }
  }

  TologResultSet executeQuery(TologPreparedStatement stm, String query, Map params) throws SQLException {
    try {
      return new TologResultSet(stm, qp.execute(query, params));
    } catch (InvalidQueryException e) {
      throw new SQLException(e.getMessage());
    }
  }

  public String getURL() {
    return url;
  }

  public String getUserName() {
    return "default";
  }
  
}
