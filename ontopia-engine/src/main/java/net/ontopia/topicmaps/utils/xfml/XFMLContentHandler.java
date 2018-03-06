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

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.topicmaps.utils.AssociationBuilder;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.xml.AbstractTopicMapContentHandler;
import net.ontopia.utils.OntopiaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL: SAX2 content handler used for importing XFML documents.
 * The content handler builds a topic map object based on a SAX event
 * stream conforming to the XFML interchange syntax.
 */
public class XFMLContentHandler extends AbstractTopicMapContentHandler {

  private static final String EL_XFML        = "xfml";
  private static final String EL_FACET       = "facet";
  private static final String EL_TOPIC       = "topic";
  private static final String EL_NAME        = "name";
  private static final String EL_PSI         = "psi";
  private static final String EL_DESCRIPTION = "description";
  private static final String EL_PAGE        = "page";
  private static final String EL_TITLE       = "title";
  private static final String EL_CONNECT     = "connect";
  private static final String EL_OCCURRENCE  = "occurrence";
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(XFMLContentHandler.class.getName());

  protected TopicMapStoreFactoryIF stores;

  protected LocatorIF map_uri;
  private TopicMapIF topicmap;
  private TopicMapBuilderIF builder;
  private StringBuilder content;
  private boolean keep_content;

  // parse state
  private TopicIF current_topic;

  // PSI holders
  private AssociationBuilder parentBuilder;
  private AssociationBuilder occursBuilder;
  private TopicIF PSI_DESCRIPTION;
  
  public XFMLContentHandler(TopicMapStoreFactoryIF stores, LocatorIF base_address) {
    super(base_address);
    this.stores = stores;
  }

  public XFMLContentHandler(TopicMapStoreFactoryIF stores, LocatorIF base_address, Collection processed_documents) {
    super(base_address, processed_documents);
    this.stores = stores;
  }

  /**
   * INTERNAL: Gets the topic map found after having parsed the input source.
   */  
  public TopicMapIF getTopicMap() {
    return topicmap;
  }
  
  // --------------------------------------------------------------------------
  // Document events
  // --------------------------------------------------------------------------
    
  @Override
  public void startDocument () {

    // Initialize variables
    parents.clear();
    info.clear();
    keep_content = false;
    content = new StringBuilder();

    topicmap = stores.createStore().getTopicMap();
    builder = topicmap.getBuilder();
    makePSIs();

    log.info("Processing document '" + doc_address + "'.");

    // Initialize list of accumulated processed documents
    this.processed_documents_accumulated = new HashSet();
  }

  @Override
  public void endDocument () {
    // Copy list of accumulated processed documents to parent list
    this.processed_documents_from_parent.addAll(processed_documents_accumulated);
    
    // log.debug("Stack size: " + parents.size());
    // log.debug("Info map: " + info);
  }
  
