
// $Id: ReaderInputStream.java,v 1.1 2008/04/10 08:12:08 geir.gronmo Exp $

package net.ontopia.utils;

import java.io.*;

/**
 * INTERNAL: An InputStream stream that turns a Reader into an
 * InputStream given an encoding.
 */
public class ReaderInputStream extends InputStream {
  protected Reader reader;
  protected ByteArrayOutputStream byteArrayOut;
  protected Writer writer;
  protected char[] chars;
  protected byte[] buffer;
  protected int index, length;

  public ReaderInputStream(Reader reader) {
    this.reader = reader;
    byteArrayOut = new ByteArrayOutputStream();
    writer = new OutputStreamWriter(byteArrayOut);
    chars = new char[1024];
  }

  public ReaderInputStream(Reader reader, String encoding) throws UnsupportedEncodingException {
    this.reader = reader;
    byteArrayOut = new ByteArrayOutputStream();
    writer = new OutputStreamWriter(byteArrayOut, encoding);
    chars = new char[1024];
  }

  public int read() throws IOException {
    if (index >= length)
      fillBuffer();
    if (index >= length)
      return -1;
    return 0xff & buffer[index++];
  }

  protected void fillBuffer() throws IOException {
    if (length < 0)
      return;
    int numChars = reader.read(chars);
    if (numChars < 0) {
      length = -1;
    } else {
      byteArrayOut.reset();
      writer.write(chars, 0, numChars);
      writer.flush();
      buffer = byteArrayOut.toByteArray();
      length = buffer.length;
      index = 0;
    }
  }

  public int read(byte[] data, int off, int len) throws IOException {
    if (index >= length)
      fillBuffer();
    if (index >= length)
      return -1;
    int amount = Math.min(len, length - index);
    System.arraycopy(buffer, index, data, off, amount);
    index += amount;
    return amount;
  }

  public int available() throws IOException {
    return (index < length) ? length - index :
      ((length >= 0) && reader.ready()) ? 1 : 0;
  }

  public void close() throws IOException {
    reader.close();
  }
  
}
