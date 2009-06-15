
// $Id: TMRAPTestCaseContentHandler.java,v 1.2 2006/02/06 13:06:38 opland Exp $

package net.ontopia.topicmaps.utils.tmrap.test;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Stack;

import net.ontopia.xml.Log4jSaxErrorHandler;
import net.ontopia.xml.SAXTracker;

import org.apache.log4j.Logger;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL
 * Interprets the parsed contents of an XML file and based on this, generates 
 * TMRAPTestDescriptors.
 * 
 * A source file should be of the form:
 * <tests>
 *   <test id="..." uri="...>
 *   <test exception="..." uri="...">
 * <test>
 * 
 * There can be any number of <test> tags, each of which generates one
 * TMRAPTestDescriptor.
 * Each <test> tag must specify the 'uri' attribute.
 * Each <test> tag must either specify the 'id' or 'expectedException' 
 * attribute, but not both.
 */
public class TMRAPTestCaseContentHandler extends SAXTracker {

  static Logger log = Logger
    .getLogger(TMRAPTestCaseContentHandler.class.getName());

  protected ErrorHandler ehandler;
  protected Collection tests;
  protected Stack parents;
  protected String tmname;

  public TMRAPTestCaseContentHandler() {
    super();
    tests = new ArrayList();
  }

  protected ErrorHandler getDefaultErrorHandler() {
    return new Log4jSaxErrorHandler(log);
  }

  public void register(XMLReader parser) {
    parser.setContentHandler(this);
    ErrorHandler _ehandler = parser.getErrorHandler();
    if (_ehandler == null || (_ehandler instanceof DefaultHandler))
      parser.setErrorHandler(getDefaultErrorHandler());
    ehandler = parser.getErrorHandler();
  }

  public void startDocument() {
    parents = new Stack();
  }

  public void endDocument() {
  }

  public void startElement(String nsuri, String lname, String qname,
                           Attributes attrs) {
    if (qname.equals("tests"))
      tests = new ArrayList();
    if (qname.equals("test")) {    
      String id = attrs.getValue("id");
      String edit = attrs.getValue("edit");
      String uri = attrs.getValue("uri");
      String view = attrs.getValue("view");
      String expectedException = attrs.getValue("exception");
      TMRAPTestDescriptor tmrapTestParameters = new TMRAPTestDescriptor(id, uri,
          expectedException, edit, view);
      tests.add(tmrapTestParameters);
    }
    
    parents.push(qname);
  }

  public void endElement(String nsuri, String name, String qname) {
    parents.pop();
  }
  
  /**
   * Gets the collection of test case descriptions.
   */
  public Collection getTestDescriptors() {
    return tests;
  }
}
