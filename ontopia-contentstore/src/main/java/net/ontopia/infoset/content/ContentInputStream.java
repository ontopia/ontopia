
package net.ontopia.infoset.content;

import java.io.IOException;
import java.io.InputStream;

/**
 * INTERNAL: 
 */

public class ContentInputStream extends InputStream {

  protected InputStream stream;
  protected int length;
  
  public ContentInputStream(InputStream stream, int length) {
    this.stream = stream;
    this.length = length;
  }

  public int getLength() {
    return length;
  }
  
  // delegate calls to InputStream
  
  public int available() throws IOException {
    return stream.available();
  }

  public void close() throws IOException {
    stream.close();
  }
  
  public void mark(int readlimit) {
    stream.mark(readlimit);
  }

  public boolean markSupported() {
    return stream.markSupported();
  }
  
  public int read() throws IOException {
    return stream.read();
  }
  
  public int read(byte[] b) throws IOException {
    return stream.read(b);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    return stream.read(b, off, len);
  }
  
  public void reset() throws IOException {
    stream.reset();
  }
  
  public long skip(long n) throws IOException {
    return stream.skip(n);
  }
  
}
