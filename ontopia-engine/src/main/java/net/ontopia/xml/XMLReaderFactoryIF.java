
package net.ontopia.xml;

import org.xml.sax.*;

/**
 * INTERNAL: A factory interface for creating SAX2 XMLReaders.
 */

public interface XMLReaderFactoryIF {

  /**
   * INTERNAL: Creates a SAX2 XMLReader object.
   *
   * @exception SAXException Thrown if there are any problems with
   * creating the XMLReader.
   */
  public XMLReader createXMLReader() throws SAXException;
    
}
