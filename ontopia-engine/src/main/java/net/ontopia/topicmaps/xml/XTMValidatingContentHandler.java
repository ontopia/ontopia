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

package net.ontopia.topicmaps.xml;

import com.thaiopensource.datatype.DatatypeLibraryLoader;

import net.ontopia.utils.OntopiaRuntimeException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.thaiopensource.validate.Schema;
import com.thaiopensource.relaxng.SchemaFactory;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.xml.sax.DraconianErrorHandler;
import com.thaiopensource.xml.sax.Jaxp11XMLReaderCreator;

/**
 * INTERNAL: SAX2 content handler used for validating XTM documents
 * using Jing.</p>
 */
public class XTMValidatingContentHandler implements ContentHandler {
  private static final String EL_TOPICMAP = "topicMap";

  private ContentHandler child; // validated events are passed here
  private ContentHandler validator; // validating handler
  private Locator locator; // stored until we can pass on to validator
  private XTMVersion xtm_version; // which XTM version to validate against
  
  public static final String XTM_1_RNC = "xtm1.rnc";
  public static final String XTM_2_RNC = "xtm2.rnc";

  public XTMValidatingContentHandler(ContentHandler child) {
    this(child, XTMVersion.XTM_1_0);
  }

  public XTMValidatingContentHandler(ContentHandler child, XTMVersion version) {
    this.child = child;
    this.xtm_version = version;
  }
  
  protected ContentHandler createValidator() {
    String rnc;
    if (xtm_version == XTMVersion.XTM_1_0) {
      rnc = XTM_1_RNC;
    } else if (xtm_version == XTMVersion.XTM_2_0 ||
             xtm_version == XTMVersion.XTM_2_1) {
      rnc = XTM_2_RNC;
    } else {
      throw new OntopiaRuntimeException("Unknown XTM version: " + xtm_version);
    }
      
    InputSource src = new InputSource(XTMValidatingContentHandler.class.getResourceAsStream(rnc));
    try {
      SchemaFactory factory = new SchemaFactory();
      factory.setXMLReaderCreator(new Jaxp11XMLReaderCreator());
      factory.setErrorHandler(new DraconianErrorHandler());
      factory.setDatatypeLibraryFactory(new DatatypeLibraryLoader());
      Schema schema = factory.createSchema(src);
      PropertyMapBuilder pmb = new PropertyMapBuilder();
      pmb.put(ValidateProperty.ERROR_HANDLER, new DraconianErrorHandler());
      return schema.createValidator(pmb.toPropertyMap()).getContentHandler();
    } catch (Exception e) {
      throw new OntopiaRuntimeException("INTERNAL ERROR: " + e, e);
    }
  }

  @Override
  public void startDocument () throws SAXException {
    child.startDocument();
  }
  
  @Override
  public void endDocument () throws SAXException {
    child.endDocument();    
  }
  
  @Override
  public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
    // initialize validator
    if (EL_TOPICMAP.equals(qName)) {
      validator = createValidator();
      if (locator != null) { // if received already
        validator.setDocumentLocator(locator);
      } 
      validator.startDocument();
    }
    if (validator != null) {
      validator.startElement(uri, name, qName, atts);
    }
    child.startElement(uri, name, qName, atts);
  }
  
  @Override
  public void characters (char ch[], int start, int length) throws SAXException {
    if (validator != null) {
      validator.characters(ch, start, length);
    }
    child.characters(ch, start, length);
  }
  
  @Override
  public void endElement (String uri, String name, String qName) throws SAXException {
    if (validator != null) {
      validator.endElement(uri, name, qName);
    }
    child.endElement(uri, name, qName);

    // clear validator
    if (EL_TOPICMAP.equals(qName)) {
      validator.endDocument();
      validator = null;
    }
  }
  
  @Override
  public void startPrefixMapping(String prefix, String uri)  throws SAXException {
    if (validator != null) {
      validator.startPrefixMapping(prefix, uri);
    }
  }
  
  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    if (validator != null) {
      validator.endPrefixMapping(prefix);
    }
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
    if (validator != null) {
      validator.characters(ch, start, length);
    }
    child.characters(ch, start, length);
  }

  @Override
  public void setDocumentLocator(Locator docloc) {
    if (validator != null) {
      validator.setDocumentLocator(docloc);
    }
    child.setDocumentLocator(docloc);
    locator = docloc; // stored in case we receive it before we have validator
  }
}
