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

import java.util.Map;
import java.io.InputStream;
import java.io.Reader;
import java.util.Calendar;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * INTERNAL: 
 */

public class SpyResultSet implements ResultSet {

  protected SpyStatement stm;
  protected String sql;
  protected SpyStats stats;
  protected ResultSet rs;

  public SpyResultSet(SpyPreparedStatement stm, SpyStats stats, ResultSet rs) {
    this.stm = stm;
    this.sql = stm.sql;
    this.stats = stats;
    this.rs = rs;
  }

  public SpyResultSet(SpyStatement stm, String sql, SpyStats stats, ResultSet rs) {
    this.stm = stm;
    this.sql = sql;
    this.stats = stats;
    this.rs = rs;
  }

  @Override
  public int getConcurrency()
    throws SQLException {
    return rs.getConcurrency();
  }

  @Override
  public int getFetchDirection()
    throws SQLException {
    return rs.getFetchDirection();
  }

  @Override
  public int getFetchSize()
    throws SQLException {
    return rs.getFetchDirection();
  }

  @Override
  public int getRow()
    throws SQLException {
    return rs.getRow();
  }

  @Override
  public int getType()
    throws SQLException {
    return rs.getType();
  }

  @Override
  public void afterLast()
    throws SQLException {
    rs.afterLast();
  }

  @Override
  public void beforeFirst()
    throws SQLException {
    rs.beforeFirst();
  }

  @Override
  public void cancelRowUpdates()
    throws SQLException {
    rs.cancelRowUpdates();
  }

  @Override
  public void clearWarnings()
    throws SQLException {
    rs.clearWarnings();
  }

  @Override
  public void close()
    throws SQLException {
    rs.close();
  }

  @Override
  public void deleteRow()
    throws SQLException {
    rs.deleteRow();
  }

  @Override
  public void insertRow()
    throws SQLException {
    rs.insertRow();
  }

  @Override
  public void moveToCurrentRow()
    throws SQLException {
    rs.moveToCurrentRow();
  }

  @Override
  public void moveToInsertRow()
    throws SQLException {
    rs.moveToInsertRow();
  }

  @Override
  public void refreshRow()
    throws SQLException {
    rs.refreshRow();
  }

  @Override
  public void updateRow()
    throws SQLException {
    rs.updateRow();
  }

  @Override
  public boolean first()
    throws SQLException {
    return rs.first();
  }

  @Override
  public boolean isAfterLast()
    throws SQLException {
    return rs.isAfterLast();
  }

  @Override
  public boolean isBeforeFirst()
    throws SQLException {
    return rs.isBeforeFirst();
  }

  @Override
  public boolean isFirst()
    throws SQLException {
    return rs.isFirst();
  }

  @Override
  public boolean isLast()
    throws SQLException {
    return rs.isLast();
  }

  @Override
  public boolean last()
    throws SQLException {
    return rs.last();
  }

  @Override
  public boolean next()
    throws SQLException {
    long st = System.currentTimeMillis();
    boolean r = rs.next();
    stats.resultNext(this, r, st, System.currentTimeMillis());
    return r;
  }

  @Override
  public boolean previous()
    throws SQLException {
    return rs.previous();
  }

  @Override
  public boolean rowDeleted()
    throws SQLException {
    return rs.rowDeleted();
  }

  @Override
  public boolean rowInserted()
    throws SQLException {
    return rs.rowInserted();
  }

  @Override
  public boolean rowUpdated()
    throws SQLException {
    return rs.rowUpdated();
  }

  @Override
  public boolean wasNull()
    throws SQLException {
    return rs.wasNull();
  }

  @Override
  public byte getByte(int columnIndex)
    throws SQLException {
    return rs.getByte(columnIndex);
  }

  @Override
  public double getDouble(int columnIndex)
    throws SQLException {
    return rs.getDouble(columnIndex);
  }

  @Override
  public float getFloat(int columnIndex)
    throws SQLException {
    return rs.getFloat(columnIndex);
  }

