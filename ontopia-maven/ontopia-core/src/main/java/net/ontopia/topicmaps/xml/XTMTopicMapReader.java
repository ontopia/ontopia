
// $Id: XTMTopicMapReader.java,v 1.62 2008/05/19 11:52:28 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import java.util.*;
import java.net.MalformedURLException;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.*;
import net.ontopia.xml.*;

import org.xml.sax.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: A topic map reader that reads the XTM 1.0 interchange
 * syntax. A topic map is built as part of the import process. A
 * variety of possible input sources are accommodated, by overloading
 * the constructor.
 */
public class XTMTopicMapReader extends AbstractXMLFormatReader
  implements TopicMapReaderIF, TopicMapImporterIF {
  protected Iterator topicmaps;
  protected TopicMapStoreFactoryIF store_factory;
  protected ExternalReferenceHandlerIF ref_handler;
  protected boolean validate;

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(XTMTopicMapReader.class.getName());

  /**
   * PUBLIC: Creates a topic map reader bound to the URL given in the
   * arguments.   
   * @param url The URL of the topic map document.
   */  
  public XTMTopicMapReader(String url) throws MalformedURLException {
    this(new InputSource(new URILocator(url).getExternalForm()),
         new URILocator(url));
  }

  /**
   * PUBLIC: Creates a topic map reader bound to the URL given in the
   * arguments.   
   * @param url The URL of the topic map document.
   * @since 2.0
   */  
  public XTMTopicMapReader(LocatorIF url) {
    this(new InputSource(url.getExternalForm()), url);
  }
  
  /**
   * PUBLIC: Creates a topic map reader bound to the reader given in
   * the arguments.   
   * @param reader The reader from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public XTMTopicMapReader(Reader reader, LocatorIF base_address) {
    this(new InputSource(reader), base_address);
  }

  /**
   * PUBLIC: Creates a topic map reader bound to the input stream
   * given in the arguments. 
   * @param stream The input stream from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public XTMTopicMapReader(InputStream stream, LocatorIF base_address) {
    this(new InputSource(stream), base_address);
  }

  /**
   * PUBLIC: Creates a topic map reader bound to the file given in the
   * arguments.   
   * @param file The file object from which to read the topic map.
   */
  public XTMTopicMapReader(File file) throws IOException {
    if (!file.exists())
      throw new FileNotFoundException(file.toString());
      
    this.base_address = new URILocator(file);
    this.source = new InputSource(base_address.getExternalForm());
    this.validate = true;
  }
  
  /**
   * PUBLIC: Creates a topic map reader bound to the input source
   * given in the arguments.   
   * @param source The SAX input source from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public XTMTopicMapReader(InputSource source, LocatorIF base_address) {
    this.source = source;
    this.base_address = base_address;
    this.validate = true;
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
    if (followTopicRefs)
      this.ref_handler = null;
    else
      this.ref_handler = new NoFollowTopicRefExternalReferenceHandler();
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
  
  public TopicMapIF read() throws IOException {
    return read(getStoreFactory());
  }
  
  protected TopicMapIF read(TopicMapStoreFactoryIF store_factory) throws IOException {
    // If source has been read, return next available topic map.
    if (topicmaps != null) {
      if (topicmaps.hasNext())
        return (TopicMapIF)topicmaps.next();
      else
        return null;
    }
    
    // Create new parser object
    XMLReader parser;
    try {
      parser = getXMLReaderFactory().createXMLReader();
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
        if (source.getEncoding() != null)
          log.debug("Encoding: " + source.getEncoding());
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
      if (e.getException() instanceof IOException)
        throw (IOException) e.getException();
      throw new IOException("XML related problem: " + e.toString());
    }

    // Get hold of all topic maps and set iterator property
    Collection tms = handler.getTopicMaps();
    topicmaps = tms.iterator();
    log.debug("Read " + tms.size() + " topic map(s).");

    // Process class-instance associations
    Iterator it = tms.iterator();
    while (it.hasNext()) {
      if (handler.getXTMVersion() == XTMVersion.XTM_1_0)
        ClassInstanceUtils.resolveAssociations1((TopicMapIF) it.next());
      else if (handler.getXTMVersion() == XTMVersion.XTM_2_0)
        ClassInstanceUtils.resolveAssociations2((TopicMapIF) it.next());
      else
        throw new OntopiaRuntimeException("Unknown XTM version!");
    }

    // Were there any topic maps?
    if (!topicmaps.hasNext())
      throw new InvalidTopicMapException("No topic maps in document " +
                                         source.getSystemId());

    // If so, return the first
    return (TopicMapIF)topicmaps.next();
  }

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

  public void importInto(TopicMapIF topicmap) throws IOException {
    // Check that store is ok
    TopicMapStoreIF store = topicmap.getStore();
    if (store == null)
      throw new IOException("Topic map not connected to a store.");

    // Use a store factory that always returns the same topic
    // map. This makes sure that all topic maps found inside the
    // source document will be imported into the document.
    
    // Read all topic maps from the source.
    readAll(new SameStoreFactory(store));
  }

  // --- Internal methods
  
  protected void configureXMLReaderFactory(ConfiguredXMLReaderFactory cxrfactory) {
    cxrfactory.setEntityResolver(new TopicMapDTDEntityResolver());
  }
  
}
