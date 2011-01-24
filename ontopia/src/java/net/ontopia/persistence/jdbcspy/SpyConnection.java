
// $Id: SpyConnection.java,v 1.6 2008/05/09 10:17:21 geir.gronmo Exp $

package net.ontopia.persistence.jdbcspy;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

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

  // J2EE 1.6 specifics - comment out remainder of methods if you have to use java 1.5

  public Clob createClob() throws SQLException {
    return conn.createClob();
  }

  public Blob createBlob() throws SQLException {
    return conn.createBlob();
  }

  public NClob createNClob() throws SQLException {
    return conn.createNClob();
  }

  public SQLXML createSQLXML() throws SQLException {
    return conn.createSQLXML();
  }

  public boolean isValid(int i) throws SQLException {
    return conn.isValid(i);
  }

  public void setClientInfo(String s, String s1) throws SQLClientInfoException {
    conn.setClientInfo(s, s1);
  }

  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    conn.setClientInfo(properties);
  }

  public String getClientInfo(String s) throws SQLException {
    return conn.getClientInfo(s);
  }

  public Properties getClientInfo() throws SQLException {
    return conn.getClientInfo();
  }

  public Array createArrayOf(String s, Object[] objects) throws SQLException {
    return conn.createArrayOf(s, objects);
  }

  public Struct createStruct(String s, Object[] objects) throws SQLException {
    return conn.createStruct(s, objects);
  }

  public <T> T unwrap(Class<T> tClass) throws SQLException {
    return conn.unwrap(tClass);
  }

  public boolean isWrapperFor(Class<?> aClass) throws SQLException {
    return conn.isWrapperFor(aClass);
  }

}
