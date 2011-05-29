
package net.ontopia.topicmaps.classify;

import java.io.*;
import java.util.*;

import net.ontopia.xml.*;
import net.ontopia.utils.*;
import net.ontopia.xml.SAXTracker;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * INTERNAL: 
 */
public class XMLFormatModule implements FormatModuleIF {

  protected Collection skipElements;
  protected String[] extensions = new String[] {".xml"};
  protected byte[] magicBytes = FormatModule.getBytes("<?xml");

  public XMLFormatModule() {
    setSkipElements(Arrays.asList(new String[] {"sgml.block", "verbatim", "example", "sgml", "author", "bibliog", "web", "Authorinfo", "AuthorInfo", "AUTHORINFO", "code.block", "code.line", "Pre", "PRE", "programlisting", "acknowl", "code"}));
  }
  
  public void setSkipElements(Collection skipElements) {
    this.skipElements = new HashSet(skipElements);
  }
  
  protected XMLReader createXMLReader() throws SAXException {
    return new DefaultXMLReaderFactory().createXMLReader();
  }

  protected ContentHandler getContentHandler(TextHandlerIF handler) {
    return new XMLHandler(handler);
  }
  
  public boolean matchesContent(ClassifiableContentIF cc) {
    return FormatModule.startsWith(cc.getContent(), magicBytes);
  }

  public boolean matchesIdentifier(ClassifiableContentIF cc) {
    return FormatModule.matchesExtension(cc.getIdentifier(), extensions);
  }
  
  public void readContent(ClassifiableContentIF cc, TextHandlerIF handler) {
    // create new parser object
    XMLReader parser;
    try {
      parser = createXMLReader();
      
      // create content handler
      parser.setContentHandler(getContentHandler(handler));
      
      // parse input source
      parser.parse(new InputSource(new BufferedInputStream(new ByteArrayInputStream(cc.getContent()))));
    } catch (SAXParseException e) {
      throw new OntopiaRuntimeException("XML parsing problem: " + e.toString() + " at: "+
                                        e.getSystemId() + ":" + e.getLineNumber() + ":" +
                                        e.getColumnNumber(), e);
    } catch (SAXException e) {
      if (e.getException() instanceof IOException)
        throw new OntopiaRuntimeException((IOException) e.getException());
      throw new OntopiaRuntimeException(e);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  private class XMLHandler extends DefaultHandler {

    private TextHandlerIF thandler;
    private int skipLevel;
    
    private XMLHandler(TextHandlerIF thandler) {
      this.thandler = thandler;
    }
    
    public void startElement(String nsuri, String lname, String qname,
                             Attributes attrs) throws SAXException {
      if (skipElements != null && skipElements.contains(lname)) {
        skipLevel++;        
      } else if (skipLevel == 0) {
        thandler.startRegion(lname);
      }
    }
    
    public void characters (char[] ch, int start, int length) {
      if (skipLevel == 0)
        thandler.text(ch, start, length);
    }
    
    public void endElement(String nsuri, String lname, String qname) throws SAXException {
      if (skipElements != null && skipElements.contains(lname)) {
        skipLevel--;        
      } else if (skipLevel == 0) {      
        thandler.endRegion();
      }
    }
    
  }
  
}