  @Override
  public int getInt(int columnIndex)
    throws SQLException {
    return rs.getInt(columnIndex);
  }

  @Override
  public long getLong(int columnIndex)
    throws SQLException {
    return rs.getLong(columnIndex);
  }

  @Override
  public short getShort(int columnIndex)
    throws SQLException {
    return rs.getShort(columnIndex);
  }

  @Override
  public void setFetchDirection(int direction)
    throws SQLException {
    rs.setFetchDirection(direction);
  }

  @Override
  public void setFetchSize(int rows)
    throws SQLException {
    rs.setFetchSize(rows);
  }
  
  @Override
  public boolean absolute(int row)
    throws SQLException {
    return rs.absolute(row);
  }

  @Override
  public boolean getBoolean(int columnIndex)
    throws SQLException {
    return rs.getBoolean(columnIndex);
  }

  @Override
  public boolean relative(int rows)
    throws SQLException {
    return rs.relative(rows);
  }

  @Override
  public byte[] getBytes(int columnIndex)
    throws SQLException {
    return rs.getBytes(columnIndex);
  }

  @Override
  public InputStream getAsciiStream(int columnIndex)
    throws SQLException {
    return rs.getAsciiStream(columnIndex);
  }
  
  @Override
  public InputStream getBinaryStream(int columnIndex)
    throws SQLException {
    return rs.getBinaryStream(columnIndex);
  }

