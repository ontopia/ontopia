
package net.ontopia.net.data;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


/**
 * INTERNAL: This class enables Java to dereference data: URLs, as
 * described in RFC 2397, ftp://sunsite.uio.no/pub/rfc/rfc2397.txt.
 */

public class DataURLConnection extends URLConnection {
  
  private DataURL dataurl = null;
  
  public DataURLConnection(URL url) {
    super(url);
  }

  public int getContentLength() {
    return dataurl.getContentLength();
  }

  public String getContentType() {
    return dataurl.getMediaType();
  }

  public String getContentEncoding() {
    return dataurl.getContentEncoding();
  }

  public InputStream getInputStream() throws IOException {
    return dataurl.getContentsAsStream();
  }

  public String getHeaderField(String name) {
    if (name.equals("Content-type"))
      return getContentType();
    else if (name.equals("Content-length"))
      return Integer.toString(getContentLength());
    else
      return null;
  }
  
  public void connect() throws IOException {
    if (connected) return;
    dataurl = new DataURL(url);
    connected = true;
  }
  
}
