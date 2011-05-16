
// $Id: URIUtils.java,v 1.17 2007/08/29 13:05:23 geir.gronmo Exp $

package net.ontopia.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.BitSet;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

/**
 * INTERNAL: Utilities for working with URIs.
 * @since 1.3.2
 */
public class URIUtils {

  /**
   * INTERNAL: Given a File object, produce a corresponding URILocator
   * object in the file: URI scheme.
   */
  public static URILocator getFileURI(File file) {
    try {
      return new URILocator(toURL(file).toExternalForm());
    }
    catch (java.net.MalformedURLException e) {
      throw new OntopiaRuntimeException("Malformed URI for File: '" + file + "'", e);
    }
  }

  /**
   * INTERNAL: Given a File object, produce a corresponding URI string
   * object in the file: URI scheme.
   * @since 3.3.0
   */
  public static String getFileURIString(File file) {
    try {
      return toURL(file).toExternalForm();
    }
    catch (java.net.MalformedURLException e) {
      throw new OntopiaRuntimeException("Impossible error", e);
    }
  }

  /**
   * INTERNAL: Given a URILocator in the file: URI scheme, produce the
   * corresponding File object.
   * @since 1.4
   */
  public static File getURIFile(LocatorIF file)
    throws java.net.MalformedURLException {
    
    String address = file.getAddress();
    if (!file.getNotation().equals("URI"))
      throw new java.net.MalformedURLException("Not a URI: " + file);
    if (!address.substring(0, 5).equals("file:"))
      throw new java.net.MalformedURLException("Not a file URI: " + file);

    // FIXME: this method is not complete, since it does not support Windows!
    return new File(address.substring(5));
  }

  /**
   * INTERNAL: Turns a string containing a url or a filename into a
   * proper LocatorIF object.
   */
  public static URILocator getURI(String uri_or_filename) {
    try {
      // first try interpreting it as a file name (see bug #679)
      File file = new File(uri_or_filename);
      if (file.exists())
        return new URILocator(file);
    } catch (java.security.AccessControlException e) {
      // in an applet we won't be allowed to call this method (see bug #1006)
      // we solve this by catching the exception and ignoring it
    }
    
    // then try loading it as a resource
    if (uri_or_filename.startsWith("classpath:")) {
      try {
          return new URILocator(StreamUtils.getResource(uri_or_filename));
      } catch (java.io.IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    // if that fails, then pretend it's a URI
    try {
      return new URILocator(uri_or_filename);
    } catch (MalformedURLException e) {
      // it wasn't a URI, so probably it was a reference to a non-existent file
      throw new OntopiaRuntimeException("Non-existent file or bad URI: " +
                                        uri_or_filename, e);
    }    
  }

  /**
   * INTERNAL: URL-encodes the string by encoding reserved characters
   * using %-codes.
   *
   * @param str String to be URL-encoded.
   * @param charenc Character encoding to use in URL. Usually UTF-8.
   */
  public static String urlEncode(String str, String charenc) throws IOException {
    byte[] encodedstr;
    if (charenc != null)
      encodedstr = str.getBytes(charenc);
    else
      encodedstr = str.getBytes(); // uses platform default, which is bad
                                   // however, avoids crash on JDK 1.3

    // RFC 2396, section 2.3:
    // ======================
    
    // Data characters that are allowed in a URI but do not have a reserved
    // purpose are called unreserved.  These include upper and lower case
    // letters, decimal digits, and a limited set of punctuation marks and
    // symbols.
    // 
    //    unreserved  = alphanum | mark
    // 
    //    mark        = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
    // 
    // Unreserved characters can be escaped without changing the semantics
    // of the URI, but this should not be done unless the URI is being used
    // in a context that does not allow the unescaped character to appear.    
    
    StringBuffer buf = new StringBuffer();
    for (int ix = 0; ix < encodedstr.length; ix++) {
      if ((encodedstr[ix] >= 'a' && encodedstr[ix] <= 'z') ||
          (encodedstr[ix] >= 'A' && encodedstr[ix] <= 'Z') ||
          (encodedstr[ix] >= '0' && encodedstr[ix] <= '9') ||
          encodedstr[ix] == '-' || encodedstr[ix] == '_' ||
          encodedstr[ix] == '.' || encodedstr[ix] == '!' ||
          encodedstr[ix] == '~' || encodedstr[ix] == '*' ||
          encodedstr[ix] == '\'' || encodedstr[ix] == '(' ||
          encodedstr[ix] == ')')
        buf.append((char) encodedstr[ix]); // unreserved char
      else if (encodedstr[ix] == ' ')
        buf.append('+');
      else
        buf.append("%" + toHexString(encodedstr[ix]));
    }

    return buf.toString();
  }
  
  /**
   * INTERNAL: Make hex string for integer.
   */
  public static String toHexString(byte n) {
    return "" + toHexDigit((byte) ((n & 0xF0) >> 4)) + toHexDigit((byte) (n & 0x0F));
  }

  private static char toHexDigit(byte n) {
    if (n < 10)
      return (char) ('0' + ((char) n));
    else
      return (char) (((char) (n - 10)) + 'A');
  }
  
  /**
   * INTERNAL: Return URILocator from uri string.
   */
  public static URILocator getURILocator(String uri) {
    try {
      return new URILocator(uri);
    } catch (MalformedURLException e) {
      // it wasn't a URI, so probably it was a reference to a non-existent file
      throw new OntopiaRuntimeException("Malformed URI:" + uri, e);
    }    
  }

  private static final BitSet UNRESERVED = new BitSet(256);
  static {
    try {
      byte[] bytes = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'():/".getBytes("US-ASCII");
      for (int i = 0; i < bytes.length ; i++) {
          UNRESERVED.set(bytes[i]);
      }
    } catch (UnsupportedEncodingException e) {
    }
  }

  /**
   * INTERNAL: Use this method instead of File.toURL() to get URLs for files.
   */
  public static URL toURL(File file) throws MalformedURLException {
    URL url = file.toURL();
    try {
      byte[] bytes = url.toString().getBytes( "US-ASCII" );
      StringBuffer buf = new StringBuffer( bytes.length );
      for ( int i = 0; i < bytes.length; i++ ) {
        byte b = bytes[i];
        if (UNRESERVED.get(b)) {
          buf.append((char)b);
        } else {
          buf.append('%' );
          buf.append(Character.forDigit( b >>> 4 & 0xf, 16 ));
          buf.append(Character.forDigit( b & 0xf, 16 ));
        }
      }
      return new URL( buf.toString() );
    } catch (UnsupportedEncodingException e) {
      // should not happen as US-ASCII must be present
      throw new RuntimeException(e);
    }
  }


}
