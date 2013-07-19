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

import java.io.Writer;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import net.ontopia.utils.StringUtils;

public class ContentWriter extends DefaultHandler {
  private Writer out;
  private boolean content;
  
  public ContentWriter(String file) throws IOException {
    out = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
  }

  public ContentWriter(String file, String encoding) throws IOException {
    out = new OutputStreamWriter(new FileOutputStream(file), encoding);
  }
  
  public void startElement(String namespaceURI,
                           String localName,
                           String qName,
                           Attributes atts) throws SAXException {
    try {
      out.write("<" + localName);
      if (atts != null) {
        for (int i = 0; i < atts.getLength(); i++) 
          out.write(" " + atts.getQName(i) + "=\"" + escape(atts.getValue(i)) +
                    "\"");
      }
      out.write('>');
    } catch (IOException e) {
      throw new SAXException(e);
    }

    content = false;
  }

  public void characters(char[] buf, int start, int len) throws SAXException {
    try {
      out.write(buf, start, len);
    } catch (IOException e) {
      throw new SAXException(e);
    }

    content = true;
  }
  
  public void endElement(String namespaceURI,
                         String localName,
                         String qName) throws SAXException {

    if (content) {
      try {
        out.write("</" + localName + ">");
      } catch (IOException e) {
        throw new SAXException(e);
      }
    }

    content = true;
  }

  public void endDocument() throws SAXException {
    try {
      out.close();
    } catch (IOException e) {
      throw new SAXException(e);
    }
  }

  // --- Internal

  protected String escape(String attrval) {
    return StringUtils.replace(StringUtils.replace(StringUtils.replace(attrval, '&', "&amp;"),
                                                   '<', "&lt;"),
                               '"', "&quot;");
  }
  
}  
