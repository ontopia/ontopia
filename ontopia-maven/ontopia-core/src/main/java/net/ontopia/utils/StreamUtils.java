
package net.ontopia.utils;

import java.io.*;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Utilities for working with streams and readers.
 * @since 1.3.3
 */
public class StreamUtils {
  
  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(StreamUtils.class.getName());
  
  static final int BUFFER_SIZE = 16384;
  
  /**
   * INTERNAL: Transfers the entire contents of the InputStream to the
   * OutputStream without modifying them in any way.   
   */
  public static void transfer(InputStream in, OutputStream out)
    throws IOException {

    byte[] buf = new byte[BUFFER_SIZE];
    while (true) {
      int read = in.read(buf, 0, BUFFER_SIZE);
      if (read == -1)
        break;

      out.write(buf, 0, read);
    }
  }
  
  /**
   * INTERNAL: Transfers the entire contents of the Reader to the
   * Writer without modifying them in any way.
   *
   * @since 2.0.4
   */
  public static void transfer(Reader in, Writer out)
    throws IOException {

    char[] buf = new char[BUFFER_SIZE];
    while (true) {
      int read = in.read(buf, 0, BUFFER_SIZE);
      if (read == -1)
        break;

      out.write(buf, 0, read);
    }
  }
  
  /**
   * INTERNAL: Returns the entire contents of a stream (well, Reader)
   * as a string.
   * @since 1.4
   */
  public static String read(Reader in) throws IOException {
    StringBuilder buffer = new StringBuilder();
    char[] buf = new char[BUFFER_SIZE];
    while (true) {
      int read = in.read(buf, 0, BUFFER_SIZE);
      if (read == -1)
        break;

      buffer.append(buf, 0, read);
    }
    
    return buffer.toString();
  }

  /**
   * INTERNAL: Returns the first given number of bytes of a stream
   * into a byte array.
   * @since 2.0
   */
  public static byte[] read(InputStream in, int length) throws IOException {
    // ISSUE: Why not just copy it all in one go?
    byte[] data = new byte[length];
    int ix = 0;
    int len = Math.min(BUFFER_SIZE, data.length);
    while (len > 0 && in.read(data, ix, len) != -1) {
      ix += len;
      len = Math.min(BUFFER_SIZE, data.length - ix);
    }
    return data;
  }

  /**
   * INTERNAL: Returns the entire contents of a stream as a byte array.   
   * @since 3.3.0
   */
  public static byte[] read(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
    transfer(in, out);
    return out.toByteArray();
  }

  /**
   * INTERNAL: Compares the contents of the two InputStreams for equality.
   * @since 4.0
   */
  public static boolean compare(InputStream r1, InputStream r2) throws IOException {

    byte[] buf1 = new byte[StreamUtils.BUFFER_SIZE];
    byte[] buf2 = new byte[StreamUtils.BUFFER_SIZE];
    while (true) {
      int read1 = r1.read(buf1, 0, StreamUtils.BUFFER_SIZE);
      int read2 = r2.read(buf2, 0, StreamUtils.BUFFER_SIZE);
      // compare lengths read
      if (read1 != read2)
        return false;
      // stop if we've reached the end
      if (read1 == -1)
        break;
      // compare read buffers
      for (int i=0; i < read1; i++) {
        if (buf1[i] != buf2[i]) return false;
      }      
    }
    return true;
  }

  /**
   * INTERNAL: Compares the contents of the two InputStream for equality 
   * and closes them afterwards.
   */
  public static boolean compareAndClose(InputStream r1, InputStream r2) throws IOException {
    try {
      try {
        return compare(r1, r2);
      } finally {
        r1.close();
      }
    } finally {
      r2.close();
    }
  }

