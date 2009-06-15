
// $Id: TologResultSet.java,v 1.3 2007/11/13 13:31:40 geir.gronmo Exp $

package net.ontopia.topicmaps.query.jdbc;

import java.sql.*;
import java.util.Map;
import java.io.InputStream;
import java.io.Reader;
import java.util.Calendar;
import java.math.BigDecimal;
import java.net.URL;
import net.ontopia.topicmaps.query.core.*;

/**
 * INTERNAL: 
 */

public class TologResultSet implements ResultSet {

  TologStatement stm;
  QueryResultIF rs;

  int rowNum;
  boolean wasNull;
  
  public TologResultSet(TologPreparedStatement stm, QueryResultIF rs) {
    this.stm = stm;
    this.rs = rs;
  }

  public TologResultSet(TologStatement stm, QueryResultIF rs) {
    this.stm = stm;
    this.rs = rs;
  }

  public int getConcurrency()
    throws SQLException {
    return ResultSet.CONCUR_READ_ONLY;
  }

  public int getFetchDirection()
    throws SQLException {
    return ResultSet.FETCH_FORWARD;
  }

  public int getFetchSize()
    throws SQLException {
    return Integer.MAX_VALUE;
  }

  public int getRow()
    throws SQLException {
    return rowNum;
  }

  public int getType()
    throws SQLException {
    return ResultSet.TYPE_FORWARD_ONLY;
  }

  public void afterLast()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void beforeFirst()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void cancelRowUpdates()
    throws SQLException {
  }

  public void clearWarnings()
    throws SQLException {
    // ignore
  }

  public void close()
    throws SQLException {
    stm.close(this);
    rs.close();
  }

  public void deleteRow()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void insertRow()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void moveToCurrentRow()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void moveToInsertRow()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void refreshRow()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateRow()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean first()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean isAfterLast()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean isBeforeFirst()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean isFirst()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean isLast()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean last()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean next()
    throws SQLException {
    boolean n = rs.next();
    if (n) rowNum++;
    return n;
  }

  public boolean previous()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean rowDeleted()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean rowInserted()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean rowUpdated()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean wasNull()
    throws SQLException {
    return wasNull;
  }

  public byte getByte(int columnIndex)
    throws SQLException {
    Byte x = (Byte)getValue(columnIndex);
    return x.byteValue();
  }

  public double getDouble(int columnIndex)
    throws SQLException {
    Double x = (Double)getValue(columnIndex);
    return x.doubleValue();
  }

  public float getFloat(int columnIndex)
    throws SQLException {
    Float x = (Float)getValue(columnIndex);
    return x.floatValue();
  }

  public int getInt(int columnIndex)
    throws SQLException {
    Integer x = (Integer)getValue(columnIndex);
    return x.intValue();
  }

  public long getLong(int columnIndex)
    throws SQLException {
    Long x = (Long)getValue(columnIndex);
    return x.longValue();
  }

  public short getShort(int columnIndex)
    throws SQLException {
    Short x = (Short)getValue(columnIndex);
    return x.shortValue();
  }

  public boolean getBoolean(int columnIndex)
    throws SQLException {
    Boolean x = (Boolean)getValue(columnIndex);
    return x.booleanValue();
  }

  public void setFetchDirection(int direction)
    throws SQLException {
    // ignore
  }

  public void setFetchSize(int rows)
    throws SQLException {
    // ignore
  }
  
  public boolean absolute(int row)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public boolean relative(int rows)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public byte[] getBytes(int columnIndex)
    throws SQLException {
    return (byte[])getValue(columnIndex);
  }

  public InputStream getAsciiStream(int columnIndex)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public InputStream getBinaryStream(int columnIndex)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public InputStream getUnicodeStream(int columnIndex)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateAsciiStream(int columnIndex, InputStream x, int length)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
   
  public void updateBinaryStream(int columnIndex, InputStream x, int length)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Reader getCharacterStream(int columnIndex)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateCharacterStream(int columnIndex, Reader x, int length)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Object getObject(int columnIndex)
    throws SQLException {
    return getValue(columnIndex);
  }
   
  public String getCursorName()
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public String getString(int columnIndex)
    throws SQLException {
    return (String)getValue(columnIndex);
  }

