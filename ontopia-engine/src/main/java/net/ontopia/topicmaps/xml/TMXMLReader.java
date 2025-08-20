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
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.ClassInstanceUtils;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.AbstractXMLFormatReader;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.ValidatingContentHandler;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * PUBLIC: A reader importing topic maps (or fragments) from the
 * TM/XML syntax.
 *
 * @since 3.1
 */
public class TMXMLReader extends AbstractXMLFormatReader implements TopicMapReaderIF {
  public static final String PROPERTY_VALIDATE = "validate";
  private boolean validate = true;
  
  // constants for state
  private static final int START          = 0; // before document element
  private static final int TOP            = 1; // inside doc elem, outside topic
  private static final int TOPIC          = 2; // inside topic
  private static final int IDENTIFIER     = 3; // inside tm:identifier
  private static final int MAYBETOPICNAME = 4; // inside characteristic
  private static final int BASENAME       = 5; // ...
  private static final int TOPICNAME      = 6;
  private static final int VARIANT        = 7;
  private static final int ASSOCIATION    = 8;
  private static final int ROLE           = 9;

  // --- Constructors

  /**
   * PUBLIC: Creates a reader reading from the given url.
   */
  public TMXMLReader(URL url) {
    super(url);
  }

  public TMXMLReader(URL url, LocatorIF base_address) {
    super(url, base_address);
  }

  /**
   * PUBLIC: Creates a reader reading from the given file name.
   */
  public TMXMLReader(File file) {
    super(file);
  }

  /**
   * PUBLIC: Creates a reader reading from the given Reader using 
   * the specified base address.
   */
  public TMXMLReader(Reader reader, LocatorIF base_address) {
    super(reader, base_address);
  }

  /**
   * PUBLIC: Creates a reader reading from the given InputStream using 
   * the specified base address.
   */
  public TMXMLReader(InputStream stream, LocatorIF base_address) {
    super(stream, base_address);
  }

  /**
   * PUBLIC: Creates a reader reading from the given location, using a
   * different base address.
   */
  public TMXMLReader(InputSource source, LocatorIF base) {
    super(source, base);
  }

  // --- Accessors

  public boolean getValidate() {
    return validate;
  }

  public void setValidate(boolean validate) {
    this.validate = validate;
  }

  // --- TopicMapReaderIF implementation

  @Override
  public TopicMapIF read() throws IOException {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    store.setBaseAddress(base_address);
    TopicMapIF topicmap = store.getTopicMap();
    importInto(topicmap);

    ClassInstanceUtils.resolveAssociations1(topicmap);
    ClassInstanceUtils.resolveAssociations2(topicmap);
    
    return topicmap;
  }
  
  @Override
  public void importInto(TopicMapIF topicmap) throws IOException {
    // Check that store is ok
    TopicMapStoreIF store = topicmap.getStore();
    if (store == null) {
      throw new IOException("Topic map not connected to a store.");
    }
    
    XMLReader parser;
    try {
      parser = DefaultXMLReaderFactory.createXMLReader();
    } catch (SAXException e) {
      throw new IOException("Problems occurred when creating SAX2 XMLReader: " +
                            e.getMessage());
    }
    
    // Register content handlers
    ContentHandler handler = new TMXMLContentHandler(topicmap, base_address);
    if (validate) {
      handler = new ValidatingContentHandler(handler, getTMXMLSchema(), true);
    }
    parser.setContentHandler(handler);
    
    // Parse input source
    try {
      parser.parse(source);
    } catch (SAXException e) {
      if (e.getException() instanceof IOException) {
        throw (IOException) e.getException();
      }
      throw new OntopiaRuntimeException(e);
      //throw new IOException("XML related problem: " + e.toString());
    }
  }

  @Override
  public Collection readAll() throws IOException {
    return Collections.singleton(read());
  }

  // --- ContentHandler

  final class TMXMLContentHandler extends AbstractTopicMapContentHandler {
    private Map nsprefixes;
    private TopicMapIF topicmap;
    private int state;
    private TopicMapBuilderIF builder;
    private TopicIF topic;
    private StringBuilder buffer;
    private TopicNameIF basename;
    private AssociationIF association;
    private boolean isuri;
    private String reifier;
    private TopicIF type; // used for topic names only

    // carryovers from start tag to object creation
    private Collection scope;
    private String datatype;
    
    public TMXMLContentHandler(TopicMapIF topicmap, LocatorIF base) {
      super(base);
      this.topicmap = topicmap;
      this.builder = topicmap.getBuilder();
      this.state = START;
      this.buffer = new StringBuilder();
      this.nsprefixes = new HashMap();
    }

