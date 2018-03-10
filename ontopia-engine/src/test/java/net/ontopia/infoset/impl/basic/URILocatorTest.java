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

import java.net.URISyntaxException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.OntopiaRuntimeException;

public class URILocatorTest extends AbstractLocatorTest {

  protected static final String NOTATION = "URI";
  protected static final String ADDRESS = "http://www.ontopia.net/"; // Note: it is normalized
  
  public URILocatorTest(String name) {
    super(name);
  }

  @Override
  protected LocatorIF createLocator() {
    return createLocator(NOTATION, ADDRESS);
  }

  @Override
  protected LocatorIF createLocator(String notation, String address) {
    if (!NOTATION.equals(notation))
      throw new OntopiaRuntimeException("Notation '" + notation +
					"' unsupported.");
    try {
      return new URILocator(address);
    } catch (URISyntaxException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
    
  // --- tests

  public void testProperties() {
    LocatorIF locator = createLocator(NOTATION, ADDRESS);
    assertTrue("notation property not correctly set",
	   NOTATION.equals(locator.getNotation()));
    assertTrue("address property not correctly set",
	   ADDRESS.equals(locator.getAddress()));
  }

  public void testGetExternalFormSimple2() {
    testExternalForm("http://www.example.com/index.jsp",
                     "http://www.example.com/index.jsp");
  }

  public void testGetExternalFormSimple3() {
    testExternalForm("http://www.example.com/index.jsp?bongo",
                     "http://www.example.com/index.jsp?bongo");
  }

  public void testGetExternalFormSimple4() {
    testExternalForm("http://www.example.com/index.jsp?bongo#bash",
                     "http://www.example.com/index.jsp?bongo#bash");
  }

  public void testGetExternalFormDirnameSpace() {
    testExternalForm("http://www.ontopia.no/space%20in%20url.html",
                     "http://www.ontopia.no/space%20in%20url.html");
  }

  public void testReferenceResolutionRFC3986() {
    String base = "http://a/b/c/d;p?q";
    
    testAbsoluteResolution(base, "g:h", "g:h");
    testAbsoluteResolution(base, "g", "http://a/b/c/g");
    testAbsoluteResolution(base, "./g", "http://a/b/c/g");
    testAbsoluteResolution(base, "g/", "http://a/b/c/g/");
    testAbsoluteResolution(base, "/g", "http://a/g");
    testAbsoluteResolution(base, "//g", "http://g");
    //testAbsoluteResolution(base, "?y", "http://a/b/c/d;p?y");
    testAbsoluteResolution(base, "g?y", "http://a/b/c/g?y");
    testAbsoluteResolution(base, "#s", "http://a/b/c/d;p?q#s");
    testAbsoluteResolution(base, "g#s", "http://a/b/c/g#s");
    testAbsoluteResolution(base, "g?y#s", "http://a/b/c/g?y#s");
    testAbsoluteResolution(base, ";x", "http://a/b/c/;x");
    testAbsoluteResolution(base, "g;x", "http://a/b/c/g;x");
    testAbsoluteResolution(base, "g;x?y#s", "http://a/b/c/g;x?y#s");
    testAbsoluteResolution(base, "", "http://a/b/c/d;p?q");
    testAbsoluteResolution(base, ".", "http://a/b/c/");
    testAbsoluteResolution(base, "./", "http://a/b/c/");
    testAbsoluteResolution(base, "..", "http://a/b/");
    testAbsoluteResolution(base, "../", "http://a/b/");
    testAbsoluteResolution(base, "../g", "http://a/b/g");
    testAbsoluteResolution(base, "../..", "http://a/");
    testAbsoluteResolution(base, "../../", "http://a/");
    testAbsoluteResolution(base, "../../g", "http://a/g");
  }
  
  public void testEscapedAmpersand() {
    testExternalForm("http://www.ontopia.net/?foo=bar%26baz",
                     "http://www.ontopia.net/?foo=bar%26baz");
  }

  public void testEscapedHash() {
    testExternalForm("http://www.ontopia.net/?foo=bar%23baz",
                     "http://www.ontopia.net/?foo=bar%23baz");
  }

  public void _testNonAsciiIdempotency() throws URISyntaxException {
    String original = "http://dbpedia.org/resource/K%C3%B8benhavn";

    URILocator uri1 = new URILocator(original);
    assertEquals("External form differs from original",
                 original, uri1.getExternalForm());
    URILocator uri2 = new URILocator(uri1.getExternalForm());
    assertEquals("External form differs from original",
                 original, uri2.getExternalForm());
  }
  
  // https://github.com/ontopia/ontopia/issues/414
  public void testIssue414() {
    URILocator locator = URILocator.create("http://www.example.org/cablemap/european-cables.ctm#EASTERN%20EUROPEAN%20POSTS%20COLLECTIVE");
    URILocator locator2 = URILocator.create("http://www.example.org/example#fragm%7Bent");

    assertNotNull(locator);
    assertNotNull(locator2);
    
    assertTrue(locator.getUri().getSchemeSpecificPart().endsWith("european-cables.ctm"));
    assertTrue(locator2.getUri().getSchemeSpecificPart().endsWith("example"));
    
    assertEquals("EASTERN%20EUROPEAN%20POSTS%20COLLECTIVE", locator.getUri().getRawFragment());
    assertEquals("fragm%7Bent", locator2.getUri().getRawFragment());

    assertEquals("EASTERN EUROPEAN POSTS COLLECTIVE", locator.getUri().getFragment());
    assertEquals("fragm{ent", locator2.getUri().getFragment());
  }
  
  // https://github.com/ontopia/ontopia/issues/366
  public void testIssue366() {
    String fragmentUTF = "MAIN-\u30BF\u30A4\u30C8\u30EB\u8AAD\u307F";
    
    URILocator locator = URILocator.create("http://www.infocom.co.jp/dsp/sample#" + fragmentUTF);

    assertNotNull(locator);
    assertTrue(locator.getUri().getSchemeSpecificPart().endsWith("sample"));
    assertEquals(fragmentUTF, locator.getUri().getRawFragment());
    assertEquals(fragmentUTF, locator.getUri().getFragment());
  }

  // https://github.com/ontopia/ontopia/issues/289
  public void testIssue289() {
    URILocator locator = URILocator.create("http://en.wikipedia.org/wiki/Beethoven/x+y/");

    assertNotNull(locator);
    assertTrue(locator.getUri().getSchemeSpecificPart().endsWith("x+y/"));
  }

  // --- Internal

  private void testAbsoluteResolution(String base, String uri, String external) {
    try {
      // tests based on http://tools.ietf.org/html/rfc3986#section-5.4.1
      LocatorIF baseURI = new URILocator(base);
      LocatorIF locator = baseURI.resolveAbsolute(uri);
      assertTrue("incorrect external form for URI '" + uri + "': '" +
          locator.getExternalForm() + "', correct '" + external + "'",
          locator.getExternalForm().equals(external));
    } catch (URISyntaxException e) {
      fail("INTERNAL ERROR: " + e);
    }
  }
  
  private void testExternalForm(String uri, String external) {
    try {
      LocatorIF locator = new URILocator(uri);
      assertTrue("incorrect external form for URI '" + uri + "': '" +
                 locator.getExternalForm() + "', correct '" + external + "'",
                 locator.getExternalForm().equals(external));
    } catch (URISyntaxException e) {
      fail("INTERNAL ERROR: " + e);
    }
  }
}