  public void updateString(int columnIndex, String x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public byte getByte(String columnName)
    throws SQLException {
    Byte x = (Byte)getValue(columnName);
    return x.byteValue();
  }

  public double getDouble(String columnName)
    throws SQLException {
    Double x = (Double)getValue(columnName);
    return x.doubleValue();
  }

  public float getFloat(String columnName)
    throws SQLException {
    Float x = (Float)getValue(columnName);
    return x.floatValue();
  }

  public int findColumn(String columnName)
    throws SQLException {
    return rs.getIndex(columnName);
  }

  public int getInt(String columnName)
    throws SQLException {
    Integer x = (Integer)getValue(columnName);
    return x.intValue();
  }

  public long getLong(String columnName)
    throws SQLException {
    Long x = (Long)getValue(columnName);
    return x.longValue();
  }

  public short getShort(String columnName)
    throws SQLException {
    Short x = (Short)getValue(columnName);
    return x.shortValue();
  }

  public void updateNull(String columnName)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateNull(int columnIndex)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  

  public boolean getBoolean(String columnName)
    throws SQLException {
    Boolean x = (Boolean)getValue(columnName);
    return x.booleanValue();
  }

  public byte[] getBytes(String columnName)
    throws SQLException {
    return (byte[])getValue(columnName);
  }

  public void updateByte(String columnName, byte x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateDouble(String columnName, double x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateFloat(String columnName, float x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
   
  public void updateInt(String columnName, int x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateLong(String columnName, long x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateShort(String columnName, short x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateBoolean(String columnName, boolean x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateBytes(String columnName, byte[] x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public BigDecimal getBigDecimal(int columnIndex)
    throws SQLException {
    return (BigDecimal)getValue(columnIndex);
  }

  public BigDecimal getBigDecimal(int columnIndex, int x)
    throws SQLException {
    return (BigDecimal)getValue(columnIndex); // fixme
  }

  public void updateBigDecimal(int columnIndex, BigDecimal x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Array getArray(int columnIndex)
    throws SQLException {
    return (Array)getValue(columnIndex);
  }

  public Blob getBlob(int columnIndex)
    throws SQLException {
    return (Blob)getValue(columnIndex);
  }

  public Clob getClob(int columnIndex)
    throws SQLException {
    return (Clob)getValue(columnIndex);
  }
   
  public Date getDate(int columnIndex)
    throws SQLException {
    return (Date)getValue(columnIndex);
  }
   
  public void updateDate(int columnIndex, Date x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Ref getRef(int columnIndex)
    throws SQLException {
    return (Ref)getValue(columnIndex);
  }

  public ResultSetMetaData getMetaData()
    throws SQLException {
    return new TologResultSetMetaData(this);
  }

  public SQLWarning getWarnings()
    throws SQLException {
    // todo
    return null;
  }

  public Statement getStatement()
    throws SQLException {
    return stm;
  }

  public Time getTime(int columnIndex)
    throws SQLException {
    return (Time)getValue(columnIndex);
  }

  public void updateTime(int columnIndex, Time x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Timestamp getTimestamp(int columnIndex)
    throws SQLException {
    return (Timestamp)getValue(columnIndex);
  }

  public void updateTimestamp(int columnIndex, Timestamp x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public InputStream getAsciiStream(String columnName)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public InputStream getBinaryStream(String columnName)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public InputStream getUnicodeStream(String columnName)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateAsciiStream(String columnName, InputStream x, int length)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateBinaryStream(String columnName, InputStream x, int length)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Reader getCharacterStream(String columnName)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateCharacterStream(String columnName, Reader x, int length)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Object getObject(String columnName)
    throws SQLException {
    return getValue(columnName);
  }

  public void updateObject(String columnName, Object x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public void updateObject(String columnName, Object x, int scale)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Object getObject(int columnIndex, Map map)
    throws SQLException {
    return getValue(columnIndex); // fixme
  }

  public String getString(String columnName)
    throws SQLException {
    return (String)getValue(columnName);
  }

  public void updateString(String columnName, String x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public BigDecimal getBigDecimal(String columnName)
    throws SQLException {
    return (BigDecimal)getValue(columnName);
  }

  public BigDecimal getBigDecimal(String columnName, int scale)
    throws SQLException {
    return (BigDecimal)getValue(columnName); // fixme
  }

  public void updateBigDecimal(String columnName, BigDecimal x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Array getArray(String columnName)
    throws SQLException {
    return (Array)getValue(columnName);
  }

  public Blob getBlob(String columnName)
    throws SQLException {
    return (Blob)getValue(columnName);
  }

  public Clob getClob(String columnName)
    throws SQLException {
    return (Clob)getValue(columnName);
  }

  public Date getDate(String columnName)
    throws SQLException {
    return (Date)getValue(columnName);
  }

  public void updateDate(String columnName, Date x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Date getDate(int columnIndex, Calendar cal)
    throws SQLException {
    return (Date)getValue(columnIndex); // fixme
  }

  public Ref getRef(String columnName)
    throws SQLException {
    return (Ref)getValue(columnName);
  }

  public Time getTime(String columnName)
    throws SQLException {
    return (Time)getValue(columnName);
  }

  public void updateTime(String columnName, Time x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Time getTime(int columnIndex, Calendar cal)
    throws SQLException {
    return (Time)getValue(columnIndex); // fixme
  }

  public Timestamp getTimestamp(String columnName)
    throws SQLException {
    return (Timestamp)getValue(columnName);
  }

  public void updateTimestamp(String columnName, Timestamp x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }

  public Timestamp getTimestamp(int columnIndex, Calendar cal)
    throws SQLException {
    return (Timestamp)getValue(columnIndex); // fixme
  }

  public Object getObject(String columnName, Map map)
    throws SQLException {
    return getValue(columnName); // fixme
  }

  public Date getDate(String columnName, Calendar cal)
    throws SQLException {
    return (Date)getValue(columnName); // fixme
  }

  public Time getTime(String columnName, Calendar cal)
    throws SQLException {
    return (Time)getValue(columnName); // fixme
  }

  public Timestamp getTimestamp(String columnName, Calendar cal)
    throws SQLException {
    return (Timestamp)getValue(columnName); // fixme
  }

  // J2EE 1.4

  //! public URL getURL(int columnIndex)
  //!   throws SQLException {
  //!   return rs.getURL(columnIndex);
  //! }
  //! 
  //! public void updateArray(int columnIndex, Array x)
  //!   throws SQLException {
  //!   rs.updateArray(columnIndex, x);
  //! }
  //! 
  //! public void updateArray(String columnName, Array x)
  //!   throws SQLException {
  //!   rs.updateArray(columnName, x);
  //! }
  
  public void updateObject(int columnIndex, Object x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public void updateObject(int columnIndex, Object x, int scale)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public void updateByte(int columnIndex, byte x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public void updateDouble(int columnIndex, double x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public void updateFloat(int columnIndex, float x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public void updateInt(int columnIndex, int x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public void updateLong(int columnIndex, long x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public void updateShort(int columnIndex, short x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public void updateBoolean(int columnIndex, boolean x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
  
  public void updateBytes(int columnIndex, byte[] x)
    throws SQLException {
    throw new SQLException("Not supported.");
  }
   
  //! public void updateRef(int columnIndex, Ref x)
  //!   throws SQLException {
  //!   rs.updateRef(columnIndex, x);
  //! }

  //! public void updateRef(String columnName, Ref x)
  //!   throws SQLException {
  //!   rs.updateRef(columnName, x);
  //! }

  //! public URL getURL(String columnName)
  //!   throws SQLException {
  //!   return rs.getURL(columnName);
  //! }
  //!  
  //! public void updateBlob(int columnIndex, Blob x)
  //!   throws SQLException {
  //!   rs.updateBlob(columnIndex, x);
  //! }
  //! 
  //! public void updateClob(int columnIndex, Clob x)
  //!   throws SQLException {
  //!   rs.updateClob(columnIndex, x);
  //! }
  //! 
  //! public void updateBlob(String columnName, Blob x)
  //!   throws SQLException {
  //!   rs.updateBlob(columnName, x);
  //! }
  //! 
  //! public void updateClob(String columnName, Clob x)
  //!   throws SQLException {
  //!   rs.updateClob(columnName, x);
  //! }

  // --- Helpers

  private Object getValue(int columnIndex) {
    Object x = rs.getValue(columnIndex-1);
    if (x == null) this.wasNull = true;
    return x;
  }

  private Object getValue(String columnName) {
    Object x = rs.getValue(columnName);
    if (x == null) this.wasNull = true;
    return x;
  }

  int getColumnCount() {
    return rs.getWidth();
  }

  String getColumnName(int columnIndex) {
    return rs.getColumnName(columnIndex-1);
  }
}
