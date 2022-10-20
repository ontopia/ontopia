/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.DefaultXMLReaderFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL: 
 */
public class XMLFormatModule implements FormatModuleIF {

  protected Collection<String> skipElements;
  protected String[] extensions = new String[] {".xml"};
  protected byte[] magicBytes = FormatModule.getBytes("<?xml");

  public XMLFormatModule() {
    setSkipElements(Arrays.asList(new String[] {"sgml.block", "verbatim", "example", "sgml", "author", "bibliog", "web", "Authorinfo", "AuthorInfo", "AUTHORINFO", "code.block", "code.line", "Pre", "PRE", "programlisting", "acknowl", "code"}));
  }
  
  public void setSkipElements(Collection<String> skipElements) {
    this.skipElements = new HashSet<String>(skipElements);
  }
  
  protected XMLReader createXMLReader() throws SAXException {
    return new DefaultXMLReaderFactory().createXMLReader();
  }

  protected ContentHandler getContentHandler(TextHandlerIF handler) {
    return new XMLHandler(handler);
  }
  
  @Override
  public boolean matchesContent(ClassifiableContentIF cc) {
    return FormatModule.startsWith(cc.getContent(), magicBytes);
  }

  @Override
  public boolean matchesIdentifier(ClassifiableContentIF cc) {
    return FormatModule.matchesExtension(cc.getIdentifier(), extensions);
  }
  
  @Override
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
      if (e.getException() instanceof IOException) {
        throw new OntopiaRuntimeException((IOException) e.getException());
      }
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
    
    @Override
    public void startElement(String nsuri, String lname, String qname,
                             Attributes attrs) throws SAXException {
      if (skipElements != null && skipElements.contains(lname)) {
        skipLevel++;        
      } else if (skipLevel == 0) {
        thandler.startRegion(lname);
      }
    }
    
    @Override
    public void characters (char[] ch, int start, int length) {
      if (skipLevel == 0) {
        thandler.text(ch, start, length);
      }
    }
    
    @Override
    public void endElement(String nsuri, String lname, String qname) throws SAXException {
      if (skipElements != null && skipElements.contains(lname)) {
        skipLevel--;        
      } else if (skipLevel == 0) {      
        thandler.endRegion();
      }
    }
    
  }
  
}
