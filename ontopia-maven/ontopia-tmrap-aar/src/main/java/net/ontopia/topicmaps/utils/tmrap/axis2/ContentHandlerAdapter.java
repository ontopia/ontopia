package net.ontopia.topicmaps.utils.tmrap.axis2;

import java.util.Enumeration;
import org.xml.sax.AttributeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

public class ContentHandlerAdapter implements DocumentHandler {

  private ContentHandler ch;
  private NamespaceSupport sup;

  public ContentHandlerAdapter(ContentHandler ch) {
    this.ch = ch;
    this.sup = new NamespaceSupport();
  }

  public void setDocumentLocator(Locator locator) {
    this.ch.setDocumentLocator(locator);
  }

  public void startDocument() throws SAXException {
    this.ch.startDocument();
  }

  public void endDocument() throws SAXException {
    this.ch.endDocument();
  }

  public void startElement(String name, AttributeList atts) throws SAXException {
    this.sup.pushContext();
    for (int i = 0; i < atts.getLength(); ++i) {
      String aName = atts.getName(i);
      if (aName.startsWith("xmlns:")){
        this.sup.declarePrefix(aName.substring("xmlns:".length()), atts.getValue(i));
      } else if (aName.equals("xmlns")) {
        this.sup.declarePrefix("", atts.getValue(i));
      }
    }
    String[] parts = new String[3];
    AttributesImpl ai = new AttributesImpl();
    for (int i = 0; i < atts.getLength(); ++i) {
      String aName = atts.getName(i);
      if ((aName.startsWith("xmlns:")) || (aName.equals("xmlns"))) {
        continue;
      }
      parts = this.sup.processName(aName, parts, true);
      ai.addAttribute(parts[0], parts[1], parts[2], atts.getType(i), atts.getValue(i));
    }
    String p;
    for (Enumeration e = this.sup.getDeclaredPrefixes(); e.hasMoreElements(); this.ch.startPrefixMapping(p, this.sup.getURI(p))) {
      p = (String) e.nextElement();
    }
    parts = this.sup.processName(name, parts, false);
    this.ch.startElement(parts[0], parts[1], parts[2], ai);
  }

  public void endElement(String name) throws SAXException {
    String[] parts = new String[3];
    parts = this.sup.processName(name, parts, false);
    this.ch.endElement(parts[0], parts[1], parts[2]);
    String p;
    for (Enumeration e = this.sup.getDeclaredPrefixes(); e.hasMoreElements(); this.ch.endPrefixMapping(p)) {
      p = (String)e.nextElement();
    }
    this.sup.popContext();
  }

  public void characters(char[] c, int start, int length) throws SAXException {
    this.ch.characters(c, start, length);
  }

  public void ignorableWhitespace(char[] c, int start, int length) throws SAXException {
    this.ch.ignorableWhitespace(c, start, length);
  }

  public void processingInstruction(String target, String data) throws SAXException {
    this.ch.processingInstruction(target, data);
  }

}
