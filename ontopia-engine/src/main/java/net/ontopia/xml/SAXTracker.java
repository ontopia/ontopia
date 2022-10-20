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

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL: A helper base class for SAX applications that makes it
 * much easier to write SAX applications, since it helps applications
 * keep track of the element stack and preserve the contents of
 * various elements.
 */

public abstract class SAXTracker extends DefaultHandler {
  protected Set<String>  keepContentsOf;
  protected Locator      locator;
  /**
   * The contents of the current element, or null if not
   * instructed to keep the contents of the current element.
   */
  protected StringBuilder content;
  private   boolean       keepContents;
  /**
   * The stack of currently open elements.
   */
  protected Stack<String> openElements;
  
  // --- Configuration interface
  
  public SAXTracker() {
    keepContentsOf = new HashSet<>();
    openElements = new Stack<>();
  }

  /**
   * INTERNAL: Instructs the tracker to keep the contents of all
   * elements with the given name.
   */
  
  public void keepContentsOf(String elementName) {
    keepContentsOf.add(elementName);
  }

  // --- Utility interface

  /**
   * INTERNAL: Returns true if the parent element has the given name.
   */
  public boolean isParent(String localName) {
    return !openElements.isEmpty() && openElements.peek().equals(localName);
  }
  
  // --- ContentHandler interface

  @Override
  public void startElement(String nsuri, String lname, String qname,
			   Attributes attrs) throws SAXException {
    openElements.push(qname);

    keepContents = keepContentsOf.contains(qname);
    if (keepContents) {
      content = new StringBuilder();
    }
  }

  @Override
  public void characters(char[] chars, int start, int length) {
    if (keepContents) {
      content.append(chars, start, length);
    }
  }
    
  @Override
  public void endElement(String nsuri, String lname, String qname) throws SAXException {
    keepContents = false;
    openElements.pop();
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }
}
