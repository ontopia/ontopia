
// $Id: ContentWriter.java,v 1.2 2006/04/28 13:05:13 larsga Exp $

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
