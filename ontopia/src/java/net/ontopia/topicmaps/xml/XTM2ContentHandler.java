
// $Id: XTM2ContentHandler.java,v 1.9 2008/06/13 08:36:30 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.io.IOException;
import java.net.MalformedURLException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.XMLReaderFactoryIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.SameStoreFactory;
import net.ontopia.topicmaps.impl.utils.ReificationUtils;
import org.apache.log4j.Logger;

/**
 * INTERNAL: Reads XTM 2.0.
 */
public class XTM2ContentHandler extends DefaultHandler {
  static final String NS_XTM2 = "http://www.topicmaps.org/xtm/";
  static final String XTM_NAMETYPE =
    "http://psi.topicmaps.org/iso13250/model/topic-name";
  static final String XTM_URITYPE = "http://www.w3.org/2001/XMLSchema#anyURI";
  static final String XTM_STRINGTYPE = "http://www.w3.org/2001/XMLSchema#string";
  
  // Define a logging category.
  static Logger log = Logger.getLogger(XTM2ContentHandler.class.getName());
  
  private TopicMapIF topicmap;
  private LocatorIF doc_address;
  private TopicMapStoreFactoryIF store_factory;
  private TopicMapBuilderIF builder;
  private XMLReaderFactoryIF xrfactory;
  private Set read_documents; // set of LocatorIFs for XTMs already read

  private boolean keep_content;
  private TopicIF topic;
  private StringBuffer content;
  private String datatype; // datatype attribute content
  private TopicIF type;
  private List scope;
  private TopicIF nametype;
  private int context; // used for simple state tracking
  private LocatorIF locator;
  private AssociationIF association; // created when we hit the first role
  private TopicIF player;
  private List itemids;
  private TopicNameIF basename; // used as parent for variants
  private TopicIF reifier;

  private static final int CONTEXT_TYPE        = 1;
  private static final int CONTEXT_SCOPE       = 2;
  private static final int CONTEXT_INSTANCEOF  = 3;
  private static final int CONTEXT_ROLE        = 4;
  private static final int CONTEXT_ROLE_TYPE   = 5;
  private static final int CONTEXT_TOPIC_MAP   = 6;
  private static final int CONTEXT_TOPIC       = 7;
  private static final int CONTEXT_TOPIC_NAME  = 8;
  private static final int CONTEXT_OCCURRENCE  = 9;
  private static final int CONTEXT_ASSOCIATION = 10;
  private static final int CONTEXT_VARIANT     = 11;

  public XTM2ContentHandler(TopicMapStoreFactoryIF store_factory,
                            XMLReaderFactoryIF xrfactory,
                            LocatorIF doc_address) {
    this(store_factory, xrfactory, doc_address,
         new CompactHashSet());
  }

  public XTM2ContentHandler(TopicMapStoreFactoryIF store_factory,
                            XMLReaderFactoryIF xrfactory,
                            LocatorIF doc_address,
                            Set read_documents) {
    this.store_factory = store_factory;
    this.xrfactory = xrfactory;
    this.doc_address = doc_address;
    this.read_documents = read_documents;
    this.content = new StringBuffer();
    this.scope = new ArrayList();
    this.itemids = new ArrayList();

    read_documents.add(doc_address);
  }
  
  // --- ContentHandler interface

  public void startElement(String uri, String name, String qname,
                           Attributes atts) {
    try {
      startElement_(uri, name, qname, atts);
    } catch(Exception e) {
      if (logError())
        log.error("Exception was thrown from within startElement", e);
      throw new OntopiaRuntimeException(e);
    }
  }

