
package net.ontopia.xml;

import org.xml.sax.*;

/**
 * INTERNAL: A factory interface for creating SAX InputSources.
 */

public interface InputSourceFactoryIF {

  /**
   * INTERNAL: Creates a SAX InputSource object.
   */
  public InputSource createInputSource();
    
}




