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

package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;
import net.ontopia.topicmaps.utils.ClassInstanceUtils;
import net.ontopia.topicmaps.utils.NoFollowTopicRefExternalReferenceHandler;
import net.ontopia.topicmaps.utils.SameStoreFactory;
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
 * PUBLIC: A topic map reader that reads the XTM 1.0 interchange
 * syntax. A topic map is built as part of the import process. A
 * variety of possible input sources are accommodated, by overloading
 * the constructor.
 */
public class XTMTopicMapReader extends AbstractXMLFormatReader implements TopicMapReaderIF {
  public static final String PROPERTY_VALIDATION = "validation";
  public static final String PROPERTY_EXTERNAL_REFERENCE_HANDLER = "externalReferenceHandler";
  protected Iterator topicmaps;
  protected TopicMapStoreFactoryIF store_factory;
  protected ExternalReferenceHandlerIF ref_handler;
  protected boolean validate = true;

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(XTMTopicMapReader.class.getName());

  /**
   * PUBLIC: Creates a topic map reader bound to the URL given in the
   * arguments.   
   * @param url The URL of the topic map document.
   */  
  public XTMTopicMapReader(URL url) {
    super(url);
  }
  
  public XTMTopicMapReader(URL url, LocatorIF base_address) {
    super(url, base_address);
  }
  
  /**
   * PUBLIC: Creates a topic map reader bound to the reader given in
   * the arguments.   
   * @param reader The reader from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public XTMTopicMapReader(Reader reader, LocatorIF base_address) {
    super(reader, base_address);
  }

  /**
   * PUBLIC: Creates a topic map reader bound to the input stream
   * given in the arguments. 
   * @param stream The input stream from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public XTMTopicMapReader(InputStream stream, LocatorIF base_address) {
    super(stream, base_address);
  }

  /**
   * PUBLIC: Creates a topic map reader bound to the file given in the
   * arguments.   
   * @param file The file object from which to read the topic map.
   */
  public XTMTopicMapReader(File file) {
    super(file);
  }
  
  /**
   * PUBLIC: Creates a topic map reader bound to the input source
   * given in the arguments.   
   * @param source The SAX input source from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public XTMTopicMapReader(InputSource source, LocatorIF base_address) {
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

  /**
   * PUBLIC: Gets the external reference handler. The reference
   * handler will receive notifications on references to external
   * topics and topic maps.
   */
  public ExternalReferenceHandlerIF getExternalReferenceHandler() {
    return ref_handler;
  }

  /**
   * PUBLIC: Sets the external reference handler.
   */
  public void setExternalReferenceHandler(ExternalReferenceHandlerIF ref_handler) {
    this.ref_handler = ref_handler;
  }

  /**
   * PUBLIC: If set to false topicRef elements pointing to external
   * documents will not be traversed. The default is that those the
   * documents pointed to by those elements will be loaded (as per the
   * XTM specification).
   *
   * @since 3.0
   */
  public void setFollowTopicRefs(boolean followTopicRefs) {
    if (followTopicRefs) {
      this.ref_handler = null;
    } else {
      this.ref_handler = new NoFollowTopicRefExternalReferenceHandler();
    }
  }

  /**
   * PUBLIC: Turn validation of XTM documents according to DTD on or
   * off. The validation checks if the documents read follow the DTD,
   * and will abort import if they do not.
   * @param validate Will validate if true, will not if false.
   * @since 2.0
   */
  public void setValidation(boolean validate) {
    this.validate = validate;
  }

  /**
   * PUBLIC: Returns true if validation is on, false otherwise.
   * @since 2.0
   */
  public boolean getValidation() {
    return validate;
  }
  
  @Override
  public TopicMapIF read() throws IOException {
    return read(getStoreFactory());
  }
  
