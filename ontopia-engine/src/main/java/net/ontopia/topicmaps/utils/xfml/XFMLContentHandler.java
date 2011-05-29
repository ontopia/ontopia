
package net.ontopia.topicmaps.utils.xfml;

import java.util.*;
import java.io.IOException;
import java.net.MalformedURLException;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.topicmaps.xml.AbstractTopicMapContentHandler;
import net.ontopia.topicmaps.xml.IgnoreTopicMapDTDEntityResolver;
import net.ontopia.xml.*;
import net.ontopia.utils.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: SAX2 content handler used for importing XFML documents.
 * The content handler builds a topic map object based on a SAX event
 * stream conforming to the XFML interchange syntax.
 */
public class XFMLContentHandler extends AbstractTopicMapContentHandler {

  static final String NO_URI = "URI";

  static final String EL_XFML        = "xfml";
  static final String EL_FACET       = "facet";
  static final String EL_TOPIC       = "topic";
  static final String EL_NAME        = "name";
  static final String EL_PSI         = "psi";
  static final String EL_DESCRIPTION = "description";
  static final String EL_PAGE        = "page";
  static final String EL_TITLE       = "title";
  static final String EL_CONNECT     = "connect";
  static final String EL_OCCURRENCE  = "occurrence";
  
  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(XFMLContentHandler.class.getName());

  protected TopicMapStoreFactoryIF stores;
  protected XMLReaderFactoryIF xrfactory;

  protected LocatorIF map_uri;
  private TopicMapIF topicmap;
  private TopicMapBuilderIF builder;
  private StringBuffer content;
  private boolean keep_content;

  // parse state
  private TopicIF current_topic;

  // PSI holders
  private AssociationBuilder parentBuilder;
  private AssociationBuilder occursBuilder;
  private TopicIF PSI_DESCRIPTION;
  
  public XFMLContentHandler(TopicMapStoreFactoryIF stores, XMLReaderFactoryIF xrfactory, LocatorIF base_address) {
    super(base_address);
    this.stores = stores;
    this.xrfactory = xrfactory;
  }

