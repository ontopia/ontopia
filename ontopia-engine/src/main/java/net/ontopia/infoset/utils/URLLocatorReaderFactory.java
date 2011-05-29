
package net.ontopia.infoset.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.core.LocatorReaderFactoryIF;

/**
 * INTERNAL: Creates a Reader for a LocatorIF that contains a URL in
 * its address field.<p>
 *
 * Warning: At this point the reader uses the default charcter
 * encoding. In the future the correct encoding will be chosen based
 * on information from the actual URL connection.<p>
 */

public class URLLocatorReaderFactory implements LocatorReaderFactoryIF {

  public URLLocatorReaderFactory() {
  }  
  
  public Reader createReader(LocatorIF locator) throws IOException {
    URL url = new URL(locator.getAddress());
    return new InputStreamReader(url.openConnection().getInputStream());
  }
  
}





