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
public class HTMLFormatModule extends XMLFormatModule {

  protected byte[][] magicBytes = FormatModule.getBytes(new String[] {"<HTML", "<html", "<!DOCTYPE html ", "<!DOCTYPE HTML ", "<!DOCTYPE HTML ", "<!doctype html "});
  
  public HTMLFormatModule() {
    this.extensions = new String[] {".htm", ".html", ".xhtml", ".shtml"};
    setSkipElements(Arrays.asList(new String[] {"style", "STYLE", "pre", "PRE", "script", "SCRIPT"}));
  }

  public boolean matchesContent(ClassifiableContentIF cc) {
    return FormatModule.startsWithSkipWhitespace(cc.getContent(), magicBytes);
  }
  
  protected XMLReader createXMLReader() throws SAXException {
    return new org.ccil.cowan.tagsoup.Parser();
  }

  protected ContentHandler getContentHandler(TextHandlerIF handler) {
    return new HTMLHandler(handler);
  }

  private class HTMLHandler extends DefaultHandler {

    private TextHandlerIF thandler;
    private int skipLevel;
    
    private HTMLHandler(TextHandlerIF thandler) {
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
