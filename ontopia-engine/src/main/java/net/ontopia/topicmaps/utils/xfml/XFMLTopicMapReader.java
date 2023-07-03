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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;
import net.ontopia.topicmaps.xml.IgnoreTopicMapDTDEntityResolver;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.AbstractXMLFormatReader;
import net.ontopia.xml.DefaultXMLReaderFactory;
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
public class XFMLTopicMapReader extends AbstractXMLFormatReader implements TopicMapReaderIF {
  protected TopicMapStoreFactoryIF store_factory;

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(XFMLTopicMapReader.class.getName());

  /**
   * Creates an XFML reader.
   * @param url The URL of the XFML document.
   */  
  public XFMLTopicMapReader(URL url) {
    super(url);
  }

  /**
   * Creates an XFML reader bound to the reader given in the arguments.
   * @param reader The reader from which the XFML document is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */

  public XFMLTopicMapReader(Reader reader, LocatorIF base_address) {
    super(reader, base_address);
  }

  /**
   * Creates an XFML reader bound to the input stream given in the
   * arguments.
   * @param stream The input stream from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public XFMLTopicMapReader(InputStream stream, LocatorIF base_address) {
    super(stream, base_address);
  }

  /**
   * PUBLIC: Creates an XFML reader bound to the file given in the
   * argument.   
   * @param file The file object from which to read the topic map.
   */
  public XFMLTopicMapReader(File file) {
    super(file);
  }
  
  /**
   * PUBLIC: Creates a topic map reader bound to the input source
   * given in the arguments.
   * @param source The SAX input source from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public XFMLTopicMapReader(InputSource source, LocatorIF base_address) {
    super(source, base_address);
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
  
  @Override
  public TopicMapIF read() throws IOException {
    // Create new parser object
    XMLReader parser;
    try {
      parser = DefaultXMLReaderFactory.createXMLReader();
      parser.setEntityResolver(new IgnoreTopicMapDTDEntityResolver());
      parser.setFeature("http://xml.org/sax/features/namespaces", false);
    } catch (SAXException e) {
      throw new IOException("Problems occurred when creating SAX2 XMLReader: " + e.getMessage());
    }
    
    // Create content handler
    XFMLContentHandler handler = new XFMLContentHandler(getStoreFactory(), base_address);
    
    // Register parser with content handler
    handler.register(parser);
    
    try {
      // Parse input source
      if (log.isInfoEnabled()) {
        log.info("Parsing source " + source.getSystemId());
        if (source.getEncoding() != null) {
          log.info("Encoding: " + source.getEncoding());
        }
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
      if (e.getException() instanceof IOException) {
        throw (IOException) e.getException();
      }
      throw new IOException("XML related problem: " + e.toString());
    }

    // Return topic map
    return handler.getTopicMap();
  }

  @Override
  public Collection readAll() throws IOException {
    Collection result = new ArrayList();
    TopicMapIF tm = read();
    if (tm != null) {
      result.add(tm);
    }
    return result;      
  }

  @Override
  public void importInto(TopicMapIF topicmap) throws IOException {
    // Check that store is ok
    TopicMapStoreIF store = topicmap.getStore();
    if (store == null) {
      throw new IOException("Topic map not connected to a store.");
    }
    
    // Read XFML from the source.
    read();
  }

  /**
   * XFMLTopicMapReader has no additional options to set.
   * @param properties 
   */
  @Override
  public void setAdditionalProperties(Map<String, Object> properties) {
    // no-op
  }
}
