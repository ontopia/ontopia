/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
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

package net.ontopia.topicmaps.utils.xfml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;
import net.ontopia.topicmaps.xml.IgnoreTopicMapDTDEntityResolver;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
import net.ontopia.xml.AbstractXMLFormatReader;
import net.ontopia.xml.ConfiguredXMLReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * PUBLIC: A topic map reader that is capable of reading the XFML format
 * for faceted hierarchical metadata. 
 */
public class XFMLTopicMapReader extends AbstractXMLFormatReader implements TopicMapReaderIF, TopicMapImporterIF {
  protected TopicMapStoreFactoryIF store_factory;

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(XFMLTopicMapReader.class.getName());

  /**
   * Creates an XFML reader.
   * @param url The URL of the XFML document.
   */  
  public XFMLTopicMapReader(String url) throws MalformedURLException {
    this(new InputSource(new URILocator(url).getExternalForm()), new URILocator(url));
  }

  /**
   * Creates an XFML reader bound to the reader given in the arguments.
   * @param reader The reader from which the XFML document is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */

  public XFMLTopicMapReader(Reader reader, LocatorIF base_address) {
    this(new InputSource(reader), base_address);
  }

  /**
   * Creates an XFML reader bound to the input stream given in the
   * arguments.
   * @param stream The input stream from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public XFMLTopicMapReader(InputStream stream, LocatorIF base_address) {
    this(new InputSource(stream), base_address);
  }

  /**
   * PUBLIC: Creates an XFML reader bound to the file given in the
   * argument.   
   * @param file The file object from which to read the topic map.
   */
  public XFMLTopicMapReader(File file) throws IOException {
    try {
      if (!file.exists())
        throw new FileNotFoundException(file.toString());
      
      this.base_address = new URILocator(URIUtils.toURL(file));
      this.source = new InputSource(base_address.getExternalForm());
    }
    catch (java.net.MalformedURLException e) {
      throw new OntopiaRuntimeException("Internal error. File " + file + " had " +
                                        "invalid URL representation.");
    }
  }
  
  /**
   * PUBLIC: Creates a topic map reader bound to the input source
   * given in the arguments.
   * @param source The SAX input source from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public XFMLTopicMapReader(InputSource source, LocatorIF base_address) {
    this.source = source;
    this.base_address = base_address;
  }

  /**
   * PUBLIC: Gets the store factory which will be used to create stores.
   */
  public TopicMapStoreFactoryIF getStoreFactory() {
    // Initialize default factory
    if (store_factory == null) {
      store_factory = new InMemoryStoreFactory();
    }
    return store_factory;
  }

  /**
   * PUBLIC: Sets the store factory which will be used to create stores.</p>
   *
   * <p>Default: {@link
   * net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory}</p>
   *
   * @param store_factory The store factory to use. If the parameter
   * is null the default store factory will be used.
   */
  public void setStoreFactory(TopicMapStoreFactoryIF store_factory) {
    this.store_factory = store_factory;
  }

  // Actual reading of the XFML document
  
  public TopicMapIF read() throws IOException {
    // Create new parser object
    XMLReader parser;
    try {
      parser = getXMLReaderFactory().createXMLReader();
      
    } catch (SAXException e) {
      throw new IOException("Problems occurred when creating SAX2 XMLReader: " + e.getMessage());
    }
    
    // Create content handler
    XFMLContentHandler handler = new XFMLContentHandler(getStoreFactory(), getXMLReaderFactory(), base_address);
    
    // Register parser with content handler
    handler.register(parser);
    
    try {
      // Parse input source
      if (log.isInfoEnabled()) {
        log.info("Parsing source " + source.getSystemId());
        if (source.getEncoding() != null) log.info("Encoding: " + source.getEncoding());
        log.info("Parser: " + parser + " (namespace support: " + parser.getFeature("http://xml.org/sax/features/namespaces") + ")");
      }
      parser.parse(source);
      // log.info("Done.");
    } catch (FileNotFoundException e) {
      log.error("Resource not found: " + e.getMessage());
      throw e;
    } catch (SAXParseException e) {
      throw new OntopiaRuntimeException("XML parsing problem: " + e.toString() + " at: "+
                                        e.getSystemId() + ":" + e.getLineNumber() + ":" +
                                        e.getColumnNumber(), e);
    } catch (SAXException e) {
      if (e.getException() instanceof IOException)
        throw (IOException) e.getException();
      throw new IOException("XML related problem: " + e.toString());
    }

    // Return topic map
    return handler.getTopicMap();
  }

  public Collection readAll() throws IOException {
    Collection result = new ArrayList();
    TopicMapIF tm = read();
    if (tm != null) 
      result.add(tm);
    return result;      
  }

  public void importInto(TopicMapIF topicmap) throws IOException {
    // Check that store is ok
    TopicMapStoreIF store = topicmap.getStore();
    if (store == null)
      throw new IOException("Topic map not connected to a store.");
    
    // Read XFML from the source.
    read();
  }

  // --- Internal methods
  
  protected void configureXMLReaderFactory(ConfiguredXMLReaderFactory cxrfactory) {
    cxrfactory.setEntityResolver(new IgnoreTopicMapDTDEntityResolver());
    cxrfactory.setFeature("http://xml.org/sax/features/namespaces", false);
  }
  
}