  public void endElement(String uri, String name, String qname) {
    try {
      endElement_(uri, name, qname);
    } catch(Exception e) {
      if (logError())
        log.error("Exception was thrown from within startElement", e);
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void startElement_(String uri, String name, String qname,
                            Attributes atts) throws SAXException {
    if (uri != NS_XTM2) // we only react to XTM 2.0 elements
      return;

    // <TOPICMAP
    if (name == "topicMap") {
      TopicMapStoreIF store = store_factory.createStore();
      topicmap = store.getTopicMap();
      builder = topicmap.getBuilder();
      context = CONTEXT_TOPIC_MAP;

      if ((store instanceof net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore) &&
          store.getBaseAddress() == null)
        ((net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore)store).setBaseAddress(doc_address);

      reifier = getReifier(atts);
      reify(topicmap, reifier);

      // <TOPIC
    } else if (name == "topic") {
      topic = builder.makeTopic();
      addItemIdentifier(topic, makeIDLocator(atts.getValue("", "id")));
      context = CONTEXT_TOPIC;

      // <ITEMIDENTITY
    } else if (name == "itemIdentity") {
      LocatorIF loc = makeLocator(atts.getValue("", "href"));
      if (context == CONTEXT_TOPIC_MAP)
        topicmap.addItemIdentifier(loc);
      else if (context == CONTEXT_TOPIC)
        addItemIdentifier(topic, loc);
      else if (context == CONTEXT_TOPIC_NAME ||
               context == CONTEXT_OCCURRENCE ||
               context == CONTEXT_ASSOCIATION ||
               context == CONTEXT_ROLE ||
               context == CONTEXT_VARIANT)
        itemids.add(loc);
      else
        throw new OntopiaRuntimeException("UNKNOWN CONTEXT: " + context);

      // <SUBJECTLOCATOR
    } else if (name == "subjectLocator") {
      LocatorIF sl = makeLocator(atts.getValue("", "href"));
      TopicIF other = topicmap.getTopicBySubjectLocator(sl);
      if (other == null)
        topic.addSubjectLocator(sl);
      else
        merge(topic, other);

      // <SUBJECTIDENTIFIER
    } else if (name == "subjectIdentifier") {
      LocatorIF si = makeLocator(atts.getValue("", "href"));
      TopicIF other = topicmap.getTopicBySubjectIdentifier(si);
      if (other == null)
        topic.addSubjectIdentifier(si);
      else
        merge(topic, other);

      // <VALUE
    } else if (name == "value")
      keep_content = true;

      // <RESOURCEDATA
    else if (name == "resourceData") {
      keep_content = true;
      datatype = atts.getValue("", "datatype");
    
      // <TYPE
    } else if (name == "type") {
      if (context == CONTEXT_ROLE)
        context = CONTEXT_ROLE_TYPE; // must remember role context after </type
      else
        context = CONTEXT_TYPE;

      // <TOPICREF
    } else if (name == "topicRef") {
      TopicIF ref = getTopicByIid(makeLocator(atts.getValue("", "href")));
      if (context == CONTEXT_TYPE)
        type = ref;
      else if (context == CONTEXT_ROLE_TYPE) {
        type = ref;
        context = CONTEXT_ROLE;
      } else if (context == CONTEXT_SCOPE)
        scope.add(ref);
      else if (context == CONTEXT_INSTANCEOF)
        topic.addType(ref);
      else if (context == CONTEXT_ROLE)
        player = ref;
      else
        throw new InvalidTopicMapException("topicRef in unknown context " +
                                           context);
      
      // <SCOPE
    } else if (name == "scope")
      context = CONTEXT_SCOPE;

      // <INSTANCEOF
    else if (name == "instanceOf")
      context = CONTEXT_INSTANCEOF;

      // <RESOURCEREF
    else if (name == "resourceRef")
      locator = makeLocator(atts.getValue("", "href"));

      // <ROLE
    else if (name == "role") {
      if (association == null) {
        association = builder.makeAssociation(type);
        addScope(association);
        reify(association, reifier);
        addItemIdentifiers(association);
        clear();
      }
      context = CONTEXT_ROLE;
      reifier = getReifier(atts);

      // MERGEMAP
    } else if (name == "mergeMap")
      loadMap(makeLocator(atts.getValue("", "href")));

      // VARIANT
    else if (name == "variant") {
      context = CONTEXT_VARIANT;
      if (basename == null)
        makeTopicName(); // can't store properties across the variants
      reifier = getReifier(atts);

    } else if (name == "name") {
      context = CONTEXT_TOPIC_NAME;
      reifier = getReifier(atts);
      
    } else if (name == "occurrence") {
      context = CONTEXT_OCCURRENCE;
      reifier = getReifier(atts);
      
    } else if (name == "association") {
      context = CONTEXT_ASSOCIATION;
      reifier = getReifier(atts);
    }
  }

  public void characters(char ch[], int start, int length) {
    if (keep_content) 
      content.append(ch, start, length);      
  }
  
  public void endElement_(String uri, String name, String qName)
    throws MalformedURLException {
    if (uri != NS_XTM2) // we only react to XTM 2.0 elements
      return;

      // </VALUE
    if (name == "value" || name == "resourceData")
      keep_content = false;

      // </NAME
    else if (name == "name") {
      if (basename == null)
        makeTopicName();
      basename = null; // no more variants now
      
      // </OCCURRENCE
    } else if (name == "occurrence") {
      OccurrenceIF occ;
      if (locator == null && datatype == null)
        occ = builder.makeOccurrence(topic, type, content.toString());
      else if (locator == null && datatype != null) {
        if (datatype.equals(XTM_URITYPE)) {
          locator = makeLocator(content.toString());
          occ = builder.makeOccurrence(topic, type, locator);
        } else {
          LocatorIF dtloc = new URILocator(datatype);
          occ = builder.makeOccurrence(topic, type, content.toString(),
                                       dtloc);
        }
      } else
        occ = builder.makeOccurrence(topic, type, locator);
      addScope(occ);
      addItemIdentifiers(occ);
      reify(occ, reifier);
      clear();

      // </ROLE
    } else if (name == "role") {
      AssociationRoleIF role =
        builder.makeAssociationRole(association, type, player);
      reify(role, reifier);
      addItemIdentifiers(role);
      clear();

      // </ASSOCIATION
    } else if (name == "association") {
      addItemIdentifiers(association);
      association = null;

      // </VARIANT>
    } else if (name == "variant") {
      VariantNameIF variant;
      if (locator == null && datatype == null)
        variant = builder.makeVariantName(basename, content.toString());
      else if (locator == null && datatype != null) {
        if (datatype.equals(XTM_URITYPE)) {
          locator = makeLocator(content.toString());
          variant = builder.makeVariantName(basename, locator);
        } else {
          LocatorIF dtloc = new URILocator(datatype);
          variant = builder.makeVariantName(basename, content.toString(),
                                            dtloc);
        }
      } else
        variant = builder.makeVariantName(basename, locator);
      addScope(variant);
      addItemIdentifiers(variant);
      reify(variant, reifier);
      clear();

      if (variant.getScope().size() <= basename.getScope().size())
        throw new InvalidTopicMapException("Variant " + variant + " scope " +
                                           "was not a superset of parent base "+
                                           "name's scope");
    }
  }

  // --- Internal helpers

  /**
   * INTERNAL: Clears the internal fields used for intermediate values
   * of objects.
   */
  private void clear() {
    type = null;
    content.setLength(0);
    scope.clear();
    locator = null;
    itemids.clear();
  }

  private TopicIF getDefaultNameType() {
    if (nametype == null) {
      LocatorIF psi = makeLocator(XTM_NAMETYPE);
      nametype = topicmap.getTopicBySubjectIdentifier(psi);
      if (nametype == null) {
        nametype = builder.makeTopic();
        nametype.addSubjectIdentifier(psi);
      }
    }
    return nametype;
  }

  /**
   * INTERNAL: Separate method for creating base names because this
   * code is required in two places.
   */
  private void makeTopicName() {
    if (type == null)
      type = getDefaultNameType();
    basename = builder.makeTopicName(topic, type, content.toString());
    addScope(basename);
    addItemIdentifiers(basename);
    reify(basename, reifier);
    clear();
  }
  
  private LocatorIF makeIDLocator(String id) {
    return doc_address.resolveAbsolute('#' + id);
  }

  private LocatorIF makeLocator(String uri) {
    return doc_address.resolveAbsolute(uri);
  }

  private TopicIF getTopicByIid(LocatorIF itemid) {
    if (itemid.getAddress().indexOf('#') == -1)
      throw new InvalidTopicMapException("Topic references must have fragment identifiers; invalid reference: " + itemid.getAddress());
    
    TMObjectIF obj = topicmap.getObjectByItemIdentifier(itemid);
    if (obj == null) {
      TopicIF topic = builder.makeTopic();
      topic.addItemIdentifier(itemid);
      return topic;
    } else if (obj instanceof TopicIF)
      return (TopicIF) obj;
    // FIXME: else what?
    return null;
  }

  private void addScope(ScopedIF obj) {
    for (int ix = 0; ix < scope.size(); ix++)
      obj.addTheme((TopicIF) scope.get(ix));
  }

  private void addItemIdentifier(TopicIF topic, LocatorIF ii) {
    TMObjectIF obj = topicmap.getObjectByItemIdentifier(ii);
    if (obj == null)
      topic.addItemIdentifier(ii);
    else if (obj instanceof TopicIF)
      merge(topic, (TopicIF) obj);
    else // itemid collision
      throw new InvalidTopicMapException("Another object " + obj + " already " +
                                         "has source locator " + ii);
  }

  private void addItemIdentifiers(TMObjectIF object) {
    for (int ix = 0; ix < itemids.size(); ix++)
      object.addItemIdentifier((LocatorIF) itemids.get(ix));
    itemids.clear();
  }
  
  private void merge(TopicIF target, TopicIF source) {
    if (target.equals(source))
      return;
    if (source.equals(nametype))
      nametype = target;
    MergeUtils.mergeInto(target, source);
  }

  private void loadMap(LocatorIF mapuri) throws SAXException {
    // Have we read this before?
    if (read_documents.contains(mapuri))
      return;
    
    // Create new parser object
    XMLReader parser = xrfactory.createXMLReader();
        
    // Initialize nested content handler
    TopicMapStoreFactoryIF sfactory = new SameStoreFactory(topicmap.getStore());
    XTM2ContentHandler handler =
      new XTM2ContentHandler(sfactory, xrfactory, mapuri, read_documents);
    parser.setContentHandler(handler);
    
    // Parse input source
    try {
      InputSource source = new InputSource(mapuri.getExternalForm());
      parser.parse(source);
    } catch (IOException e) {
      throw new OntopiaRuntimeException("Cannot include topic map '" +
                                        mapuri.getAddress() + "': " +
                                        e.getMessage(), e);
    }
  }

  private TopicIF getReifier(Attributes atts) {
    String ref = atts.getValue("", "reifier");
    if (ref == null)
      return null;

    LocatorIF itemid = makeLocator(ref);
    return getTopicByIid(itemid);
  }

  private void reify(ReifiableIF reifiable, TopicIF reifier) {
    ReificationUtils.reify(reifiable, reifier);
  }

  private boolean logError() {
    try {
      return Boolean.valueOf(System.getProperty("net.ontopia.topicmaps.xml.XTMContentHandler.logError")).booleanValue();
    } catch (SecurityException e) {
      return false;
    }
  }
  
  // --- External interface
  
  public Collection getTopicMaps() {
    if (topicmap == null)
      return Collections.EMPTY_SET;
    else
      return Collections.singleton(topicmap);
  }
}
