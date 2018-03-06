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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.KeyGenerator;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.utils.SameStoreFactory;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.DefaultXMLReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL: Reads both XTM 2.0 and XTM 2.1.
 */
public class XTM2ContentHandler extends DefaultHandler {
  public static final String NS_XTM2 = "http://www.topicmaps.org/xtm/";
  public static final String XTM_URITYPE = "http://www.w3.org/2001/XMLSchema#anyURI";
  public static final String XTM_STRINGTYPE = "http://www.w3.org/2001/XMLSchema#string";
  private static final String HREF = "href";
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(XTM2ContentHandler.class.getName());
  
  private TopicMapIF topicmap;
  private LocatorIF doc_address;
  private TopicMapStoreFactoryIF store_factory;
  private TopicMapBuilderIF builder;
  private Set read_documents; // set of LocatorIFs for XTMs already read

  private boolean keep_content;
  private TopicIF topic;
  private StringBuilder content;
  private String datatype; // datatype attribute content
  private TopicIF type;
  private List<TopicIF> scope;
  private TopicIF nametype;
  private int context; // used for simple state tracking
  private int nextContext; // used for elements like <reifier/> where have to fall back to the previous state
  private LocatorIF locator;
  private AssociationIF association; // created when we hit the first role
  private TopicIF player;
  private List<LocatorIF> itemids;
  private TopicNameIF basename; // used as parent for variants
  private TopicIF reifier;         // stores reifier until end tag
  private TopicIF stacked_reifier; // used for associations (roles can be reified)
  private List<LocatorIF> stacked_itemids; // used for associations
  private List<RoleReification> delayedRoleReification; // see issue 116 below
  private boolean xtm21;  // Indicates if we are in XTM 2.1 oder 2.0 mode (needed handle the fragment identifier in <topicRef/> right)

  private boolean seenReifier; // Indicates if the reifier attribute
                               // has been processed.  Used to
                               // validate the XTM 2.1 topic map even
                               // if validation against the RELAX-NG
                               // schema is disabled
  
  private boolean seenIdentity; // Indicates if the topic has an
                                // identity. Used to ensure that XTM
                                // 2.1 topics have an iid, sid or slo
                                // even if the validation against the
                                // RELAX-NG schema is disabled.

  private static final int CONTEXT_TYPE        = 1;
  private static final int CONTEXT_SCOPE       = 2;
  private static final int CONTEXT_INSTANCEOF  = 3;
  private static final int CONTEXT_ROLE        = 4;
  private static final int CONTEXT_TOPIC_MAP   = 6;
  private static final int CONTEXT_TOPIC       = 7;
  private static final int CONTEXT_TOPIC_NAME  = 8;
  private static final int CONTEXT_OCCURRENCE  = 9;
  private static final int CONTEXT_ASSOCIATION = 10;
  private static final int CONTEXT_VARIANT     = 11;
  private static final int CONTEXT_REIFIER     = 12;

  public XTM2ContentHandler(TopicMapStoreFactoryIF store_factory,
                            LocatorIF doc_address) {
    this(store_factory, doc_address,
         new CompactHashSet());
  }

  public XTM2ContentHandler(TopicMapStoreFactoryIF store_factory,
                            LocatorIF doc_address,
                            Set read_documents) {
    this.store_factory = store_factory;
    this.doc_address = doc_address;
    this.read_documents = read_documents;
    this.content = new StringBuilder();
    this.scope = new ArrayList<TopicIF>();
    this.itemids = new ArrayList<LocatorIF>();
    this.delayedRoleReification = new ArrayList<RoleReification>();

    read_documents.add(doc_address);
  }
  
  // --- ContentHandler interface