  @Override
  public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
    try {
    
    //log.debug("S: '" + uri + "' " + qName);

    // ----- <xfml> -----------------------------------------------------------
    if (EL_XFML.equals(qName)) {
      String version = atts.getValue("version");
      if (version == null) {
        log.warn("No version attribute on 'xfml' element");
      }
      if (!"1.0".equals(version)) {
        log.warn("Unsupported XFML version: " + version);
      }

      String mapurl = atts.getValue("url");
      if (mapurl == null) {
        log.warn("No url attribute on 'xfml' element");
      } else {
        try {
          map_uri = new URILocator(mapurl);

          TopicMapStoreIF store = topicmap.getStore();
          if (store instanceof AbstractTopicMapStore && store.getBaseAddress() == null) {
            ((AbstractTopicMapStore) store).setBaseAddress(map_uri);
          }

          doc_address = map_uri;
          
          // Add this document to the list of processed documents.
          processed_documents_accumulated.add(getBaseAddress());
        } catch (URISyntaxException e) {
          log.warn("Invalid xfml base URL: " + mapurl);
        }
      }

      // FIXME: what to do about language?
    }

    // ----- <facet> ----------------------------------------------------------
    else if (EL_FACET.equals(qName)) {
      String id = atts.getValue("id");
      // FIXME: complain if no id

      current_topic = builder.makeTopic();
      registerSourceLocator(current_topic, id);
      
      keep_content = true;
    }

    // ----- <topic> ----------------------------------------------------------
    else if (EL_TOPIC.equals(qName)) {
      String id = atts.getValue("id");
      // FIXME: complain if no id

      current_topic = builder.makeTopic();
      registerSourceLocator(current_topic, id);

      String parentid = atts.getValue("parentTopicid");
      if (parentid == null) {
        parentid = atts.getValue("facetid");
      }
      // FIXME: complain if no refs

      TopicIF parent = resolveTopicRef("#" + parentid);
      parentBuilder.makeAssociation(parent, current_topic);
    }

    // ----- <page> -----------------------------------------------------------
    else if (EL_PAGE.equals(qName)) {
      String url = atts.getValue("url");
      // FIXME: complain if no url

      current_topic = builder.makeTopic();
      current_topic.addSubjectLocator(createLocator(url));
    }

    // ----- <occurrence>------------------------------------------------------
    else if (EL_OCCURRENCE.equals(qName)) {
      String topicid = atts.getValue("topicid");
      // FIXME: complain if none

      TopicIF subject = resolveTopicRef("#" + topicid);
      occursBuilder.makeAssociation(subject, current_topic);
    }
    
    // ----- <name> -----------------------------------------------------------
    // ----- <psi> ------------------------------------------------------------
    // ----- <description> ----------------------------------------------------
    // ----- <title> ----------------------------------------------------------
    // ----- <connect> --------------------------------------------------------
    else if (EL_NAME.equals(qName) || EL_PSI.equals(qName) || EL_DESCRIPTION.equals(qName) ||
            EL_TITLE.equals(qName) || EL_CONNECT.equals(qName)) {
      keep_content = true;
    }   
    
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Override
  public void characters (char ch[], int start, int length) {
    if (keep_content) {
      content.append(ch, start, length);
    }      
  }

  @Override
  public void endElement (String uri, String name, String qName) throws SAXException {
    // log.debug("E: " + qName);

    // ----- </facet> ---------------------------------------------------------
    if (EL_FACET.equals(qName)) {
      builder.makeTopicName(current_topic, content.toString());
    
    // ----- </name> ----------------------------------------------------------
    // ----- </title> ---------------------------------------------------------
    } else if ((EL_NAME.equals(qName) || EL_TITLE.equals(qName)) &&
             current_topic != null) {
      builder.makeTopicName(current_topic, content.toString());
    
    // ----- </psi> -----------------------------------------------------------
    } else if (EL_PSI.equals(qName)) {
      addSubjectIdentifier(current_topic, createLocator(content.toString()));
    
    // ----- </description> ---------------------------------------------------
    } else if (EL_DESCRIPTION.equals(qName)) {
      builder.makeOccurrence(current_topic, PSI_DESCRIPTION, content.toString());
    }

    // ----- </connect> -------------------------------------------------------
    else if (EL_CONNECT.equals(qName)) {
      current_topic.addItemIdentifier(createLocator(content.toString()));
    }
    
    keep_content = false;
    content.setLength(0);
  }

  // --------------------------------------------------------------------------
  // Misc. methods
  // --------------------------------------------------------------------------

  protected LocatorIF getBaseAddress() {
    return map_uri;
  }

  protected TopicIF resolveTopicRef(String address) throws SAXException {
    LocatorIF locator = createLocator(address);
    TopicIF topic = (TopicIF) topicmap.getObjectByItemIdentifier(locator);
    if (topic == null) {
      topic = topicmap.getTopicBySubjectIdentifier(locator);
    }
    
    if (topic == null) {
      if (address.charAt(0) == '#' ||
          locator.getAddress().startsWith(getBaseAddress().getAddress()+"#")) {
        // this is a local reference; create a topic and return it
        topic = builder.makeTopic();
        topic.addItemIdentifier(locator);
        
      } else {
        throw new OntopiaRuntimeException("INTERNAL: Topic ID must begin with '#'");
      }
    }

    return topic;
  }

  protected void registerSourceLocator(TopicIF tmobject, String id) {
    // No need to register source locator if id is null
    if (id == null) {
      return;
    }
    // Create source locator
    LocatorIF locator = createLocator("#" + id);
    // Add the source locator
    addItemIdentifier(tmobject, locator);
  }

  protected void registerSourceLocator(TMObjectIF tmobject, String id) {
    // No need to register source locator if id is null
    if (id == null) {
      return;
    }
    tmobject.addItemIdentifier(createLocator("#" + id));
  }

  protected void addItemIdentifier(TopicIF topic, LocatorIF locator) {

    // Check to see if source locator is a subject indicator of
    // another topic. If so they should merge.
    TopicIF other_topic = topicmap.getTopicBySubjectIdentifier(locator);
    
    if (other_topic != null) {
      if (log.isInfoEnabled()) {
        log.info("Topic " + topic + " merged with + " + other_topic +
                 " because the source locator is the same as the subject indicator of the other: " + locator);
      }
      
      // Merge topic with other topic. 
      MergeUtils.mergeInto(topic, other_topic);

      // Subject indicator is no longer needed
      topic.removeSubjectIdentifier(locator);
    }
    
    // Add source locator to object
    topic.addItemIdentifier(locator);
  }

  protected void addSubjectIdentifier(TopicIF topic, LocatorIF locator) {
    // Check to see if subject indicator is a source locator of another topic. If so they should merge.
    TMObjectIF other_topic = topicmap.getObjectByItemIdentifier(locator);

    if (other_topic != null && other_topic instanceof TopicIF) {
      if (log.isInfoEnabled()) {
        log.info("Topic " + topic + " merged with + " + other_topic +
                 " because the subject indicator is the same as the source locator of the other: " + locator);
      }
      
      // Merge topic with other topic. 
      MergeUtils.mergeInto(topic, (TopicIF)other_topic);

      // Note: subject indicator was never added (there is no need to).
    }
    else {  
      // Add subject indicator to object
      topic.addSubjectIdentifier(locator);
    }
  }
  
  protected LocatorIF createLocator(String address) {
    if (address.length() == 0) {
      return getBaseAddress();
    } else if (address.charAt(0) == '#') {
      // this is necessary because URI refs of the form "#foo" are
      // same-document references (RFC 2396 - 4.2), and resolve
      // relative to the document address, regardless of what base
      // address may be in effect inside the document
      return doc_address.resolveAbsolute(address);
    } else {
      return getBaseAddress().resolveAbsolute(address);
    }
  }

  protected LocatorIF createURILocator(String address) {
    try {
      return new URILocator(address);
    } catch (URISyntaxException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // --- Internal methods

  private void makePSIs() {
    try {
      PSI_DESCRIPTION = builder.makeTopic();
      PSI_DESCRIPTION.addSubjectIdentifier(new URILocator("http://psi.ontopia.net/xtm/occurrence-type/description"));

      String xfml = "http://psi.ontopia.net/xfml/#";
      TopicIF parent_child = makeTopic(xfml + "parent-child");
      TopicIF parent = makeTopic(xfml + "parent");
      TopicIF child = makeTopic(xfml + "child");
      parentBuilder = new AssociationBuilder(parent_child, parent, child);

      TopicIF occurs_on = makeTopic(xfml + "occurs-on");
      TopicIF page = makeTopic(xfml + "page");
      TopicIF subject = makeTopic(xfml + "subject");
      occursBuilder = new AssociationBuilder(occurs_on, subject, page);
    } catch (URISyntaxException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  private TopicIF makeTopic(String psi) throws URISyntaxException {
    TopicIF topic = builder.makeTopic();
    topic.addSubjectIdentifier(new URILocator(psi));
    return topic;
  }
  
}
