
// $Id: SpyResultSet.java,v 1.6 2008/05/09 10:17:21 geir.gronmo Exp $

package net.ontopia.persistence.jdbcspy;

import java.sql.*;
import java.util.Map;
import java.io.InputStream;
import java.io.Reader;
import java.util.Calendar;
import java.math.BigDecimal;
import java.net.URL;

/**
 * INTERNAL: 
 */

public class SpyResultSet implements ResultSet {

  SpyStatement stm;
  String sql;
  SpyStats stats;
  ResultSet rs;

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

  public int getConcurrency()
    throws SQLException {
    return rs.getConcurrency();
  }

  public int getFetchDirection()
    throws SQLException {
    return rs.getFetchDirection();
  }

  public int getFetchSize()
    throws SQLException {
    return rs.getFetchDirection();
  }

  public int getRow()
    throws SQLException {
    return rs.getRow();
  }

  public int getType()
    throws SQLException {
    return rs.getType();
  }

  public void afterLast()
    throws SQLException {
    rs.afterLast();
  }

  public void beforeFirst()
    throws SQLException {
    rs.beforeFirst();
  }

  public void cancelRowUpdates()
    throws SQLException {
    rs.cancelRowUpdates();
  }

  public void clearWarnings()
    throws SQLException {
    rs.clearWarnings();
  }

  public void close()
    throws SQLException {
    rs.close();
  }

  public void deleteRow()
    throws SQLException {
    rs.deleteRow();
  }

  public void insertRow()
    throws SQLException {
    rs.insertRow();
  }

  public void moveToCurrentRow()
    throws SQLException {
    rs.moveToCurrentRow();
  }

  public void moveToInsertRow()
    throws SQLException {
    rs.moveToInsertRow();
  }

  public void refreshRow()
    throws SQLException {
    rs.refreshRow();
  }

  public void updateRow()
    throws SQLException {
    rs.updateRow();
  }

  public boolean first()
    throws SQLException {
    return rs.first();
  }

  public boolean isAfterLast()
    throws SQLException {
    return rs.isAfterLast();
  }

  public boolean isBeforeFirst()
    throws SQLException {
    return rs.isBeforeFirst();
  }

  public boolean isFirst()
    throws SQLException {
    return rs.isFirst();
  }

  public boolean isLast()
    throws SQLException {
    return rs.isLast();
  }

  public boolean last()
    throws SQLException {
    return rs.last();
  }

  public boolean next()
    throws SQLException {
    long st = System.currentTimeMillis();
    boolean r = rs.next();
    stats.resultNext(this, r, st, System.currentTimeMillis());
    return r;
  }

  public boolean previous()
    throws SQLException {
    return rs.previous();
  }

  public boolean rowDeleted()
    throws SQLException {
    return rs.rowDeleted();
  }

  public boolean rowInserted()
    throws SQLException {
    return rs.rowInserted();
  }

  public boolean rowUpdated()
    throws SQLException {
    return rs.rowUpdated();
  }

  public boolean wasNull()
    throws SQLException {
    return rs.wasNull();
  }

  public byte getByte(int columnIndex)
    throws SQLException {
    return rs.getByte(columnIndex);
  }

  public double getDouble(int columnIndex)
    throws SQLException {
    return rs.getDouble(columnIndex);
  }

  public float getFloat(int columnIndex)
    throws SQLException {
    return rs.getFloat(columnIndex);
  }

  public int getInt(int columnIndex)
    throws SQLException {
    return rs.getInt(columnIndex);
  }

  public long getLong(int columnIndex)
    throws SQLException {
    return rs.getLong(columnIndex);
  }

  public short getShort(int columnIndex)
    throws SQLException {
    return rs.getShort(columnIndex);
  }

  public void setFetchDirection(int direction)
    throws SQLException {
    rs.setFetchDirection(direction);
  }

  public void setFetchSize(int rows)
    throws SQLException {
    rs.setFetchSize(rows);
  }
  
  public boolean absolute(int row)
    throws SQLException {
    return rs.absolute(row);
  }

  public boolean getBoolean(int columnIndex)
    throws SQLException {
    return rs.getBoolean(columnIndex);
  }