  public XFMLContentHandler(TopicMapStoreFactoryIF stores, XMLReaderFactoryIF xrfactory, LocatorIF base_address, Collection processed_documents) {
    super(base_address, processed_documents);
    this.stores = stores;
    this.xrfactory = xrfactory;
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
    
  public void startDocument () {

    // Initialize variables
    parents.clear();
    info.clear();
    keep_content = false;
    content = new StringBuffer();

    topicmap = stores.createStore().getTopicMap();
    builder = topicmap.getBuilder();
    makePSIs();

    log.info("Processing document '" + doc_address + "'.");

    // Initialize list of accumulated processed documents
    this.processed_documents_accumulated = new HashSet();
  }

  public void endDocument () {
    // Copy list of accumulated processed documents to parent list
    this.processed_documents_from_parent.addAll(processed_documents_accumulated);
    
    // log.debug("Stack size: " + parents.size());
    // log.debug("Info map: " + info);
  }
  
  public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
    try {
    
    //log.debug("S: '" + uri + "' " + qName);

    // ----- <xfml> -----------------------------------------------------------
    if (qName == EL_XFML) {
      String version = atts.getValue("version");
      if (version == null)
        log.warn("No version attribute on 'xfml' element");
      if (!version.equals("1.0"))
        log.warn("Unsupported XFML version: " + version);

      String mapurl = atts.getValue("url");
      if (mapurl == null)
        log.warn("No url attribute on 'xfml' element");
      else {
        try {
          map_uri = new URILocator(mapurl);

          TopicMapStoreIF store = topicmap.getStore();
          if (store instanceof AbstractTopicMapStore && store.getBaseAddress() == null)
            ((AbstractTopicMapStore) store).setBaseAddress(map_uri);

          doc_address = map_uri;
          
          // Add this document to the list of processed documents.
          processed_documents_accumulated.add(getBaseAddress());
        } catch (MalformedURLException e) {
          log.warn("Invalid xfml base URL: " + mapurl);
        }
      }

      // FIXME: what to do about language?
    }

    // ----- <facet> ----------------------------------------------------------
    else if (qName == EL_FACET) {
      String id = atts.getValue("id");
      // FIXME: complain if no id

      current_topic = builder.makeTopic();
      registerSourceLocator(current_topic, id);
      
      keep_content = true;
    }

    // ----- <topic> ----------------------------------------------------------
    else if (qName == EL_TOPIC) {
      String id = atts.getValue("id");
      // FIXME: complain if no id

      current_topic = builder.makeTopic();
      registerSourceLocator(current_topic, id);

      String parentid = atts.getValue("parentTopicid");
      if (parentid == null)
        parentid = atts.getValue("facetid");
      // FIXME: complain if no refs

      TopicIF parent = resolveTopicRef("#" + parentid);
      parentBuilder.makeAssociation(parent, current_topic);
    }

    // ----- <page> -----------------------------------------------------------
    else if (qName == EL_PAGE) {
      String url = atts.getValue("url");
      // FIXME: complain if no url

      current_topic = builder.makeTopic();
      current_topic.addSubjectLocator(createLocator(url));
    }

    // ----- <occurrence>------------------------------------------------------
    else if (qName == EL_OCCURRENCE) {
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
    else if (qName == EL_NAME || qName == EL_PSI || qName == EL_DESCRIPTION ||
             qName == EL_TITLE || qName == EL_CONNECT) 
      keep_content = true;   
    
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public void characters (char ch[], int start, int length) {
    if (keep_content) 
      content.append(ch, start, length);      
  }

  public void endElement (String uri, String name, String qName) throws SAXException {
    // log.debug("E: " + qName);

    // ----- </facet> ---------------------------------------------------------
    if (qName == EL_FACET)
      builder.makeTopicName(current_topic, content.toString());
    
    // ----- </name> ----------------------------------------------------------
    // ----- </title> ---------------------------------------------------------
    else if ((qName == EL_NAME || qName == EL_TITLE) &&
             current_topic != null)
      builder.makeTopicName(current_topic, content.toString());
    
    // ----- </psi> -----------------------------------------------------------
    else if (qName == EL_PSI)
      addSubjectIdentifier(current_topic, createLocator(content.toString()));
    
    // ----- </description> ---------------------------------------------------
    else if (qName == EL_DESCRIPTION) {
      OccurrenceIF occ = builder.makeOccurrence(current_topic, PSI_DESCRIPTION, content.toString());
    }

    // ----- </connect> -------------------------------------------------------
    else if (qName == EL_CONNECT) 
      current_topic.addItemIdentifier(createLocator(content.toString()));
    
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
    if (topic == null)
      topic = topicmap.getTopicBySubjectIdentifier(locator);
    
    if (topic == null) {
      if (address.charAt(0) == '#' ||
          locator.getAddress().startsWith(getBaseAddress().getAddress()+"#")) {
        // this is a local reference; create a topic and return it
        topic = builder.makeTopic();
        topic.addItemIdentifier(locator);
        
      } else
        throw new OntopiaRuntimeException("INTERNAL: Topic ID must begin with '#'");
    }

    return topic;
  }

  protected void registerSourceLocator(TopicIF tmobject, String id) {
    // No need to register source locator if id is null
    if (id == null) return;
    // Create source locator
    LocatorIF locator = createLocator("#" + id);
    // Add the source locator
    addItemIdentifier(tmobject, locator);
  }

  protected void registerSourceLocator(TMObjectIF tmobject, String id) {
    // No need to register source locator if id is null
    if (id == null) return;
    tmobject.addItemIdentifier(createLocator("#" + id));
  }

  protected void addItemIdentifier(TopicIF topic, LocatorIF locator) {

    // Check to see if source locator is a subject indicator of
    // another topic. If so they should merge.
    TopicIF other_topic = topicmap.getTopicBySubjectIdentifier(locator);
    
    if (other_topic != null) {
      if (log.isInfoEnabled())
        log.info("Topic " + topic + " merged with + " + other_topic +
                 " because the source locator is the same as the subject indicator of the other: " + locator);
      
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
      if (log.isInfoEnabled())
        log.info("Topic " + topic + " merged with + " + other_topic +
                 " because the subject indicator is the same as the source locator of the other: " + locator);
      
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
    if (address.length() == 0)
      return getBaseAddress();
    else if (address.charAt(0) == '#')
      // this is necessary because URI refs of the form "#foo" are
      // same-document references (RFC 2396 - 4.2), and resolve
      // relative to the document address, regardless of what base
      // address may be in effect inside the document
      return doc_address.resolveAbsolute(address);
    else
      return getBaseAddress().resolveAbsolute(address);
  }

  protected LocatorIF createURILocator(String address) {
    try {
      return new URILocator(address);
    } catch (MalformedURLException e) {
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
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  private TopicIF makeTopic(String psi) throws MalformedURLException {
    TopicIF topic = builder.makeTopic();
    topic.addSubjectIdentifier(new URILocator(psi));
    return topic;
  }
  
}
