
package net.ontopia.xml;

import org.xml.sax.*;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * INTERNAL: A factory for creating SAX2 XMLReaders using the JAXP
 * SAXParserFactory.getXMLReader() method.<p>
 *
 * The created parser is namespace-aware and non-validating by
 * default.<p>
 */
public class JaxpXMLReaderFactory implements XMLReaderFactoryIF {

  protected SAXParserFactory factory;

  /**
   * INTERNAL: Creates a factory that creates XML readers that namespace
   * aware and not validating.
   */
  public JaxpXMLReaderFactory() {
    this(true, false);
  }
  
  /**
   * INTERNAL: Creates a factory that creates XML readers with the
   * specified settings for namespace awareness and validation.
   */
  protected JaxpXMLReaderFactory(boolean namespace_aware, boolean validating) {
    factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(namespace_aware);
    factory.setValidating(validating);
  }
  
  /**
   * Creates a new SAX2 XMLReader object.
   *
   * @exception SAXException Thrown if the class cannot be loaded,
   * instantiated, and cast to XMLReader.
   */
  public XMLReader createXMLReader() throws SAXException {
    try {
      return factory.newSAXParser().getXMLReader();
    } catch (ParserConfigurationException e) {
      throw new SAXException(e);
    }
  }
    
}