    @Override
    public void startElement(String uri, String name, String qName,
                             Attributes atts)
      throws SAXException {
      //System.out.println("<" + name + ": " + state);
      
//       try {
      switch(state) {
      case START: // before document element
        // this is the document element, of which we only want the id, if given
        handleReifier(topicmap, atts);
        state = TOP;
        break;
      case TOP:   // inside doc elem, outside topic
        // this has to be the start element of a topic
        topic = getTopicById(atts.getValue("", "id"));
        TopicIF ttype = getType(uri, name);
        if (ttype != null) {
          topic.addType(ttype);
      }
        state = TOPIC;
        break;
      case TOPIC:
        // this has to be some property of the topic
        if (TMXMLWriter.NS_TM.equals(uri) &&
            ("identifier".equals(name) || "locator".equals(name))) {
          state = IDENTIFIER;
      } else if (atts.getValue("", "role") != null) {
          // it's an association, of some kind
          // let's make association and role played by this topic
          association = builder.makeAssociation(getType(uri, name));
          TopicIF roletype = getTopicByAttRef(atts.getValue("", "role"));
          builder.makeAssociationRole(association, roletype, topic);
          scope = getScope(atts);
          addScope(association);
          handleReifier(association, atts);

          if (atts.getValue("", "topicref") != null) {
            // binary association
            TopicIF other = getTopicByAttRef(atts.getValue("", "topicref"));
            roletype = getTopicByAttRef(atts.getValue("", "otherrole"));
            builder.makeAssociationRole(association, roletype, other);
          }

          // if unary or binary association: nothing more happens
          // if n-ary association: child elements for roles appear
          state = ASSOCIATION;
        } else {
          state = MAYBETOPICNAME;
          scope = getScope(atts);
          reifier = atts.getValue("", "reifier");
          checkDatatype(atts);
          type = getType(uri, name); // needed if this is a topic name
        }
        break;
      case MAYBETOPICNAME:
        // could be occurrence, could be topic name
        if (TMXMLWriter.NS_TM.equals(uri) && "value".equals(name)) {
          // ok, it was a topic name
          state = BASENAME;
          // we were collecting chars in case it was an occurrence; now dump
          buffer.setLength(0);
        }
        break;
      case TOPICNAME:
        if (TMXMLWriter.NS_TM.equals(uri) && "variant".equals(name)) {
          state = VARIANT;
          scope = getScope(atts);
          reifier = atts.getValue("", "reifier");
          datatype = atts.getValue("", "datatype");
          isuri = datatype != null && datatype.equals(TMXMLWriter.XSD_ANYURI);
        }
        break;
      case ASSOCIATION:
        // this must be the role element inside an association element
        TopicIF roletype = getType(uri, name);
        TopicIF other = getTopicByAttRef(atts.getValue("", "topicref"));
        AssociationRoleIF role = builder.makeAssociationRole(association,
                                                             roletype, other);
        handleReifier(role, atts);
        state = ROLE;
        break;
      }
//       } catch (Throwable e) {
//         e.printStackTrace();
//         throw new OntopiaRuntimeException(e);
//       }
    }

    @Override
    public void characters(char ch[], int start, int length) {
      if (state == IDENTIFIER ||
          state == BASENAME ||
          state == VARIANT ||
          state == MAYBETOPICNAME) {
        buffer.append(ch, start, length);
      }
    }