  public boolean relative(int rows)
    throws SQLException {
    return rs.relative(rows);
  }

  public byte[] getBytes(int columnIndex)
    throws SQLException {
    return rs.getBytes(columnIndex);
  }

  public InputStream getAsciiStream(int columnIndex)
    throws SQLException {
    return rs.getAsciiStream(columnIndex);
  }
  
  public InputStream getBinaryStream(int columnIndex)
    throws SQLException {
    return rs.getBinaryStream(columnIndex);
  }

  public InputStream getUnicodeStream(int columnIndex)
    throws SQLException {
    return rs.getUnicodeStream(columnIndex);
  }

  public void updateAsciiStream(int columnIndex, InputStream x, int length)
    throws SQLException {
    rs.updateAsciiStream(columnIndex, x, length);
  }
   
  public void updateBinaryStream(int columnIndex, InputStream x, int length)
    throws SQLException {
    rs.updateBinaryStream(columnIndex, x, length);
  }

  public Reader getCharacterStream(int columnIndex)
    throws SQLException {
    return rs.getCharacterStream(columnIndex);
  }

  public void updateCharacterStream(int columnIndex, Reader x, int length)
    throws SQLException {
    rs.updateCharacterStream(columnIndex, x, length);
  }

  public Object getObject(int columnIndex)
    throws SQLException {
    return rs.getObject(columnIndex);
  }
   
  public String getCursorName()
    throws SQLException {
    return rs.getCursorName();
  }

  public String getString(int columnIndex)
    throws SQLException {
    return rs.getString(columnIndex);
  }

  public void updateString(int columnIndex, String x)
    throws SQLException {
    rs.updateString(columnIndex, x);
  }
  
  public byte getByte(String columnName)
    throws SQLException {
    return rs.getByte(columnName);
  }

  public double getDouble(String columnName)
    throws SQLException {
    return rs.getDouble(columnName);
  }

  public float getFloat(String columnName)
    throws SQLException {
    return rs.getFloat(columnName);
  }

  public int findColumn(String columnName)
    throws SQLException {
    return rs.findColumn(columnName);
  }

  public int getInt(String columnName)
    throws SQLException {
    return rs.getInt(columnName);
  }

  public long getLong(String columnName)
    throws SQLException {
    return rs.getLong(columnName);
  }

  public short getShort(String columnName)
    throws SQLException {
    return rs.getShort(columnName);
  }

  public void updateNull(String columnName)
    throws SQLException {
    rs.updateNull(columnName);
  }

  public void updateNull(int columnIndex)
    throws SQLException {
    rs.updateNull(columnIndex);
  }
  

  public boolean getBoolean(String columnName)
    throws SQLException {
    return rs.getBoolean(columnName);
  }

  public byte[] getBytes(String columnName)
    throws SQLException {
    return rs.getBytes(columnName);
  }

  public void updateByte(String columnName, byte x)
    throws SQLException {
    rs.updateByte(columnName, x);
  }

  public void updateDouble(String columnName, double x)
    throws SQLException {
    rs.updateDouble(columnName, x);
  }

  public void updateFloat(String columnName, float x)
    throws SQLException {
    rs.updateFloat(columnName, x);
  }
   
  public void updateInt(String columnName, int x)
    throws SQLException {
    rs.updateInt(columnName, x);
  }

  public void updateLong(String columnName, long x)
    throws SQLException {
    rs.updateLong(columnName, x);
  }

  public void updateShort(String columnName, short x)
    throws SQLException {
    rs.updateShort(columnName, x);
  }

  public void updateBoolean(String columnName, boolean x)
    throws SQLException {
    rs.updateBoolean(columnName, x);
  }

  public void updateBytes(String columnName, byte[] x)
    throws SQLException {
    rs.updateBytes(columnName, x);
  }

  public BigDecimal getBigDecimal(int columnIndex)
    throws SQLException {
    return rs.getBigDecimal(columnIndex);
  }

  public BigDecimal getBigDecimal(int columnIndex, int x)
    throws SQLException {
    return rs.getBigDecimal(columnIndex, x);
  }

  public void updateBigDecimal(int columnIndex, BigDecimal x)
    throws SQLException {
    rs.updateBigDecimal(columnIndex, x);
  }

