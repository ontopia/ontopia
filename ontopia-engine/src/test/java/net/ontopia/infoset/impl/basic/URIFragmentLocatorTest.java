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

import java.net.MalformedURLException;
import net.ontopia.infoset.core.LocatorIF;
import org.junit.Assert;
import org.junit.Test;

public class URIFragmentLocatorTest {
  
  // --- tests
  
  @Test
  public void testGetExternalFormSimple() throws MalformedURLException {
    testExternalForm("http://www.example.com", "fragment");
  }

  @Test
  public void testGetExternalFormSimple2() throws MalformedURLException {
    testExternalForm("http://www.example.com/index.jsp", "fragment");
  }

  @Test
  public void testGetExternalFormSimple3() throws MalformedURLException {
    testExternalForm("http://www.example.com/index.jsp?bongo", "fragment");
  }

  @Test
  public void testGetExternalFormHostname() throws MalformedURLException {
    testExternalForm("http://www.%F8l.no/", "fragment");
  }

  @Test
  public void testGetExternalFormDirname() throws MalformedURLException {
    testExternalForm("http://www.ontopia.no/%F8l.html", "fragment");
  }

  @Test
  public void testGetExternalFormDirnameSpace() throws MalformedURLException {
    testExternalForm("http://www.ontopia.no/space%20in%20url.html",
                     "fragment");
  }

  // --- helpers
  
  private void testExternalForm(String uri, String frag) throws MalformedURLException {
      LocatorIF base = new URILocator(uri);
      LocatorIF locator = base.resolveAbsolute("#" + frag);

      String correct = base.getExternalForm() + "#" + frag;
      String external = locator.getExternalForm();
      
      Assert.assertTrue("incorrect external form for URI '" + locator.getAddress() +
                 "': '" + external + "', correct '" + correct + "'",
                 external.equals(correct));
  }  
}
