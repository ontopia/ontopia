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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.xml.Slf4jSaxErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL: Abstract SAX2 content handler used for reading various
 * kinds of topic map documents.
 */
public abstract class AbstractTopicMapContentHandler extends DefaultHandler {  
  protected Collection<TopicIF> propagated_themes;

  // The list of processed documents passed on to the current
  // document. Note that each document may contain multiple topic
  // maps.
  protected Collection<LocatorIF> processed_documents_current;
  // The list of processed retrieved from the parent. Note that this
  // collection does not change until the endDocument event is
  // reached.
  protected Collection<LocatorIF> processed_documents_from_parent;
  // The list of documents processed up til the current document. The
  // processed documents for each topic map in the document is added
  // to this list.
  protected Collection<LocatorIF> processed_documents_accumulated;
  
  // Parse state info
  protected Stack<String> parents;
  protected Map<String, Object> info;

  /* current base uri, as modified by xml:base, is in the stack */

  /** document base uri, used for intra-document references.
      see RFC 2396, section 4.2 */
  protected LocatorIF doc_address;

  protected Locator locator;
  protected ErrorHandler ehandler;
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(AbstractTopicMapContentHandler.class.getName());
  
  public AbstractTopicMapContentHandler(LocatorIF base_address) {
    this(base_address, new HashSet<LocatorIF>());
 } 

  public AbstractTopicMapContentHandler(LocatorIF base_address, Collection<LocatorIF> processed_documents_from_parent) {
    this.doc_address = base_address;
    parents = new Stack<>();
    info = new HashMap<>();
    this.processed_documents_from_parent = processed_documents_from_parent;
  }
  
  public Collection<TopicIF> getPropagatedThemes() {
    return propagated_themes;
  }
  
  public void setPropagatedThemes(Collection<TopicIF> propagated_themes) {
    this.propagated_themes = propagated_themes;
  }

  protected void propagateThemes(ScopedIF scoped) {
    if (propagated_themes != null) {
      for (TopicIF scope : propagated_themes) {
        scoped.addTheme(scope);
      }
    }
  }
  
  @Override
  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }

  /**
   * INTERNAL: Registers the content handler with the given XML reader
   * object.
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
      // Don't read the DTD (document type definition)
      parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    } catch (SAXException e) {
      // this one isn't important enough for us to warn about
      log.debug("SAX external-parameter-entities feature not supported.");
    }
        
    // Register handlers with parser
    parser.setContentHandler(this);
    ErrorHandler _ehandler = parser.getErrorHandler();
    if (_ehandler == null || (_ehandler instanceof org.xml.sax.helpers.DefaultHandler)) {
      parser.setErrorHandler(getDefaultErrorHandler());
    }

    // Get hold of actual error handler
    ehandler = parser.getErrorHandler();
  }

  protected String getLocationInfo() {
    if (locator == null) {
      return "";
    }
    return "(resource '" + locator.getSystemId() + "' line " + locator.getLineNumber() + " col " + locator.getColumnNumber() + ")";
  }

  protected ErrorHandler getDefaultErrorHandler() {
    return new Slf4jSaxErrorHandler(log);
  }
  
}
