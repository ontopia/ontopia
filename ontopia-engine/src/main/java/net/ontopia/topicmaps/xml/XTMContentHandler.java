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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
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
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.utils.SameStoreFactory;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
import net.ontopia.xml.DefaultXMLReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * INTERNAL: SAX2 content handler used for importing XTM 1.0 topic map
 * documents. The content handler builds a topic map object based on a
 * SAX event stream conforming to the XTM topic map interchange
 * syntax. See http://www.topicmaps.org/xtm/1.0/
 */

// Keep a map of:
//
// - subject indicators                 -> topic
// - element ids / source locator       -> object
//
// Todos:
//
// - should make the source locator registration optional.
// - could configure the content handler via a Map/Properties object.
// - the registerSourceLocator call on <topic> can be optimized
//
// FIXMEs:
//
// - maintain list of mergable topics?
// - note that resourceData.id is required.
// - keep track of nested merge maps, themes should also be added to all scoped objects in nested mergeMaps.
// - what do we do with the xml:base attribute? should it become the TopicMap.baseAddress?
//
// Source locators: topicMap, topic, association
// 
// +id on association
// +id on baseName
//  id on baseNameString
//  id on instanceOf
// +id on member ! [no 1-1 mapping]
//  id on mergeMap
// +id on occurrence
//  id on parameters
//  id on resourceData
//  id on resourceRef
//  id on roleSpec
//  id on scope
//  id on subjectIdentity
//  id on subjectIndicatorRef
// +id on topic
// +id on topicMap
//  id on topicRef
// +id on variant
//  id on variantName
//
// o EL_ASSOCIATION
// o EL_BASENAME
// o EL_BASENAMESTRING ~
// o EL_INSTANCEOF
// o EL_MEMBER
// EL_MERGEMAP
// o EL_OCCURRENCE
// o EL_PARAMETERS
// o EL_RESOURCEDATA ~
// o EL_RESOURCEREF *
// o EL_ROLESPEC
// o EL_SCOPE
// o EL_SUBJECTIDENTITY
// o EL_SUBJECTINDICATORREF *
// o EL_TOPIC
// o EL_TOPICMAP
// o EL_TOPICREF *
// o EL_VARIANT
// o EL_VARIANTNAME
//
// * = EMPTY
// ~ = #PCDATA
//