  @Override
  public void startElement(String uri, String name, String qname,
                           Attributes atts) {
    try {
      startElement_(uri, name, qname, atts);
    } catch(Exception e) {
      if (logError()) {
        log.error("Exception was thrown from within startElement", e);
      }
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public void endElement(String uri, String name, String qname) {
    try {
      endElement_(uri, name, qname);
    } catch(Exception e) {
      if (logError()) {
        log.error("Exception was thrown from within startElement", e);
      }
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void startElement_(String uri, String name, String qname,
                            Attributes atts) throws SAXException {
    if (!NS_XTM2.equals(uri)) { // we only react to XTM 2.0 elements
      return;
    }

    // <TOPICMAP
    if ("topicMap".equals(name)) {
      TopicMapStoreIF store = store_factory.createStore();
      topicmap = store.getTopicMap();
      builder = topicmap.getBuilder();
      context = CONTEXT_TOPIC_MAP;
      xtm21 = "2.1".equals(atts.getValue("", "version"));

      if ((store instanceof net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore) &&
          store.getBaseAddress() == null) {
        ((net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore)store).setBaseAddress(doc_address);
      }

      handleTopicMapReifier(getReifier(atts));

      // <TOPIC
    } else if ("topic".equals(name)) {
      topic = builder.makeTopic();
      seenIdentity = false;
      final String id = atts.getValue("", "id");
      if (id != null) {
        seenIdentity = true;
        addItemIdentifier(topic, makeIDLocator(id));
      } else if (!xtm21) {
        throw new InvalidTopicMapException("No id attribute on <topic>.");
      }

      context = CONTEXT_TOPIC;

      // <ITEMIDENTITY
    } else if ("itemIdentity".equals(name)) {
      LocatorIF loc = makeLocator(atts.getValue("", HREF));
      if (context == CONTEXT_TOPIC_MAP) {
        topicmap.addItemIdentifier(loc);
      } else if (context == CONTEXT_TOPIC) {
        addItemIdentifier(topic, loc);
      } else if (context == CONTEXT_TOPIC_NAME ||
               context == CONTEXT_OCCURRENCE ||
               context == CONTEXT_ASSOCIATION ||
               context == CONTEXT_ROLE ||
               context == CONTEXT_VARIANT) {
        itemids.add(loc);
      } else {
        throw new OntopiaRuntimeException("UNKNOWN CONTEXT: " + context);
      }

      // <SUBJECTLOCATOR
    } else if ("subjectLocator".equals(name)) {
      seenIdentity = true;
      LocatorIF sl = makeLocator(atts.getValue("", HREF));
      TopicIF other = topicmap.getTopicBySubjectLocator(sl);
      if (other == null) {
        topic.addSubjectLocator(sl);
      } else {
        merge(topic, other);
      }

      // <SUBJECTIDENTIFIER
    } else if ("subjectIdentifier".equals(name)) {
      seenIdentity = true;
      LocatorIF si = makeLocator(atts.getValue("", HREF));
      TopicIF other = topicmap.getTopicBySubjectIdentifier(si);
      if (other == null) {
        topic.addSubjectIdentifier(si);
      } else {
        merge(topic, other);
      }

      // <VALUE
    } else if ("value".equals(name)) {
      keep_content = true;

      // <RESOURCEDATA
    } else if ("resourceData".equals(name)) {
      keep_content = true;
      datatype = atts.getValue("", "datatype");
    
      // <TYPE
    } else if ("type".equals(name)) {
      nextContext = context;
      context = CONTEXT_TYPE;

      // <TOPICREF
    } else if ("topicRef".equals(name)) {
      handleTopicReference(getTopicByIid(makeLocator(atts.getValue("", HREF))));

      // <SUBJECTIDENTIFIERREF
    } else if ("subjectIdentifierRef".equals(name)) {
      if (!xtm21) {
        throw new InvalidTopicMapException("The <subjectIdentifierRef/> is illegal in XTM 2.0");
      }
      handleTopicReference(getTopicBySid(makeLocator(atts.getValue("", HREF))));
      
      // <SUBJECTLOCATORREF
    } else if ("subjectLocatorRef".equals(name)) {
      if (!xtm21) {
        throw new InvalidTopicMapException("The <subjectLocatorRef/> is illegal in XTM 2.0");
      }
      handleTopicReference(getTopicBySlo(makeLocator(atts.getValue("", HREF))));
      
      // <SCOPE
    } else if ("scope".equals(name)) {
      context = CONTEXT_SCOPE;

      // <INSTANCEOF
    } else if ("instanceOf".equals(name)) {
      context = CONTEXT_INSTANCEOF;

      // <RESOURCEREF
    } else if ("resourceRef".equals(name)) {
      locator = makeLocator(atts.getValue("", HREF));

      // <ROLE
    } else if ("role".equals(name)) {
      if (association == null) {
        association = builder.makeAssociation(type);
        addScope(association);
        if (!itemids.isEmpty()) {
          stacked_itemids = new ArrayList<LocatorIF>(itemids);
        }
        clear();
      }
      context = CONTEXT_ROLE;
      reifier = getReifier(atts);

      // <MERGEMAP
    } else if ("mergeMap".equals(name)) {
      loadMap(makeLocator(atts.getValue("", HREF)));

      // <VARIANT
    } else if ("variant".equals(name)) {
      context = CONTEXT_VARIANT;
      if (basename == null) {
        makeTopicName(); // can't store properties across the variants
      }
      reifier = getReifier(atts);

      // <NAME
    } else if ("name".equals(name)) {
      context = CONTEXT_TOPIC_NAME;
      reifier = getReifier(atts);
      
      // <OCCURRENCE
    } else if ("occurrence".equals(name)) {
      context = CONTEXT_OCCURRENCE;
      reifier = getReifier(atts);

      // <ASSOCIATION
    } else if ("association".equals(name)) {
      context = CONTEXT_ASSOCIATION;
      stacked_reifier = getReifier(atts);
      
      // <REIFIER
    } else if ("reifier".equals(name)) {
      if (!xtm21) {
        throw new InvalidTopicMapException("The <reifier/> is illegal in XTM 2.0");
      }
      if (seenReifier) {
        throw new InvalidTopicMapException("Having a reifier attribute and a reifier element is illegal in XTM 2.1");
      }

      nextContext = context;
      context = CONTEXT_REIFIER;
    }
  }

  @Override
  public void characters(char ch[], int start, int length) {
    if (keep_content) {
      content.append(ch, start, length);
    }      
  }
  
  public void endElement_(String uri, String name, String qName)
    throws URISyntaxException {
    if (!NS_XTM2.equals(uri)) { // we only react to XTM 2.0 elements
      return;
    }

    if ("topic".equals(name)) {
      if (!seenIdentity) {
        throw new InvalidTopicMapException("The topic has neither id, subject identifier, item identifier, nor subject locator");
      }
    }
      // </VALUE
    else if ("value".equals(name) || "resourceData".equals(name)) {
      keep_content = false;

      // </NAME
    } else if ("name".equals(name)) {
      if (basename == null) {
        makeTopicName();
      }
      basename = null; // no more variants now
      
      // </OCCURRENCE
    } else if ("occurrence".equals(name)) {
      OccurrenceIF occ;
      if (locator == null && datatype == null) {
        occ = builder.makeOccurrence(topic, type, content.toString());
      } else if (locator == null && datatype != null) {
        if (datatype.equals(XTM_URITYPE)) {
          locator = makeLocator(content.toString());
          occ = builder.makeOccurrence(topic, type, locator);
        } else {
          LocatorIF dtloc = new URILocator(datatype);
          occ = builder.makeOccurrence(topic, type, content.toString(),
                                       dtloc);
        }
      } else {
        occ = builder.makeOccurrence(topic, type, locator);
      }
      addScope(occ);
      addItemIdentifiers(occ);
      reify(occ, reifier);
      clear();

      // </ROLE
    } else if ("role".equals(name)) {
      AssociationRoleIF role =
        builder.makeAssociationRole(association, type, player);
      if (reifier != null && reifier.getReified() != null) { 
        // this is essentially issue 116: the role is reified by a topic that
        // already has a reifier. we can't merge the roles, because we haven't
        // seen the whole association yet. so we have to delay the reification
        // until after the association is done.
        delayedRoleReification.add(new RoleReification(role, reifier));
      } else {
        reify(role, reifier);
      }
      addItemIdentifiers(role);
      clear();

      // </ASSOCIATION
    } else if ("association".equals(name)) {
      if (stacked_itemids != null) {
        itemids = stacked_itemids;
        stacked_itemids = null;
      }
      addItemIdentifiers(association);
      reify(association, stacked_reifier);
      association = null;
      if (!delayedRoleReification.isEmpty()) {
        for (RoleReification rr : delayedRoleReification) {
          reify(rr.role, rr.reifier);
        }
        delayedRoleReification.clear();
      }

      // </VARIANT>
    } else if ("variant".equals(name)) {
      VariantNameIF variant;
      if (locator == null && datatype == null) {
        variant = builder.makeVariantName(basename, content.toString(), Collections.emptySet());
      } else if (locator == null && datatype != null) {
        if (datatype.equals(XTM_URITYPE)) {
          locator = makeLocator(content.toString());
          variant = builder.makeVariantName(basename, locator, Collections.emptySet());
        } else {
          LocatorIF dtloc = new URILocator(datatype);
          variant = builder.makeVariantName(basename, content.toString(),
                                            dtloc, Collections.emptySet());
        }
      } else {
        variant = builder.makeVariantName(basename, locator, Collections.emptySet());
      }
      addScope(variant);
      addItemIdentifiers(variant);
      reify(variant, reifier);
      clear();

      if (variant.getScope().size() <= basename.getScope().size()) {
        throw new InvalidTopicMapException("Variant " + variant + " scope " +
                                           "was not a superset of parent base "+
                                           "name's scope");
      }
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

  /**
   * Assigns the provided reifier to the topic map iff the reifier is not
   * {@code null} and iff the reifier comes from the master XTM file.
   */
  private void handleTopicMapReifier(final TopicIF reifier) {
    if (reifier != null && read_documents.size() == 1) {
      // this means we are reading the master XTM file, and thus can accept
      // reifiers. had we been reading a sub-file there would be more
      // read_documents, and then we couldn't actually set the reifier, as
      // sub-topicmaps are considered different topic maps from the master,
      // and their reifiers are consequently to be discarded.
      reify(topicmap, reifier);
    }
  }

  /**
   * Handles a topic reference (created by <topicRef/>, 
   * <subjectIdentifierRef/> and <subjectLocatorRef/>) conext-senitively.
   *
   * @param ref The topic.
   */
  private void handleTopicReference(final TopicIF ref) {
      if (context == CONTEXT_TYPE) {
        type = ref;
        context = nextContext;
      }
      else if (context == CONTEXT_SCOPE) {
        scope.add(ref);
      }
      else if (context == CONTEXT_INSTANCEOF) {
        topic.addType(ref);
      }
      else if (context == CONTEXT_ROLE) {
        player = ref;
      }
      else if (context == CONTEXT_REIFIER) {
        if (nextContext == CONTEXT_TOPIC_MAP) {
          handleTopicMapReifier(ref);
        }
        else if (nextContext == CONTEXT_ASSOCIATION) {
          stacked_reifier = ref;
        }
        else {
          reifier = ref;
        }
        context = nextContext;
      }
      else {
        throw new InvalidTopicMapException("topicRef in unknown context " + context);
      }
  }

  private TopicIF getDefaultNameType() {
    if (nametype == null) {
      LocatorIF psi = PSI.getSAMNameType();
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
    if (type == null) {
      type = getDefaultNameType();
    }
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
    if (!xtm21 && itemid.getAddress().indexOf('#') == -1) {
      throw new InvalidTopicMapException("Topic references must have fragment identifiers; invalid reference: " + itemid.getAddress());
    }
    
    TMObjectIF obj = topicmap.getObjectByItemIdentifier(itemid);
    if (obj == null) {
      TopicIF topic = builder.makeTopic();
      topic.addItemIdentifier(itemid);
      return topic;
    } 
    else if (obj instanceof TopicIF) {
      return (TopicIF) obj;
    }
    throw new InvalidTopicMapException("Another Topic Maps construct " + obj + " has already the item identifier " + itemid.getAddress());
  }

  /**
   * Returns a topic by its subject locator. If no topic with the provided subject locator
   * exists in the topic map, a new topic with the provided subject locator will
   * be created.
   *
   * @param slo The subject locator
   * @return A topic with the provided subject locator.
   */
  private TopicIF getTopicBySlo(final LocatorIF slo) {
    TopicIF topic = topicmap.getTopicBySubjectLocator(slo);
    if (topic == null) {
      topic = builder.makeTopic();
      topic.addSubjectLocator(slo);
    }
    return topic;
  }

  /**
   * Returns a topic by its subject identifier. If no topic with the provided subject identifier
   * exists in the topic map, a new topic with the provided subject identifier will
   * be created.
   *
   * @param slo The subject identifier
   * @return A topic with the provided subject identifier.
   */
  private TopicIF getTopicBySid(final LocatorIF sid) {
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(sid);
    if (topic == null) {
      // Does a topic with an item identifier equals to the provided sid exists?
      final TMObjectIF obj = topicmap.getObjectByItemIdentifier(sid);
      topic = (obj instanceof TopicIF) ? (TopicIF) obj : builder.makeTopic();
      topic.addSubjectIdentifier(sid);
    }
    return topic;
  }

  private void addScope(final ScopedIF obj) {
    for(TopicIF theme: scope) {
      obj.addTheme(theme);
    }
  }

  private void addItemIdentifier(TopicIF topic, LocatorIF ii) {
    seenIdentity = true;
    TMObjectIF obj = topicmap.getObjectByItemIdentifier(ii);
    if (obj == null) {
      topic.addItemIdentifier(ii);
    } else if (obj instanceof TopicIF) {
      merge(topic, (TopicIF) obj);
    } else {
      // itemid collision
      throw new InvalidTopicMapException("Another object " + obj + " already " +
                                         "has item identifier " + ii);
    }
  }

  private void addItemIdentifiers(ReifiableIF object) {
    for (int ix = 0; ix < itemids.size(); ix++) {
      LocatorIF itemid = itemids.get(ix);
      try {
        object.addItemIdentifier(itemid);
      } catch (UniquenessViolationException e) {
        TMObjectIF other = topicmap.getObjectByItemIdentifier(itemid);
        if (other != null && canBeMerged(object, other)) {
          MergeUtils.mergeInto(object, (ReifiableIF) other);
        } else {
          throw e;
        }
      }
    }
    itemids.clear();
  }

  private boolean canBeMerged(ReifiableIF object, TMObjectIF other) {
    return object.getClass().equals(other.getClass()) &&
      KeyGenerator.makeKey(object).equals(KeyGenerator.makeKey((ReifiableIF) other));
  }
  
  private void merge(TopicIF target, TopicIF source) {
    if (target.equals(source)) {
      return;
    }
    if (source.equals(nametype)) {
      nametype = target;
    }
    MergeUtils.mergeInto(target, source);
  }

  private void loadMap(LocatorIF mapuri) throws SAXException {
    // Have we read this before?
    if (read_documents.contains(mapuri)) {
      return;
    }
    
    // Create new parser object
    XMLReader parser = DefaultXMLReaderFactory.createXMLReader();
        
    // Initialize nested content handler
    TopicMapStoreFactoryIF sfactory = new SameStoreFactory(topicmap.getStore());
    XTM2ContentHandler handler =
      new XTM2ContentHandler(sfactory, mapuri, read_documents);
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
    if (ref == null) {
      seenReifier = false;
      return null;
    }
    seenReifier = true;
    LocatorIF itemid = makeLocator(ref);
    return getTopicByIid(itemid);
  }

  private void reify(ReifiableIF reifiable, TopicIF reifier) {
    reifiable.setReifier(reifier);
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
    if (topicmap == null) {
      return Collections.EMPTY_SET;
    } else {
      return Collections.singleton(topicmap);
    }
  }

  // --- Helper class

  class RoleReification {
    private AssociationRoleIF role;
    private TopicIF reifier;

    public RoleReification(AssociationRoleIF role, TopicIF reifier) {
      this.role = role;
      this.reifier = reifier;
    }
  }
}
