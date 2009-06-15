
// $Id: TologPreparedStatement.java,v 1.3 2007/11/13 13:31:40 geir.gronmo Exp $

package net.ontopia.topicmaps.query.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

/**
 * INTERNAL: 
 */

public class TologPreparedStatement extends TologStatement implements PreparedStatement {

  String sql;
  Map params = new HashMap();
  
  public TologPreparedStatement(TologConnection conn, String sql) {
    super(conn);
    this.sql = sql;
  }

  public TologPreparedStatement(TologConnection conn, String sql, int resultSetType, int resultSetConcurrency) {
    super(conn);
    this.sql = sql;
  }

  public int executeUpdate()
    throws SQLException {
    throw new SQLException("Not supported: executeUpdate()");
  }

  public void addBatch()
    throws SQLException {
    throw new SQLException("Not supported: addBatch()");
  }

  public int[] executeBatch()
    throws SQLException {
    throw new SQLException("Not supported: executeBatch()");
  }

  public void clearParameters()
    throws SQLException {
    params.clear();
  }

  public boolean execute()
    throws SQLException {
    throw new SQLException("Not supported: execute()");
  }

  public void setByte(int parameterIndex, byte x)
    throws SQLException {
    registerParameter(parameterIndex, new Byte(x));
  }

  public void setDouble(int parameterIndex, double x)
    throws SQLException {
    registerParameter(parameterIndex, new Double(x));
  }

  public void setFloat(int parameterIndex, float x)
    throws SQLException {
    registerParameter(parameterIndex, new Float(x));
  }

  public void setInt(int parameterIndex, int x)
    throws SQLException {
    registerParameter(parameterIndex, new Integer(x));
  }

  public void setNull(int parameterIndex, int sqlType)
    throws SQLException {
    registerParameter(parameterIndex, null);
  }

  public void setLong(int parameterIndex, long x)
    throws SQLException {
    registerParameter(parameterIndex, new Long(x));
  }

  public void setShort(int parameterIndex, short x)
    throws SQLException {
    registerParameter(parameterIndex, new Short(x));
  }

  public void setBoolean(int parameterIndex, boolean x)
    throws SQLException {
    registerParameter(parameterIndex, new Boolean(x));
  }

  public void setBytes(int parameterIndex, byte[] x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setAsciiStream(int parameterIndex,InputStream x, int length)
    throws SQLException {
    throw new SQLException("Not supported: setAsciiStream(int,InputStream,int)");
  }
  
  public void setBinaryStream(int parameterIndex, InputStream x, int length)
    throws SQLException {
    throw new SQLException("Not supported: setBinaryStream(int,InputStream,int)");
  }
  
  public void setUnicodeStream(int parameterIndex, InputStream x, int length)
    throws SQLException {
    throw new SQLException("Not supported: setUnicodeStream(int,InputStream,int)");
  }

  public void setCharacterStream(int parameterIndex, Reader x, int length)
    throws SQLException {
    throw new SQLException("Not supported: setCharacterStream(int,InputStream,int)");
  }

  public void setObject(int parameterIndex, Object x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setObject(int parameterIndex, Object x, int targetSqlType)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setNull(int parameterIndex, int sqlType, String typeName)
    throws SQLException {
    registerParameter(parameterIndex, null);
  }

  public void setString(int parameterIndex, String x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setBigDecimal(int parameterIndex, BigDecimal x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setArray(int parameterIndex, Array x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setBlob(int parameterIndex, Blob x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setClob(int parameterIndex, Clob x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setDate(int parameterIndex, Date x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setRef(int parameterIndex, Ref x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public ResultSet executeQuery()
    throws SQLException {
    return conn.executeQuery(this, sql, params);
  }

  public ResultSetMetaData getMetaData()
    throws SQLException {
    return new TologResultSetMetaData(this.rs);
  }

  public void setTime(int parameterIndex, Time x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setTimestamp(int parameterIndex, Timestamp x)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setDate(int parameterIndex, Date x, Calendar cal)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setTime(int parameterIndex, Time x, Calendar cal)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
    throws SQLException {
    registerParameter(parameterIndex, x);
  }

  // J2EE 1.4

  //! public ParameterMetaData getParameterMetaData()
  //!   throws SQLException {
  //!   return pstm.getParameterMetaData();
  //! }

  //! public void setURL(int parameterIndex, URL x)
  //!   throws SQLException {
  //!   pstm.setURL(parameterIndex, x);
  //! }

  // --- Helpers

  private void registerParameter(int parameterIndex, Object val) {
    params.put(Integer.toString(parameterIndex), val);
  }

}
