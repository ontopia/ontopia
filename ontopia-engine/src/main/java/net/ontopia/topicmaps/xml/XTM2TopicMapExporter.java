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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.xml.PrettyPrinter;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.utils.PSI;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * INTERNAL: Exports topic maps to the XTM 2.0 or 2.1 interchange format.
 */
public class XTM2TopicMapExporter extends AbstractTopicMapExporter {
  private static final String CDATA = "CDATA";
  private static final String HREF = "href";
  private static final String RESOURCEREF = "resourceRef";
  private static final String RESOURCEDATA = "resourceData";
  private static final String TOPICREF = "topicRef";

  protected boolean export_itemids = false;
  protected AttributesImpl atts;
  protected static final AttributesImpl EMPTY_ATTR_LIST =
    new AttributesImpl();
  protected static final String EMPTY_NAMESPACE = "";
  protected static final String EMPTY_LOCALNAME = "";
  private final boolean xtm21Mode;

  public XTM2TopicMapExporter() {
    this(false);
  }

  /**
   * EXPERIMENTAL: XTM 2.0 or XTM 2.1 output.
   *
   * @param xtm21 {@code true} to enable XTM 2.1, otherwise XTM 2.0
   * will be written.
   */
  public XTM2TopicMapExporter(final boolean xtm21) {
    this.xtm21Mode = xtm21;
    this.atts = new AttributesImpl();
  }

  public void setExportItemIdentifiers(boolean export_itemids) {
    this.export_itemids = export_itemids;
  }
  
  /**
   * INTERNAL: Default export method. An XML pretty printer is used and the
   * output is written to stdout.
   */
  public void export(TopicMapIF tm) throws SAXException, IOException {
    PrintStream stream = new PrintStream(new BufferedOutputStream(System.out));
    export(tm, new PrettyPrinter(stream));
  }

