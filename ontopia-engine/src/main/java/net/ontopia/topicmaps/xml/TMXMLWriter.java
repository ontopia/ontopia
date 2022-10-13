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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;
import net.ontopia.xml.PrettyPrinter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * PUBLIC: A writer exporting topic maps (or fragments) to the TM/XML
 * syntax.
 *
 * @since 3.1
 */
public class TMXMLWriter extends AbstractTopicMapExporter
  implements TopicMapWriterIF {
  private static final String CDATA = "CDATA";
  private static final String SCOPE = "scope";
  public static final String PROPERTY_PREFIXES = "prefixes";
  public static final String PROPERTY_DOCUMENT_ELEMENT = "documentElement";
  protected static final AttributesImpl EMPTY_ATTR_LIST = new AttributesImpl();
  protected static final String EMPTY_NAMESPACE = "";
  protected static final String EMPTY_LOCALNAME = "";
  
  private ContentHandler out;
  
  // If writer is instantiated, the void close() method closes it.
  private Writer writer = null;

  private String docelem = "topicmap";
  private AttributesImpl atts = new AttributesImpl();
  private Map nsuris; // nsuri -> prefix
  private Map prefixes; // inverse
  private Set exported; // contains IDs of exported associations
  private Set unassigned; // namespace URIs not yet given a prefix

  public static final String NS_ISO = "http://psi.topicmaps.org/iso13250/model/";
  public static final String NS_XTM = "http://www.topicmaps.org/xtm/1.0/core.xtm#";
  public static final String NS_TM = "http://psi.ontopia.net/xml/tm-xml/";
  public static final String XSD_ANYURI = "http://www.w3.org/2001/XMLSchema#anyURI";
  public static final String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";

  // --- Constructors

  /**
   * PUBLIC: Creates a writer writing to the given writer in the utf-8
   * character encoding.
   */
  public TMXMLWriter(Writer out) throws IOException {
    this(out, "utf-8");
  }

  /**
   * PUBLIC: Creates a writer writing to the given writer in the given
   * character encoding.
   * @since 3.2
   */
  public TMXMLWriter(Writer out, String encoding) throws IOException, IOException, IOException {
    this.out = makePrinter(out, encoding);
    init();
  }

  /**
   * PUBLIC: Creates a writer writing to the given file in UTF-8.
   * @since 3.2
   */
  public TMXMLWriter(File out) throws IOException {
    String encoding = "utf-8";
    writer = new OutputStreamWriter(new FileOutputStream(out), encoding);
    this.out = makePrinter(writer, encoding);
    init();
  }
  
  /**
   * PUBLIC: Creates a writer writing to the given file in given encoding.
   */
  public TMXMLWriter(File out, String encoding) throws IOException {
    writer = new OutputStreamWriter(new FileOutputStream(out), encoding);
    this.out = makePrinter(writer, encoding);
    init();
  }

  public TMXMLWriter(OutputStream out, String encoding) throws IOException {
    writer = new OutputStreamWriter(out, encoding);
    this.out = makePrinter(writer, encoding);
    init();
  }

  /**
   * INTERNAL: Creates a writer writing to the given ContentHandler.
   */
  public TMXMLWriter(ContentHandler out) {
    this.out = out;
    init();
  }

  private void init() {
    this.filter = null;
    this.exported = new CompactHashSet();
    this.nsuris = new HashMap();
    this.prefixes = new HashMap();
    this.unassigned = new CompactHashSet();
    addPrefix("iso", NS_ISO);
    addPrefix("xtm", NS_XTM);
    addPrefix("tm", NS_TM);
  }
  
  /**
   * PUBLIC: Closes the Writer created for internal use.
   *
   * Call this method when you have finished using the fragment
   * exporter interface on the topic map writer. Don't call this
   * method if using the TopicMapWriterIF interface.
   */
  public void close() throws IOException {
    if (writer != null)
      writer.close();
  }
  
  // --- Accessors

  /**
   * PUBLIC: Returns the element type name of the document element.
   */
  public String getDocumentElement() {
    return docelem;
  }

  /**
   * PUBLIC: Sets the document element type name to use.
   */
  public void setDocumentElement(String docelem) {
    this.docelem = docelem;
  }

  // --- TopicMapWriterIF implementation

  /**
   * PUBLIC: Writes the given topic map to the underlying writer.
   * @exception IOException Thrown if writing the topic map fails.
   * @param topicmap The topic map to be exported.
   */
  @Override
  public void write(TopicMapIF topicmap) throws IOException {
    try {
      gatherPrefixes(topicmap.getTopics());
      startTopicMap(topicmap);
      writeTopics(filterCollection(topicmap.getTopics()));
      endTopicMap();
      close();      
    } catch (SAXException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // --- Fragment-exporter interface

  /**
   * PUBLIC: Writes the start tag of the document element (to be used
   * in fragment exporting only).
   */
  public void startTopicMap(TopicMapIF topicmap) throws SAXException {
    assignRemainingNamespaces();
    Iterator it = nsuris.keySet().iterator();
    while (it.hasNext()) {
      String nsuri = (String) it.next();
      String prefix = (String) nsuris.get(nsuri);

      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "xmlns:" + prefix, CDATA, nsuri);
    }

    if (topicmap != null) // topic map can be null in some situations (particularly when using tmrap)
      addReifierAttribute(topicmap, atts);
    
    out.startDocument();
    out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, docelem, atts);
    atts.clear();
  }

  /**
   * PUBLIC: Gets the namespace prefixes to be used (to be used in
   * fragment exporting mode only). Must be called before
   * startTopicMap.
   */
  public void gatherPrefixes(Collection topics) {
    Iterator it = topics.iterator();
    while (it.hasNext())
      gatherPrefixes((TopicIF) it.next());
  }
  
  /**
   * PUBLIC: Writes a set of topics (fragment exporting mode only).
   */
  public void writeTopics(Collection topics) throws SAXException {    
    Iterator it = topics.iterator();
    while (it.hasNext())
      writeTopic((TopicIF) it.next());
  }

  /**
   * PUBLIC: Writes a single topic (fragment exporting mode only).
   */
  public void writeTopic(TopicIF topic) throws SAXException {
    final String TOPIC = getElementTypeName(NS_TM + "topic");
    final String BASENAME = getElementTypeName(NS_ISO + "topic-name");
    final String OCCURRENCE = getElementTypeName(NS_TM + "occurrence");
    final String ASSOCIATION = getElementTypeName(NS_TM + "association");
    final String ROLE = "role";
    
    String elem = TOPIC; // special name meaning: no topic type
    Iterator typeit = filterCollection(topic.getTypes()).iterator();
    if (typeit.hasNext())
      elem = getElementTypeName((TopicIF) typeit.next(), TOPIC);
    
    atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "id", CDATA, getTopicId(topic));
    out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, elem, atts);
    atts.clear();
    
    // indicators
    Iterator it = filterCollection(topic.getSubjectIdentifiers()).iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
      out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "tm:identifier", EMPTY_ATTR_LIST);
      writeText(getSubjectIndicatorRef(topic, loc));
      out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "tm:identifier");
    }

    // subject locator
    it = filterCollection(topic.getSubjectLocators()).iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
      out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "tm:locator", EMPTY_ATTR_LIST);
      writeText(loc.getExternalForm());
      out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "tm:locator");
    }

    // topic name
    it = filterCollection(topic.getTopicNames()).iterator();
    while (it.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it.next();
      String bnelem = getElementTypeName(bn.getType(), BASENAME);

      String scope = getScope(bn);
      if (scope != null)
        atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, SCOPE, CDATA, scope);

      addReifierAttribute(bn, atts);
      
      out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, bnelem, atts);
      out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "tm:value", EMPTY_ATTR_LIST);
      writeText(bn.getValue());
      out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "tm:value");

      // variant names
      Iterator it2 = filterCollection(bn.getVariants()).iterator();
      while (it2.hasNext()) {
        VariantNameIF vn = (VariantNameIF) it2.next();

        atts.clear();
        scope = getScope(vn);
        if (scope != null)
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, SCOPE, CDATA, scope);
        if (!Objects.equals(vn.getDataType(), DataTypes.TYPE_STRING))
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "datatype", CDATA, vn.getDataType().getAddress());

        addReifierAttribute(vn, atts);
        
        out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "tm:variant", atts);
        writeText(vn.getValue());
        out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "tm:variant");
      }
      
      out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, bnelem);
      atts.clear();
    }

    // occurrence
    it = filterCollection(topic.getOccurrences()).iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      
      String occelem = getElementTypeName(occ.getType(), OCCURRENCE);

      String scope = getScope(occ);
      if (scope != null && filterOk(scope))
        atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, SCOPE, CDATA, scope);
      if (!Objects.equals(occ.getDataType(), DataTypes.TYPE_STRING))
        atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "datatype", CDATA, occ.getDataType().getAddress());

      addReifierAttribute(occ, atts);

      out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, occelem, atts);
      writeText(occ.getValue());
      out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, occelem);
      atts.clear();
    }

    // remaining types
    // (if getTypes() returned more types they are still in typeit;
    // if so, we can output them as associations here)
    while (typeit.hasNext()) {
      TopicIF type = (TopicIF) typeit.next();
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "role", CDATA, "xtm:instance");
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "otherrole", CDATA, "xtm:class");
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "topicref", CDATA, getTopicId(type));
      out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "xtm:class-instance", atts);
      out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "xtm:class-instance");
      atts.clear();
    }
    
    // association
    it = topic.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      AssociationIF assoc = role.getAssociation();
      if (!filterOk(assoc))
        continue; // Do not output filtered associations.
      if (exported.contains(assoc.getObjectId()))
        continue; // output each association only once
      exported.add(assoc.getObjectId());

      String assocelem = getElementTypeName(assoc.getType(), ASSOCIATION);
      String scope = getScope(assoc);
      if (scope != null)
        atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, SCOPE, CDATA, scope);
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "role", CDATA,
                        getElementTypeName(role.getType(), ROLE));

      addReifierAttribute(assoc, atts);
      
      int arity = assoc.getRoles().size();
      if (arity == 1 || arity == 2) {
        // unary or binary
        AssociationRoleIF otherrole = null;
        Iterator it2 = assoc.getRoles().iterator();
        while (it2.hasNext()) {
          AssociationRoleIF r = (AssociationRoleIF) it2.next();
          if (!r.equals(role)) {
            otherrole = r;
            break;
          }
        }

        if (otherrole != null && otherrole.getPlayer() != null) {
          // if unary we skip spec of the other role
          // also skip if player is null
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "topicref", CDATA,
                            getTopicId(otherrole.getPlayer()));
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "otherrole", CDATA,
                            getElementTypeName(otherrole.getType(), ROLE));
        }
        out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, assocelem, atts);
        out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, assocelem);
      } else {
        // n-ary
        out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, assocelem, atts);

        Iterator it2 = assoc.getRoles().iterator();
        while (it2.hasNext()) {
          AssociationRoleIF r = (AssociationRoleIF) it2.next();
          if (r.equals(role))
            continue; // this is our role, which is already covered

          atts.clear();
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "topicref", CDATA, getTopicId(r.getPlayer()));
          String roleelem = getElementTypeName(r.getType(), ROLE);
          out.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, roleelem, atts);
          out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, roleelem);
        }
        
        out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, assocelem);
      }
      
      atts.clear();
    }    
    
    out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, elem);
  }

  /**
   * PUBLIC: Write the end tag of the document element (fragment mode
   * only).
   */
  public void endTopicMap() throws SAXException {
    out.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, docelem);
    out.endDocument();   
  }

  /**
   * PUBLIC: Gets the namespace prefixes to be used (to be used in
   * fragment exporting mode only). Must be called before
   * startTopicMap.
   */
  public void gatherPrefixes(TopicIF topic) {
    findPrefixFor(topic.getTypes());

    // base names
    Iterator it = topic.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it.next();
      findPrefixFor(bn.getType());
      findPrefixFor(bn.getScope());

      Iterator it2 = bn.getVariants().iterator();
      while (it2.hasNext()) {
        VariantNameIF vn = (VariantNameIF) it2.next();
        findPrefixFor(vn.getScope());
      }
    }

    // occurrences
    it = topic.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      findPrefixFor(occ.getType());
      findPrefixFor(occ.getScope());
    }

    // associations
    it = topic.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      AssociationIF assoc = role.getAssociation();      
      findPrefixFor(assoc.getType());
      findPrefixFor(assoc.getScope());

      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role2 = (AssociationRoleIF) it2.next();
        findPrefixFor(role2.getType());
      }
    }
  }

 /**
   * PRIVATE: Returns the namespace URI to prefix mapping maintained
   * internally by the writer. Never, ever call this method. It exists
   * only for testing purposes.
   */
  public Map getNamespaceURIMapping() {
    return nsuris;
  }
  
  // --- Internal methods

  private String getScope(ScopedIF scoped) {
    Iterator it = filterCollection(scoped.getScope()).iterator();
    if (!it.hasNext())
      return null;
    
    StringBuilder buf = new StringBuilder();
    while (it.hasNext()) {
      TopicIF theme = (TopicIF) it.next();
      buf.append(getElementTypeName(theme, null) + " ");
    }
    return buf.substring(0, buf.length() - 1); // lose last space
  }
  
  private void findPrefixFor(Collection topics) {
    Iterator it = topics.iterator();
    while (it.hasNext())
      findPrefixFor((TopicIF) it.next());
  }

  private void findPrefixFor(TopicIF type) {
    getElementTypeName(type, "");
  }
  
  private void writeText(String text) throws SAXException {
    char[] ch = text.toCharArray();
    out.characters(ch, 0, ch.length);
  }
  
  private String getElementTypeName(TopicIF topic, String def) {
    if (topic == null)
      return def;
    
    Collection subjids = topic.getSubjectIdentifiers();
    if (!subjids.isEmpty()) {
      LocatorIF subjid = (LocatorIF) subjids.iterator().next();
      String elementTypeName = getElementTypeName(subjid.getAddress());
      if (elementTypeName != null) {
        return elementTypeName;
      }
    }
    
    return getTopicId(topic);
  }

  private String getElementTypeName(String psi) {
    int slash = psi.lastIndexOf('/');
    int hash = psi.lastIndexOf('#');
    int pos = Math.max(slash, hash);

    String localname = psi.substring(pos + 1); // FIXME: could crash
    String prefix = getPrefix(psi.substring(0, pos + 1));
    return localname.isEmpty() ? null : (prefix + ":" + localname); // avoid creating <www.ikke.no:> element
  }

  private String getPrefix(String baseurl) {
    String prefix = (String) nsuris.get(baseurl);

    if (prefix == null && !unassigned.contains(baseurl)) {
      // try to make nice, friendly prefix out of URI
      int first = baseurl.lastIndexOf('/');
      int second = baseurl.lastIndexOf('/', first - 1); // FIXME: could crash?
      if (first != -1 && second != -1) {
        String candidate = baseurl.substring(second + 1, first);
        candidate = StringUtils.normalizeId(candidate);
        if (candidate != null && candidate.length() <= 1) 
          candidate = null; // could be too short after cutting
        if (candidate != null && !prefixes.containsKey(candidate))
          prefix = candidate;
      } 

      if (prefix == null)
        unassigned.add(baseurl); // will assign preXX prefix later
      else
        addPrefix(prefix, baseurl);
    }
    
    return prefix;
  }

  private void assignRemainingNamespaces() {
    // We fix bug #1933 by sorting the namespace URIs before assigning
    // prefixes to them.
    List uris = new ArrayList(unassigned);
    unassigned = null;
    Collections.sort(uris);
    for (int ix = 0; ix < uris.size(); ix++)
      addPrefix("pre" + nsuris.size(), (String) uris.get(ix));
  }

  private String getTopicId(TopicIF topic) {
    LocatorIF baseaddr = topic.getTopicMap().getStore().getBaseAddress();
    if (baseaddr != null) {
      String base = baseaddr.getAddress();
      Iterator it = topic.getItemIdentifiers().iterator();
      while (it.hasNext()) {
        String extractedId = extractRelativeId(base, (LocatorIF) it.next());
        if (extractedId != null) return extractedId;
      }
    }    
    return "id" + topic.getObjectId();
  }

  private String extractRelativeId(String base, LocatorIF srcloc) {
    String addr = srcloc.getAddress();
    if (addr.startsWith(base) && addr.length() > base.length()) {
      String id = addr.substring(base.length() + 1);
      if (isValidXMLId(id))
        return id;
    }
    return null;
  }
  
  private PrettyPrinter makePrinter(Writer out, String encoding)
    throws IOException {
    return new PrettyPrinter(out, encoding);
  }
  
  public void addPrefix(String prefix, String nsuri) {
    prefixes.put(prefix, nsuri);
    nsuris.put(nsuri, prefix);
  }

  // see XTMTopicMapExporter
  protected String getSubjectIndicatorRef(TopicIF topic, LocatorIF indicator) {
    TopicMapIF topicmap = topic.getTopicMap();
    LocatorIF baseloc = topicmap.getStore().getBaseAddress();
    String address = indicator.getExternalForm();

    if (baseloc != null) {
      String base = baseloc.getExternalForm();
      if (base != null && address.startsWith(base)
          && address.indexOf('#') != -1) {
        String id = address.substring(address.indexOf('#'));
        if (isValidXMLId(id.substring(1)))
          return id;
      }
    }

    return address;
  }

  /**
   * Add reifier attribute if object has a reifier.
   */
  private void addReifierAttribute(ReifiableIF tmobject, AttributesImpl atts) {
    TopicIF reifier = tmobject.getReifier();
    if (reifier != null && (filter == null || filter.test(reifier))) {
      String reifierAttribute = getTopicId(reifier);
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "reifier", CDATA, reifierAttribute);
    }
  }
  
  /**
   * Sets additional properties for the TMXMLWriter. Accepted properties:
   * <ul><li>'documentElement' (String), corresponds to 
   * {@link #setDocumentElement(java.lang.String)}</li>
   * <li>'prefixes' (Map), each key-value pair is passed to 
   * {@link #addPrefix(java.lang.String, java.lang.String)} as Strings.</li>
   * </ul>
   * @param properties 
   */
  @Override
  public void setAdditionalProperties(Map<String, Object> properties) {
    Object value = properties.get(PROPERTY_DOCUMENT_ELEMENT);
    if ((value != null) && (value instanceof String)) {
      setDocumentElement((String) value);
    }
    value = properties.get(PROPERTY_PREFIXES);
    if ((value != null) && (value instanceof Map)) {
      Map _prefixes = (Map) value;
      for (Object k : _prefixes.entrySet()) {
        addPrefix(k.toString(), _prefixes.get(k).toString());
      }
    }
  }
}
