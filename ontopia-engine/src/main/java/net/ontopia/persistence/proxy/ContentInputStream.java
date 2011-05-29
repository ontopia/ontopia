
package net.ontopia.persistence.proxy;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * INTERNAL: InputStream that knows its length.
 *
 * @since 4.0
 */

public class ContentInputStream extends FilterInputStream {

  protected long length;
  protected long lengthRead;
  protected boolean closed;
  
  public ContentInputStream(InputStream stream, long length) {
    super(stream);
    this.length = length;
  }

  public long getLength() {
    return length;
  }

  public int read() throws IOException {
    int result = super.read();
    if (result != -1)
      lengthRead++;
    if (result == -1 || lengthRead >= length)
      close();
    return result;
  }

  public int read(byte[] bbuf) throws IOException {
    int result = super.read(bbuf);
    if (result != -1)
      lengthRead += result;
    if (result == -1 || lengthRead >= length)
      close();
    
    return result;
  }

  public int read(byte[] bbuf, int off, int len) throws IOException {
    int result = super.read(bbuf, off, len);
    if (result != -1)
      lengthRead += result;
    if (result == -1 || lengthRead >= length) close();
    return result;
  }

  public void close() throws IOException {
    if (closed) return;
    closed = true;
    super.close();
  }
  
}
