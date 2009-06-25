// $Id: URLLocatorReaderFactory.java,v 1.4 2002/05/29 13:38:36 hca Exp $

package net.ontopia.infoset.utils;

import java.io.*;
import java.net.URL;
import net.ontopia.infoset.core.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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





