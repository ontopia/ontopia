
// $Id: JDBCUtils.java,v 1.5 2007/02/27 11:33:39 grove Exp $

package net.ontopia.topicmaps.db2tm;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.persistence.proxy.*;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Data source that reads tables via JDBC.
 */
public class JDBCUtils {

  static DateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
  static DateFormat df_datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  //! public static String getString(ResultSet rs, int ix, int sql_type) throws Exception {
  //!   String v = _getString(rs, ix, sql_type);
  //!   System.out.println("V: " + sql_type + " " + rs.getString(ix) + "->" + v);
  //!   return v;
  //! }

  //! public static int getDatatypeByName(String sql_type) throws Exception {
  //!   if (sql_type.equals("date"))
  //!     return Types.DATE;
  //!   else if(sql_type.equals("timestamp"))
  //!     return Types.DATETIME;
  //!   else if(sql_type.equals("varchar"))
  //!     return Types.VARCHAR;
  //!   else if(sql_type.equals("integer"))
  //!     return Types.INTEGER;
  //!   // TODO: complete list of datatypes
  //!   else
  //!     throw new DB2TMConfigException("Unsupported datatype: '" + sql_type);
  //! }
  
  public static String getString(ResultSet rs, int ix, int sql_type) throws Exception {
    switch (sql_type) {
    case Types.DATE:
      java.sql.Date date = rs.getDate(ix);
      if (date == null) return null;
      return df_date.format(date);
    case Types.TIMESTAMP:
      java.sql.Timestamp timestamp = rs.getTimestamp(ix);
      if (timestamp == null) return null;
      return df_datetime.format(timestamp);
    default:
      return rs.getString(ix);
    }
  }
  
  public static void setObject(PreparedStatement stmt, int ix, String value, int sql_type) throws Exception {
    if (value == null || value.length() == 0) {
      stmt.setNull(ix, sql_type);
    } else {
      switch (sql_type) {
      case Types.BIGINT:
        stmt.setLong(ix, Long.parseLong(value));
        break;
      case Types.VARCHAR:
      case Types.LONGVARCHAR:
      case Types.CHAR:
        //! if (value.length() > 500) {
        //!   System.out.println("L: " + value.length());
        //!   //stmt.setString(ix, value);
        //!   stmt.setString(ix, value.substring(0, 500) + "[cut]"); // HACK: to get around bug in oracle
        //! } else
          stmt.setString(ix, value);
        break;
      case Types.INTEGER:
        stmt.setInt(ix, Integer.parseInt(value));
        break;
      case Types.DOUBLE:
        stmt.setDouble(ix, Double.parseDouble(value));
        break;
      case Types.DECIMAL:
      case Types.FLOAT:
      case Types.REAL:
        stmt.setFloat(ix, Float.parseFloat(value));
        break;
      case Types.SMALLINT:
        stmt.setShort(ix, Short.parseShort(value));
        break;
      case Types.DATE:
        stmt.setDate(ix, new java.sql.Date(df_date.parse(value).getTime()));
        break;
      case Types.TIMESTAMP:
        stmt.setTimestamp(ix, new java.sql.Timestamp(df_datetime.parse(value).getTime()));
        break;
        //! case Types.BLOB:
      //!   try {
      //!     InputStream stream = (InputStream) value;
      //!     stmt.setBinaryStream(ix, stream, stream.available());
      //!   } catch (IOException ex) {
      //!     throw new SQLException(ex.toString());
      //!   }
      //!   break;
      case Types.CLOB:
        stmt.setCharacterStream(ix, new StringReader(value), value.length());
        break;
      default:
        stmt.setObject(ix, value, sql_type);
        break;
      }
    }    
  }

  public static String getHighPrecisionString(ResultSet rs, int ix, int sql_type) throws Exception {
    // HACK: to make DATE type include timestamp information if available
    switch (sql_type) {
    case Types.DATE:
      return getString(rs, ix, Types.TIMESTAMP);
    default:
      return getString(rs, ix, sql_type);
    }
  }
  
  public static void setHighPrecisionObject(PreparedStatement stmt, int ix, String value, int sql_type) throws Exception {
    // HACK: to make DATE type include timestamp information if available
    switch (sql_type) {
    case Types.DATE:
      setObject(stmt, ix, value, Types.TIMESTAMP);
      break;
    default:
      setObject(stmt, ix, value, sql_type);
      break;
    }
  }
  
}