  @Override
  public InputStream getUnicodeStream(int columnIndex)
    throws SQLException {
    return rs.getUnicodeStream(columnIndex);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length)
    throws SQLException {
    rs.updateAsciiStream(columnIndex, x, length);
  }
   
  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, int length)
    throws SQLException {
    rs.updateBinaryStream(columnIndex, x, length);
  }

  @Override
  public Reader getCharacterStream(int columnIndex)
    throws SQLException {
    return rs.getCharacterStream(columnIndex);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, int length)
    throws SQLException {
    rs.updateCharacterStream(columnIndex, x, length);
  }

  @Override
  public Object getObject(int columnIndex)
    throws SQLException {
    return rs.getObject(columnIndex);
  }
   
  @Override
  public String getCursorName()
    throws SQLException {
    return rs.getCursorName();
  }

  @Override
  public String getString(int columnIndex)
    throws SQLException {
    return rs.getString(columnIndex);
  }

  @Override
  public void updateString(int columnIndex, String x)
    throws SQLException {
    rs.updateString(columnIndex, x);
  }
  
  @Override
  public byte getByte(String columnName)
    throws SQLException {
    return rs.getByte(columnName);
  }

  @Override
  public double getDouble(String columnName)
    throws SQLException {
    return rs.getDouble(columnName);
  }

  @Override
  public float getFloat(String columnName)
    throws SQLException {
    return rs.getFloat(columnName);
  }

  @Override
  public int findColumn(String columnName)
    throws SQLException {
    return rs.findColumn(columnName);
  }

  @Override
  public int getInt(String columnName)
    throws SQLException {
    return rs.getInt(columnName);
  }

  @Override
  public long getLong(String columnName)
    throws SQLException {
    return rs.getLong(columnName);
  }

  @Override
  public short getShort(String columnName)
    throws SQLException {
    return rs.getShort(columnName);
  }

  @Override
  public void updateNull(String columnName)
    throws SQLException {
    rs.updateNull(columnName);
  }

  @Override
  public void updateNull(int columnIndex)
    throws SQLException {
    rs.updateNull(columnIndex);
  }
  

  @Override
  public boolean getBoolean(String columnName)
    throws SQLException {
    return rs.getBoolean(columnName);
  }

  @Override
  public byte[] getBytes(String columnName)
    throws SQLException {
    return rs.getBytes(columnName);
  }

  @Override
  public void updateByte(String columnName, byte x)
    throws SQLException {
    rs.updateByte(columnName, x);
  }

  @Override
  public void updateDouble(String columnName, double x)
    throws SQLException {
    rs.updateDouble(columnName, x);
  }

  @Override
  public void updateFloat(String columnName, float x)
    throws SQLException {
    rs.updateFloat(columnName, x);
  }
   
  @Override
  public void updateInt(String columnName, int x)
    throws SQLException {
    rs.updateInt(columnName, x);
  }

  @Override
  public void updateLong(String columnName, long x)
    throws SQLException {
    rs.updateLong(columnName, x);
  }

  @Override
  public void updateShort(String columnName, short x)
    throws SQLException {
    rs.updateShort(columnName, x);
  }

  @Override
  public void updateBoolean(String columnName, boolean x)
    throws SQLException {
    rs.updateBoolean(columnName, x);
  }

  @Override
  public void updateBytes(String columnName, byte[] x)
    throws SQLException {
    rs.updateBytes(columnName, x);
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex)
    throws SQLException {
    return rs.getBigDecimal(columnIndex);
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex, int x)
    throws SQLException {
    return rs.getBigDecimal(columnIndex, x);
  }

  @Override
  public void updateBigDecimal(int columnIndex, BigDecimal x)
    throws SQLException {
    rs.updateBigDecimal(columnIndex, x);
  }

  @Override
  public Array getArray(int columnIndex)
    throws SQLException {
    return rs.getArray(columnIndex);
  }

  @Override
  public Blob getBlob(int columnIndex)
    throws SQLException {
    return rs.getBlob(columnIndex);
  }

  @Override
  public Clob getClob(int columnIndex)
    throws SQLException {
    return rs.getClob(columnIndex);
  }
   
  @Override
  public Date getDate(int columnIndex)
    throws SQLException {
    return rs.getDate(columnIndex);
  }
   
  @Override
  public void updateDate(int columnIndex, Date x)
    throws SQLException {
    rs.updateDate(columnIndex, x);
  }

  @Override
  public Ref getRef(int columnIndex)
    throws SQLException {
    return rs.getRef(columnIndex);
  }

  @Override
  public ResultSetMetaData getMetaData()
    throws SQLException {
    return rs.getMetaData();
  }

  @Override
  public SQLWarning getWarnings()
    throws SQLException {
    return rs.getWarnings();
  }

  @Override
  public Statement getStatement()
    throws SQLException {
    return rs.getStatement();
  }

  @Override
  public Time getTime(int columnIndex)
    throws SQLException {
    return rs.getTime(columnIndex);
  }

  @Override
  public void updateTime(int columnIndex, Time x)
    throws SQLException {
    rs.updateTime(columnIndex, x);
  }

  @Override
  public Timestamp getTimestamp(int columnIndex)
    throws SQLException {
    return rs.getTimestamp(columnIndex);
  }

  @Override
  public void updateTimestamp(int columnIndex, Timestamp x)
    throws SQLException {
    rs.updateTimestamp(columnIndex, x);
  }

  @Override
  public InputStream getAsciiStream(String columnName)
    throws SQLException {
    return rs.getAsciiStream(columnName);
  }

  @Override
  public InputStream getBinaryStream(String columnName)
    throws SQLException {
    return rs.getBinaryStream(columnName);
  }

  @Override
  public InputStream getUnicodeStream(String columnName)
    throws SQLException {
    return rs.getUnicodeStream(columnName);
  }

  @Override
  public void updateAsciiStream(String columnName, InputStream x, int length)
    throws SQLException {
    rs.updateAsciiStream(columnName, x, length);
  }

  @Override
  public void updateBinaryStream(String columnName, InputStream x, int length)
    throws SQLException {
    rs.updateBinaryStream(columnName, x, length);
  }

  @Override
  public Reader getCharacterStream(String columnName)
    throws SQLException {
    return rs.getCharacterStream(columnName);
  }

  @Override
  public void updateCharacterStream(String columnName, Reader x, int length)
    throws SQLException {
    rs.updateCharacterStream(columnName, x, length);
  }

  @Override
  public Object getObject(String columnName)
    throws SQLException {
    return rs.getObject(columnName);
  }

  @Override
  public void updateObject(String columnName, Object x)
    throws SQLException {
    rs.updateObject(columnName, x);
  }

  @Override
  public void updateObject(String columnName, Object x, int scale)
    throws SQLException {
    rs.updateObject(columnName, x, scale);
  }

  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map)
    throws SQLException {
    return rs.getObject(columnIndex, map);
  }

  @Override
  public String getString(String columnName)
    throws SQLException {
    return rs.getString(columnName);
  }

  @Override
  public void updateString(String columnName, String x)
    throws SQLException {
    rs.updateString(columnName, x);
  }

  @Override
  public BigDecimal getBigDecimal(String columnName)
    throws SQLException {
    return rs.getBigDecimal(columnName);
  }

  @Override
  public BigDecimal getBigDecimal(String columnName, int scale)
    throws SQLException {
    return rs.getBigDecimal(columnName, scale);
  }

  @Override
  public void updateBigDecimal(String columnName, BigDecimal x)
    throws SQLException {
    rs.updateBigDecimal(columnName, x);
  }

  @Override
  public Array getArray(String columnName)
    throws SQLException {
    return rs.getArray(columnName);
  }

  @Override
  public Blob getBlob(String columnName)
    throws SQLException {
    return rs.getBlob(columnName);
  }

  @Override
  public Clob getClob(String columnName)
    throws SQLException {
    return rs.getClob(columnName);
  }

  @Override
  public Date getDate(String columnName)
    throws SQLException {
    return rs.getDate(columnName);
  }

  @Override
  public void updateDate(String columnName, Date x)
    throws SQLException {
    rs.updateDate(columnName, x);
  }

  @Override
  public Date getDate(int columnIndex, Calendar cal)
    throws SQLException {
    return rs.getDate(columnIndex, cal);
  }

  @Override
  public Ref getRef(String columnName)
    throws SQLException {
    return rs.getRef(columnName);
  }

  @Override
  public Time getTime(String columnName)
    throws SQLException {
    return rs.getTime(columnName);
  }

  @Override
  public void updateTime(String columnName, Time x)
    throws SQLException {
    rs.updateTime(columnName, x);
  }

  @Override
  public Time getTime(int columnIndex, Calendar cal)
    throws SQLException {
    return rs.getTime(columnIndex, cal);
  }

  @Override
  public Timestamp getTimestamp(String columnName)
    throws SQLException {
    return rs.getTimestamp(columnName);
  }

  @Override
  public void updateTimestamp(String columnName, Timestamp x)
    throws SQLException {
    rs.updateTimestamp(columnName, x);
  }

  @Override
  public Timestamp getTimestamp(int columnIndex, Calendar cal)
    throws SQLException {
    return rs.getTimestamp(columnIndex, cal);
  }

  @Override
  public Object getObject(String columnName, Map<String, Class<?>> map)
    throws SQLException {
    return rs.getObject(columnName, map);
  }

  @Override
  public Date getDate(String columnName, Calendar cal)
    throws SQLException {
    return rs.getDate(columnName, cal);
  }

  @Override
  public Time getTime(String columnName, Calendar cal)
    throws SQLException {
    return rs.getTime(columnName, cal);
  }

  @Override
  public Timestamp getTimestamp(String columnName, Calendar cal)
    throws SQLException {
    return rs.getTimestamp(columnName, cal);
  }

  // J2EE 1.4

  @Override
  public URL getURL(int columnIndex)
    throws SQLException {
    return rs.getURL(columnIndex);
  }
  
  @Override
  public void updateArray(int columnIndex, Array x)
    throws SQLException {
    rs.updateArray(columnIndex, x);
  }
  
  @Override
  public void updateArray(String columnName, Array x)
    throws SQLException {
    rs.updateArray(columnName, x);
  }
  
  @Override
  public void updateObject(int columnIndex, Object x)
    throws SQLException {
    rs.updateObject(columnIndex, x);
  }
  
  @Override
  public void updateObject(int columnIndex, Object x, int scale)
    throws SQLException {
    rs.updateObject(columnIndex, x, scale);
  }
  
  @Override
  public void updateByte(int columnIndex, byte x)
    throws SQLException {
    rs.updateByte(columnIndex, x);
  }
  
  @Override
  public void updateDouble(int columnIndex, double x)
    throws SQLException {
    rs.updateDouble(columnIndex, x);
  }
  
  @Override
  public void updateFloat(int columnIndex, float x)
    throws SQLException {
    rs.updateFloat(columnIndex, x);
  }
  
  @Override
  public void updateInt(int columnIndex, int x)
    throws SQLException {
    rs.updateInt(columnIndex, x);
  }
  
  @Override
  public void updateLong(int columnIndex, long x)
    throws SQLException {
    rs.updateLong(columnIndex, x);
  }
  
  @Override
  public void updateShort(int columnIndex, short x)
    throws SQLException {
    rs.updateShort(columnIndex, x);
  }
  
  @Override
  public void updateBoolean(int columnIndex, boolean x)
    throws SQLException {
    rs.updateBoolean(columnIndex, x);
  }
  
  @Override
  public void updateBytes(int columnIndex, byte[] x)
    throws SQLException {
    rs.updateBytes(columnIndex, x);
  }

  // J2EE 1.4
   
  @Override
  public void updateRef(int columnIndex, Ref x)
    throws SQLException {
    rs.updateRef(columnIndex, x);
  }

  @Override
  public void updateRef(String columnName, Ref x)
    throws SQLException {
    rs.updateRef(columnName, x);
  }

  @Override
  public URL getURL(String columnName)
    throws SQLException {
    return rs.getURL(columnName);
  }
   
  @Override
  public void updateBlob(int columnIndex, Blob x)
    throws SQLException {
    rs.updateBlob(columnIndex, x);
  }
  
  @Override
  public void updateClob(int columnIndex, Clob x)
    throws SQLException {
    rs.updateClob(columnIndex, x);
  }
  
  @Override
  public void updateBlob(String columnName, Blob x)
    throws SQLException {
    rs.updateBlob(columnName, x);
  }
  
  @Override
  public void updateClob(String columnName, Clob x)
    throws SQLException {
    rs.updateClob(columnName, x);
  }

  // J2EE 1.6 specifics - comment out remainder of methods if you have to use java 1.5

  @Override
  public RowId getRowId(int i) throws SQLException {
    return rs.getRowId(i);
  }

  @Override
  public RowId getRowId(String s) throws SQLException {
    return rs.getRowId(s);
  }

  @Override
  public void updateRowId(int i, RowId rowId) throws SQLException {
    rs.updateRowId(i, rowId);
  }

  @Override
  public void updateRowId(String s, RowId rowId) throws SQLException {
    rs.updateRowId(s, rowId);
  }

  @Override
  public int getHoldability() throws SQLException {
    return rs.getHoldability();
  }

  @Override
  public boolean isClosed() throws SQLException {
    return rs.isClosed();
  }

  @Override
  public void updateNString(int i, String s) throws SQLException {
    rs.updateNString(i, s);
  }

  @Override
  public void updateNString(String s, String s1) throws SQLException {
    rs.updateNString(s, s1);
  }

  @Override
  public void updateNClob(int i, NClob nClob) throws SQLException {
    rs.updateNClob(i, nClob);
  }

  @Override
  public void updateNClob(String s, NClob nClob) throws SQLException {
    rs.updateNClob(s, nClob);
  }

  @Override
  public NClob getNClob(int i) throws SQLException {
    return rs.getNClob(i);
  }

  @Override
  public NClob getNClob(String s) throws SQLException {
    return rs.getNClob(s);
  }

  @Override
  public SQLXML getSQLXML(int i) throws SQLException {
    return rs.getSQLXML(i);
  }

  @Override
  public SQLXML getSQLXML(String s) throws SQLException {
    return rs.getSQLXML(s);
  }

  @Override
  public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
    rs.updateSQLXML(i, sqlxml);
  }

  @Override
  public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
    rs.updateSQLXML(s, sqlxml);
  }

  @Override
  public String getNString(int i) throws SQLException {
    return rs.getNString(i);
  }

  @Override
  public String getNString(String s) throws SQLException {
    return rs.getNString(s);
  }

  @Override
  public Reader getNCharacterStream(int i) throws SQLException {
    return rs.getNCharacterStream(i);
  }

  @Override
  public Reader getNCharacterStream(String s) throws SQLException {
    return rs.getNCharacterStream(s);
  }

  @Override
  public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {
    rs.updateNCharacterStream(i, reader, l);
  }

  @Override
  public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {
    rs.updateNCharacterStream(s, reader, l);
  }

  @Override
  public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
    rs.updateAsciiStream(i, inputStream, l);
  }

  @Override
  public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
    rs.updateBinaryStream(i, inputStream, l);
  }

  @Override
  public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {
    rs.updateCharacterStream(i, reader, l);
  }

  @Override
  public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
    rs.updateAsciiStream(s, inputStream, l);
  }

  @Override
  public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
    rs.updateBinaryStream(s, inputStream, l);
  }

  @Override
  public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {
    rs.updateCharacterStream(s, reader, l);
  }

  @Override
  public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {
    rs.updateBlob(i, inputStream, l);
  }

  @Override
  public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {
    rs.updateBlob(s, inputStream, l);
  }

  @Override
  public void updateClob(int i, Reader reader, long l) throws SQLException {
    rs.updateClob(i, reader, l);
  }

  @Override
  public void updateClob(String s, Reader reader, long l) throws SQLException {
    rs.updateClob(s, reader, l);
  }

  @Override
  public void updateNClob(int i, Reader reader, long l) throws SQLException {
    rs.updateNClob(i, reader, l);
  }

  @Override
  public void updateNClob(String s, Reader reader, long l) throws SQLException {
    rs.updateNClob(s, reader, l);
  }

  @Override
  public void updateNCharacterStream(int i, Reader reader) throws SQLException {
    rs.updateNCharacterStream(i, reader);
  }

  @Override
  public void updateNCharacterStream(String s, Reader reader) throws SQLException {
    rs.updateNCharacterStream(s, reader);
  }

  @Override
  public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
    rs.updateAsciiStream(i, inputStream);
  }

  @Override
  public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
    rs.updateBinaryStream(i, inputStream);
  }

  @Override
  public void updateCharacterStream(int i, Reader reader) throws SQLException {
    rs.updateCharacterStream(i, reader);
  }

  @Override
  public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {
    rs.updateAsciiStream(s, inputStream);
  }

  @Override
  public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {
    rs.updateBinaryStream(s, inputStream);
  }

  @Override
  public void updateCharacterStream(String s, Reader reader) throws SQLException {
    rs.updateCharacterStream(s, reader);
  }

  @Override
  public void updateBlob(int i, InputStream inputStream) throws SQLException {
    rs.updateBlob(i, inputStream);
  }

  @Override
  public void updateBlob(String s, InputStream inputStream) throws SQLException {
    rs.updateBlob(s, inputStream);
  }

  @Override
  public void updateClob(int i, Reader reader) throws SQLException {
    rs.updateClob(i, reader);
  }

  @Override
  public void updateClob(String s, Reader reader) throws SQLException {
    rs.updateClob(s, reader);
  }

  @Override
  public void updateNClob(int i, Reader reader) throws SQLException {
    rs.updateNClob(i, reader);
  }

  @Override
  public void updateNClob(String s, Reader reader) throws SQLException {
    rs.updateNClob(s, reader);
  }

  @Override
  public <T> T unwrap(Class<T> tClass) throws SQLException {
    return rs.unwrap(tClass);
  }

  @Override
  public boolean isWrapperFor(Class<?> aClass) throws SQLException {
    return rs.isWrapperFor(aClass);
  }

  // J2EE 1.7 specifics - comment out remainder of methods if you have to use java 1.6 or lower

  @Override
  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    return rs.getObject(columnIndex, type);
  }

  @Override
  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    return rs.getObject(columnLabel, type);
  }
}
