
// $Id: XTM2TopicMapExporter.java,v 1.5 2008/07/04 10:20:37 lars.garshol Exp $

package net.ontopia.topicmaps.xml;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
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
import net.ontopia.xml.PrettyPrinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;

/**
 * INTERNAL: Exports topic maps to the XTM 2.0 or 2.1 interchange format.
 */
public class XTM2TopicMapExporter extends AbstractTopicMapExporter {
  protected boolean export_itemids = false;
  protected AttributeListImpl atts;
  protected static final AttributeListImpl EMPTY_ATTR_LIST = new AttributeListImpl();
  private final boolean xtm21Mode;
  private static final LocatorIF XTM2_NAMETYPE;

  static {
    LocatorIF tmp = null;
    try {
      tmp = new URILocator(XTM2ContentHandler.XTM_NAMETYPE);
    } catch (java.net.MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
    XTM2_NAMETYPE = tmp;
  }

  public XTM2TopicMapExporter() {
    this(false);
  }

  /**
   * EXPERIMENTAL: XTM 2.0 or XTM 2.1 output.
   *
   * @param xtm21 {@code true} to enable XTM 2.1, otherwise XTM 2.0 will be written.
   */
  public XTM2TopicMapExporter(final boolean xtm21) {
    this.xtm21Mode = xtm21;
    this.atts = new AttributeListImpl();
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
  public void export(TopicMapIF tm, DocumentHandler dh) throws SAXException {
    dh.startDocument();

    atts.addAttribute("xmlns", "CDATA", "http://www.topicmaps.org/xtm/");
    atts.addAttribute("version", "CDATA", xtm21Mode ? "2.1" : "2.0");
    addReifier(atts, tm);
    dh.startElement("topicMap", atts);
    writeReifier(tm, dh);
    writeItemIdentities(tm, dh);
    
    Iterator it = filterCollection(tm.getTopics()).iterator();
    while (it.hasNext())
      write((TopicIF) it.next(), dh);

    it = filterCollection(tm.getAssociations()).iterator();
    while (it.hasNext())
      write((AssociationIF) it.next(), dh);
    
    dh.endElement("topicMap");
    
    dh.endDocument();
  }

  // --- INTERNAL
  
  private void write(TopicIF topic, DocumentHandler dh) throws SAXException {
    final Collection<LocatorIF> iids = topic.getItemIdentifiers();
    final Collection<LocatorIF> sids = topic.getSubjectIdentifiers();
    final Collection<LocatorIF> slos = topic.getSubjectLocators();

    atts.clear();
    if (!xtm21Mode || (iids.isEmpty() && sids.isEmpty() && slos.isEmpty())) {
      atts.addAttribute("id", "CDATA", getElementId(topic));
    }
    dh.startElement("topic", atts);

    write(topic.getItemIdentifiers(), "itemIdentity", dh);
    write(topic.getSubjectIdentifiers(), "subjectIdentifier", dh);
    write(topic.getSubjectLocators(), "subjectLocator", dh);

    Iterator it = filterCollection(topic.getTypes()).iterator();
    if (it.hasNext()) {
      dh.startElement("instanceOf", EMPTY_ATTR_LIST);
      while (it.hasNext())
        writeTopicRef((TopicIF) it.next(), dh);
      dh.endElement("instanceOf");
    }
    
    it = filterCollection(topic.getTopicNames()).iterator();
    while (it.hasNext())
      write((TopicNameIF) it.next(), dh);

    it = filterCollection(topic.getOccurrences()).iterator();
    while (it.hasNext())
      write((OccurrenceIF) it.next(), dh);
    
    dh.endElement("topic");
  }

  private void write(TopicNameIF bn, DocumentHandler dh) throws SAXException {
    atts.clear();
    addReifier(atts, bn);
    dh.startElement("name", atts);
    writeReifier(bn, dh);
    writeItemIdentities(bn, dh);
    writeType(bn, dh);
    writeScope(bn, dh);
    
    dh.startElement("value", EMPTY_ATTR_LIST);
    write(bn.getValue(), dh);
    dh.endElement("value");

    Iterator it = filterCollection(bn.getVariants()).iterator();
    while (it.hasNext()) {
      VariantNameIF vn = (VariantNameIF) it.next();
      write(vn, dh);
    }
    
    dh.endElement("name");
  }

  private void write(VariantNameIF vn, DocumentHandler dh) throws SAXException{
    atts.clear();
    addReifier(atts, vn);
    dh.startElement("variant", atts);
    writeReifier(vn, dh);
    writeItemIdentities(vn, dh);
    writeScope(vn, dh);

    atts.clear();
    if (vn.getDataType().equals(DataTypes.TYPE_URI)) {
      atts.addAttribute("href", "CDATA", vn.getLocator().getExternalForm());
      dh.startElement("resourceRef", atts);
      dh.endElement("resourceRef");
    } else {
      addDatatype(atts, vn.getDataType());
      dh.startElement("resourceData", atts);
      write(vn.getValue(), dh);
      dh.endElement("resourceData");
    }
    
    dh.endElement("variant");
  }

  private void write(OccurrenceIF occ, DocumentHandler dh) throws SAXException{
    atts.clear();
    addReifier(atts, occ);
    dh.startElement("occurrence", atts);
    writeReifier(occ, dh);
    writeItemIdentities(occ, dh);
    writeType(occ, dh);
    writeScope(occ, dh);

    atts.clear();
    if (occ.getDataType().equals(DataTypes.TYPE_URI)) {
      atts.addAttribute("href", "CDATA", occ.getLocator().getExternalForm());
      dh.startElement("resourceRef", atts);
      dh.endElement("resourceRef");
    } else {
      addDatatype(atts, occ.getDataType());
      dh.startElement("resourceData", atts);
      write(occ.getValue(), dh);
      dh.endElement("resourceData");
    }
    
    dh.endElement("occurrence");
  }

  private void write(AssociationIF assoc, DocumentHandler dh)
    throws SAXException {
    Collection roles = filterCollection(assoc.getRoles());
    if (roles.isEmpty())
      return; // don't export empty assocs; they aren't valid
    
    atts.clear();
    addReifier(atts, assoc);
    dh.startElement("association", atts);
    writeReifier(assoc, dh);
    writeItemIdentities(assoc, dh);
    writeType(assoc, dh);
    writeScope(assoc, dh);

    Iterator it = assoc.getRoles().iterator();
    while (it.hasNext())
      write((AssociationRoleIF) it.next(), dh);
   
    dh.endElement("association");
  }

  private void write(AssociationRoleIF role, DocumentHandler dh)
    throws SAXException {
    atts.clear();
    addReifier(atts, role);
    dh.startElement("role", atts);
    writeReifier(role, dh);
    writeItemIdentities(role, dh);
    writeType(role, dh);
    writeTopicRef(role.getPlayer(), dh);
    dh.endElement("role");
  }

  private void write(Collection<LocatorIF> locators, String element, DocumentHandler dh)
    throws SAXException {
    Iterator<LocatorIF> it = locators.iterator();
    while (it.hasNext()) {
      LocatorIF loc = it.next();
      atts.clear();
      String uri = loc.getExternalForm();
      atts.addAttribute("href", "CDATA", uri);
      dh.startElement(element, atts);
      dh.endElement(element);
    }
  }
  
  private void write(String str, DocumentHandler dh) throws SAXException {
    if (str != null && !str.equals("")) {
      char[] chars = str.toCharArray();
      dh.characters(chars, 0, chars.length);
    }
  }
  
  private void writeType(TypedIF obj, DocumentHandler dh) throws SAXException {
    if (obj.getType() == null ||
        ((obj instanceof TopicNameIF) && isDefaultNameType(obj.getType())))
      return;
    
    dh.startElement("type", EMPTY_ATTR_LIST);
    writeTopicRef(obj.getType(), dh);
    dh.endElement("type");
  }

  private void writeReifier(final ReifiableIF reifiable, final DocumentHandler dh) throws SAXException {
    if (!xtm21Mode // Reifier attribute was used already.
          || reifiable.getReifier() == null) {
      return; 
    }
    dh.startElement("reifier", EMPTY_ATTR_LIST);
    writeTopicRef(reifiable.getReifier(), dh);
    dh.endElement("reifier");
  }

  private void writeTopicRef(final TopicIF topic, final DocumentHandler dh)
    throws SAXException {
    atts.clear();
    if (!xtm21Mode) {
      atts.addAttribute("href", "CDATA", "#" + getElementId(topic));
      dh.startElement("topicRef", atts);
      dh.endElement("topicRef");
    }
    else {
      // XTM 2.1
      // 1st try: Write subject identifier reference
      Iterator<LocatorIF> iter = topic.getSubjectIdentifiers().iterator();
      if (iter.hasNext()) {
        atts.addAttribute("href", "CDATA", iter.next().getExternalForm());
        dh.startElement("subjectIdentifierRef", atts);
        dh.endElement("subjectIdentifierRef");
      }
      else {
        iter = topic.getSubjectLocators().iterator();
        // 2nd try: Write subject locator reference
        if (iter.hasNext()) {
          atts.addAttribute("href", "CDATA", iter.next().getExternalForm());
          dh.startElement("subjectLocatorRef", atts);
          dh.endElement("subjectLocatorRef");
        }
        else {
          // 3rd: Neither sid nor slo found, write an item identifier or generate an id
          iter = topic.getItemIdentifiers().iterator();
          final String ref = iter.hasNext() ? iter.next().getExternalForm() : "#" + getElementId(topic);
          atts.addAttribute("href", "CDATA", ref);
          dh.startElement("topicRef", atts);
          dh.endElement("topicRef");
        }
      }
    }
  }

  private void writeScope(ScopedIF obj, DocumentHandler dh)
    throws SAXException {
    Iterator it = obj.getScope().iterator();
    if (!it.hasNext())
      return;

    dh.startElement("scope", EMPTY_ATTR_LIST);
    while (it.hasNext())
      writeTopicRef((TopicIF) it.next(), dh);
    dh.endElement("scope");

  }

  private void writeItemIdentities(TMObjectIF obj, DocumentHandler dh)
    throws SAXException {
    if (export_itemids)
      write(obj.getItemIdentifiers(), "itemIdentity", dh);
  }

  private boolean isDefaultNameType(TopicIF type) {
    return type.getSubjectIdentifiers().contains(XTM2_NAMETYPE);
  }

  private void addReifier(AttributeListImpl atts, ReifiableIF reified) {
    if (xtm21Mode || reified.getReifier() == null) {
      return;
    }
    atts.addAttribute("reifier", "CDATA",
                        "#" + getElementId(reified.getReifier()));
  }

  private void addDatatype(AttributeListImpl atts, LocatorIF datatype) {
    if (!datatype.equals(DataTypes.TYPE_STRING)) 
      atts.addAttribute("datatype", "CDATA", datatype.getExternalForm());
  }
}
