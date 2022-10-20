/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.utils.tmrap;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Stack;
import net.ontopia.xml.Slf4jSaxErrorHandler;
import net.ontopia.xml.SAXTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL
 * Interprets the parsed contents of an XML file and based on this, generates 
 * TMRAPTestDescriptors.
 * 
 * A source file should be of the form:
 * <tests>
 *   <test id="..." uri="...">
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

  private static Logger log = LoggerFactory
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
    return new Slf4jSaxErrorHandler(log);
  }

  public void register(XMLReader parser) {
    parser.setContentHandler(this);
    ErrorHandler _ehandler = parser.getErrorHandler();
    if (_ehandler == null || (_ehandler instanceof DefaultHandler)) {
      parser.setErrorHandler(getDefaultErrorHandler());
    }
    ehandler = parser.getErrorHandler();
  }

  @Override
  public void startDocument() {
    parents = new Stack();
  }

  @Override
  public void endDocument() {
    // no-op
  }

  @Override
  public void startElement(String nsuri, String lname, String qname,
                           Attributes attrs) {
    if ("tests".equals(qname)) {
      tests = new ArrayList();
    }
    if ("test".equals(qname)) {    
      String id = attrs.getValue("id");
      String edit = attrs.getValue("edit");
      String uri = attrs.getValue("uri");
      String view = attrs.getValue("view");
      String expectedException = attrs.getValue("exception");
      TMRAPTestDescriptor tmrapTestParameters = new TMRAPTestDescriptor(id, uri,
          expectedException, edit, view);
      tests.add(new TMRAPTestDescriptor[] {tmrapTestParameters});
    }
    
    parents.push(qname);
  }

  @Override
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