  protected TopicMapIF read(TopicMapStoreFactoryIF store_factory) throws IOException {
    // If source has been read, return next available topic map.
    if (topicmaps != null) {
      if (topicmaps.hasNext()) {
        return (TopicMapIF)topicmaps.next();
      } else {
        return null;
      }
    }
    
    // Create new parser object
    XMLReader parser;
    try {
      parser = DefaultXMLReaderFactory.createXMLReader();
      if (validate) {
        parser.setEntityResolver(new TopicMapDTDEntityResolver());
      } else {
        parser.setEntityResolver(new IgnoreTopicMapDTDEntityResolver());
      }
    } catch (SAXException e) {
      throw new IOException("Problems occurred when creating SAX2 XMLReader: " + e.getMessage());
    }

    // Set up content handler
    XTMSnifferContentHandler handler =
      new XTMSnifferContentHandler(this, store_factory, parser, base_address);
    parser.setContentHandler(handler);
    try {
      parser.setProperty(XTMContentHandler.SAX_DECL_HANDLER, handler);
    } catch (SAXException e) {
      log.warn("Parser does not support SAX DeclHandler: " + e.getMessage());
      throw new OntopiaRuntimeException(e);
    }
    try {
      parser.setProperty(XTMContentHandler.SAX_LEXICAL_HANDLER, handler);
    } catch (SAXException e) {
      log.warn("Parser does not support SAX LexicalHandler: " + e.getMessage());
      throw new OntopiaRuntimeException(e);
    }
    
    // Parse input source
    try {
      if (log.isDebugEnabled()) {
        log.debug("Parsing source " + source.getSystemId());
        if (source.getEncoding() != null) {
          log.debug("Encoding: " + source.getEncoding());
        }
        log.debug("Parser: " + parser + " (namespace support: " + parser.getFeature("http://xml.org/sax/features/namespaces") + ")");
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

    // Get hold of all topic maps and set iterator property
    Collection tms = handler.getTopicMaps();
    topicmaps = tms.iterator();
    log.debug("Read " + tms.size() + " topic map(s).");

    // Process class-instance associations
    Iterator it = tms.iterator();
    while (it.hasNext()) {
      if (handler.getXTMVersion() == XTMVersion.XTM_1_0) {
        ClassInstanceUtils.resolveAssociations1((TopicMapIF) it.next());
      } else if (handler.getXTMVersion() == XTMVersion.XTM_2_0) {
        ClassInstanceUtils.resolveAssociations2((TopicMapIF) it.next());
      } else {
        throw new OntopiaRuntimeException("Unknown XTM version!");
      }
    }

    // Were there any topic maps?
    if (!topicmaps.hasNext()) {
      throw new InvalidTopicMapException("No topic maps in document " +
                                         source.getSystemId());
    }

    // If so, return the first
    return (TopicMapIF)topicmaps.next();
  }

  @Override
  public Collection readAll() throws IOException {
    return readAll(getStoreFactory());
  }
  
  protected Collection readAll(TopicMapStoreFactoryIF store_factory) throws IOException {
    Collection result = new ArrayList();
    TopicMapIF tm = read(store_factory);
    while (tm != null) {
      result.add(tm);
      tm = read(store_factory);
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

    // Use a store factory that always returns the same topic
    // map. This makes sure that all topic maps found inside the
    // source document will be imported into the document.
    
    // Read all topic maps from the source.
    readAll(new SameStoreFactory(store));
  }

  /**
   * Sets additional properties for the XTMTopicMapReader. Accepts properties 'validation' and 
   * 'externalReferenceHandler'. The value of 'validation' has to be a boolean and corresponds
   * to the {@link #setValidation(boolean)} method. The value of 'externalReferenceHandler' has
   * to be an {@link ExternalReferenceHandlerIF} and corresponds to the 
   * {@link #setExternalReferenceHandler(net.ontopia.topicmaps.xml.ExternalReferenceHandlerIF)}
   * method.
   * @param properties 
   */
  @Override
  public void setAdditionalProperties(Map<String, Object> properties) {
    Object o = properties.get(PROPERTY_VALIDATION);
    if ((o != null) && (o instanceof Boolean)) {
      setValidation((Boolean) o);
    }
    o = properties.get(PROPERTY_EXTERNAL_REFERENCE_HANDLER);
    if ((o != null) && (o instanceof ExternalReferenceHandlerIF)) {
      setExternalReferenceHandler((ExternalReferenceHandlerIF) o);
    }
  }
}
