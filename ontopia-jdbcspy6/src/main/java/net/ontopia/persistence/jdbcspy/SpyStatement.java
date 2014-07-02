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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Map;

/**
 * INTERNAL: 
 */

public class SpyStatement implements Statement {

  SpyConnection conn;
  SpyStats stats;
  Statement stm;

  public SpyStatement(SpyConnection conn, SpyStats stats, Statement stm) {
    this.conn = conn;
    this.stats = stats;
    this.stm = stm;
  }

  public int getFetchDirection()
    throws SQLException {
    return stm.getFetchDirection();
  }

  public int getFetchSize()
    throws SQLException {
    return stm.getFetchSize();
  }

  public int getMaxFieldSize()
    throws SQLException {
    return stm.getMaxFieldSize();
  }

  public int getMaxRows()
    throws SQLException {
    return stm.getMaxRows();
  }

  public boolean getMoreResults()
    throws SQLException {
    return stm.getMoreResults();
  }

  public int getQueryTimeout()
    throws SQLException {
    return stm.getQueryTimeout();
  }

  public int getResultSetConcurrency()
    throws SQLException {
    return stm.getResultSetConcurrency();
  }

  public int getResultSetType()
    throws SQLException {
    return stm.getResultSetType();
  }

  public int getUpdateCount()
    throws SQLException {
    return stm.getUpdateCount();
  }

  public void cancel()
    throws SQLException {
    stm.cancel();
  }

  public void clearBatch()
    throws SQLException {
    stm.clearBatch();
  }

  public void clearWarnings()
    throws SQLException {
    stm.clearWarnings();
  }

  public void close()
    throws SQLException {
    stm.close();
  }

  public void setFetchDirection(int fetchDirection)
    throws SQLException {
    stm.setFetchDirection(fetchDirection);
  }

  public void setFetchSize(int fetchSize)
    throws SQLException {
    stm.setFetchSize(fetchSize);
  }

  public void setMaxFieldSize(int maxFieldSize)
    throws SQLException {
    stm.setMaxFieldSize(maxFieldSize);
  }

  public void setMaxRows(int maxRows)
    throws SQLException {
    stm.setMaxRows(maxRows);
  }

  public void setQueryTimeout(int queryTimeout)
    throws SQLException {
    stm.setQueryTimeout(queryTimeout);
  }

  public void setEscapeProcessing(boolean escapeProcessing)
    throws SQLException {
      stm.setEscapeProcessing(escapeProcessing);
  }

  public int executeUpdate(String sql)
    throws SQLException {
    long st = System.currentTimeMillis();
    int r = stm.executeUpdate(sql);
    stats.statementExecuteUpdate(this, sql, r, st, System.currentTimeMillis());
    return r;
  }

  public void addBatch(String sql)
    throws SQLException {
    stm.addBatch(sql);
  }

  public void setCursorName(String cursorName)
    throws SQLException {
    stm.setCursorName(cursorName);
  }

  public boolean execute(String sql)
    throws SQLException {
    long st = System.currentTimeMillis();
    boolean r = stm.execute(sql);
    stats.statementExecute(this, sql, st, System.currentTimeMillis());
    return r;
  }

  public Connection getConnection()
    throws SQLException {
    //! return stm.getConnection();
    return conn;
  }

  public ResultSet getResultSet()
    throws SQLException {
    return new SpyResultSet(this, "ResultSet.getResultSet()", stats, stm.getResultSet());
  }

  public SQLWarning getWarnings()
    throws SQLException {
    return stm.getWarnings();
  }

  public ResultSet executeQuery(String sql)
    throws SQLException {
    long st = System.currentTimeMillis();
    ResultSet r = new SpyResultSet(this, sql, stats, stm.executeQuery(sql));
    stats.statementExecuteQuery(this, sql, st, System.currentTimeMillis());
    return r;    
  }

  public int[] executeBatch()
    throws SQLException {
    // TODO: figure out how to log this one
    //! long st = System.currentTimeMillis();
    int[] r = stm.executeBatch();
    //! stats.statementExecuteBatch(this, , r.length, st, System.currentTimeMillis());
    return r;
  }

  // J2EE 1.4

  public int getResultSetHoldability()
    throws SQLException {
    return stm.getResultSetHoldability();
  }

  public ResultSet getGeneratedKeys()
    throws SQLException {
    return new SpyResultSet(this, "ResultSet.getGeneratedKeys()", stats, stm.getGeneratedKeys());
  }

  public boolean getMoreResults(int moreResults)
    throws SQLException {
    return stm.getMoreResults(moreResults);
  }

  public boolean execute(String sql, String[] columnNames)
    throws SQLException {
    long st = System.currentTimeMillis();
    boolean r = stm.execute(sql, columnNames);
    stats.statementExecute(this, sql, st, System.currentTimeMillis());
    return r;
  }

  public boolean execute(String sql, int[] columnIndexes)
    throws SQLException {
    long st = System.currentTimeMillis();
    boolean r = stm.execute(sql, columnIndexes);
    stats.statementExecute(this, sql, st, System.currentTimeMillis());
    return r;
  }

  public int executeUpdate(String sql, int[] columnIndexes)
    throws SQLException {
    long st = System.currentTimeMillis();
    int r = stm.executeUpdate(sql, columnIndexes);
    stats.statementExecuteUpdate(this, sql, r, st, System.currentTimeMillis());
    return r;
  }

  public int executeUpdate(String sql, String[] columnNames)
    throws SQLException {
    long st = System.currentTimeMillis();
    int r = stm.executeUpdate(sql, columnNames);
    stats.statementExecuteUpdate(this, sql, r, st, System.currentTimeMillis());
    return r;
  }
  
  public int executeUpdate(String sql, int autoGeneratedKeys)
    throws SQLException {
    long st = System.currentTimeMillis();
    int r = stm.executeUpdate(sql, autoGeneratedKeys);
    stats.statementExecuteUpdate(this, sql, r, st, System.currentTimeMillis());
    return r;
  }
  
  public boolean execute(String sql, int autoGeneratedKeys)
    throws SQLException {
    long st = System.currentTimeMillis();
    boolean r = stm.execute(sql, autoGeneratedKeys);
    stats.statementExecute(this, sql, st, System.currentTimeMillis());
    return r;
  }

  // J2EE 1.6 specifics - comment out remainder of methods if you have to use java 1.5

  public Object getObject(String s, Map<String, Class<?>> stringClassMap) throws SQLException {
    return null;
  }

  public boolean isClosed() throws SQLException {
    return stm.isClosed();
  }

  public void setPoolable(boolean b) throws SQLException {
    stm.setPoolable(b);
  }

  public boolean isPoolable() throws SQLException {
    return stm.isPoolable();
  }

  public <T> T unwrap(Class<T> tClass) throws SQLException {
    return stm.unwrap(tClass);
  }

  public boolean isWrapperFor(Class<?> aClass) throws SQLException {
    return stm.isWrapperFor(aClass);
  }

}
