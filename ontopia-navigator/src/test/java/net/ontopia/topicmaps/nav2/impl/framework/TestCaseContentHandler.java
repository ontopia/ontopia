/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.impl.framework;

import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import net.ontopia.xml.Slf4jSaxErrorHandler;
import net.ontopia.xml.SAXTracker;
import org.xml.sax.helpers.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Ignore;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;

/**
 * INTERNAL: A class which reads in the framework test configuration
 * file and builds the different test cases specified in the
 * <code>nav2/config/tests.xml</code> file.
 */
@Ignore
public class TestCaseContentHandler extends SAXTracker {

  static Logger log = LoggerFactory
    .getLogger(TestCaseContentHandler.class.getName());

  protected ErrorHandler ehandler;
  protected Map tests;
  protected Map parameters;
  protected int test;
  protected Stack stack;
  protected Stack parents;
  protected String tmname;

  public TestCaseContentHandler() {
    super();
    tests = new HashMap();
    test = 0;
  }

  protected ErrorHandler getDefaultErrorHandler() {
    return new Slf4jSaxErrorHandler(log);
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
    if (qname == "test") {
      stack = new Stack();
      tmname = attrs.getValue("tm");
    } else if (qname == "jsp") {
      parameters = new HashMap();
      for (int i = 0; i < attrs.getLength(); i++) {
        parameters.put(attrs.getQName(i), attrs.getValue(i));
      }
      stack.push(parameters);
    }
    parents.push(qname);
  }

  public void endElement(String nsuri, String name, String qname) {
    if (qname == "test") createTests(stack);
    parents.pop();
  }
  
  /**
   * Gets the map describing the test cases.
   *
   * @return Map containing the topic map name and the JSP file name
   *         together as key and the corresponsing tests as a
   *         Collection.
   */
  public Map getTests() {
    return tests;
  }

  
  // -- internal helper method(s)
  
  private void createTests(Stack stack) {
    while (!stack.empty()) {
      Map params = (Map) stack.pop();
      String key = tmname + "$$$" + (String) params.get("file");
      params.remove("file");
      Map result = new HashMap();
      Iterator it = params.keySet().iterator();
      while (it.hasNext()) {
        String tmp = (String) it.next();
        result.put(tmp, (String) params.get(tmp));
      }
      // Check to see if there are more tests using the same key.
      Collection testsWithSameKey = (Collection) tests.get(key);
      if (testsWithSameKey == null) 
        testsWithSameKey = new ArrayList();
      testsWithSameKey.add(result);
      tests.put(key, testsWithSameKey);
    }
  }

}