public class XTMContentHandler extends AbstractTopicMapContentHandler
                               implements LexicalHandler, DeclHandler {
  
  private static final String EL_ASSOCIATION = "association";
  private static final String EL_BASENAME = "baseName";
  private static final String EL_BASENAMESTRING = "baseNameString";
  private static final String EL_INSTANCEOF = "instanceOf";
  private static final String EL_MEMBER = "member";
  private static final String EL_MERGEMAP = "mergeMap";
  private static final String EL_OCCURRENCE = "occurrence";
  private static final String EL_PARAMETERS = "parameters";
  private static final String EL_RESOURCEDATA = "resourceData";
  private static final String EL_RESOURCEREF = "resourceRef";
  private static final String EL_ROLESPEC = "roleSpec";
  private static final String EL_SCOPE = "scope";
  private static final String EL_SUBJECTIDENTITY = "subjectIdentity";
  private static final String EL_SUBJECTINDICATORREF = "subjectIndicatorRef";
  private static final String EL_TOPIC = "topic";
  private static final String EL_TOPICMAP = "topicMap";
  private static final String EL_TOPICREF = "topicRef";
  private static final String EL_VARIANT = "variant";
  private static final String EL_VARIANTNAME = "variantName";
  private static final String EL_HREF = "href";
  private static final String EL_XLINK_HREF = "xlink:href";
  private static final String MSG_MERGED_WITH = " merged with ";
  private static final String MSG_TOPIC = "Topic ";
  
  public static final String NS_XTM = "http://www.topicmaps.org/xtm/1.0/";
  public static final String NS_XLINK = "http://www.w3.org/1999/xlink";
  public static final String NS_NS = "http://www.w3.org/XML/1998/namespace";
  public static final LocatorIF nullPSI = URILocator.create("http://psi.ontopia.net/xtm/1.0/null-topic");
  
  public static final String SAX_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
  public static final String SAX_DECL_HANDLER = "http://xml.org/sax/properties/declaration-handler";
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(XTMContentHandler.class.getName());
  
  protected TopicMapStoreFactoryIF stores;
  
  private LazyTopic lazyTopic;
  private TopicMapIF topicmap;
  private TopicMapBuilderIF builder;
  private Collection<TopicMapIF> topicmaps;
  private Stack<LocatorIF> bases;
  private StringBuilder content;
  private boolean keep_content;
  
  /**
   * Keeps track of the declared entities, in order that the base URI
   * can be set correctly in external entities.
   */
  protected Map<String, String> entities;
  
  /**
   * Used to tell if we are reading the top-level XTM document (false)
   * or if we are reading a merged-in XTM document (true).
   */
  protected boolean isSubDocument;
  
  protected ExternalReferenceHandlerIF ref_handler;
  
  public XTMContentHandler(TopicMapStoreFactoryIF stores, LocatorIF base_address) {
    super(base_address);
    this.stores = stores;
    this.entities = new HashMap<>();
    this.bases = new Stack<>();
  }
  
  public XTMContentHandler(TopicMapStoreFactoryIF stores, LocatorIF base_address, Collection<LocatorIF> processed_documents) {
    super(base_address, processed_documents);
    this.stores = stores;
    this.entities = new HashMap<>();
    this.bases = new Stack<>();
  }
  
  /**
   * INTERNAL: Gets the topic maps found after having parsed the input source.
   */  
  public Collection<TopicMapIF> getTopicMaps() {
    return topicmaps;
  }
  
  /**
   * INTERNAL: Gets the external reference handler. The reference
   * handler will receive notifications on references to external
   * topics and topic maps.
   */
  public ExternalReferenceHandlerIF getExternalReferenceHandler() {
    return ref_handler;
  }
  
  /**
   * INTERNAL: Sets the external reference handler.
   */
  public void setExternalReferenceHandler(ExternalReferenceHandlerIF ref_handler) {
    this.ref_handler = ref_handler;
  }
  
  /**
   * INTERNAL: Tell the handler whether this is a top-level document
   * or not.
   */
  public void setSubDocument(boolean isSubDocument) {
    this.isSubDocument = isSubDocument;
  }
  
  /**
   * INTERNAL: Registers the handler with the parser and configures the
   * parser.
   */
  @Override
  public void register(XMLReader parser) {
    super.register(parser);
    try {
      parser.setProperty(SAX_LEXICAL_HANDLER, this);
    } catch (SAXException e) {
      log.warn("Parser does not support SAX LexicalHandler: " +e.getMessage());
    }
    try {
      parser.setProperty(SAX_DECL_HANDLER, this);
    } catch (SAXException e) {
      log.warn("Parser does not support SAX DeclHandler: " + e.getMessage());
      throw new OntopiaRuntimeException(e);
    }
  }
  
  // --------------------------------------------------------------------------
  // Document events
  // --------------------------------------------------------------------------
  
  @Override
  public void startDocument () {
    
    // Initialize variables
    parents.clear();
    info.clear();
    bases.clear();
    keep_content = false;
    content = new StringBuilder();
    
    topicmaps = new ArrayList<>();
    
    // Initialize base address stack. Top level base added to
    // stack. The user specified base address overrides the system id
    // of the source.
    if (doc_address != null) {
      bases.push(doc_address);
    } else if (locator != null && locator.getSystemId() != null) {
      try {
        bases.push(new URILocator(locator.getSystemId()));
      } catch (URISyntaxException e) {
        // Ignore; throw exception later instead [see getBaseAddress()].
      } 
    }
    log.debug("Processing document '" + doc_address + "'.");
    
    // Initialize list of accumulated processed documents
    this.processed_documents_accumulated = new HashSet<>();
    
    // Add this document to the list of processed documents. Note: we
    // are adding it here, since all the topic maps in the document
    // have the same base address.
    this.processed_documents_from_parent.add(getBaseAddress());
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
    
    //System.out.println("S: '" + uri + "' " + qName + " (" + getLocationInfo() + ")");
    
    // Handle xml:base attribute.
    if (atts.getValue(NS_NS, "base") != null) {
      // Push new base address onto bases stack
      // Note: xml:base can contain relative URIs
      LocatorIF base_address = createLocator(atts.getValue(NS_NS, "base"));
      bases.push(base_address);
    } else {
      // Push parent base address onto bases stack
      bases.push(bases.peek());
    }
    
    if (NS_XTM.equals(uri) || "".equals(uri)) {
      if (NS_XTM.equals(uri)) { 
        qName = name; // use the local name, since qName may have prefix
      }
      
      // -----------------------------------------------------------------------------
      // S: topicRef
      // -----------------------------------------------------------------------------
      if (EL_TOPICREF.equals(qName)) {
        
        // Resolve reference
        String href = atts.getValue(NS_XLINK, EL_HREF);
        if (href == null) {
          href = atts.getValue(EL_XLINK_HREF);
        }

        // Process in context of parent
        String parent_type = parents.peek();        
        // FIXME: subjectIdentity.topicRef vs. instanceOf.topicRef
        if (EL_SUBJECTIDENTITY.equals(parent_type) &&
            info.get(EL_TOPIC) == this.lazyTopic && this.lazyTopic != null) {
          LocatorIF loc = createLocator(href);
          addItemIdentifier(this.lazyTopic, loc);

          if (!loc.getAddress().startsWith(getBaseAddress().getAddress() + '#')) {
            // load external document
            getReferencedExternalTopic(loc);
          }
            
        } else {
          TopicIF referenced_topic = resolveTopicRef(href); 
          // Process the topic reference in the correct context
          processTopicReference(referenced_topic);
        }
      }
      
      // -----------------------------------------------------------------------------
      // S: instanceof
      // -----------------------------------------------------------------------------
      else if (EL_INSTANCEOF.equals(qName)) {
        
        // Push element on parent stack
        parents.push(EL_INSTANCEOF);
      }
      
      // -----------------------------------------------------------------------------
      // S: member
      // -----------------------------------------------------------------------------
      else if (EL_MEMBER.equals(qName)) {
        
        // Push element on parent stack
        parents.push(EL_MEMBER);
      }
      
      // -----------------------------------------------------------------------------
      // S: roleSpec
      // -----------------------------------------------------------------------------
      else if (EL_ROLESPEC.equals(qName)) {
        
        // Push element on parent stack
        parents.push(EL_ROLESPEC);      
      }
      
      // -----------------------------------------------------------------------------
      // S: association
      // -----------------------------------------------------------------------------
      else if (EL_ASSOCIATION.equals(qName)) {
        
        if (builder == null) {
          throw new InvalidTopicMapException("Association outside topic map. Did you forget or misspell the 'topicMap' element?");
        }
        
        // Create association
        TopicIF atype = getNullTopic(builder.getTopicMap());
        AssociationIF assoc = builder.makeAssociation(atype);
        
        // Add propagated themes
        propagateThemes(assoc);
        
        // Element id
        registerSourceLocator(assoc, atts.getValue("", "id"));
        
        // Put association on info map
        info.put(EL_ASSOCIATION, assoc);
        
        // Push element on parent stack
        parents.push(EL_ASSOCIATION);   
      }
      
      // -----------------------------------------------------------------------------
      // S: baseName
      // -----------------------------------------------------------------------------
      else if (EL_BASENAME.equals(qName)) {
        // Create basename
        TopicIF topic = getParentTopic();
        TopicNameIF basename = builder.makeTopicName(topic, "");
        // FIXME: register with parent topic, but this might be
        // troublesome since topics can be merged.
        
        // Add propagated themes
        propagateThemes(basename);
        
        // Register element id
        registerSourceLocator(basename, atts.getValue("", "id"));
        
        // Put basename on info map
        info.put(EL_BASENAME, basename);
        
        // Push element on parent stack
        parents.push(EL_BASENAME);      
      }
      
      // -----------------------------------------------------------------------------
      // S: baseNameString
      // -----------------------------------------------------------------------------
      else if (EL_BASENAMESTRING.equals(qName)) {
        keep_content = true;
        content.setLength(0);
      }
      
      // -----------------------------------------------------------------------------
      // S: topic
      // -----------------------------------------------------------------------------
      else if (EL_TOPIC.equals(qName)) {
        
        // Check to see if topic already exist
        String id = atts.getValue("", "id");
        
        // Look up existing topic
        TopicIF topic = null;
        LocatorIF locator = null;
        if (id != null) {
          locator = createLocator('#' + id);
          topic = resolveSourceLocatorOrSubjectIndicator(locator);
        }
        
        // Put lazy topic on info map if not already found
        if (topic != null) {
          info.put(EL_TOPIC, topic);
        } else {
          if (this.lazyTopic == null) {
            topic = builder.makeTopic();
            if (locator != null) {
              addItemIdentifier(topic, locator);
            }
            info.put(EL_TOPIC, topic);
          } else {
            if (locator != null) {
              addItemIdentifier(this.lazyTopic, locator);
            }
            info.put(EL_TOPIC, this.lazyTopic);
          }
        }
        
        // Push element on parent stack
        parents.push(EL_TOPIC);
      }
      
      // -----------------------------------------------------------------------------
      // S: occurrence
      // -----------------------------------------------------------------------------
      else if (EL_OCCURRENCE.equals(qName)) {
        // Create occurrence
        TopicIF topic = getParentTopic();
        TopicIF otype = getDefaultOccurrenceTopic(builder.getTopicMap());
        OccurrenceIF occurs = builder.makeOccurrence(topic, otype, "");
        // FIXME: register with parent topic, but this might be
        // troublesome since topics can be merged.
        
        // Add propagated themes
        propagateThemes(occurs);
        
        // Put occurrence on info map
        info.put(EL_OCCURRENCE, occurs);
        
        // Register element id
        registerSourceLocator(occurs, atts.getValue("", "id"));
        
        // Push element on parent stack
        parents.push(EL_OCCURRENCE);    
      }
      
      // -----------------------------------------------------------------------------
      // S: resourceRef
      // -----------------------------------------------------------------------------
      else if (EL_RESOURCEREF.equals(qName)) {
        
        // Create locator
        String href = atts.getValue(NS_XLINK, EL_HREF);
        if (href == null) {
          href = atts.getValue(EL_XLINK_HREF);
        }
        
        // Process in context of parent
        String parent_type = parents.peek();
        
        // References a topic:
        
        if (EL_MEMBER.equals(parent_type)) {
          // Resolve referenced topic
          TopicIF referenced_topic = resolveResourceRef(createLocator(href));
          // Create new association role
          processMember(referenced_topic);
        }
        else if (EL_MERGEMAP.equals(parent_type)) {
          // Resolve referenced topic
          TopicIF referenced_topic = resolveResourceRef(createLocator(href));
          ExternalDocument merge_map = (ExternalDocument)info.get(EL_MERGEMAP);
          merge_map.addTheme(referenced_topic);
        }
        else if (EL_SCOPE.equals(parent_type)) {
          // Resolve referenced topic
          TopicIF referenced_topic = resolveResourceRef(createLocator(href));
          processTheme(referenced_topic);
        }
        
        // References an information resource:
        
        else if (EL_OCCURRENCE.equals(parent_type)) {
          // Create locator
          LocatorIF locator = createLocator(href);
          // Set occurrence locator
          OccurrenceIF occurs = (OccurrenceIF)info.get(EL_OCCURRENCE);
          occurs.setLocator(locator);
        }
        else if (EL_VARIANTNAME.equals(parent_type)) {
          // Create locator
          LocatorIF locator = createLocator(href);
          // Set variant name locator
          Stack<VariantNameIF> variants = (Stack<VariantNameIF>)info.get(EL_VARIANT);
          VariantNameIF vname = variants.peek();
          vname.setLocator(locator);
        }
        
        // Can reference both:
        
        else if (EL_SUBJECTIDENTITY.equals(parent_type)) {
          
          // FIXME: Need to check if the topic is a local topic.
          // Local topics are easily recognizable as long as the
          // locator syntax is ok.
          
          // FIXME: Don't know what to do if reference resolves to an
          // external topic. It is actually very hard, if not
          // impossible, to realize whether the reference references
          // an external information resource or an external topic.
          
          // FIXME: Should the topics merge if the addressable subject
          // is another topic?
         
          LocatorIF subject = createLocator(href);
          if (info.get(EL_TOPIC) == this.lazyTopic && this.lazyTopic != null) {
            this.lazyTopic.addSubjectLocator(subject);
          } else {
            // Check to see if another topic has this addressable topic.
            TopicIF other_topic = topicmap.getTopicBySubjectLocator(subject);
            TopicIF current_topic = (TopicIF)info.get(EL_TOPIC);
            
            // Create new topic
            if (other_topic == null) {
              // Set subject resource
              current_topic.addSubjectLocator(subject);
              
            } else if (!other_topic.equals(current_topic)) {
              if (log.isInfoEnabled()) {
                log.debug(MSG_TOPIC + current_topic + MSG_MERGED_WITH + other_topic +
                    " because they both have the same addressable subject: " + subject);
              }
              
              // Merge existing topic with current topic. 
              other_topic.merge(current_topic);
              
              // update info map
              info.put(EL_TOPIC, other_topic);
            }
          }
        }
        else {
          throw new OntopiaRuntimeException("Unknown parent: " + parent_type);
        }
      } 
      
      // -----------------------------------------------------------------------------
      // S: resourceData
      // -----------------------------------------------------------------------------
      else if (EL_RESOURCEDATA.equals(qName)) {
        keep_content = true;
        content.setLength(0);
      }
      
      // -----------------------------------------------------------------------------
      // S: variantName
      // -----------------------------------------------------------------------------
      else if (EL_VARIANTNAME.equals(qName)) {
        
        // Push element on parent stack
        parents.push(EL_VARIANTNAME);   
      }
      
      // -----------------------------------------------------------------------------
      // S: variant
      // -----------------------------------------------------------------------------
      else if (EL_VARIANT.equals(qName)) {
        
        // Create variant name
        TopicNameIF bname = (TopicNameIF)info.get(EL_BASENAME);
        VariantNameIF vname = builder.makeVariantName(bname, "", Collections.<TopicIF>emptySet());
        
        // Add variant to parent name
        if (info.containsKey(EL_VARIANT)) {
          Stack<VariantNameIF> variants = (Stack<VariantNameIF>)info.get(EL_VARIANT);
          
          // Loop over parent variant names and inherit their themes
          for (VariantNameIF variant : variants) {
            for (TopicIF scope : variant.getScope()) {
              vname.addTheme(scope);
            }
          }
          
          // This is a nested variant so put it on the stack.
          variants.push(vname);
          
        } else {
          // This is a top level variant, so create a new stack.
          Stack<VariantNameIF> variants = new Stack<>();
          variants.push(vname);
          info.put(EL_VARIANT, variants);         
        }
        
        // Register element id
        registerSourceLocator(vname, atts.getValue("", "id"));
        
        // Push element on parent stack
        parents.push(EL_VARIANT);       
      }
      
      // -----------------------------------------------------------------------------
      // S: parameters
      // -----------------------------------------------------------------------------
      else if (EL_PARAMETERS.equals(qName)) {
        // Push element on parent stack
        parents.push(EL_PARAMETERS);    
      }
      
      // -----------------------------------------------------------------------------
      // S: scope
      // -----------------------------------------------------------------------------
      else if (EL_SCOPE.equals(qName)) {
        // Push element on parent stack
        parents.push(EL_SCOPE); 
      }
      
      // -----------------------------------------------------------------------------
      // S: subjectIndicatorRef
      // -----------------------------------------------------------------------------
      else if (EL_SUBJECTINDICATORREF.equals(qName)) {
        
        // Get reference
        String href = atts.getValue(NS_XLINK, EL_HREF);
        if (href == null) {
          href = atts.getValue(EL_XLINK_HREF);
        }
        LocatorIF indicator = createLocator(href);
        
        // Process in context of parent
        String parent_type = parents.peek();
        
        // If the parent is subjectIdentity this reference should
        // become a subject indicator of the parent topic.
        if (EL_SUBJECTIDENTITY.equals(parent_type)) {
          // FIXME: Should merge with parent topic if it references
          // another topic.
          
          if (info.get(EL_TOPIC) == this.lazyTopic && this.lazyTopic != null) {
            TopicIF t = addSubjectIdentifier(this.lazyTopic, indicator);
            if (t != this.lazyTopic) { // topic was merged away
              info.put(EL_TOPIC, t);
            }
          } else {
            // Check to see if another topic has this addressable topic.
            TopicIF current_topic = (TopicIF)info.get(EL_TOPIC);
            TopicIF rtopic = registerSubjectIndicator(current_topic, indicator);
            // update info map
            if (!rtopic.equals(current_topic)) {
              info.put(EL_TOPIC, rtopic);
            }
          }
          
        } else {
          // Resolve reference
          TopicIF rtopic = registerSubjectIndicator(null, indicator); 
          // Other parent elements
          processTopicReference(rtopic);
        }
        
      }
      
      // -----------------------------------------------------------------------------
      // S: subjectIdentity
      // -----------------------------------------------------------------------------
      else if (EL_SUBJECTIDENTITY.equals(qName)) {
        
        // Push element on parent stack
        parents.push(EL_SUBJECTIDENTITY);
      }
      
      // -----------------------------------------------------------------------------
      // S: mergeMap
      // -----------------------------------------------------------------------------
      else if (EL_MERGEMAP.equals(qName)) {
        
        // Get merge map address
        String href = atts.getValue(NS_XLINK, EL_HREF);
        if (href == null) {
          href = atts.getValue(EL_XLINK_HREF);
        }
        
        // Put merge map on info map
        ExternalDocument merge_map = new ExternalDocument(URIUtils.resolveMergeResource(getBaseAddress(), href));
        info.put(EL_MERGEMAP, merge_map);
        
        // Push element on parent stack
        parents.push(EL_MERGEMAP);
      }
      
      // -----------------------------------------------------------------------------
      // S: topicMap
      // -----------------------------------------------------------------------------      
      else if (EL_TOPICMAP.equals(qName)) {
        
        // Initialize the list of processed documents for the current topic map
        processed_documents_current = new HashSet<>(processed_documents_from_parent);
        
        // Get topic map object
        topicmap = stores.createStore().getTopicMap();
        topicmaps.add(topicmap);
        
        if (topicmap instanceof net.ontopia.topicmaps.impl.basic.TopicMap) {
          this.lazyTopic = null;
        } else {
          this.lazyTopic = new LazyTopic();
        }

        // Get hold of topic map builder
        builder = topicmap.getBuilder();
        
        // Set base address on in-memory store
        TopicMapStoreIF store = topicmap.getStore();
        if ((store instanceof net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore) &&
            store.getBaseAddress() == null) {
          ((net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore)store).setBaseAddress(doc_address);
        }
        
        // Register element id
        if (!isSubDocument) {
          // if we are a subdocument we must not create a source locator since
          // that would imply that the sub-topic map is the same as the parent
          // http://www.y12.doe.gov/sgml/sc34/document/0299.htm#merge-prop-srclocs
          // also see bug #457
          registerSourceLocator(topicmap, atts.getValue("", "id"));
        }
        
        // Push element on parent stack
        parents.push(EL_TOPICMAP);
        
      } else {
        log.warn("Unknown element '" + qName + "'");
      }
      
    } else { // if there is a namespace URI that is not the XTM ns URI
      // log.error("Unknown element: {" + uri + "}" + name + "[" + qName + "]");
      
      if (EL_TOPICMAP.equals(qName)) {
        log.error("Unrecognized <topicMap> element " + getLocationInfo());
      } 
    }
  } catch (SAXException | OntopiaRuntimeException e) {
    if (logError()) {
      log.error("Exception was thrown from within startElement", e);
    }
    throw new OntopiaRuntimeException(e);
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
    //System.out.println("E: " + qName + " (" + getLocationInfo() + ")");
    try {
      
    if (NS_XTM.equals(uri) || "".equals(uri)) {
      if (NS_XTM.equals(uri)) {
        qName = name;
      }
      
      // -----------------------------------------------------------------------------
      // E: instanceOf
      // -----------------------------------------------------------------------------
      if (EL_INSTANCEOF.equals(qName)) {
        // Pop element off parent stack
        parents.pop();
      }
      // -----------------------------------------------------------------------------
      // E: member
      // -----------------------------------------------------------------------------
      else if (EL_MEMBER.equals(qName)) {
        // Pop element off parent stack
        parents.pop();
        
        // If EL_MEMBER key doesn't exist no players occurred.
        if (info.containsKey(EL_MEMBER)) {
          info.remove(EL_MEMBER);
        } else {
          // Note that we're not setting the player, since there isn't one.
          // Add association role to association
          AssociationIF assoc = (AssociationIF)info.get(EL_ASSOCIATION);
          // Set role type if it was specified
          TopicIF nullTopic = getNullTopic(builder.getTopicMap());
          TopicIF roletype = (TopicIF)info.get(EL_ROLESPEC);
          if (roletype == null) {
            roletype = nullTopic;
          }
          TopicIF player = nullTopic;
          builder.makeAssociationRole(assoc, roletype, player);
        }
        
        // Remove role type from info map
        info.remove(EL_ROLESPEC);
      }
      // -----------------------------------------------------------------------------
      // E: roleSpec
      // -----------------------------------------------------------------------------
      else if (EL_ROLESPEC.equals(qName)) {
        // Pop element of parent stack
        parents.pop();
      }
      // -----------------------------------------------------------------------------
      // E: association
      // -----------------------------------------------------------------------------
      else if (EL_ASSOCIATION.equals(qName)) {
        // Pop element of parent stack
        parents.pop();
        
        // Remove association from info map
        info.remove(EL_ASSOCIATION);
      }
      // -----------------------------------------------------------------------------
      // E: baseName
      // -----------------------------------------------------------------------------
      else if (EL_BASENAME.equals(qName)) {
        // Pop element of parent stack
        parents.pop();
        
        // Remove occurrence from info map
        info.remove(EL_BASENAME);      
      }
      // -----------------------------------------------------------------------------
      // E: baseNameString
      // -----------------------------------------------------------------------------
      else if (EL_BASENAMESTRING.equals(qName)) {
        // Set the name value of the base name
        TopicNameIF basename = (TopicNameIF)info.get(EL_BASENAME);
        basename.setValue(content.toString());
        keep_content = false;
      }
      // -----------------------------------------------------------------------------
      // E: topic
      // -----------------------------------------------------------------------------
      else if (EL_TOPIC.equals(qName)) {
        // resolve lazy topic if it still exists
        if (info.get(EL_TOPIC)  == this.lazyTopic && this.lazyTopic != null) {
          createTopicFromLazyTopic();
        }
        
        // Pop element of parent stack
        parents.pop();
        
        // Remove topic from info map
        info.remove(EL_TOPIC);
      }
      // -----------------------------------------------------------------------------
      // E: occurrence
      // -----------------------------------------------------------------------------
      else if (EL_OCCURRENCE.equals(qName)) {
        // Pop element of parent stack
        parents.pop();
        
        // Remove occurrence from info map
        info.remove(EL_OCCURRENCE);
      }
      // -----------------------------------------------------------------------------
      // E: resourceData
      // -----------------------------------------------------------------------------
      else if (EL_RESOURCEDATA.equals(qName)) {
        
        if (info.containsKey(EL_VARIANT)) {
          Stack<VariantNameIF> variants = (Stack<VariantNameIF>)info.get(EL_VARIANT);
          VariantNameIF vname = variants.peek();
          vname.setValue(content.toString());
          keep_content = false;
        }
        else if (info.containsKey(EL_OCCURRENCE)) {
          OccurrenceIF occurs = (OccurrenceIF)info.get(EL_OCCURRENCE);
          occurs.setValue(content.toString());
          keep_content = false;
        }
        else {
          throw new OntopiaRuntimeException("Unknown resourceData context.");
        }
        
      }
      // -----------------------------------------------------------------------------
      // E: variant
      // -----------------------------------------------------------------------------
      else if (EL_VARIANT.equals(qName)) {
        // Pop element of parent stack
        parents.pop();
        
        // Process in context of parent
        String parent_type = parents.peek();
        
        if (EL_BASENAME.equals(parent_type)) {
          // Remove variant stack from info map      
          info.remove(EL_VARIANT);
        }
        else if (EL_VARIANT.equals(parent_type)) {
          // Pop variant name of info map variant name stack.
          Stack<VariantNameIF> variants = (Stack<VariantNameIF>)info.get(EL_VARIANT);
          variants.pop();
        }      
      }
      // -----------------------------------------------------------------------------
      // E: variantName
      // -----------------------------------------------------------------------------
      else if (EL_VARIANTNAME.equals(qName)) {
        // Pop element of parent stack
        parents.pop();
      }
      // -----------------------------------------------------------------------------
      // E: parameters
      // -----------------------------------------------------------------------------
      else if (EL_PARAMETERS.equals(qName)) {
        // Pop element of parent stack
        parents.pop();
      }
      // -----------------------------------------------------------------------------
      // E: scope
      // -----------------------------------------------------------------------------
      else if (EL_SCOPE.equals(qName)) {
        // Pop element of parent stack
        parents.pop();
      }
      // -----------------------------------------------------------------------------
      // E: subjectIdentity
      // -----------------------------------------------------------------------------
      else if (EL_SUBJECTIDENTITY.equals(qName)) {
        // Pop element off parent stack
        parents.pop();
      }
      // -----------------------------------------------------------------------------
      // E: mergeMap
      // -----------------------------------------------------------------------------
      else if (EL_MERGEMAP.equals(qName)) {
        
        // Get merge map from info map
        ExternalDocument merge_map = (ExternalDocument)info.get(EL_MERGEMAP);
        LocatorIF locator = merge_map.getLocator();
        
        // Ask external topic map reference handler whether merge map
        // reference should be traversed
        if (getExternalReferenceHandler() != null) {
          locator = getExternalReferenceHandler().externalTopicMap(locator);
        }
        
        // Import topic map if merge map locator is not null. Note
        // that the reference handler can set it to null if it decides
        // that the reference is not to be resolved.
        if (locator != null) {
          // Process merge map
          merge_map.importInto(topicmap);
        }
        
        // Pop element off parent stack
        parents.pop();
        
        // Remove merge map from info map
        info.remove(EL_MERGEMAP);
      }
      
      // -----------------------------------------------------------------------------
      // E: topicMap
      // -----------------------------------------------------------------------------
      else if (EL_TOPICMAP.equals(qName)) {
        
        // Add processed documents to accumulated list.
        processed_documents_accumulated.addAll(processed_documents_current);

        // Remove null-topic if not used
        removeNullTopic(topicmap);
        
        // Remove occurrence default topic if not used
        removeDefaultOccurrenceTopic(topicmap);

        // Clear topic map related info
        this.topicmap = null;
        this.lazyTopic = null;
        this.builder = null;
        
        // Pop element of parent stack
        parents.pop();
      }
      
    }
    // Pop current base address off bases stack
    bases.pop();
    
  } catch (OntopiaRuntimeException | SAXException e) {
    if (logError()) {
      log.error("Exception was thrown from within endElement", e);
    }
    throw new OntopiaRuntimeException(e);
  }
    
  }
  
  @Override
  public void startPrefixMapping(String prefix, String uri) {
    // log.debug("Start prefix:   {" + uri + "} '" + prefix + "'");
  }
  
  @Override
  public void endPrefixMapping(String prefix) {
    // log.debug("End prefix: '" + prefix + "'");
  }
  
  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  // returns topic because reify() can merge topics
  private TopicIF reify(ReifiableIF reifiable, TopicIF reifier) {
    reifiable.setReifier(reifier);
    return reifiable.getReifier();
  }

  private boolean logError() {
    try {
      return Boolean.parseBoolean(System.getProperty("net.ontopia.topicmaps.xml.XTMContentHandler.logError"));
    } catch (SecurityException e) {
      return false;
    }
  }
  
  protected LocatorIF getBaseAddress() {
    if (bases.size() > 0) {
      LocatorIF base = bases.peek();
      if (base != null) {
        return base;
      }
    }
    throw new OntopiaRuntimeException("Base address is not specified.");
  }
  
  protected TopicIF resolveTopicRef(String address) throws SAXException {
    LocatorIF locator = createLocator(address);
    
    TMObjectIF object = topicmap.getObjectByItemIdentifier(locator);
    if (object != null && !(object instanceof TopicIF)) {
      throw new OntopiaRuntimeException("topicRef element with URI '" + address +
          "' referred to non-topic: " + object);
    }
    
    TopicIF topic = (TopicIF) object;
    if (topic == null) {
      topic = topicmap.getTopicBySubjectIdentifier(locator);
    }
    
    if (topic == null) {
      if ((address.length() > 0 && address.charAt(0) == '#') ||
          locator.getAddress().startsWith(getBaseAddress().getAddress() + '#')) {
        // this is a local reference; create a topic and return it
        topic = registerSourceLocator(topic, locator);
        
      } else { 
        // this is an external reference; resolve it
        topic = getReferencedExternalTopic(locator);
      }
    }
    
    return topic;
  }
  
  protected TopicIF resolveResourceRef(LocatorIF locator) {
    // Look up in the topic map to see if a topic with the identity already exist
    TopicIF topic = topicmap.getTopicBySubjectLocator(locator);
    if (topic != null) {
      return topic;
    }
    
    // If there is no such topic create a new one with the given subject indicator.
    topic = builder.makeTopic();
    // log.debug("New topic (resourceRef): " + locator);
    
    // Set addressable subject
    topic.addSubjectLocator(locator);
    return topic;
  }

  protected TopicIF registerSubjectLocator(TopicIF topic, LocatorIF locator) {
    // merge with existing, if any
    TopicIF existing = topicmap.getTopicBySubjectLocator(locator);
    if (existing != null && !Objects.equals(existing, topic)) {
      existing.merge(topic);
      topic = existing;
    }
    // add subject locator
    topic.addSubjectLocator(locator);
    return topic;
  }
  
  protected void registerSourceLocator(TMObjectIF tmobject, String id) {
    // No need to register source locator if id is null
    if (id == null) {
      return;
    }
    addItemIdentifier(tmobject, createLocator('#' + id));
  }
  
  protected TopicIF registerSourceLocator(TopicIF topic, LocatorIF locator) {
    TopicIF tsrcloc = resolveSourceLocatorOrSubjectIndicator(locator);
    
    if (tsrcloc != null) {
      if (topic != null && !topic.equals(tsrcloc)) {
        if (log.isInfoEnabled()) {
          log.debug(MSG_TOPIC + topic + MSG_MERGED_WITH + tsrcloc +
              " because the subject indicator is the same as the source locator of the other: " + locator);
        }
        tsrcloc.merge(topic);
      }
      topic = tsrcloc;
    }
    
    if (topic == null) {
      // create new topic if none exists
      topic = builder.makeTopic();
    }
    
    // add source locator
    addItemIdentifier(topic, locator);
    return topic;
  }
  
  protected TopicIF registerSubjectIndicator(TopicIF topic, LocatorIF locator) {
    TopicIF tsrcloc = resolveSourceLocatorOrSubjectIndicator(locator);
    
    if (tsrcloc != null) {
      if (topic != null && tsrcloc != topic) {
        if (log.isInfoEnabled()) {
          log.debug(MSG_TOPIC + topic + MSG_MERGED_WITH + tsrcloc +
              " because the subject indicator is the same as the source locator of the other: " + locator);
        }
        tsrcloc.merge(topic);
      }
      topic = tsrcloc;
    }
    
    if (topic == null) {
      // create new topic if none exists
      topic = builder.makeTopic();
    }
    
    // add subject indicator
    return addSubjectIdentifier(topic, locator);
  }
  
  protected TopicIF resolveSourceLocatorOrSubjectIndicator(LocatorIF locator) {
    // look up objects
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(locator);
    TMObjectIF osrcloc = topicmap.getObjectByItemIdentifier(locator);
    
    if (osrcloc != null && osrcloc instanceof TopicIF) {
      TopicIF tsrcloc = (TopicIF)osrcloc;
      if (topic != null && tsrcloc != topic) {
        if (log.isInfoEnabled()) {
          log.debug(MSG_TOPIC + topic + MSG_MERGED_WITH + tsrcloc +
              " because the subject indicator is the same as the source locator of the other: " + locator);
        }
        // ISSUE: should we keep the oldest topic in this case? or
        // perhaps the one with the indicator
        tsrcloc.merge(topic);
      }
      return tsrcloc;
    }
    return topic;
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
  
  protected TopicIF getReferencedExternalTopic(LocatorIF orig_locator) throws SAXException {
    /// 0) Check to see if we have this topic already
    TMObjectIF tsrcloc = topicmap.getObjectByItemIdentifier(orig_locator);
    if (tsrcloc != null && tsrcloc instanceof TopicIF) {
      return (TopicIF)tsrcloc;
    }
    
    /// 1) Load external topic map, if necessary
    
    // Ask external topic reference handler whether reference should be traversed
    LocatorIF locator = orig_locator;
    if (getExternalReferenceHandler() != null) {
      locator = getExternalReferenceHandler().externalTopic(orig_locator);
    }
    
    if (locator != null) {
      String locstr = locator.getAddress();
      int frag_offset = locstr.indexOf('#');
      LocatorIF ref;
      if (frag_offset == -1) {
        ref = locator;
      } else {
        ref = createURILocator(locstr.substring(0, frag_offset));
      }
      
      // Traverse external reference
      ExternalDocument doc = new ExternalDocument(ref);
      doc.importInto(topicmap);
      
      // Make a new attempt at resolving source locator, since the imported
      // file usually will have created this topic. This fixes bug #750.
      tsrcloc = topicmap.getObjectByItemIdentifier(orig_locator);
      if (tsrcloc != null && tsrcloc instanceof TopicIF) {
        return (TopicIF)tsrcloc;
      }
    }
    
    /// 2) If topic still doesn't exist, create it
    return registerSourceLocator(null, orig_locator);
  }
  
  protected void processTheme(TopicIF theme) {
    // Locate scoped object
    ScopedIF scoped;
    if (info.containsKey(EL_VARIANT)) {
      Stack<VariantNameIF> variants = (Stack<VariantNameIF>)info.get(EL_VARIANT);
      scoped = variants.peek();
    }
    else if (info.containsKey(EL_BASENAME)) {
      scoped = (ScopedIF)info.get(EL_BASENAME);
    } else if (info.containsKey(EL_OCCURRENCE)) {
      scoped = (ScopedIF)info.get(EL_OCCURRENCE);
    } else if (info.containsKey(EL_ASSOCIATION)) {
      scoped = (ScopedIF)info.get(EL_ASSOCIATION);
    } else if (info.containsKey(EL_MERGEMAP)) {
      ExternalDocument merge_map = (ExternalDocument)info.get(EL_MERGEMAP);
      merge_map.addTheme(theme);
      return;
    } else {
      throw new OntopiaRuntimeException("Unknown resourceData context.");
    }
    
    // Add referenced topic to scope of scoped object
    scoped.addTheme(theme);
  }
  
  protected void processTopicReference(TopicIF referenced_topic) {
    // This method is used by topicRef and subjectIndicatorRef.
    
    // Process in context of parent
    String parent_type = parents.peek();
    
    if (EL_INSTANCEOF.equals(parent_type)) {
      
      if (info.containsKey(EL_ASSOCIATION)) {
        // Set association type
        AssociationIF assoc = (AssociationIF)info.get(EL_ASSOCIATION);
        assoc.setType(referenced_topic);
      }
      else if (info.containsKey(EL_OCCURRENCE)) {
        // Set occurrence type
        OccurrenceIF occurs = (OccurrenceIF)info.get(EL_OCCURRENCE);
        occurs.setType(referenced_topic);
      }
      else if (info.containsKey(EL_BASENAME)) {
        // Set basename type
        TopicNameIF bname = (TopicNameIF)info.get(EL_BASENAME);
        bname.setType(referenced_topic);
      }
      else if (info.containsKey(EL_TOPIC)) {
        TopicIF topic = (TopicIF)info.get(EL_TOPIC);
        topic.addType(referenced_topic);
      }
      
    }
    else if (EL_SCOPE.equals(parent_type)) {
      processTheme(referenced_topic);
    }
    else if (EL_MEMBER.equals(parent_type)) {
      // Create new association role
      processMember(referenced_topic);
    }                      
    else if (EL_ROLESPEC.equals(parent_type)) {
      // Put role type on info map
      info.put(EL_ROLESPEC, referenced_topic);
    }                      
    else if (EL_PARAMETERS.equals(parent_type)) {
      processTheme(referenced_topic);
    }                      
    else if (EL_SUBJECTIDENTITY.equals(parent_type)) {
      TopicIF current_topic = (TopicIF)info.get(EL_TOPIC);
      
      if (!current_topic.equals(referenced_topic)) {
        if (log.isInfoEnabled()) {
          log.debug(MSG_TOPIC + current_topic + MSG_MERGED_WITH + referenced_topic +
          " because it is an addressable subject (subjectIndentity>:<topicRef>)");
        }
        
        // merge referenced topic with current topic.
        referenced_topic.merge(current_topic);
        
        // update info map
        info.put(EL_TOPIC, referenced_topic);
      }
    }
    else if (EL_MERGEMAP.equals(parent_type)) {
      // FIXME: do something else?
      processTheme(referenced_topic);
    }
    else {
      throw new OntopiaRuntimeException("Unknown parent: " + parent_type);
    }
  }
  
  protected void processMember(TopicIF player) {
    // Add association role to association
    AssociationIF assoc = (AssociationIF)info.get(EL_ASSOCIATION);
    TopicIF roletype = (TopicIF)info.get(EL_ROLESPEC);
    if (roletype == null) {
      roletype = getNullTopic(builder.getTopicMap());
    }
    if (player == null) {
      player = getNullTopic(builder.getTopicMap());
    }
    AssociationRoleIF role = builder.makeAssociationRole(assoc, roletype, player);
    
    // Put previous role onto info map, so that it is possible to
    // recognize whether the member element was empty or not.
    info.put(EL_MEMBER, role);      
  }

  protected void addItemIdentifier(TMObjectIF tmobject, LocatorIF sourceLocator) {
    tmobject.addItemIdentifier(sourceLocator);
    
    // handle implicit reification
    if (tmobject instanceof ReifiableIF) {
      TopicIF reifier = topicmap.getTopicBySubjectIdentifier(sourceLocator);
      if (reifier != null) {
        reify((ReifiableIF)tmobject, reifier);
      }
    }
  }

  // can cause merging, therefore returns topic
  protected TopicIF addSubjectIdentifier(TopicIF topic, LocatorIF subjectIndicator) {
    topic.addSubjectIdentifier(subjectIndicator);
    
    // handle implicit reification
    if (!(topic instanceof LazyTopic)) {
      TMObjectIF reified = topicmap.getObjectByItemIdentifier(subjectIndicator);
      if (reified != null && reified instanceof ReifiableIF) {
        return reify((ReifiableIF)reified, topic);
      }
    }

    return topic;
  }

  // --------------------------------------------------------------------------
  // Lazy initialization of topics
  // --------------------------------------------------------------------------

  // creating topics can be postponed until first characteristic found
  
  class LazyTopic implements TopicIF {
    protected List<TopicIF> types = new ArrayList<>();
    protected List<LocatorIF> subjectLocators = new ArrayList<>();
    protected List<LocatorIF> subjectIndicators = new ArrayList<>();
    protected List<LocatorIF> sourceLocators = new ArrayList<>();

    // implementations of methods collecting lazy data
    @Override
    public void addSubjectLocator(LocatorIF subject) throws ConstraintViolationException {
      this.subjectLocators.add(subject);
    }
    @Override
    public void addSubjectIdentifier(LocatorIF subject_identifier) throws ConstraintViolationException {
      this.subjectIndicators.add(subject_identifier);
    }
    @Override
    public void addItemIdentifier(LocatorIF source_locator) throws ConstraintViolationException {
      this.sourceLocators.add(source_locator);
    }
    // methods below should never be invoked  
    @Override
    public Collection<LocatorIF> getSubjectLocators() {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<LocatorIF> getSubjectIdentifiers() {
      throw new UnsupportedOperationException();
    }
    @Override
    public void removeSubjectLocator(LocatorIF subject_identifier) {
      throw new UnsupportedOperationException();
    }
    @Override
    public void removeSubjectIdentifier(LocatorIF subject_identifier) {
      throw new UnsupportedOperationException();
    }
    @Override
    public void removeItemIdentifier(LocatorIF source_locator) {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<TopicNameIF> getTopicNames() {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<TopicNameIF> getTopicNamesByType(TopicIF type) {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<OccurrenceIF> getOccurrences() {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<OccurrenceIF> getOccurrencesByType(TopicIF type) {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<AssociationRoleIF> getRoles() {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype, TopicIF assoc_type) {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<AssociationIF> getAssociations() {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<AssociationIF> getAssociationsByType(TopicIF type) {
      throw new UnsupportedOperationException();
    }
    @Override
    public void merge(TopicIF topic) {
      throw new UnsupportedOperationException();
    }
    public Collection<TopicIF> getScope() {
      throw new UnsupportedOperationException();
    }
    public void addTheme(TopicIF theme) {
      throw new UnsupportedOperationException();
    }
    public void removeTheme(TopicIF theme) {
      throw new UnsupportedOperationException();
    }
    @Override
    public String getObjectId() {
      throw new UnsupportedOperationException();
    }
    @Override
    public boolean isReadOnly() {
      throw new UnsupportedOperationException();
    }
    @Override
    public TopicMapIF getTopicMap() {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<LocatorIF> getItemIdentifiers() {
      throw new UnsupportedOperationException();
    }
    @Override
    public Collection<TopicIF> getTypes() {
      throw new UnsupportedOperationException();
    }
    @Override
    public void addType(TopicIF type) {
      this.types.add(type);
    }
    @Override
    public void removeType(TopicIF type) {
      throw new UnsupportedOperationException();
    }
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
    @Override
    public ReifiableIF getReified() {
      throw new UnsupportedOperationException();
    }
  }
  
  protected TopicIF getParentTopic() {
    TopicIF topic = (TopicIF)info.get(EL_TOPIC);
    if (topic == null) {
      // create new topic
      topic = builder.makeTopic();
      info.put(EL_TOPIC, topic);
      return topic;
    } else if (topic == this.lazyTopic && this.lazyTopic != null) {
      return createTopicFromLazyTopic();
    } else {
      return topic;
    }
  }

  protected TopicIF createTopicFromLazyTopic() {
    // loop over identities until first existing topic is found
    
    TopicIF topic = null;
    for (int i=0; i < lazyTopic.subjectLocators.size(); i++) {
      topic = topicmap.getTopicBySubjectLocator(lazyTopic.subjectLocators.get(i));
      if (topic != null) {
        break;
      }
    }            
    
    if (topic == null) {
      for (int i=0; i < lazyTopic.subjectIndicators.size(); i++) {
        topic = topicmap.getTopicBySubjectIdentifier(lazyTopic.subjectIndicators.get(i));
        if (topic != null) {
          break;
        }
      }            
    }
    if (topic == null) {
      for (int i=0; i < lazyTopic.sourceLocators.size(); i++) {
        TMObjectIF object = topicmap.getObjectByItemIdentifier(lazyTopic.sourceLocators.get(i));
        if (object instanceof TopicIF) {
          topic = (TopicIF)object;
        }
        if (topic != null) {
          break;
        }
      }
    }
    
    // create new topic if not already found
    if (topic == null) {
      topic = builder.makeTopic();
    }

    // then add remaining identities
    for (int i=0; i < lazyTopic.subjectLocators.size(); i++) {
      topic = registerSubjectLocator(topic, lazyTopic.subjectLocators.get(i));
    }            

    for (int i=0; i < lazyTopic.subjectIndicators.size(); i++) {
      topic = registerSubjectIndicator(topic, lazyTopic.subjectIndicators.get(i));
    }            

    for (int i=0; i < lazyTopic.sourceLocators.size(); i++) {
      topic = registerSourceLocator(topic, lazyTopic.sourceLocators.get(i));
    }

    // copy types
    for (int i=0; i < lazyTopic.types.size(); i++) {
      topic.addType(lazyTopic.types.get(i));
    }
    
    // reset lazy topic
    lazyTopic.subjectLocators.clear();
    lazyTopic.subjectIndicators.clear();
    lazyTopic.sourceLocators.clear();
    lazyTopic.types.clear();
    
    // update info map
    info.put(EL_TOPIC, topic);
    return topic;
  }
  
  // --------------------------------------------------------------------------
  // External documents
  // --------------------------------------------------------------------------
  
  class ExternalDocument {
    
    protected LocatorIF href;
    
    protected Set<TopicIF> scope = new HashSet<>();
    
    ExternalDocument(LocatorIF href) {
      this.href = href;
    }
    
    public LocatorIF getLocator() {
      return href;
    }
    
    public void setLocator(LocatorIF href) {
      this.href = href;
    }
    
    public Collection<TopicIF> getScope() {
      return scope;
    }
    
    public void addTheme(TopicIF theme) {
      scope.add(theme);
    }
    
    public void removeTheme(TopicIF theme) {
      scope.remove(theme);
    }
    
    public boolean importInto(TopicMapIF topicmap) throws SAXException {      
      // Process merge map if the document hasn't already been read.
      if (!processed_documents_current.contains(getLocator())) {
        
        // Create new parser object
        XMLReader parser = DefaultXMLReaderFactory.createXMLReader();
        
        // Initialize nested content handler
        TopicMapStoreFactoryIF sfactory = new SameStoreFactory(topicmap.getStore());
        XTMContentHandler handler = new XTMContentHandler(sfactory, getLocator(),
            processed_documents_current);
        // Copy handler configuration
        handler.setExternalReferenceHandler(getExternalReferenceHandler());
        // Tell handler it is reading a sub-document
        handler.setSubDocument(true);
        
        // Set propagated themes
        Collection<TopicIF> themes = new HashSet<>();
        if (propagated_themes != null) {
          themes.addAll(propagated_themes);
        }
        themes.addAll(getScope());
        handler.setPropagatedThemes(themes);
        
        // Register parser with content handler
        handler.register(parser);
        
        // Parse input source
        try {
          String url = getLocator().getExternalForm();
          InputSource source = new InputSource(url);
          parser.parse(source);
        } catch (IOException e) {
          throw new OntopiaRuntimeException("Cannot include topic map '" + getLocator().getAddress() + "': " + e.getMessage(), e);
        }
        
        // Resource was processed        
        return true;
      } else {
        log.debug("Resource has already been processed '" + href + "' (ignoring). " + getLocationInfo());
        // Resource wasn't processed
        return false;
      }
    }
    
  }

  // --------------------------------------------------------------------------
  // Lexical events
  // --------------------------------------------------------------------------
  
  // the purpose of this machinery is to ensure that URIs are resolved
  // correctly inside external entities.
  
  @Override
  public void startEntity(String name) {
    String sysid = entities.get(name);
    if (sysid != null) {
      bases.push(createLocator(sysid));
    }
  }
  
  @Override
  public void endEntity(String name) {
    if (entities.get(name) != null) {
      bases.pop();
    }
  }
  
  @Override
  public void comment(char[] ch, int start, int length) {
    // no-op
  }
  
  @Override
  public void startCDATA() {
    // no-op
  }
  
  @Override
  public void endCDATA() {
    // no-op
  }
  
  @Override
  public void startDTD(String name, String publicId, String systemId) {
    // no-op
  }
  
  @Override
  public void endDTD() {
    // no-op
  }
  
  // --------------------------------------------------------------------------
  // DTD events
  // --------------------------------------------------------------------------
  
  // we must intercept these events in order to know the URIs of entities
  
  @Override
  public void externalEntityDecl(String name,  String publicId, 
                                 String systemId) {
    if (systemId != null) {
      entities.put(name, systemId);
    }
  }
  
  @Override
  public void attributeDecl(String eName, String aName, String type,
                            String mode, String value) {
    // no-op
  }
  
  @Override
  public void elementDecl(String name, String model) {
    // no-op
  }
  
  @Override
  public void internalEntityDecl(String name, String value) {
    // no-op
  }
  
  // --------------------------------------------------------------------------
  // Null topic
  // --------------------------------------------------------------------------

  public static TopicIF getNullTopic(TopicMapIF topicmap) {
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(nullPSI);
    if (topic == null) {
      topic = topicmap.getBuilder().makeTopic();
      topic.addSubjectIdentifier(nullPSI);
    }
    return topic;
  }

  public static TopicIF getDefaultOccurrenceTopic(TopicMapIF topicmap) {
    TopicIF topic = topicmap
        .getTopicBySubjectIdentifier(PSI.getXTMOccurrence());
    if (topic == null) {
      topic = topicmap.getBuilder().makeTopic();
      topic.addSubjectIdentifier(PSI.getXTMOccurrence());
    }
    return topic;
  }
  
  public static void removeNullTopic(TopicMapIF topicmap) {
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(nullPSI);
    if (topic != null) {
      if (topic.getReified() != null) {
        return;
      }
      ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)topicmap.getIndex(ClassInstanceIndexIF.class.getName());
      if (cindex.usedAsType(topic)) {
        return;
      }
      ScopeIndexIF sindex = (ScopeIndexIF)topicmap.getIndex(ScopeIndexIF.class.getName());
      if (sindex.usedAsTheme(topic)) {
        return;
      }
      topic.remove();
    }
  }
  
  public static void removeDefaultOccurrenceTopic(TopicMapIF topicmap) {
    TopicIF topic = topicmap
        .getTopicBySubjectIdentifier(PSI.getXTMOccurrence());
    if (topic != null) {
      if (topic.getReified() != null) {
        return;
      }
      ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)topicmap.getIndex(ClassInstanceIndexIF.class.getName());
      if (cindex.usedAsType(topic)) {
        return;
      }
      ScopeIndexIF sindex = (ScopeIndexIF)topicmap.getIndex(ScopeIndexIF.class.getName());
      if (sindex.usedAsTheme(topic)) {
        return;
      }
      topic.remove();
    }
  }  
}
