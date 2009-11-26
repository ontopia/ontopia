
// $Id: XTMValidatingContentHandler.java,v 1.8 2008/08/19 12:08:45 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.io.StringReader;

import net.ontopia.utils.OntopiaRuntimeException;

import org.relaxng.datatype.helpers.DatatypeLibraryLoader2;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.Schema;
import com.thaiopensource.relaxng.SchemaFactory;
import com.thaiopensource.relaxng.util.DraconianErrorHandler;
import com.thaiopensource.relaxng.util.Jaxp11XMLReaderCreator;

/**
 * INTERNAL: SAX2 content handler used for validating XTM documents
 * using Jing.</p>
 */
public class XTMValidatingContentHandler implements ContentHandler {

  static final String EL_TOPICMAP = "topicMap";

  private ContentHandler child; // validated events are passed here
  private ContentHandler validator; // validating handler
  private Locator locator; // stored until we can pass on to validator
  private int xtm_version; // which XTM version to validate against
  
  public XTMValidatingContentHandler(ContentHandler child) {
    this(child, XTMSnifferContentHandler.VERSION_XTM10);
  }

  public XTMValidatingContentHandler(ContentHandler child, int xtm_version) {
    this.child = child;
    this.xtm_version = xtm_version;
  }
  
  protected ContentHandler createValidator() {
    String rnc;
    if (xtm_version == XTMSnifferContentHandler.VERSION_XTM10)
      rnc = DTD.getXTMRelaxNG();
    else if (xtm_version == XTMSnifferContentHandler.VERSION_XTM20)
      rnc = DTD.getXTM2RelaxNG();
    else
      throw new OntopiaRuntimeException("Unknown XTM version: " + xtm_version);
      
    InputSource src = new InputSource(new StringReader(rnc));
    try {
      SchemaFactory factory = new SchemaFactory();
      factory.setXMLReaderCreator(new Jaxp11XMLReaderCreator());
      factory.setErrorHandler(new DraconianErrorHandler());
      factory.setDatatypeLibraryFactory(new DatatypeLibraryLoader2());
      Schema schema = factory.createSchema(src);
      return schema.createValidator(new DraconianErrorHandler());
    } catch (Exception e) {
      throw new OntopiaRuntimeException("INTERNAL ERROR: " + e, e);
    }
  }

  public void startDocument () throws SAXException {
    child.startDocument();
  }
  
  public void endDocument () throws SAXException {
    child.endDocument();    
  }
  
  public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
    // initialize validator
    if (EL_TOPICMAP == qName) {
      validator = createValidator();
      if (locator != null) // if received already
        validator.setDocumentLocator(locator); 
      validator.startDocument();
    }
    if (validator != null) validator.startElement(uri, name, qName, atts);
    child.startElement(uri, name, qName, atts);
  }
  
  public void characters (char ch[], int start, int length) throws SAXException {
    if (validator != null) validator.characters(ch, start, length);
    child.characters(ch, start, length);
  }
  
  public void endElement (String uri, String name, String qName) throws SAXException {
    if (validator != null) validator.endElement(uri, name, qName);
    child.endElement(uri, name, qName);

    // clear validator
    if (EL_TOPICMAP == qName) {
      validator.endDocument();
      validator = null;
    }
  }
  
  public void startPrefixMapping(java.lang.String prefix, java.lang.String uri)  throws SAXException {
    if (validator != null) validator.startPrefixMapping(prefix, uri);
  }
  
  public void endPrefixMapping(java.lang.String prefix) throws SAXException {
    if (validator != null) validator.endPrefixMapping(prefix);
  }

  public void skippedEntity(String entityname) {
  }

  public void processingInstruction(String target, String data) {
  }

  public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
    if (validator != null) validator.characters(ch, start, length);
    child.characters(ch, start, length);
  }

  public void setDocumentLocator(Locator docloc) {
    if (validator != null) validator.setDocumentLocator(docloc);
    child.setDocumentLocator(docloc);
    locator = docloc; // stored in case we receive it before we have validator
  }
}