  /**
   * INTERNAL: Compares the contents of the two Readers for equality.
   * @since 4.0
   */
  public static boolean compare(Reader r1, Reader r2) throws IOException {

    char[] buf1 = new char[StreamUtils.BUFFER_SIZE];
    char[] buf2 = new char[StreamUtils.BUFFER_SIZE];
    while (true) {
      int read1 = r1.read(buf1, 0, StreamUtils.BUFFER_SIZE);
      int read2 = r2.read(buf2, 0, StreamUtils.BUFFER_SIZE);
      // compare lengths read
      if (read1 != read2)
        return false;
      // stop if we've reached the end
      if (read1 == -1)
        break;
      // compare read buffers
      for (int i=0; i < read1; i++) {
        if (buf1[i] != buf2[i]) return false;
      }      
    }
    return true;
  }

  /**
   * INTERNAL: Same as getInputStream(null, name);
   *
   * @since 3.4.3
   */
  public static InputStream getInputStream(String name) throws IOException {
    return getInputStream(null, name);
  }

  /**
   * INTERNAL: Returns an input stream for the given url. Supports
   * file: and classpath:. If no scheme given then file system will be
   * checked before classpath. Exception will be thrown if resource is
   * not found when scheme is given. If no scheme was given null is
   * returned. File references will be interpreted relative to basedir.
   * If basedir is null, it will be ignored.
   *
   * @since 5.1.1
   */
  public static InputStream getInputStream(File basedir, String name)
    throws IOException {
    InputStream istream;
    if (name.startsWith("classpath:")) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      String resourceName = name.substring("classpath:".length());
      istream = cl.getResourceAsStream(resourceName);
      if (istream == null)
        throw new IOException("Resource '" + resourceName + "' not found through class loader.");
      log.debug("File loaded through class loader: " + name);
    } else if (name.startsWith("file:")) {
      File f = makeFile(basedir, name.substring("file:".length()));
      if (f.exists()) {
        log.debug("File loaded from file system: " + name);
        istream = new FileInputStream(f);
      } else
        throw new IOException("File '" + f + "' not found.");
    } else {
      File f = makeFile(basedir, name);
      if (f.exists()) {
        log.debug("File loaded from file system: " + name);
        istream = new FileInputStream(f);
      } else {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        istream = cl.getResourceAsStream(name);
        if (istream != null)
          log.debug("File loaded through class loader: " + name);
      }
    }
    return istream;
  }

  private static File makeFile(File basedir, String name) {
    if (basedir == null)
      return new File(name);
    else
      return new File(basedir, name);
  }
  
  /**
   * INTERNAL: Returns an input stream for the given url. Supports
   * file: and classpath:. If no schema given then file system will be
   * checked before classpath. Exception will be thrown if resource is
   * not found when schema is given. If no schema was given null is
   * returned.
   *
   * @since 3.4.3
   */
  public static URL getResource(String name) throws IOException {
    URL url;
    if (name.startsWith("classpath:")) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      String resourceName = name.substring("classpath:".length());
      url = cl.getResource(resourceName);
      if (url == null)
        throw new FileNotFoundException("Resource '" + resourceName + "' not found through class loader.");
      log.debug("File loaded through class loader: " + name);
    } else if (name.startsWith("file:")) {
      File f =  new File(name.substring("file:".length()));
      if (f.exists()) {
        log.debug("File loaded from file system: " + name);
        url = URIUtils.toURL(f);
      } else
        throw new IOException("File '" + f + "' not found.");
    } else {
      File f = new File(name);
      if (f.exists()) {
        log.debug("File loaded from file system: " + name);
        url = URIUtils.toURL(f);
      } else {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        url = cl.getResource(name);
        if (url != null)
          log.debug("File loaded through class loader: " + name);
      }
    }
    return url;
  }

  public static String readString(Reader r, long length) throws IOException {
    char[] chars = new char[(int)length];
    StringBuffer result = new StringBuffer((int)length);
    int read;
    while ((read = r.read(chars)) != -1) {
      result.append(chars, 0, read);
    }
    return result.toString();
  }
  
}
