
// $Id: Handler.java,v 1.12 2004/11/28 13:44:37 larsga Exp $

package net.ontopia.net.data;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * PUBLIC: A data URL protocol handler. See RFC 2397,
 * ftp://sunsite.uio.no/pub/rfc/rfc2397.txt, for more information
 * about data URLs.</p>
 *
 * Call the install method to install the handler in your Java VM.</p>
 */

public class Handler extends URLStreamHandler {
  
  public URLConnection openConnection(URL url) throws IOException {
    URLConnection conn = new DataURLConnection(url);
    conn.connect();
    return conn;
  }

  /**
   * PUBLIC: Calling this method makes the data URL handler install
   * itself in the JVM so that the java.net.URL constructor can create
   * data: URLs.  The method is idempotent; that is, after it is
   * called the first time later calls have no effect.
   */
  
  public static void install() {
    java.util.Properties props = System.getProperties();
    String pkgs = props.getProperty("java.protocol.handler.pkgs");
    if (pkgs != null && pkgs.indexOf("net.ontopia.net") != -1) return;
    
    if (pkgs == null)
      pkgs = "net.ontopia.net";
    else 
      pkgs += "|net.ontopia.net";
    
    props.setProperty("java.protocol.handler.pkgs", pkgs);
  }
  
}
