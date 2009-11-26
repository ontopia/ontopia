
// $Id: XTMSnifferContentHandler.java,v 1.2 2008/05/29 19:26:18 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.AttributesImpl;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: This content handler is used to detect whether the XTM
 * event stream being read is an XTM 1.0 or 2.0 document. Once this is
 * clear, the handler configures the parser accordingly with the
 * correct handlers.
 */
public class XTMSnifferContentHandler extends DefaultHandler
  implements DeclHandler, LexicalHandler {
  
  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(XTMSnifferContentHandler.class.getName());

  private XTMTopicMapReader reader;
  private XMLReader parser;
  private LocatorIF base_address;
  private XTMContentHandler handler1;
  private XTM2ContentHandler handler2;
  private TopicMapStoreFactoryIF store_factory;
  private Map entities;
  private int stack_depth; // used to avoid unbalanced stacks in XTM 1.0
  private Locator locator; // stored to be passed on to real ContentHandlers
  private static final Attributes EMPTY_ATTS = new AttributesImpl();

  public static final int VERSION_UNKNOWN = 0;
  public static final int VERSION_XTM10   = 1;
  public static final int VERSION_XTM20   = 2;

  public XTMSnifferContentHandler(XTMTopicMapReader reader,
                                  TopicMapStoreFactoryIF store_factory,
                                  XMLReader parser,
                                  LocatorIF base_address) {
    this.reader = reader;
    this.store_factory = store_factory;
    this.parser = parser;
    this.base_address = base_address;
    this.entities = new HashMap();
  }

  public void startElement(String uri, String name, String qname,
                           Attributes atts) throws SAXException {
    try {
      startElement_(uri, name, qname, atts);
    } catch (Exception e) {
			if (logError()) log.error("Exception was thrown from within startElement", e);
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void startElement_(String uri, String name, String qname,
                           Attributes atts) throws SAXException {
    // The XTM 1.0 reader can handle XML files where the XTM 1.0
    // content is wrapped in other XML content. We therefore need to
    // be able to pass by multiple elements before reaching the
    // topicMap element.
    
    ContentHandler outer_handler = null;

    if (uri == XTMContentHandler.NS_XTM ||
        (uri == "" && qname == "topicMap")) {
      // We are reading XTM 1.0. Update accordingly.
      handler1 = new XTMContentHandler(store_factory,
                                       reader.getXMLReaderFactory(),
                                       base_address);
      handler1.setExternalReferenceHandler(reader.getExternalReferenceHandler());
      handler1.register(parser);
      outer_handler = handler1;
      if (reader.getValidation()) {
        outer_handler = new XTMValidatingContentHandler(handler1);
        parser.setContentHandler(outer_handler);
      }
      
      // pass on events
      if (locator != null)
        outer_handler.setDocumentLocator(locator);
      Iterator it = entities.keySet().iterator();
      while (it.hasNext()) {
        String ename = (String) it.next();
        handler1.externalEntityDecl(ename, null, (String) entities.get(ename));
      }
      
      outer_handler.startDocument();
      for (int ix = 0; ix < stack_depth; ix++) // avoid EmptyStackException
        outer_handler.startElement("", "", "fake-element", EMPTY_ATTS);
      outer_handler.startElement(uri, name, qname, atts);
      
    } else if (uri == XTM2ContentHandler.NS_XTM2) {
      // We are reading XTM 2.0. Update accordingly.
      handler2 = new XTM2ContentHandler(store_factory,
                                        reader.getXMLReaderFactory(),
                                        base_address);
      parser.setContentHandler(handler2);
      outer_handler = handler2;

      if (reader.getValidation()) {
        outer_handler = new XTMValidatingContentHandler(handler2, 
                                                        VERSION_XTM20);
        parser.setContentHandler(outer_handler);
      }

      if (locator != null)
        outer_handler.setDocumentLocator(locator);
      outer_handler.startDocument();
      outer_handler.startElement(uri, name, qname, atts);
    }

    stack_depth++;
  }

  public void endElement(String uri, String name, String qname) {
    stack_depth--;
  }

  public void endDocument() {
    // if we get here it means we never found any 1.0 or 2.0 TMs
    if (reader.getValidation())
      throw new InvalidTopicMapException("XTM input is neither 1.0 nor 2.0");
  }

  public void setDocumentLocator(Locator locator) {
    this.locator = locator; // store it so we can pass it on
  }

  // --- DeclHandler
  // This is here so we can pass on entity information to the XTM 1.0
  // handler which makes use of this information.
  
  public void externalEntityDecl(String name,  String publicId, 
                                 String systemId) {
    if (systemId != null)
      entities.put(name, systemId);
  }
  
  public void attributeDecl(String eName, String aName, String type,
                            String mode, String value) {
  }
  
  public void elementDecl(String name, String model) {
  }
  
  public void internalEntityDecl(String name, String value) {
  }

  // --- LexicalHandler
  public void startEntity(String name) {
    if (handler1 != null)
      handler1.startEntity(name);
  }
  
  public void endEntity(String name) {
    if (handler1 != null)
      handler1.endEntity(name);
  }
  
  public void comment(char[] ch, int start, int length) {
  }
  
  public void startCDATA() {
  }
  
  public void endCDATA() {
  }
  
  public void startDTD(String name, String publicId, String systemId) {
  }
  
  public void endDTD() {
  }

  // --- Internal methods

  private boolean logError() {
    try {
      return Boolean.valueOf(System.getProperty("net.ontopia.topicmaps.xml.XTMContentHandler.logError")).booleanValue();
    } catch (SecurityException e) {
      return false;
    }
  }

  // --- External interface

  public Collection getTopicMaps() {
    if (handler1 != null)
      return handler1.getTopicMaps();
    else if (handler2 != null)
      return handler2.getTopicMaps();
    else
      return Collections.EMPTY_SET;
  }

  public int getXTMVersion() {
    if (handler1 != null)
      return VERSION_XTM10;
    else if (handler2 != null)
      return VERSION_XTM20;
    else
      return VERSION_UNKNOWN;
  }
}
