
// $Id: ContentReader.java,v 1.3 2008/05/30 12:46:40 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.io.FilterReader;
import java.io.Reader;
import java.io.IOException;

/**
 * INTERNAL: Reader that knows its length.
 *
 * @since 4.0
 */

public class ContentReader extends FilterReader {

  protected long length;
  protected long lengthRead;
  protected boolean closed;
  
  public ContentReader(Reader reader, long length) {
    super(reader);
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

  public int read(char[] cbuf) throws IOException {
    int result = super.read(cbuf);
    if (result != -1)
      lengthRead += result;
    if (result == -1 || lengthRead >= length)
      close();
    
    return result;
  }

  public int read(char[] cbuf, int off, int len) throws IOException {
    int result = super.read(cbuf, off, len);
    if (result != -1)
      lengthRead += result;
    if (result == -1 || lengthRead >= length) close();
    return result;
  }

  public void close() throws IOException {
    if (closed) return;
    closed = true;
		// IMPORTANT: don't call super here as this will close the
    // enclosed stream. we want the user to close it instead.
    //!super.close();
  }
  
}