  /**
   * INTERNAL: Traverses a Topic Map and emits SAX document handler events
   * according to the Topic Map interchange format to the given document
   * handler.
   */
  public void export(TopicMapIF tm, ContentHandler dh) throws SAXException {
    dh.startDocument();

    atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "xmlns", CDATA, "http://www.topicmaps.org/xtm/");
    atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "version", CDATA, xtm21Mode ? "2.1" : "2.0");
    addReifier(atts, tm);
    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "topicMap", atts);
    writeReifier(tm, dh);
    writeItemIdentities(tm, dh);
    
    Iterator it = filterCollection(tm.getTopics()).iterator();
    while (it.hasNext()) {
      write((TopicIF) it.next(), dh);
    }

    it = filterCollection(tm.getAssociations()).iterator();
    while (it.hasNext()) {
      write((AssociationIF) it.next(), dh);
    }
    
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "topicMap");
    
    dh.endDocument();
  }

  // --- INTERNAL
  
  private void write(TopicIF topic, ContentHandler dh) throws SAXException {
    final Collection<LocatorIF> iids = topic.getItemIdentifiers();
    final Collection<LocatorIF> sids = topic.getSubjectIdentifiers();
    final Collection<LocatorIF> slos = topic.getSubjectLocators();

    atts.clear();
    if (!xtm21Mode || (iids.isEmpty() && sids.isEmpty() && slos.isEmpty())) {
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "id", CDATA, getElementId(topic));
    }

    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "topic", atts);

    writeItemIdentities(topic, dh); // this method has export_itemids test
    write(sids, "subjectIdentifier", dh);
    write(slos, "subjectLocator", dh);

    Iterator it = filterCollection(topic.getTypes()).iterator();
    if (it.hasNext()) {
      dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "instanceOf", EMPTY_ATTR_LIST);
      while (it.hasNext()) {
        writeTopicRef((TopicIF) it.next(), dh);
      }
      dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "instanceOf");
    }
    
    it = filterCollection(topic.getTopicNames()).iterator();
    while (it.hasNext()) {
      write((TopicNameIF) it.next(), dh);
    }

    it = filterCollection(topic.getOccurrences()).iterator();
    while (it.hasNext()) {
      write((OccurrenceIF) it.next(), dh);
    }
    
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "topic");
  }

  private void write(TopicNameIF bn, ContentHandler dh) throws SAXException {
    atts.clear();
    addReifier(atts, bn);
    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "name", atts);
    writeReifier(bn, dh);
    writeItemIdentities(bn, dh);
    writeType(bn, dh);
    writeScope(bn, dh);
    
    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "value", EMPTY_ATTR_LIST);
    write(bn.getValue(), dh);
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "value");

    Iterator it = filterCollection(bn.getVariants()).iterator();
    while (it.hasNext()) {
      VariantNameIF vn = (VariantNameIF) it.next();
      write(vn, dh);
    }
    
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "name");
  }

  private void write(VariantNameIF vn, ContentHandler dh) throws SAXException {
    atts.clear();
    addReifier(atts, vn);
    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "variant", atts);
    writeReifier(vn, dh);
    writeItemIdentities(vn, dh);
    writeScope(vn, dh);

    atts.clear();
    if (vn.getDataType().equals(DataTypes.TYPE_URI)) {
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, HREF, CDATA, vn.getLocator().getExternalForm());
      dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, RESOURCEREF, atts);
      dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, RESOURCEREF);
    } else {
      addDatatype(atts, vn.getDataType());
      dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, RESOURCEDATA, atts);
      write(vn.getValue(), dh);
      dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, RESOURCEDATA);
    }
    
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "variant");
  }

  private void write(OccurrenceIF occ, ContentHandler dh) throws SAXException{
    atts.clear();
    addReifier(atts, occ);
    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "occurrence", atts);
    writeReifier(occ, dh);
    writeItemIdentities(occ, dh);
    writeType(occ, dh);
    writeScope(occ, dh);

    atts.clear();
    if (occ.getDataType().equals(DataTypes.TYPE_URI)) {
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, HREF, CDATA, occ.getLocator().getExternalForm());
      dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, RESOURCEREF, atts);
      dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, RESOURCEREF);
    } else {
      addDatatype(atts, occ.getDataType());
      dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, RESOURCEDATA, atts);
      write(occ.getValue(), dh);
      dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, RESOURCEDATA);
    }
    
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "occurrence");
  }

  private void write(AssociationIF assoc, ContentHandler dh)
    throws SAXException {
    Collection roles = filterCollection(assoc.getRoles());
    if (roles.isEmpty()) {
      return; // don't export empty assocs; they aren't valid
    }
    
    atts.clear();
    addReifier(atts, assoc);
    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "association", atts);
    writeReifier(assoc, dh);
    writeItemIdentities(assoc, dh);
    writeType(assoc, dh);
    writeScope(assoc, dh);

    Iterator it = assoc.getRoles().iterator();
    while (it.hasNext()) {
      write((AssociationRoleIF) it.next(), dh);
    }
   
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "association");
  }

  private void write(AssociationRoleIF role, ContentHandler dh)
    throws SAXException {
    atts.clear();
    addReifier(atts, role);
    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "role", atts);
    writeReifier(role, dh);
    writeItemIdentities(role, dh);
    writeType(role, dh);
    writeTopicRef(role.getPlayer(), dh);
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "role");
  }

  private void write(Collection<LocatorIF> locators, String element, ContentHandler dh)
    throws SAXException {
    for (LocatorIF loc: locators) {
      atts.clear();
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, HREF, CDATA, loc.getExternalForm());
      dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, element, atts);
      dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, element);
    }
  }
  
  private void write(String str, ContentHandler dh) throws SAXException {
    if (str != null && !str.equals("")) {
      char[] chars = str.toCharArray();
      dh.characters(chars, 0, chars.length);
    }
  }
  
  private void writeType(TypedIF obj, ContentHandler dh) throws SAXException {
    if (obj.getType() == null ||
        ((obj instanceof TopicNameIF) && isDefaultNameType(obj.getType()))) {
      return;
    }
    
    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "type", EMPTY_ATTR_LIST);
    writeTopicRef(obj.getType(), dh);
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "type");
  }

  private void writeReifier(final ReifiableIF reifiable, final ContentHandler dh) throws SAXException {
    if (!xtm21Mode // Reifier attribute was used already.
          || reifiable.getReifier() == null) {
      return; 
    }
    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "reifier", EMPTY_ATTR_LIST);
    writeTopicRef(reifiable.getReifier(), dh);
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "reifier");
  }

  private void writeTopicRef(final TopicIF topic, final ContentHandler dh)
    throws SAXException {
    atts.clear();
    if (!xtm21Mode) {
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, HREF, CDATA, "#" + getElementId(topic));
      dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, TOPICREF, atts);
      dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, TOPICREF);
    }
    else {
      // XTM 2.1
      // 1st try: Write subject identifier reference
      Iterator<LocatorIF> iter = topic.getSubjectIdentifiers().iterator();
      if (iter.hasNext()) {
        atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, HREF, CDATA, iter.next().getExternalForm());
        dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "subjectIdentifierRef", atts);
        dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "subjectIdentifierRef");
      }
      else {
        iter = topic.getSubjectLocators().iterator();
        // 2nd try: Write subject locator reference
        if (iter.hasNext()) {
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, HREF, CDATA, iter.next().getExternalForm());
          dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "subjectLocatorRef", atts);
          dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "subjectLocatorRef");
        }
        else {
          // 3rd: Neither sid nor slo found, write an item identifier or generate an id
          iter = topic.getItemIdentifiers().iterator();
          final String ref = iter.hasNext() ? iter.next().getExternalForm() : "#" + getElementId(topic);
          atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, HREF, CDATA, ref);
          dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, TOPICREF, atts);
          dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, TOPICREF);
        }
      }
    }
  }

  private void writeScope(ScopedIF obj, ContentHandler dh)
    throws SAXException {
    Iterator it = obj.getScope().iterator();
    if (!it.hasNext()) {
      return;
    }

    dh.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "scope", EMPTY_ATTR_LIST);
    while (it.hasNext()) {
      writeTopicRef((TopicIF) it.next(), dh);
    }
    dh.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "scope");

  }

  private void writeItemIdentities(TMObjectIF obj, ContentHandler dh)
    throws SAXException {
    if (export_itemids) {
      write(obj.getItemIdentifiers(), "itemIdentity", dh);
    }
  }

  private boolean isDefaultNameType(TopicIF type) {
    return type.getSubjectIdentifiers().contains(PSI.getSAMNameType());
  }

  private void addReifier(AttributesImpl atts, ReifiableIF reified) {
    if (xtm21Mode || reified.getReifier() == null) {
      return;
    }
    atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "reifier", CDATA,
                        "#" + getElementId(reified.getReifier()));
  }

  private void addDatatype(AttributesImpl atts, LocatorIF datatype) {
    if (!datatype.equals(DataTypes.TYPE_STRING)) {
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "datatype", CDATA, datatype.getExternalForm());
    }
  }
}
