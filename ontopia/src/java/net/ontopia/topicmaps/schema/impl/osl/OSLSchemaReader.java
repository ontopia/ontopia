
// $Id: OSLSchemaReader.java,v 1.11 2007/07/11 11:39:14 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import org.xml.sax.*;
import org.xml.sax.helpers.LocatorImpl;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.AbstractXMLFormatReader;
import net.ontopia.xml.ConfiguredXMLReaderFactory;
import net.ontopia.xml.ConfigurableEntityResolver;
import net.ontopia.xml.EmptyInputSourceFactory;
import net.ontopia.xml.InputSourceFactoryIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.schema.core.SchemaIF;
import net.ontopia.topicmaps.schema.core.SchemaReaderIF;
import net.ontopia.topicmaps.schema.core.SchemaSyntaxException;

/**
 * PUBLIC: Reader that reads OSL schemas from their XML representation
 * into an OSL object structure.
 */
public class OSLSchemaReader extends AbstractXMLFormatReader
                             implements SchemaReaderIF {
  
  /**
   * PUBLIC: Creates a reader bound to the given URI.
   */
  public OSLSchemaReader(String uri) throws MalformedURLException {
    source = new InputSource(uri);
    base_address = new URILocator(uri);
  }
  
  /**
   * PUBLIC: Creates a reader bound to the given file.
   */
  public OSLSchemaReader(File file) {
    try {
      source = new InputSource(file.toURL().toExternalForm());
      base_address = new URILocator(file.toURL().toExternalForm());
    }
    catch (MalformedURLException e) {
      throw new OntopiaRuntimeException("INTERNAL ERROR: " + e);
    }
  }

  // --- SchemaReaderIF methods
  
  public SchemaIF read()
    throws IOException, SchemaSyntaxException {
    
    // Create new parser object
    XMLReader parser;
    try {
      parser = getXMLReaderFactory().createXMLReader();
      
    } catch (SAXException e) {
      throw new IOException("Problems occurred when creating SAX2 XMLReader: " + e.getMessage());
    }
    
    // Create content handler
    OSLSchemaContentHandler handler = new OSLSchemaContentHandler(getXMLReaderFactory(), base_address);
    parser.setContentHandler(handler);
    
    // Parse the document
    try {
      parser.parse(source);
    } catch (SAXException e) {
      if (e.getException() instanceof IOException)
        throw (IOException) e.getException();
      if (e.getException() instanceof SchemaSyntaxException)
        throw (SchemaSyntaxException) e.getException();

      LocatorImpl loc = new LocatorImpl();
      if (e instanceof SAXParseException) {
        SAXParseException e2 = (SAXParseException) e;
        loc.setColumnNumber(e2.getColumnNumber());
        loc.setLineNumber(e2.getLineNumber());
        loc.setSystemId(e2.getSystemId());
      }
      
      throw new SchemaSyntaxException(e.getMessage(), loc);
    }
    
    return handler.getSchema();
  }

  // --- Internal methods

  protected void configureXMLReaderFactory(ConfiguredXMLReaderFactory cxrfactory) {
    cxrfactory.setFeature("http://xml.org/sax/features/namespaces", false);
    cxrfactory.setEntityResolver(new IgnoreSchemaDTDEntityResolver());
  }

  // --- Entity resolver

  static class IgnoreSchemaDTDEntityResolver extends ConfigurableEntityResolver {

    protected InputSourceFactoryIF factory;
    
    public IgnoreSchemaDTDEntityResolver() {
      this(new EmptyInputSourceFactory());
    }
  
    public IgnoreSchemaDTDEntityResolver(InputSourceFactoryIF factory) {
      this.factory = factory;
      addPublicIdSource("+//IDN ontopia.net//DTD Ontopia Schema Language (1.0)//EN", factory);
    }

    public InputSource resolveEntity (String public_id, String system_id) {
      if (system_id != null && system_id.endsWith(".dtd"))
        return factory.createInputSource();
      return super.resolveEntity(public_id, system_id);
    }
  }
  
}
