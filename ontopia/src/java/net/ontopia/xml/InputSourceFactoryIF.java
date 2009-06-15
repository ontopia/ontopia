// $Id: InputSourceFactoryIF.java,v 1.4 2004/11/18 13:14:02 grove Exp $

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




