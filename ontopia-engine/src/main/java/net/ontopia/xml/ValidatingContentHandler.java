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

import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.relaxng.SchemaFactory;
import com.thaiopensource.util.SinglePropertyMap;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.xml.sax.DraconianErrorHandler;
import com.thaiopensource.xml.sax.Jaxp11XMLReaderCreator;
import com.thaiopensource.datatype.DatatypeLibraryLoader;
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
      factory.setDatatypeLibraryFactory(new DatatypeLibraryLoader());
      Schema schema = factory.createSchema(src);
	  this.validator = schema.createValidator(SinglePropertyMap.newInstance(ValidateProperty.ERROR_HANDLER, new DraconianErrorHandler())).getContentHandler();
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
