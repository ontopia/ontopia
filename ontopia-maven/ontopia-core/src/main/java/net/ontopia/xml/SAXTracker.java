
// $Id: SAXTracker.java,v 1.9 2005/02/22 15:46:52 grove Exp $

package net.ontopia.xml;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL: A helper base class for SAX applications that makes it
 * much easier to write SAX applications, since it helps applications
 * keep track of the element stack and preserve the contents of
 * various elements.
 */

public abstract class SAXTracker extends DefaultHandler {
  protected Set          keepContentsOf;
  protected Locator      locator;
  /**
   * The contents of the current element, or null if not
   * instructed to keep the contents of the current element.
   */
  protected StringBuffer content;
  private   boolean      keepContents;
  /**
   * The stack of currently open elements.
   */
  protected Stack        openElements;
  
  // --- Configuration interface
  
  public SAXTracker() {
    keepContentsOf = new HashSet();
    openElements = new Stack();
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

  public void startElement(String nsuri, String lname, String qname,
			   Attributes attrs) throws SAXException {
    openElements.push(qname);

    keepContents = keepContentsOf.contains(qname);
    if (keepContents)
      content = new StringBuffer();
  }

  public void characters(char[] chars, int start, int length) {
    if (keepContents)
      content.append(chars, start, length);
  }
    
  public void endElement(String nsuri, String lname, String qname) throws SAXException {
    keepContents = false;
    openElements.pop();
  }

  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }
}
