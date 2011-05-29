
package net.ontopia.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

/**
 * INTERNAL: A factory for creating configured SAX2 XMLReaders.</p>
 *
 * The XML readers created by this factory will have their error
 * handler, entity resolver and dtd handler properties set to the
 * values defined on the factory. If the property on the factory is
 * null the property on the XML reader will not be set at all.</p>
 */
public class ConfiguredXMLReaderFactory implements XMLReaderFactoryIF {

  protected XMLReaderFactoryIF xrfactory;

  protected ErrorHandler error_handler;
  protected EntityResolver entity_resolver;
  protected DTDHandler dtd_handler;
  protected Map features;
  protected Map properties;

  public ConfiguredXMLReaderFactory() {
    this(new DefaultXMLReaderFactory());
  }
  
  public ConfiguredXMLReaderFactory(XMLReaderFactoryIF xrfactory) {
    this.xrfactory = xrfactory;
    this.features = new HashMap();
    this.properties = new HashMap();
  }

  /**
   * INTERNAL: Gets the error handler that all XMLReaders should have
   * set when created.
   */
  public ErrorHandler getErrorHandler() {
    return error_handler;
  }

  /**
   * INTERNAL: Sets the error handler that all XMLReaders should have
   * set when created.
   */
  public void setErrorHandler(ErrorHandler error_handler) {
    this.error_handler = error_handler;
  }

  /**
   * INTERNAL: Gets the entity resolver that all XMLReaders should have
   * set when created.
   */
  public EntityResolver getEntityResolver() {
    return entity_resolver;
  }

  /**
   * INTERNAL: Sets the entity resolver that all XMLReaders should have
   * set when created.
   */
  public void setEntityResolver(EntityResolver entity_resolver) {
    this.entity_resolver = entity_resolver;
  }
  
  /**
   * INTERNAL: Gets the DTD handler that all XMLReaders should have set
   * when created.
   */
  public DTDHandler getDTDHandler() {
    return dtd_handler;
  }

  /**
   * INTERNAL: Sets the DTD handler that all XMLReaders should have set
   * when created.
   */
  public void setDTDHandler(DTDHandler dtd_handler) {
    this.dtd_handler = dtd_handler;
  }

  /**
   * INTERNAL: Returns the value of a SAX 2.0 feature as it will be set
   * in new XMLReaders created by this factory.
   * @exception SAXNotRecognizedException Thrown when asked for a feature
   *                                      that has not been set.
   * @since 1.2
   */
  public boolean getFeature(String uri) throws SAXNotRecognizedException {
    Boolean state = (Boolean) features.get(uri);
    if (state == null)
      throw new SAXNotRecognizedException("Feature " + uri + " has not been set");
    return state.booleanValue();
  }
  
  /**
   * INTERNAL: Sets a SAX 2.0 feature to the value all XMLReaders should
   * have when created by this factory.
   * @since 1.2
   */
  public void setFeature(String uri, boolean state) {
    features.put(uri, (state ? Boolean.TRUE : Boolean.FALSE));
  }

  /**
   * INTERNAL: Returns the value of a SAX 2.0 property as it will be set
   * in new XMLReaders created by this factory.
   * @return The value of the property, or null if it has not been set.
   * @since 1.2
   */
  public Object getProperty(String uri) {
    return properties.get(uri);
  }
  
  /**
   * INTERNAL: Sets a SAX 2.0 property to the value all XMLReaders should
   * have when created by this factory.
   * @since 1.2
   */
  public void setProperty(String uri, Object value) {
    properties.put(uri, value);
  }
  
  /**
   * INTERNAL: Creates a new SAX2 XMLReader object using the nested
   * factory and configures the object created before returning it.
   *
   * @exception SAXException Thrown if there are any problems either
   * creating the reader or configuring it.
   */
  public XMLReader createXMLReader() throws SAXException {
    // Create reader
    XMLReader reader = xrfactory.createXMLReader();

    // Configure reader
    if (error_handler != null) reader.setErrorHandler(error_handler);
    if (entity_resolver != null) reader.setEntityResolver(entity_resolver);
    if (dtd_handler != null) reader.setDTDHandler(dtd_handler);

    Iterator it = features.keySet().iterator();
    while (it.hasNext()) {
      String uri = (String) it.next();
      reader.setFeature(uri, getFeature(uri));
    }

    it = properties.keySet().iterator();
    while (it.hasNext()) {
      String uri = (String) it.next();
      reader.setProperty(uri, getProperty(uri));
    }
    
    return reader;
  }
    
}
