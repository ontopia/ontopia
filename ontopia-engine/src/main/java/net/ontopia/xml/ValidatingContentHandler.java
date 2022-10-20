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

import com.thaiopensource.datatype.DatatypeLibraryLoader;
import com.thaiopensource.relaxng.SchemaFactory;
import com.thaiopensource.util.SinglePropertyMap;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.xml.sax.DraconianErrorHandler;
import com.thaiopensource.xml.sax.Jaxp11XMLReaderCreator;
import net.ontopia.utils.OntopiaRuntimeException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

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
      this.validator = schema.createValidator(
              SinglePropertyMap.newInstance(ValidateProperty.ERROR_HANDLER, 
                      factory.getErrorHandler())).getContentHandler();
    } catch (Exception e) {
      throw new OntopiaRuntimeException("INTERNAL ERROR", e);
    }
  }
  
  @Override
  public void startDocument () throws SAXException {
    validator.startDocument();
    child.startDocument();
  }
  
  @Override
  public void endDocument () throws SAXException {
    validator.endDocument();
    child.endDocument();    
  }
  
  @Override
  public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
    validator.startElement(uri, name, qName, atts);
    child.startElement(uri, name, qName, atts);
  }
  
  @Override
  public void characters (char ch[], int start, int length) throws SAXException {
    validator.characters(ch, start, length);
    child.characters(ch, start, length);
  }
  
  @Override
  public void endElement (String uri, String name, String qName) throws SAXException {
    validator.endElement(uri, name, qName);
    child.endElement(uri, name, qName);
  }
  
  @Override
  public void startPrefixMapping(String prefix, String uri)  throws SAXException {
    validator.startPrefixMapping(prefix, uri);
    child.startPrefixMapping(prefix, uri);
  }
  
  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    validator.endPrefixMapping(prefix);
    child.endPrefixMapping(prefix);
  }

  @Override
  public void skippedEntity(String entityname) {
    // no-op
  }

  @Override
  public void processingInstruction(String target, String data) {
    // no-op
  }

  @Override
  public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
    validator.characters(ch, start, length);
    child.characters(ch, start, length);
  }

  @Override
  public void setDocumentLocator(Locator docloc) {
    validator.setDocumentLocator(docloc);
    child.setDocumentLocator(docloc);
  }
}
