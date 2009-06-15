
// $Id: TologResultSetMetaData.java,v 1.1 2007/11/13 12:45:23 geir.gronmo Exp $

package net.ontopia.topicmaps.query.jdbc;

import java.sql.*;

public class TologResultSetMetaData implements ResultSetMetaData {

  TologResultSet rs;
  
  public TologResultSetMetaData(TologResultSet rs) {
    this.rs = rs;
  }
  
  public int getColumnCount() throws SQLException {
    return rs.getColumnCount();
  }
  
  public boolean isAutoIncrement(int column) throws SQLException {
    return false;
  }

  public boolean isCaseSensitive(int column) throws SQLException {
    return true;
  }

  public boolean isSearchable(int column) throws SQLException {
    return false;
  }

  public boolean isCurrency(int column) throws SQLException {
    return false;
  }

  public int isNullable(int column) throws SQLException {
    return ResultSetMetaData.columnNullable;
  }

  public boolean isSigned(int column) throws SQLException {
    return false;
  }

  public int getColumnDisplaySize(int column) throws SQLException {
    return 50;
  }

  public String getColumnLabel(int column) throws SQLException {
    return rs.getColumnName(column);
  }

  public String getColumnName(int column) throws SQLException {
    return rs.getColumnName(column);
  }

  public String getSchemaName(int column) throws SQLException {
    return "schema";
  }

  public int getPrecision(int column) throws SQLException {
    return 2;
  }

  public int getScale(int column) throws SQLException {
    return 2;
  }

  public String getTableName(int column) throws SQLException {
    return "table";
  }

  public String getCatalogName(int column) throws SQLException {
    return "catalog";
  }

  public int getColumnType(int column) throws SQLException {
    return Types.JAVA_OBJECT;
  }

  public String getColumnTypeName(int column) throws SQLException {
    return "java.lang.Object";
  }

  public boolean isReadOnly(int column) throws SQLException {
    return true;
  }

  public boolean isWritable(int column) throws SQLException {
    return false;
  }

  public boolean isDefinitelyWritable(int column) throws SQLException {
    return false;
  }

  public String getColumnClassName(int column) throws SQLException {
    return "java.lang.Object";
  }

  
}
