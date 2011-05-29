
package net.ontopia.xml;

import java.io.StringReader;
import org.xml.sax.Locator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import com.thaiopensource.relaxng.Schema;
import com.thaiopensource.relaxng.SchemaFactory;
import com.thaiopensource.relaxng.util.Jaxp11XMLReaderCreator;
import com.thaiopensource.relaxng.util.DraconianErrorHandler;
import org.relaxng.datatype.helpers.DatatypeLibraryLoader2;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: SAX2 content handler used for validating XML documents
 * using Jing.</p>
 *
 * @since 2.0.3
 */
public class ValidatingContentHandler implements ContentHandler {
  private ContentHandler child; // validated events are passed here
  private ContentHandler validator; // validating handler
 
  public ValidatingContentHandler(ContentHandler child, InputSource src,
                                  boolean compact_syntax) {
    this.child = child;
    
    try {
      SchemaFactory factory = new SchemaFactory();
      factory.setCompactSyntax(compact_syntax);
      factory.setXMLReaderCreator(new Jaxp11XMLReaderCreator());
      factory.setErrorHandler(new DraconianErrorHandler());
      factory.setDatatypeLibraryFactory(new DatatypeLibraryLoader2());
      Schema schema = factory.createSchema(src);
      this.validator = schema.createValidator(new DraconianErrorHandler());
    } catch (Exception e) {
      throw new OntopiaRuntimeException("INTERNAL ERROR", e);
    }
  }
  
  public void startDocument () throws SAXException {
    validator.startDocument();
    child.startDocument();
  }
  
  public void endDocument () throws SAXException {
    validator.endDocument();
    child.endDocument();    
  }
  
  public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
    validator.startElement(uri, name, qName, atts);
    child.startElement(uri, name, qName, atts);
  }
  
  public void characters (char ch[], int start, int length) throws SAXException {
    validator.characters(ch, start, length);
    child.characters(ch, start, length);
  }
  
  public void endElement (String uri, String name, String qName) throws SAXException {
    validator.endElement(uri, name, qName);
    child.endElement(uri, name, qName);
  }
  
  public void startPrefixMapping(java.lang.String prefix, java.lang.String uri)  throws SAXException {
    validator.startPrefixMapping(prefix, uri);
    child.startPrefixMapping(prefix, uri);
  }
  
  public void endPrefixMapping(java.lang.String prefix) throws SAXException {
    validator.endPrefixMapping(prefix);
    child.endPrefixMapping(prefix);
  }

  public void skippedEntity(String entityname) {
  }

  public void processingInstruction(String target, String data) {
  }

  public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
    validator.characters(ch, start, length);
    child.characters(ch, start, length);
  }

  public void setDocumentLocator(Locator docloc) {
    validator.setDocumentLocator(docloc);
    child.setDocumentLocator(docloc);
  }
}
