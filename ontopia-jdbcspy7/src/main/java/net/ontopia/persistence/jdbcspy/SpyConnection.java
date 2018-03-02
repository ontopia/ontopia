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

package net.ontopia.persistence.jdbcspy;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * INTERNAL: 
 */

public class SpyConnection implements Connection {

  protected Connection conn;
  protected SpyStats stats;

  //! public SpyConnection(Connection conn) {
  //!   this.conn = conn;
  //!   this.stats = new SpyStats();
  //! }

  public SpyConnection(Connection conn, SpyStats stats) {
    this.conn = conn;
    this.stats = stats;
  }

  @Override
  public int getTransactionIsolation()
    throws SQLException {
    return conn.getTransactionIsolation();
  }

  @Override
  public void clearWarnings()
    throws SQLException {
    conn.clearWarnings();
  }

  @Override
  public void close()
    throws SQLException {
    long st = System.currentTimeMillis();
    conn.close();
    stats.connectionClose(this, st, System.currentTimeMillis());
  }

  @Override
  public void commit()
    throws SQLException {
    long st = System.currentTimeMillis();
    conn.commit();
    stats.connectionCommit(this, st, System.currentTimeMillis());
  }

  @Override
  public void rollback() 
    throws SQLException {
    long st = System.currentTimeMillis();
    conn.rollback();
    stats.connectionRollback(this, st, System.currentTimeMillis());
  }
  
  @Override
  public boolean getAutoCommit()
    throws SQLException {
    return conn.getAutoCommit();
  }

  @Override
  public boolean isClosed()
    throws SQLException {
    return conn.isClosed();
  }

  @Override
  public boolean isReadOnly()
    throws SQLException {
    return conn.isReadOnly();
  }

  @Override
  public void setTransactionIsolation(int level)
    throws SQLException {
    conn.setTransactionIsolation(level);
  }

  @Override
  public void setAutoCommit(boolean autoCommit)
    throws SQLException {
    conn.setAutoCommit(autoCommit);
  }

  @Override
  public void setReadOnly(boolean readOnly)
    throws SQLException {
    conn.setReadOnly(readOnly);
  }

  @Override
  public String getCatalog()
    throws SQLException {
    return conn.getCatalog();
  }

  @Override
  public void setCatalog(String catalog)
    throws SQLException {
    conn.setCatalog(catalog);
  }

  @Override
  public DatabaseMetaData getMetaData()
    throws SQLException {
    return conn.getMetaData();
  }

  @Override
  public SQLWarning getWarnings()
    throws SQLException {
    return conn.getWarnings();
  }

  @Override
  public Statement createStatement()
    throws SQLException {
    return new SpyStatement(this, stats, conn.createStatement());
  }

  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency)
    throws SQLException {
    return new SpyStatement(this, stats, conn.createStatement(resultSetType, resultSetConcurrency));
  }


  @Override
  public Map<String, Class<?>> getTypeMap()
    throws SQLException {
    return conn.getTypeMap();
  }

  @Override
  public void setTypeMap(Map<String, Class<?>> typeMap)
    throws SQLException {
    conn.setTypeMap(typeMap);
  }

  @Override
  public String nativeSQL(String sql)
    throws SQLException {
    return conn.nativeSQL(sql);
  }

  @Override
  public CallableStatement prepareCall(String sql)
    throws SQLException {
    return conn.prepareCall(sql);
  }

  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
    throws SQLException {
    return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
  }

  @Override
  public PreparedStatement prepareStatement(String sql)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql));
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql, resultSetType, resultSetConcurrency));
  }

  // J2EE 1.4

  @Override
  public Savepoint setSavepoint()
    throws SQLException {
    return conn.setSavepoint();
  }
  
  @Override
  public void releaseSavepoint(Savepoint savepoint)
    throws SQLException {
    conn.releaseSavepoint(savepoint);
  }
  
  @Override
  public void rollback(Savepoint savepoint)
    throws SQLException {
    conn.rollback(savepoint);
  }
  
  @Override
  public Savepoint setSavepoint(String savepoint)
    throws SQLException {
    return conn.setSavepoint(savepoint);
  }

  @Override
  public int getHoldability()
    throws SQLException {
    return conn.getHoldability();
  }
  
  @Override
  public void setHoldability(int holdability)
    throws SQLException {
    conn.setHoldability(holdability);
  }
  
  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
    throws SQLException {
    return new SpyStatement(this, stats, conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
  }
  
  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
    throws SQLException {
    return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
  }
  
  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql, autoGeneratedKeys));
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql, columnIndexes));
  }

  @Override
  public PreparedStatement prepareStatement(String sql, String[] columnNames)
    throws SQLException {
    return new SpyPreparedStatement(this, stats, sql, conn.prepareStatement(sql, columnNames));
  }

  // J2EE 1.6 specifics - comment out remainder of methods if you have to use java 1.5

  @Override
  public Clob createClob() throws SQLException {
    return conn.createClob();
  }

  @Override
  public Blob createBlob() throws SQLException {
    return conn.createBlob();
  }

  @Override
  public NClob createNClob() throws SQLException {
    return conn.createNClob();
  }

  @Override
  public SQLXML createSQLXML() throws SQLException {
    return conn.createSQLXML();
  }

  @Override
  public boolean isValid(int i) throws SQLException {
    return conn.isValid(i);
  }

  @Override
  public void setClientInfo(String s, String s1) throws SQLClientInfoException {
    conn.setClientInfo(s, s1);
  }

  @Override
  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    conn.setClientInfo(properties);
  }

  @Override
  public String getClientInfo(String s) throws SQLException {
    return conn.getClientInfo(s);
  }

  @Override
  public Properties getClientInfo() throws SQLException {
    return conn.getClientInfo();
  }

  @Override
  public Array createArrayOf(String s, Object[] objects) throws SQLException {
    return conn.createArrayOf(s, objects);
  }

  @Override
  public Struct createStruct(String s, Object[] objects) throws SQLException {
    return conn.createStruct(s, objects);
  }

  @Override
  public <T> T unwrap(Class<T> tClass) throws SQLException {
    return conn.unwrap(tClass);
  }

  @Override
  public boolean isWrapperFor(Class<?> aClass) throws SQLException {
    return conn.isWrapperFor(aClass);
  }

  // J2EE 1.7 specifics - comment out remainder of methods if you have to use java 1.6 or lower

  @Override
  public void setSchema(String schema) throws SQLException {
    conn.setSchema(schema);
  }

  @Override
  public String getSchema() throws SQLException {
    return conn.getSchema();
  }

  @Override
  public void abort(Executor executor) throws SQLException {
    conn.abort(executor);
  }

  @Override
  public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    conn.setNetworkTimeout(executor, milliseconds);
  }

  @Override
  public int getNetworkTimeout() throws SQLException {
    return conn.getNetworkTimeout();
  }
}