    @Override
    public void endElement(String uri, String name, String qName)
      throws SAXException {
      //System.out.println("</" + name + ": " + state);

      try {
        
      switch(state) {
      case TOP:
        state = START;
        break;
      case TOPIC:
        state = TOP;
        break;
      case IDENTIFIER:
        state = TOPIC;
        LocatorIF loc = createLocator(buffer.toString());
        buffer.setLength(0);

        if ("identifier".equals(name)) {
          registerSubjectIndicator(topic, loc);
      } else if ("locator".equals(name)) {
        registerSubjectLocator(topic, loc);
      }
        break;
      case BASENAME:
        state = TOPICNAME;        
        basename = builder.makeTopicName(topic, type, buffer.toString());
        addScope(basename);
        handleReifier(basename, reifier);
        reifier = null;
        buffer.setLength(0);
        break;
      case VARIANT:
        state = TOPICNAME;
        VariantNameIF vn;
        if (isuri) {
          try {
            vn = builder.makeVariantName(basename, new URILocator(buffer.toString()), Collections.emptySet());
          } catch (URISyntaxException e) {
            throw new SAXException("Invalid URI for variant name", e);
          }
        } else {
          vn = builder.makeVariantName(basename, buffer.toString(), Collections.emptySet());
				}
        addScope(vn);
        handleReifier(vn, reifier);
        reifier = null;
        buffer.setLength(0);
        break;
      case TOPICNAME:
        state = TOPIC;
        break;
      case MAYBETOPICNAME:
        // it turned out to be an occurrence (because we're seeing the end of
        // the element and haven't seen <value>)
        state = TOPIC;
        if (datatype == null) {
          datatype = TMXMLWriter.XSD_STRING;
      }
        OccurrenceIF occ = builder.makeOccurrence(topic, 
                                                  getType(uri, name),
                                                  buffer.toString(),
                                                  createLocator(datatype));
        buffer.setLength(0);
        addScope(occ);
        handleReifier(occ, reifier);
        reifier = null;
        break;
      case ASSOCIATION:
        state = TOPIC;
        break;
      case ROLE:
        state = ASSOCIATION;
        break;
      }

      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
      nsprefixes.put(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) {
      nsprefixes.remove(prefix);
    }

    private TopicIF getType(String uri, String name) throws SAXException {
      if (uri == null || uri.isEmpty()) {
        return getTopicById(name);
      }
      if (TMXMLWriter.NS_TM.equals(uri) && "topic".equals(name)) {
        return null; // element for typeless construct
      }
      
      try {
        return getTopicBySubjectIdentifier(new URILocator(uri + name));
      } catch (URISyntaxException e) {
        throw new SAXException("Invalid URI: " + uri + name);
      }
    }    

    private Collection getScope(Attributes atts) {
      String value = atts.getValue("", "scope");
      if (value == null) {
        return Collections.EMPTY_SET;
      }
      
      String[] tokens = StringUtils.split(value);
      Collection scope = new HashSet(tokens.length);      
      for (int ix = 0; ix < tokens.length; ix++) {
        scope.add(getTopicByAttRef(tokens[ix]));
      }
      return scope;
    }

    private void addScope(ScopedIF scoped) {
      Iterator it = scope.iterator();
      while (it.hasNext()) {
        scoped.addTheme((TopicIF) it.next());
      }
      scope = null;
    }

    private void checkDatatype(Attributes atts) {
      datatype = atts.getValue("", "datatype");
    }

    private TopicIF getTopicByAttRef(String attref) {
      if (attref.indexOf(':') == -1) {
        return getTopicById(attref);
      } else {
        return getTopicByQName(attref);
      }
    }
    
    private TopicIF getTopicById(String id) {
      LocatorIF loc = createLocator('#' + id);
      TopicIF topic = (TopicIF) topicmap.getObjectByItemIdentifier(loc);

      if (topic == null) {
        topic = builder.makeTopic();
        registerSourceLocator(topic, id);
      }
      return topic;
    }

    private TopicIF getTopicBySubjectIdentifier(LocatorIF psi) {
      TopicIF type = topicmap.getTopicBySubjectIdentifier(psi);
      if (type == null) {
        type = builder.makeTopic();
        registerSubjectIndicator(type, psi);
      }
      return type;
    }

    private TopicIF getTopicByQName(String qname) {
      int pos = qname.indexOf(':');
      String prefix = qname.substring(0, pos);
      String local = qname.substring(pos + 1);
      if (!nsprefixes.containsKey(prefix)) {
        throw new OntopiaRuntimeException("Undeclared namespace prefix " +
                                          prefix + " in " + qname);
      }

      try {
        LocatorIF psi = new URILocator(nsprefixes.get(prefix) + local);
        return getTopicBySubjectIdentifier(psi);
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException("Invalid namespace URI from qname " +
                                          qname + ": " + prefix + local);
      }
    }
    
    private void registerSubjectIndicator(TopicIF topic, LocatorIF psi) {
      TopicIF other = topicmap.getTopicBySubjectIdentifier(psi);
      if (other == null) {
        topic.addSubjectIdentifier(psi);
        return;
      }

      if (!other.equals(topic)) {
        MergeUtils.mergeInto(topic, other);
      }
    }
    
    // stolen from XTMContentHandler
    protected void registerSourceLocator(TMObjectIF tmobject, String id) {
      // No need to register source locator if id is null
      if (id == null) {
        return;
      }
      tmobject.addItemIdentifier(createLocator('#' + id));
    }

    protected void registerSubjectLocator(TopicIF topic, LocatorIF loc) {
      TopicIF other = topicmap.getTopicBySubjectLocator(loc);
      if (other == null) {
        topic.addSubjectLocator(loc);
        return;
      }

      if (other != topic) {
        MergeUtils.mergeInto(topic, other);
      }
    }
  
    // stolen from XTMContentHandler
    protected LocatorIF createLocator(String address) {
      if (address.length() == 0) {
        return doc_address;
      } else {
        return doc_address.resolveAbsolute(address);
      }
    }

    private void handleReifier(ReifiableIF reifiable, Attributes atts) {
      handleReifier(reifiable, atts.getValue("", "reifier"));
    }

    private void handleReifier(ReifiableIF reifiable, String ref) {
      if (ref == null) {
        return;
      }      
      TopicIF reifier = getTopicByAttRef(ref);
			reify(reifiable, reifier);
    }    

    private void reify(ReifiableIF reifiable, TopicIF reifier) {
      reifiable.setReifier(reifier);
    }
  }

  private InputSource getTMXMLSchema() throws IOException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream i = cl.getResourceAsStream("net/ontopia/topicmaps/xml/tmxml.rnc");
    return new InputSource(i);
  }

  /**
   * Sets additional properties for the TMXMLReader. Only accepts the property "validate", which
   * corresponds to the {@link #setValidate(boolean)} method. Only accepts a boolean value.
   * @param properties 
   */
  @Override
  public void setAdditionalProperties(Map<String, Object> properties) {
    Object value = properties.get(PROPERTY_VALIDATE);
    if ((value != null) && (value instanceof Boolean)) {
      setValidate((Boolean) value);
    }
  }
}
