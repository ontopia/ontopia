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

package net.ontopia.persistence.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Utility class for handling access to columns by proper
 * SQL type. Note that all type integers are the same as the ones in
 * java.sql.Types.
 */

public class SQLTypes {

  public static final int SIZE_THRESHOLD = 32768; // 65536;
  
  //! /**
  //!  * INTERNAL: Gets the SQL type mapped to the given java class
  //!  * referenced by the field descriptor.
  //!  */
  //! public static int getType(FieldDescriptor fdesc) {
  //!   return getType(fdesc.getValueClass());
  //! }

  /**
   * INTERNAL: Gets the SQL type mapped to the given java class.
   */
  public static int getType(Class klass) {
    // FIXME: the SQL type retrieval should be configurable, since it
    // is often database specific. See also the TypeConverterIF
    // interface.
    
    // Figure out the SQL type
    if (klass.equals(String.class))
      return Types.VARCHAR;
    else if (klass.equals(Long.class))
      return Types.BIGINT;
    else if (klass.equals(Integer.class))
      return Types.INTEGER;
    else if (klass.equals(Float.class))
      return Types.FLOAT;
    else if (klass.equals(Double.class))
      return Types.DOUBLE;
    else if (klass.equals(Boolean.class))
      return Types.SMALLINT;
    else if (klass.equals(Character.class))
      return Types.CHAR;
    else if (klass.equals(Short.class))
      return Types.BIT;
    else if (klass.equals(Byte.class))
      return Types.TINYINT;
    else if (klass.equals(Reader.class))
      return Types.CLOB;
    else
      throw new OntopiaRuntimeException("Cannot map value type " + klass + " to SQL type.");           
  }

  /**
   * INTERNAL: Gets the java class mapped to by the given SQL type.
   */
  public static Class getType(int sql_type) {
    // FIXME: the SQL type retrieval should be configurable, since it
    // is often database specific. See also the TypeConverterIF
    // interface.
    
    // Figure out the SQL type
    switch (sql_type) {
    case Types.VARCHAR:
      return String.class;
    case Types.BIGINT:
      return Long.class;
    case Types.INTEGER:
      return Integer.class;
    case Types.FLOAT:
      return Float.class;
    case Types.DOUBLE:
      return Double.class;
    case Types.SMALLINT:
      return Boolean.class;
    case Types.CHAR:
      return Character.class;
    case Types.BIT:
      return Short.class;
    case Types.TINYINT:
      return Byte.class;
    case Types.CLOB:
      return Reader.class;
    default:
      throw new OntopiaRuntimeException("Cannot map SQL type " + sql_type + " to value type.");
    }
  }

