
// $Id: SpyPreparedStatement.java,v 1.8 2008/05/09 10:17:21 geir.gronmo Exp $

package net.ontopia.persistence.jdbcspy;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;
import java.math.BigDecimal;

/**
 * INTERNAL: 
 */

public class SpyPreparedStatement extends SpyStatement implements PreparedStatement {

  PreparedStatement pstm;
  String sql;

  public SpyPreparedStatement(SpyConnection conn, SpyStats stats, 
			      String sql, PreparedStatement pstm) {
    super(conn, stats, pstm);
    this.sql = sql;
    this.pstm = pstm;
  }

  public int executeUpdate()
    throws SQLException {
    long st = System.currentTimeMillis();
    int r = pstm.executeUpdate();
    stats.preparedExecuteUpdate(this, r, st, System.currentTimeMillis());
    return r;
  }

  public void addBatch()
    throws SQLException {
    pstm.addBatch();
  }

  public int[] executeBatch()
    throws SQLException {
    // overrided from SpyStatement because now we know the actual sql statements
    long st = System.currentTimeMillis();
    int[] r = stm.executeBatch();
    stats.statementExecuteBatch(this, sql, r.length, st, System.currentTimeMillis());
    return r;
  }

  public void clearParameters()
    throws SQLException {
    pstm.clearParameters();
  }

  public boolean execute()
    throws SQLException {
    long st = System.currentTimeMillis();
    boolean r = pstm.execute();
    stats.preparedExecute(this, st, System.currentTimeMillis());
    return r;
  }

  public void setByte(int parameterIndex, byte x)
    throws SQLException {
    pstm.setByte(parameterIndex, x);
  }

  public void setDouble(int parameterIndex, double x)
    throws SQLException {
    pstm.setDouble(parameterIndex, x);
  }

  public void setFloat(int parameterIndex, float x)
    throws SQLException {
    pstm.setFloat(parameterIndex, x);
  }

  public void setInt(int parameterIndex, int x)
    throws SQLException {
    pstm.setInt(parameterIndex, x);
  }

  public void setNull(int parameterIndex, int sqlType)
    throws SQLException {
    pstm.setNull(parameterIndex, sqlType);
  }

  public void setLong(int parameterIndex, long x)
    throws SQLException {
    pstm.setLong(parameterIndex, x);
  }

  public void setShort(int parameterIndex, short x)
    throws SQLException {
    pstm.setShort(parameterIndex, x);
  }

  public void setBoolean(int parameterIndex, boolean x)
    throws SQLException {
    pstm.setBoolean(parameterIndex, x);
  }

  public void setBytes(int parameterIndex, byte[] x)
    throws SQLException {
    pstm.setBytes(parameterIndex, x);
  }

  public void setAsciiStream(int parameterIndex,InputStream x, int length)
    throws SQLException {
    pstm.setAsciiStream(parameterIndex, x, length);
  }
  
  public void setBinaryStream(int parameterIndex, InputStream x, int length)
    throws SQLException {
    pstm.setBinaryStream(parameterIndex, x, length);
  }
  
  public void setUnicodeStream(int parameterIndex, InputStream x, int length)
    throws SQLException {
    pstm.setUnicodeStream(parameterIndex, x, length);
  }

  public void setCharacterStream(int parameterIndex, Reader x, int length)
    throws SQLException {
    pstm.setCharacterStream(parameterIndex, x, length);
  }

  public void setObject(int parameterIndex, Object x)
    throws SQLException {
    pstm.setObject(parameterIndex, x);
  }

  public void setObject(int parameterIndex, Object x, int targetSqlType)
    throws SQLException {
    pstm.setObject(parameterIndex, x, targetSqlType);
  }

  public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
    throws SQLException {
    pstm.setObject(parameterIndex, x, targetSqlType, scale);
  }

  public void setNull(int parameterIndex, int sqlType, String typeName)
    throws SQLException {
    pstm.setNull(parameterIndex, sqlType, typeName);
  }

  public void setString(int parameterIndex, String x)
    throws SQLException {
    pstm.setString(parameterIndex, x);
  }

  public void setBigDecimal(int parameterIndex, BigDecimal x)
    throws SQLException {
    pstm.setBigDecimal(parameterIndex, x);
  }

  public void setArray(int parameterIndex, Array x)
    throws SQLException {
    pstm.setArray(parameterIndex, x);
  }

  public void setBlob(int parameterIndex, Blob x)
    throws SQLException {
    pstm.setBlob(parameterIndex, x);
  }

  public void setClob(int parameterIndex, Clob x)
    throws SQLException {
    pstm.setClob(parameterIndex, x);
  }

  public void setDate(int parameterIndex, Date x)
    throws SQLException {
    pstm.setDate(parameterIndex, x);
  }

  public void setRef(int parameterIndex, Ref x)
    throws SQLException {
    pstm.setRef(parameterIndex, x);
  }

  public ResultSet executeQuery()
    throws SQLException {
    long st = System.currentTimeMillis();
    ResultSet r = new SpyResultSet(this, stats, pstm.executeQuery());
    stats.preparedExecuteQuery(this, st, System.currentTimeMillis());
    return r;
  }

  public ResultSetMetaData getMetaData()
    throws SQLException {
    return pstm.getMetaData();
  }

  public void setTime(int parameterIndex, Time x)
    throws SQLException {
    pstm.setTime(parameterIndex, x);
  }

  public void setTimestamp(int parameterIndex, Timestamp x)
    throws SQLException {
    pstm.setTimestamp(parameterIndex, x);
  }

  public void setDate(int parameterIndex, Date x, Calendar cal)
    throws SQLException {
    pstm.setDate(parameterIndex, x, cal);
  }

  public void setTime(int parameterIndex, Time x, Calendar cal)
    throws SQLException {
    pstm.setTime(parameterIndex, x, cal);
  }

  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
    throws SQLException {
    pstm.setTimestamp(parameterIndex, x, cal);
  }

  // J2EE 1.4

  public ParameterMetaData getParameterMetaData()
    throws SQLException {
    return pstm.getParameterMetaData();
  }

  public void setURL(int parameterIndex, URL x)
    throws SQLException {
    pstm.setURL(parameterIndex, x);
  }

}
