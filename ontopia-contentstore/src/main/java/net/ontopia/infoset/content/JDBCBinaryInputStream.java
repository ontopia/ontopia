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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * INTERNAL: 
 */

public class JDBCBinaryInputStream extends ContentInputStream {

  protected PreparedStatement ps;
  protected ResultSet rs;
  
  JDBCBinaryInputStream(PreparedStatement ps, ResultSet rs, InputStream stream, int length) {
    super(stream, length);
    this.ps = ps;
    this.rs = rs;
  }

  @Override
  public int available() throws IOException {
    return stream.available();
  }

  @Override
  public void close() throws IOException {
    try {
      stream.close();
    } finally {
      try {
        if (ps != null) {
          ps.close();
          ps = null;
        }
        if (rs != null) {
          rs.close();
          rs = null;
        }
      } catch (SQLException e) {
        throw new IOException(e.getMessage());
      }
    }
  }
  
  @Override
  public void mark(int readlimit) {
    stream.mark(readlimit);
  }

  @Override
  public boolean markSupported() {
    return stream.markSupported();
  }
  
  @Override
  public int read() throws IOException {
    return stream.read();
  }
  
  @Override
  public int read(byte[] b) throws IOException {
    return stream.read(b);
  }
  
  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return stream.read(b, off, len);
  }
  
  @Override
  public void reset() throws IOException {
    stream.reset();
  }
  
  @Override
  public long skip(long n) throws IOException {
    return stream.skip(n);
  }
  
}
