
package net.ontopia.persistence.proxy;

import java.io.IOException;
import java.io.FilterReader;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * INTERNAL: Reader that knows its length.
 *
 * @since 4.0
 */

public class SQLReader extends FilterReader {

  protected ResultSet rs;
  protected Statement stm;
  
  public SQLReader(Reader reader, ResultSet rs, Statement stm) throws SQLException {
    super(reader);
    this.rs = rs;
    this.stm = stm;
  }

  public int read() throws IOException {
    int result = super.read();
    if (result == -1) close();
    return result;
  }

  public int read(char[] cbuf) throws IOException {
    int result = super.read(cbuf);
    //! if (result == -1 || result < cbuf.length) close();
    return result;
  }

  public int read(char[] cbuf, int off, int len) throws IOException {
    int result = super.read(cbuf, off, len);
    //! if (result == -1 || result < len) close();
    return result;
  }

  public void close() throws IOException {
    try {
      if (rs != null) {
        rs.close();
        rs = null;
      }
    } catch (Exception e) {
      // ignore
    }
    try {
      if (stm != null) {
        stm.close();
        stm = null;
      }
    } catch (Exception e) {
      // ignore
    }    
    super.close();
  }
  
}