  public Array getArray(int columnIndex)
    throws SQLException {
    return rs.getArray(columnIndex);
  }

  public Blob getBlob(int columnIndex)
    throws SQLException {
    return rs.getBlob(columnIndex);
  }

  public Clob getClob(int columnIndex)
    throws SQLException {
    return rs.getClob(columnIndex);
  }
   
  public Date getDate(int columnIndex)
    throws SQLException {
    return rs.getDate(columnIndex);
  }
   
  public void updateDate(int columnIndex, Date x)
    throws SQLException {
    rs.updateDate(columnIndex, x);
  }

  public Ref getRef(int columnIndex)
    throws SQLException {
    return rs.getRef(columnIndex);
  }

  public ResultSetMetaData getMetaData()
    throws SQLException {
    return rs.getMetaData();
  }

  public SQLWarning getWarnings()
    throws SQLException {
    return rs.getWarnings();
  }

  public Statement getStatement()
    throws SQLException {
    return rs.getStatement();
  }

  public Time getTime(int columnIndex)
    throws SQLException {
    return rs.getTime(columnIndex);
  }

  public void updateTime(int columnIndex, Time x)
    throws SQLException {
    rs.updateTime(columnIndex, x);
  }

  public Timestamp getTimestamp(int columnIndex)
    throws SQLException {
    return rs.getTimestamp(columnIndex);
  }

  public void updateTimestamp(int columnIndex, Timestamp x)
    throws SQLException {
    rs.updateTimestamp(columnIndex, x);
  }

  public InputStream getAsciiStream(String columnName)
    throws SQLException {
    return rs.getAsciiStream(columnName);
  }

  public InputStream getBinaryStream(String columnName)
    throws SQLException {
    return rs.getBinaryStream(columnName);
  }

  public InputStream getUnicodeStream(String columnName)
    throws SQLException {
    return rs.getUnicodeStream(columnName);
  }

  public void updateAsciiStream(String columnName, InputStream x, int length)
    throws SQLException {
    rs.updateAsciiStream(columnName, x, length);
  }

  public void updateBinaryStream(String columnName, InputStream x, int length)
    throws SQLException {
    rs.updateBinaryStream(columnName, x, length);
  }

  public Reader getCharacterStream(String columnName)
    throws SQLException {
    return rs.getCharacterStream(columnName);
  }

  public void updateCharacterStream(String columnName, Reader x, int length)
    throws SQLException {
    rs.updateCharacterStream(columnName, x, length);
  }

  public Object getObject(String columnName)
    throws SQLException {
    return rs.getObject(columnName);
  }

  public void updateObject(String columnName, Object x)
    throws SQLException {
    rs.updateObject(columnName, x);
  }

  public void updateObject(String columnName, Object x, int scale)
    throws SQLException {
    rs.updateObject(columnName, x, scale);
  }

  public Object getObject(int columnIndex, Map map)
    throws SQLException {
    return rs.getObject(columnIndex, map);
  }

  public String getString(String columnName)
    throws SQLException {
    return rs.getString(columnName);
  }

  public void updateString(String columnName, String x)
    throws SQLException {
    rs.updateString(columnName, x);
  }

  public BigDecimal getBigDecimal(String columnName)
    throws SQLException {
    return rs.getBigDecimal(columnName);
  }

  public BigDecimal getBigDecimal(String columnName, int scale)
    throws SQLException {
    return rs.getBigDecimal(columnName, scale);
  }

  public void updateBigDecimal(String columnName, BigDecimal x)
    throws SQLException {
    rs.updateBigDecimal(columnName, x);
  }

  public Array getArray(String columnName)
    throws SQLException {
    return rs.getArray(columnName);
  }

  public Blob getBlob(String columnName)
    throws SQLException {
    return rs.getBlob(columnName);
  }

  public Clob getClob(String columnName)
    throws SQLException {
    return rs.getClob(columnName);
  }

  public Date getDate(String columnName)
    throws SQLException {
    return rs.getDate(columnName);
  }

  public void updateDate(String columnName, Date x)
    throws SQLException {
    rs.updateDate(columnName, x);
  }

