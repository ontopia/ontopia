/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.schema.impl.osl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.schema.core.SchemaIF;
import net.ontopia.topicmaps.schema.core.SchemaReaderIF;
import net.ontopia.topicmaps.schema.core.SchemaSyntaxException;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.URIUtils;
import net.ontopia.xml.AbstractXMLFormatReader;
import net.ontopia.xml.ConfigurableEntityResolver;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.EmptyInputSourceFactory;
import net.ontopia.xml.InputSourceFactoryIF;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;

/**
 * PUBLIC: Reader that reads OSL schemas from their XML representation
 * into an OSL object structure.
 */
public class OSLSchemaReader extends AbstractXMLFormatReader
                             implements SchemaReaderIF {
  
  /**
   * PUBLIC: Creates a reader bound to the given URI.
   */
  public OSLSchemaReader(String uri) throws MalformedURLException, IOException {
    source = uri.startsWith("classpath:") 
      ? new InputSource(StreamUtils.getInputStream(uri)) 
      : new InputSource(uri);
    base_address = new URILocator(uri);
  }
  
  /**
   * PUBLIC: Creates a reader bound to the given file.
   */
  public OSLSchemaReader(File file) {
    try {
      source = new InputSource(URIUtils.toURL(file).toExternalForm());
      base_address = new URILocator(URIUtils.toURL(file).toExternalForm());
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
      parser = DefaultXMLReaderFactory.createXMLReader();
      parser.setFeature("http://xml.org/sax/features/namespaces", false);
      parser.setEntityResolver(new IgnoreSchemaDTDEntityResolver());
      
    } catch (SAXException e) {
      throw new IOException("Problems occurred when creating SAX2 XMLReader: " + e.getMessage());
    }
    
    // Create content handler
    OSLSchemaContentHandler handler = new OSLSchemaContentHandler(base_address);
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
