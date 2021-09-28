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

package net.ontopia.xml;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import net.ontopia.utils.StringUtils;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;

/**
 * INTERNAL: SAX document handler that prints canonical XML. Note that
 * this does not support all of http://www.w3.org/TR/xml-c14n, only
 * what is needed for Canonical XTM.
 */
public class CanonicalPrinter implements DocumentHandler {
  protected PrintWriter writer;
  
  /**
   * Creates a CanonicalPrinter that writes to the given OutputStream.
   * The encoding used is always utf-8.
   */
  public CanonicalPrinter(OutputStream stream) throws UnsupportedEncodingException {
    this.writer = new PrintWriter(new OutputStreamWriter(stream, "utf-8"));
  }

  /**
   * Creates a CanonicalPrinter that writes to the given Writer.
   */
  public CanonicalPrinter(Writer writer) {
    this.writer = new PrintWriter(writer);
  }  

  // Document events
    
  public void startDocument() {
  }

  public void startElement(String name, AttributeList atts) {
    // first: sort attributes
    String[] attNames = new String[atts.getLength()]; 
    for (int i = 0; i < atts.getLength(); i++) {
      attNames[i] = atts.getName(i);
    }
    java.util.Arrays.sort(attNames);

    // then write it out in sorted order
    writer.print("<" + name);
    for (int i = 0; i < attNames.length; i++) 
      writer.print(" " + attNames[i] + "=\"" + escape(atts.getValue(attNames[i])) +
		   "\"");
    writer.print(">");
  }

  public void endElement(String name) {
    writer.print("</" + name + ">");
  }

  public void characters (char ch[], int start, int length) {
    StringBuffer content = new StringBuffer();
    for (int i = start; i < start + length; i++) {
      switch(ch[i]) {
      case '&':
	content.append("&amp;");
	break;
      case '<':
	content.append("&lt;");
	break;
      case '>':
	content.append("&gt;");
	break;
      case 13: /* that is, \u000D */
        content.append("&#xD;");
        break;
      default:
	content.append(ch[i]);
      }
    }
    writer.print(content.toString());
  }

  public void ignorableWhitespace (char ch[], int start, int length) {
    writer.write(ch, start, length);
  }

  public void processingInstruction (String target, String data) {
    writer.print("<?" + target + " " + data + "?>\n");
  }

  public void endDocument() {
    writer.flush();
  }

  public void setDocumentLocator (Locator locator) {
  }

  // --- Internal methods

  public String escape(String attrval) {
    return StringUtils.replace(StringUtils.replace(StringUtils.replace(attrval, "&", "&amp;"), "<", "&lt;"), "\"", "&quot;");
  }
}