  public Date getDate(int columnIndex, Calendar cal)
    throws SQLException {
    return rs.getDate(columnIndex, cal);
  }

  public Ref getRef(String columnName)
    throws SQLException {
    return rs.getRef(columnName);
  }

  public Time getTime(String columnName)
    throws SQLException {
    return rs.getTime(columnName);
  }

  public void updateTime(String columnName, Time x)
    throws SQLException {
    rs.updateTime(columnName, x);
  }

  public Time getTime(int columnIndex, Calendar cal)
    throws SQLException {
    return rs.getTime(columnIndex, cal);
  }

  public Timestamp getTimestamp(String columnName)
    throws SQLException {
    return rs.getTimestamp(columnName);
  }

  public void updateTimestamp(String columnName, Timestamp x)
    throws SQLException {
    rs.updateTimestamp(columnName, x);
  }

  public Timestamp getTimestamp(int columnIndex, Calendar cal)
    throws SQLException {
    return rs.getTimestamp(columnIndex, cal);
  }

  public Object getObject(String columnName, Map map)
    throws SQLException {
    return rs.getObject(columnName, map);
  }

  public Date getDate(String columnName, Calendar cal)
    throws SQLException {
    return rs.getDate(columnName, cal);
  }

  public Time getTime(String columnName, Calendar cal)
    throws SQLException {
    return rs.getTime(columnName, cal);
  }

  public Timestamp getTimestamp(String columnName, Calendar cal)
    throws SQLException {
    return rs.getTimestamp(columnName, cal);
  }

  // J2EE 1.4

  public URL getURL(int columnIndex)
    throws SQLException {
    return rs.getURL(columnIndex);
  }
  
  public void updateArray(int columnIndex, Array x)
    throws SQLException {
    rs.updateArray(columnIndex, x);
  }
  
  public void updateArray(String columnName, Array x)
    throws SQLException {
    rs.updateArray(columnName, x);
  }
  
  public void updateObject(int columnIndex, Object x)
    throws SQLException {
    rs.updateObject(columnIndex, x);
  }
  
  public void updateObject(int columnIndex, Object x, int scale)
    throws SQLException {
    rs.updateObject(columnIndex, x, scale);
  }
  
  public void updateByte(int columnIndex, byte x)
    throws SQLException {
    rs.updateByte(columnIndex, x);
  }
  
  public void updateDouble(int columnIndex, double x)
    throws SQLException {
    rs.updateDouble(columnIndex, x);
  }
  
  public void updateFloat(int columnIndex, float x)
    throws SQLException {
    rs.updateFloat(columnIndex, x);
  }
  
  public void updateInt(int columnIndex, int x)
    throws SQLException {
    rs.updateInt(columnIndex, x);
  }
  
  public void updateLong(int columnIndex, long x)
    throws SQLException {
    rs.updateLong(columnIndex, x);
  }
  
  public void updateShort(int columnIndex, short x)
    throws SQLException {
    rs.updateShort(columnIndex, x);
  }
  
  public void updateBoolean(int columnIndex, boolean x)
    throws SQLException {
    rs.updateBoolean(columnIndex, x);
  }
  
  public void updateBytes(int columnIndex, byte[] x)
    throws SQLException {
    rs.updateBytes(columnIndex, x);
  }

  // J2EE 1.4
   
  public void updateRef(int columnIndex, Ref x)
    throws SQLException {
    rs.updateRef(columnIndex, x);
  }

  public void updateRef(String columnName, Ref x)
    throws SQLException {
    rs.updateRef(columnName, x);
  }

  public URL getURL(String columnName)
    throws SQLException {
    return rs.getURL(columnName);
  }
   
  public void updateBlob(int columnIndex, Blob x)
    throws SQLException {
    rs.updateBlob(columnIndex, x);
  }
  
  public void updateClob(int columnIndex, Clob x)
    throws SQLException {
    rs.updateClob(columnIndex, x);
  }
  
  public void updateBlob(String columnName, Blob x)
    throws SQLException {
    rs.updateBlob(columnName, x);
  }
  
  public void updateClob(String columnName, Clob x)
    throws SQLException {
    rs.updateClob(columnName, x);
  }

}
