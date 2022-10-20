/*
 * #!
 * Ontopia Content Store
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

package net.ontopia.infoset.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * INTERNAL: Content store implementation on top of JDBC that uses
 * native database sequences to generate keys. Note that this class
 * requires JDBC3.
 */

public class JDBCSequenceContentStore implements ContentStoreIF {

  protected Connection conn;
  
  protected String sql_get;
  protected String sql_put;
  protected String sql_remove;
  
  public JDBCSequenceContentStore(Connection conn) {
    this(conn, "TM_CONTENT_STORE", "rkey", "rvalue", "nextval('seq_content_store')");
  }

  public JDBCSequenceContentStore(Connection conn, String tblname, String keyname, String valname, String nextseq) {
    this.conn = conn;
    init(tblname, keyname, valname, nextseq);
  }

  protected void init(String tblname, String keyname, String valname, String nextseq) {
    this.sql_get = "select " + valname + " from " + tblname +" where " + keyname + " = ?";
    this.sql_put = "insert into " + tblname + " (" + keyname + ", " + valname + ") values (" + nextseq + ", ?)";
    this.sql_remove = "delete from " + tblname + " where " + keyname + " = ?";
  }

  @Override
  public boolean containsKey(int key) throws ContentStoreException {
    try {
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
        ps = conn.prepareStatement(sql_get);
        ps.setInt(1, key);
        try {
          rs = ps.executeQuery();
          return rs.next();
        } catch (SQLException e) {
          return false;
        }
      } finally {
        if (ps != null) {
          ps.close();
        }
        if (rs != null) {
          rs.close();
        }
      }
    } catch (Throwable t) {
      throw new ContentStoreException(t);
    }
  }

  @Override
  public ContentInputStream get(int key) throws ContentStoreException {
    try {
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
        ps = conn.prepareStatement(sql_get);
        ps.setInt(1, key);
      } catch (SQLException e) {
        if (ps != null) {
          ps.close();
        }      
        throw e;
      }
      
      try {
        rs = ps.executeQuery();
      } catch (SQLException e) {
        return null;
      }
    
      try {
        // NOTE: ps and rs will not get closed until binary input stream is closed.
        if (rs.next()) {
          InputStream istream = rs.getBinaryStream(1);
          return new JDBCBinaryInputStream(ps, rs, istream, readLength(istream));
        } else {
          return null;
        }
      } catch (SQLException e) {
        if (ps != null) {
          ps.close();
        }
        if (rs != null) {
          rs.close();
        }
        throw e;
      }
    } catch (Throwable t) {
      throw new ContentStoreException(t);
    }
  }

  protected int readLength(InputStream stream) throws IOException {
    byte[] b = new byte[4];
    if (stream.read(b) < 4) {
      throw new RuntimeException("Could not read content length.");
    }
    
    return
      ((b[3] & 0xFF) << 0) +
      ((b[2] & 0xFF) << 8) +
      ((b[1] & 0xFF) << 16) +
      ((b[0] & 0xFF) << 24);
  }

  @Override
  public int add(ContentInputStream data) throws ContentStoreException {
    return add(data, data.getLength());
  }  
  
  @Override
  public int add(InputStream data, int length) throws ContentStoreException {
    try {
      // Push length onto stream
      PushbackInputStream pstream = new PushbackInputStream(data, 4);
      byte[] b = new byte[4];    
      b[3] = (byte)(length >>> 0);
      b[2] = (byte)(length >>> 8);
      b[1] = (byte)(length >>> 16);
      b[0] = (byte)(length >>> 24);
      pstream.unread(b);

      // Add new entry
      PreparedStatement ps = null;
      try {
        ps = conn.prepareStatement(sql_put, Statement.RETURN_GENERATED_KEYS);
        ps.setBinaryStream(1, pstream, length + b.length);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
          return rs.getInt(1);
        } else {
          throw new RuntimeException("No keys were generated.");
        }
      } finally {
        if (ps != null) {
          ps.close();
        }
        if (pstream != null) {
          pstream.close();
        }
      }
    } catch (Throwable t) {
      throw new ContentStoreException(t);
    }
  }

  @Override
  public boolean remove(int key) throws ContentStoreException {
    try {
      PreparedStatement ps = null;
      try {
        ps = conn.prepareStatement(sql_remove);
        ps.setInt(1, key);
        int rows = ps.executeUpdate();
        return (rows >= 1);
      } finally {
        if (ps != null) {
          ps.close();
        }
      }
    } catch (Throwable t) {
      throw new ContentStoreException(t);
    }
  }

  @Override
  public void close() throws ContentStoreException {
    //! conn.close();
  }
}
