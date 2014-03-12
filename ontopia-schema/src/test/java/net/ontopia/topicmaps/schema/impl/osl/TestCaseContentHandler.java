/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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
package net.ontopia.topicmaps.schema.impl.osl;

import java.util.ArrayList;
import java.util.Collection;
import net.ontopia.xml.SAXTracker;
import org.junit.Ignore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Ignore
public class TestCaseContentHandler extends SAXTracker {

  private Collection<String[]> tests;

  public TestCaseContentHandler() {
    this.tests = new ArrayList<String[]>();
  }

  public Collection<String[]> getTests() {
    return tests;
  }

  public void startElement(String nsuri, String lname, String qname,
          Attributes attrs) throws SAXException {

    if (qname.equals("test")) {
      tests.add(new String[]{attrs.getValue("topicmap"), attrs.getValue("schema"),
          attrs.getValue("valid")});
    }
    super.startElement(nsuri, lname, qname, attrs);
  }
}
