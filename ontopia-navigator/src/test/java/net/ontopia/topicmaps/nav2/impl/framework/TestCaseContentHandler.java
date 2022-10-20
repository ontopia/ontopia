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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import net.ontopia.xml.SAXTracker;
import net.ontopia.xml.Slf4jSaxErrorHandler;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL: A class which reads in the framework test configuration
 * file and builds the different test cases specified in the
 * <code>nav2/config/tests.xml</code> file.
 */
@Ignore
public class TestCaseContentHandler extends SAXTracker {

  private static final Logger log = LoggerFactory
    .getLogger(TestCaseContentHandler.class.getName());

  protected ErrorHandler ehandler;
  protected Map<String, Set<Map<String, String>>> tests;
  protected Map<String, String> parameters;
  protected int test;
  protected Stack<Map<String, String>> stack;
  protected Stack<String> parents;
  protected String tmname;

  public TestCaseContentHandler() {
    super();
    tests = new LinkedHashMap<String, Set<Map<String, String>>>();
    test = 0;
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
    parents = new Stack<String>();
  }

  @Override
  public void endDocument() {
    // no-op
  }

  @Override
  public void startElement(String nsuri, String lname, String qname,
                           Attributes attrs) {
    if ("test".equals(qname)) {
      stack = new Stack<Map<String, String>>();
      tmname = attrs.getValue("tm");
    } else if ("jsp".equals(qname)) {
      parameters = new LinkedHashMap<String, String>();
      
      for (int i = 0; i < attrs.getLength(); i++) {
        parameters.put(attrs.getQName(i), attrs.getValue(i));
      }
      stack.push(parameters);
    }
    parents.push(qname);
  }

  @Override
  public void endElement(String nsuri, String name, String qname) {
    if ("test".equals(qname)) {
      createTests(stack);
    }
    parents.pop();
  }
  
  /**
   * Gets the map describing the test cases.
   *
   * @return Map containing the topic map name and the JSP file name
   *         together as key and the corresponsing tests as a
   *         Collection.
   */
  public Map<String, Set<Map<String, String>>> getTests() {
    return tests;
  }

  
  // -- internal helper method(s)
  
  private void createTests(Stack<Map<String, String>> stack) {
    while (!stack.empty()) {
      Map<String, String> params = stack.pop();
      String key = tmname + "$$$" + params.remove("file");
      Map<String, String> result = new LinkedHashMap<String, String>(params);
      // Check to see if there are more tests using the same key.
      Set<Map<String, String>> testsWithSameKey = tests.get(key);
      if (testsWithSameKey == null) {
        testsWithSameKey = new HashSet<Map<String, String>>();
      }
      testsWithSameKey.add(result);
      tests.put(key, testsWithSameKey);
    }
  }

}