  /**
   * INTERNAL: Reads the object of the given type at the specified
   * index from the result set.
   */
  public static Object getObject(ResultSet rs, int index, int sql_type, boolean direct) throws SQLException {
    Object value;
    long longVal;
    int intVal;
    boolean boolVal;
    double doubleVal;
    float floatVal;
    short shortVal;
    byte byteVal;

    switch (sql_type) {
    case Types.BIGINT:
      longVal = rs.getLong(index);
      return (rs.wasNull() ? null : new Long(longVal));
    case Types.VARCHAR:
    case Types.LONGVARCHAR:
    case Types.CHAR:
      return rs.getString(index);
    case Types.DECIMAL:
    case Types.NUMERIC:
      return rs.getBigDecimal(index);
    case Types.INTEGER:
      intVal = rs.getInt(index);
      return (rs.wasNull() ? null : new Integer(intVal));
    case Types.TIME:
      return rs.getTime(index);
    case Types.DATE:
      return rs.getDate(index);
    case Types.TIMESTAMP:
      return rs.getTimestamp(index);
    case Types.DOUBLE:
      doubleVal = rs.getDouble(index);
      return (rs.wasNull() ? null : new Double(doubleVal));
    case Types.FLOAT:
    case Types.REAL:
      floatVal = rs.getFloat(index);
      return (rs.wasNull() ? null : new Float(floatVal));
    case Types.SMALLINT:
      shortVal = rs.getShort(index);
      return (rs.wasNull() ? null : new Short(shortVal));
    case Types.TINYINT:
      byteVal = rs.getByte(index);
      return (rs.wasNull() ? null : new Byte(byteVal));
    case Types.LONGVARBINARY:
    case Types.VARBINARY:
    case Types.BINARY:
      return rs.getBytes(index);
    case Types.BLOB: {
      try {
        if (direct) {
          return rs.getBinaryStream(index);        
        } else  {
          // attempt to turn into a string
          InputStream reader = rs.getBinaryStream(index);
          if (reader == null) return null;
          byte[] bytes = new byte[SQLTypes.SIZE_THRESHOLD];
          int lengthRead = reader.read(bytes);
          if (lengthRead <= SQLTypes.SIZE_THRESHOLD && reader.read() == -1) {
						if (lengthRead > 0) {
							byte[] result = new byte[lengthRead];
							System.arraycopy(bytes, 0, result, 0, result.length);
							return result;
						} else if (lengthRead == 0)
							return new byte[] { };
						else
							return null;
          } else {
            return new OnDemandValue();
          }
        }
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    case Types.CLOB: {
      try {
        if (direct) {
          return rs.getCharacterStream(index);        
        } else  {
          // attempt to turn into a string
          Reader reader = rs.getCharacterStream(index);
          if (reader == null) return null;
          char[] chars = new char[SQLTypes.SIZE_THRESHOLD];
          int lengthRead = reader.read(chars);
          if (lengthRead <= SQLTypes.SIZE_THRESHOLD && reader.read() == -1) {
            if (lengthRead > 0) {
              return new String(chars, 0, lengthRead);
            } else {
              return "";
            }
          } else {
            return new OnDemandValue();
          }
        }
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    case Types.BIT:
      boolVal = rs.getBoolean(index);
      return (rs.wasNull() ? null : (boolVal ? Boolean.TRUE : Boolean.FALSE));
    default:
      value = rs.getObject(index);
      return (rs.wasNull()? null : value);
    }
  }


  /**
   * INTERNAL: Binds the object of the given type at the specified
   * index in the prepared statement.
   */
  public static void setObject(PreparedStatement stmt, int index, Object value, int sql_type) throws SQLException {
    if (value == null) {
      stmt.setNull(index, sql_type);
    } else {
      // Special processing for BLOB and CLOB types, because they
      // should be mapped to java.io.InputStream and java.io.Reader,
      // respectively, while JDBC driver expects java.sql.Blob and
      // java.sql.Clob.
      switch (sql_type) {
      case Types.BIGINT:
        stmt.setLong(index, ((Long)value).longValue());
        break;
      case Types.VARCHAR:
      case Types.LONGVARCHAR:
      case Types.CHAR:
        stmt.setString(index, (String)value);
        break;
      case Types.INTEGER:
        stmt.setInt(index, ((Integer)value).intValue());
        break;
      case Types.DOUBLE:
        stmt.setDouble(index, ((Double)value).doubleValue());
        break;
      case Types.FLOAT:
      case Types.REAL:
        stmt.setFloat(index, ((Float)value).floatValue());
        break;
      case Types.SMALLINT:
        stmt.setShort(index, ((Short)value).shortValue());
        break;
      case Types.BLOB:
        if (value instanceof byte[]) {
          //stmt.setString(index, (String)value);
          byte[] b = (byte[])value;
          stmt.setBinaryStream(index, new ByteArrayInputStream(b), b.length);
        } else if (value instanceof OnDemandValue) {
          OnDemandValue odv = (OnDemandValue)value;
          ContentInputStream blob = (ContentInputStream)odv.getValue();
          if (blob != null) 
            stmt.setBinaryStream(index, blob, (int)blob.getLength());
          else
            stmt.setNull(index, sql_type);
        }
        break;
      case Types.CLOB:
        if (value instanceof String) {
          //stmt.setString(index, (String)value);
          String s = (String)value;
          stmt.setCharacterStream(index, new StringReader(s), s.length());
        } else if (value instanceof OnDemandValue) {
          OnDemandValue odv = (OnDemandValue)value;
          ContentReader clob = (ContentReader)odv.getValue();
          if (clob != null)
            stmt.setCharacterStream(index, clob, (int)clob.getLength());
          else
            stmt.setNull(index, sql_type);
        } else {
          throw new OntopiaRuntimeException("Unsupported CLOB value: " + value);
        }
        break;
      default:
        stmt.setObject(index, value, sql_type);
        break;
      }
    }
  }
  
}





