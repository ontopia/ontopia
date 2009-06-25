
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
 * INTERNAL: Exports topic maps to the XTM 2.0 interchange format.
 */
public class XTM2TopicMapExporter extends AbstractTopicMapExporter {
  protected AttributeListImpl atts;
  protected static final AttributeListImpl EMPTY_ATTR_LIST =
    new AttributeListImpl();
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
    this.atts = new AttributeListImpl();
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
    atts.addAttribute("version", "CDATA", "2.0");
    addReifier(atts, tm);
    dh.startElement("topicMap", atts);
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
    atts.clear();
    atts.addAttribute("id", "CDATA", getElementId(topic));
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
    writeItemIdentities(vn, dh);
    writeScope(vn, dh);

    atts.clear();
    if (vn.getDataType().equals(XTM2ContentHandler.XTM_URITYPE)) {
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
    writeItemIdentities(occ, dh);
    writeType(occ, dh);
    writeScope(occ, dh);

    atts.clear();
    if (occ.getDataType().equals(XTM2ContentHandler.XTM_URITYPE)) {
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
    writeItemIdentities(role, dh);
    writeType(role, dh);
    writeTopicRef(role.getPlayer(), dh);
    dh.endElement("role");
  }

  private void write(Collection locators, String element, DocumentHandler dh)
    throws SAXException {
    String base = null; // WORKING HERE
    Iterator it = locators.iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
      atts.clear();

      String uri = loc.getExternalForm();
//       if (element.equals("itemIdentity") && loc.getAddress().startsWith(
//                                                                         uri = loc.getExternalForm();
      
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

  private void writeTopicRef(TopicIF topic, DocumentHandler dh)
    throws SAXException {
    atts.clear();
    atts.addAttribute("href", "CDATA", "#" + getElementId(topic));
    dh.startElement("topicRef", atts);
    dh.endElement("topicRef");
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
    write(obj.getItemIdentifiers(), "itemIdentity", dh);
  }

  private boolean isDefaultNameType(TopicIF type) {
    return type.getSubjectIdentifiers().contains(XTM2_NAMETYPE);
  }

  private void addReifier(AttributeListImpl atts, ReifiableIF reified) {
    if (reified.getReifier() != null)
      atts.addAttribute("reifier", "CDATA",
                        "#" + getElementId(reified.getReifier()));
  }

  private void addDatatype(AttributeListImpl atts, LocatorIF datatype) {
    if (!datatype.equals(XTM2ContentHandler.XTM_URITYPE) &&
        !datatype.equals(XTM2ContentHandler.XTM_STRINGTYPE)) 
      atts.addAttribute("datatype", "CDATA", datatype.getExternalForm());
  }
}
