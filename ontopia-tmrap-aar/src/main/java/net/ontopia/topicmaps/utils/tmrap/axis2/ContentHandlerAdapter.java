/*
 * #!
 * Ontopia TMRAP Axis Archive
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
package net.ontopia.topicmaps.utils.tmrap.axis2;

import java.util.Enumeration;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

public class ContentHandlerAdapter implements ContentHandler {

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

  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    this.sup.pushContext();
    for (int i = 0; i < atts.getLength(); ++i) {
      String aName = atts.getQName(i);
      if (aName.startsWith("xmlns:")){
        this.sup.declarePrefix(aName.substring("xmlns:".length()), atts.getValue(i));
      } else if ("xmlns".equals(aName)) {
        this.sup.declarePrefix("", atts.getValue(i));
      }
    }
    String[] parts = new String[3];
    AttributesImpl ai = new AttributesImpl();
    for (int i = 0; i < atts.getLength(); ++i) {
      String aName = atts.getQName(i);
      if ((aName.startsWith("xmlns:")) || ("xmlns".equals(aName))) {
        continue;
      }
      parts = this.sup.processName(aName, parts, true);
      ai.addAttribute(parts[0], parts[1], parts[2], atts.getType(i), atts.getValue(i));
    }
    String p;
    for (Enumeration e = this.sup.getDeclaredPrefixes(); e.hasMoreElements(); this.ch.startPrefixMapping(p, this.sup.getURI(p))) {
      p = (String) e.nextElement();
    }
    parts = this.sup.processName(qName, parts, false);
    this.ch.startElement(parts[0], parts[1], parts[2], ai);
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    String[] parts = new String[3];
    parts = this.sup.processName(qName, parts, false);
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

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
  }

  public void endPrefixMapping(String prefix) throws SAXException {
  }

  public void skippedEntity(String name) throws SAXException {
  }
}
