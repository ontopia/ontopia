// $Id: LocatorReaderFactoryIF.java,v 1.4 2004/11/29 19:19:53 grove Exp $

package net.ontopia.infoset.core;

import java.io.IOException;
import java.io.Reader;

/**
 * INTERNAL: Factory that creates a Reader from a LocatorIF.<p>
 */

public interface LocatorReaderFactoryIF {

  /**
   * INTERNAL: Returns a reader that can read the contents of the
   * resource referenced by the locator.
   *
   * @return A reader for the locator. An IOException is thrown if a
   * reader cannot be created.
   */
  public Reader createReader(LocatorIF locator) throws IOException;
  
}





