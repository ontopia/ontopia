
// $Id: AbstractTopicMapContentHandler.java,v 1.25 2003/01/22 12:43:09 larsga Exp $

package net.ontopia.topicmaps.xml;

import java.util.*;
import net.ontopia.xml.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.apache.log4j.*;

/**
 * INTERNAL: Abstract SAX2 content handler used for reading various
 * kinds of topic map documents.</p>
 */

public abstract class AbstractTopicMapContentHandler extends DefaultHandler {

  // protected boolean validating = true;
  
  protected Collection propagated_themes;

  // The list of processed documents passed on to the current
  // document. Note that each document may contain multiple topic
  // maps.
  protected Collection processed_documents_current;
  // The list of processed retrieved from the parent. Note that this
  // collection does not change until the endDocument event is
  // reached.
  protected Collection processed_documents_from_parent;
  // The list of documents processed up til the current document. The
  // processed documents for each topic map in the document is added
  // to this list.
  protected Collection processed_documents_accumulated;
  
  // Parse state info
  protected Stack parents;
  protected Map info;

  /* current base uri, as modified by xml:base, is in the stack */

  /** document base uri, used for intra-document references.
      see RFC 2396, section 4.2 */
  protected LocatorIF doc_address;

  protected Locator locator;
  protected ErrorHandler ehandler;
  
  // Define a logging category.
  static Logger log = Logger.getLogger(AbstractTopicMapContentHandler.class.getName());
  
  public AbstractTopicMapContentHandler(LocatorIF base_address) {
    this(base_address, new HashSet());
 } 

  public AbstractTopicMapContentHandler(LocatorIF base_address, Collection processed_documents_from_parent) {
    this.doc_address = base_address;
    parents = new Stack();
    info = new HashMap();
    this.processed_documents_from_parent = processed_documents_from_parent;
  }
  
  public Collection getPropagatedThemes() {
    return propagated_themes;
  }
  
  public void setPropagatedThemes(Collection propagated_themes) {
    this.propagated_themes = propagated_themes;
  }

  protected void propagateThemes(ScopedIF scoped) {
    if (propagated_themes != null) {
      Iterator iter = propagated_themes.iterator();
      while(iter.hasNext())
        scoped.addTheme((TopicIF)iter.next());
    }
  }
  
  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }

  /**
   * INTERNAL: Registers the content handler with the given XML reader
   * object.</p>
   *
   * The content handler will register itself as the content handler of
   * the XML reader. It will also attempt to set the SAX core features
   * 'http://xml.org/sax/features/string-interning' to 'true' and
   * 'http://xml.org/sax/features/external-parameter-entities' to
   * 'false'.
   */
  public void register(XMLReader parser) {
    
    // Set required parser features

    try {
      // Use interned strings for names.
      parser.setFeature("http://xml.org/sax/features/string-interning", true);
    } catch (SAXException e) {
      log.warn("SAX string-interning feature not supported.");
    }

    try {
      // Don't read the DTD (document type definition)
      parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    } catch (SAXException e) {
      // this one isn't important enough for us to warn about
      log.debug("SAX external-parameter-entities feature not supported.");
    }
        
    // Register handlers with parser
    parser.setContentHandler(this);
    ErrorHandler _ehandler = parser.getErrorHandler();
    if (_ehandler == null || (_ehandler instanceof org.xml.sax.helpers.DefaultHandler))
      parser.setErrorHandler(getDefaultErrorHandler());

    // Get hold of actual error handler
    ehandler = parser.getErrorHandler();
  }

  protected String getLocationInfo() {
    if (locator == null) return "";
    return "(resource '" + locator.getSystemId() + "' line " + locator.getLineNumber() + " col " + locator.getColumnNumber() + ")";
  }

  protected ErrorHandler getDefaultErrorHandler() {
    return new Log4jSaxErrorHandler(log);
  }
  
}
