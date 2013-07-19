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

package net.ontopia.infoset.impl.basic;

import junit.framework.TestCase;
import net.ontopia.infoset.core.LocatorIF;

public class URIFragmentLocatorTest extends TestCase {
  
  public URIFragmentLocatorTest(String name) {
    super(name);
  }

  // --- tests
  
  public void testGetExternalFormSimple() {
    testExternalForm("http://www.example.com", "fragment");
  }

  public void testGetExternalFormSimple2() {
    testExternalForm("http://www.example.com/index.jsp", "fragment");
  }

  public void testGetExternalFormSimple3() {
    testExternalForm("http://www.example.com/index.jsp?bongo", "fragment");
  }

  public void testGetExternalFormHostname() {
    testExternalForm("http://www.%F8l.no/", "fragment");
  }

  public void testGetExternalFormDirname() {
    testExternalForm("http://www.ontopia.no/%F8l.html", "fragment");
  }

  public void testGetExternalFormDirnameSpace() {
    testExternalForm("http://www.ontopia.no/space%20in%20url.html",
                     "fragment");
  }

  // --- helpers
  
  private void testExternalForm(String uri, String frag) {
    try {
      LocatorIF base = new URILocator(uri);
      LocatorIF locator = base.resolveAbsolute("#" + frag);

      String correct = base.getExternalForm() + "#" + frag;
      String external = locator.getExternalForm();
      
      assertTrue("incorrect external form for URI '" + locator.getAddress() +
                 "': '" + external + "', correct '" + correct + "'",
                 external.equals(correct));
    } catch (java.net.MalformedURLException e) {
      fail("INTERNAL ERROR: " + e);
    }
  }  
}
