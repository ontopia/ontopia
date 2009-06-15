
// $Id: SpyConnection.java,v 1.6 2008/05/09 10:17:21 geir.gronmo Exp $

package net.ontopia.persistence.jdbcspy;

import java.sql.*;
import java.util.Map;

/**
 * INTERNAL: 
 */

public class SpyConnection implements Connection {

  Connection conn;
  SpyStats stats;

  //! public SpyConnection(Connection conn) {
  //!   this.conn = conn;
  //!   this.stats = new SpyStats();
  //! }

  public SpyConnection(Connection conn, SpyStats stats) {
    this.conn = conn;
    this.stats = stats;
  }

  public int getTransactionIsolation()
    throws SQLException {
    return conn.getTransactionIsolation();
  }

  public void clearWarnings()
    throws SQLException {
    conn.clearWarnings();
  }

  public void close()
    throws SQLException {
    long st = System.currentTimeMillis();
    conn.close();
    stats.connectionClose(this, st, System.currentTimeMillis());
  }

  public void commit()
    throws SQLException {
    long st = System.currentTimeMillis();
    conn.commit();
    stats.connectionCommit(this, st, System.currentTimeMillis());
  }

  public void rollback() 
    throws SQLException {
    long st = System.currentTimeMillis();
    conn.rollback();
    stats.connectionRollback(this, st, System.currentTimeMillis());
  }
  
  public boolean getAutoCommit()
    throws SQLException {
    return conn.getAutoCommit();
  }

  public boolean isClosed()
    throws SQLException {
    return conn.isClosed();
  }

  public boolean isReadOnly()
    throws SQLException {
    return conn.isReadOnly();
  }

  public void setTransactionIsolation(int level)
    throws SQLException {
    conn.setTransactionIsolation(level);
  }

  public void setAutoCommit(boolean autoCommit)
    throws SQLException {
    conn.setAutoCommit(autoCommit);
  }

  public void setReadOnly(boolean readOnly)
    throws SQLException {
    conn.setReadOnly(readOnly);
  }

  public String getCatalog()
    throws SQLException {
    return conn.getCatalog();
  }

  public void setCatalog(String catalog)
    throws SQLException {
    conn.setCatalog(catalog);
  }

  public DatabaseMetaData getMetaData()
    throws SQLException {
    return conn.getMetaData();
  }

  public SQLWarning getWarnings()
    throws SQLException {
    return conn.getWarnings();
  }

  public Statement createStatement()
    throws SQLException {
    return new SpyStatement(this, stats, conn.createStatement());
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency)
    throws SQLException {
    return new SpyStatement(this, stats, conn.createStatement(resultSetType, resultSetConcurrency));
  }


  public Map getTypeMap()
    throws SQLException {
    return conn.getTypeMap();
  }

  public void setTypeMap(Map typeMap)
    throws SQLException {
    conn.setTypeMap(typeMap);
  }

  public String nativeSQL(String sql)
    throws SQLException {
    return conn.nativeSQL(sql);
  }

  public CallableStatement prepareCall(String sql)
    throws SQLException {
    return conn.prepareCall(sql);
  }

  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
    throws SQLException {
    return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
  }

  public PreparedStatement prepareStatement(String sql)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql));
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql, resultSetType, resultSetConcurrency));
  }

  // J2EE 1.4

  public Savepoint setSavepoint()
    throws SQLException {
    return conn.setSavepoint();
  }
  
  public void releaseSavepoint(Savepoint savepoint)
    throws SQLException {
    conn.releaseSavepoint(savepoint);
  }
  
  public void rollback(Savepoint savepoint)
    throws SQLException {
    conn.rollback(savepoint);
  }
  
  public Savepoint setSavepoint(String savepoint)
    throws SQLException {
    return conn.setSavepoint(savepoint);
  }

  public int getHoldability()
    throws SQLException {
    return conn.getHoldability();
  }
  
  public void setHoldability(int holdability)
    throws SQLException {
    conn.setHoldability(holdability);
  }
  
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
    throws SQLException {
    return new SpyStatement(this, stats, conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
  }
  
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
    throws SQLException {
    return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
  }
  
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
  }

  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql, autoGeneratedKeys));
  }

  public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql, columnIndexes));
  }

  public PreparedStatement prepareStatement(String sql, String[] columnNames)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql, columnNames));
  }

}
