
package net.ontopia.topicmaps.db2tm;

import java.io.*;
import java.sql.*;
import java.text.*;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.persistence.proxy.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Data source that reads tables via JDBC.
 */
public class JDBCUtils {

  static DateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
  static DateFormat df_datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public static String getString(ResultSet rs, int ix, int sql_type)
    throws SQLException {
    switch (sql_type) {
    case Types.DATE:
      Date date = rs.getDate(ix);
      if (date == null) return null;
      return df_date.format(date);
    case Types.TIMESTAMP:
      Timestamp timestamp = rs.getTimestamp(ix);
      if (timestamp == null) return null;
      return df_datetime.format(timestamp);
    default:
      return rs.getString(ix);
    }
  }
  
  public static void setObject(PreparedStatement stmt, int ix, String value, int sql_type) throws SQLException {
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
        try {
          stmt.setDate(ix, new Date(df_date.parse(value).getTime()));
        } catch (ParseException e) {
          throw new OntopiaRuntimeException("Couldn't parse '" + value + "'", e);
        }
        break;
      case Types.TIMESTAMP:
        try {
          stmt.setTimestamp(ix, new Timestamp(df_datetime.parse(value).getTime()));
        } catch (ParseException e) {
          throw new OntopiaRuntimeException("Couldn't parse '" + value + "'", e);
        }
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
  
  public static void setHighPrecisionObject(PreparedStatement stmt, int ix, String value, int sql_type) throws SQLException {
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
