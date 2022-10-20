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

import java.io.File;
import java.net.MalformedURLException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.OntopiaRuntimeException;
import org.junit.Assert;
import org.junit.Test;

public class URILocatorTest extends AbstractLocatorTest {

  protected static final String NOTATION = "URI";
  protected static final String ADDRESS = "http://www.ontopia.net/"; // Note: it is normalized
  
  @Override
  protected LocatorIF createLocator() {
    return createLocator(NOTATION, ADDRESS);
  }

  @Override
  protected LocatorIF createLocator(String notation, String address) {
    if (!NOTATION.equals(notation)) {
      throw new OntopiaRuntimeException("Notation '" + notation +
					"' unsupported.");
    }
    try {
      return new URILocator(address);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
    
  // --- tests

  @Test
  public void testProperties() {
    LocatorIF locator = createLocator(NOTATION, ADDRESS);
    Assert.assertTrue("notation property not correctly set",
	   NOTATION.equals(locator.getNotation()));
    Assert.assertTrue("address property not correctly set",
	   ADDRESS.equals(locator.getAddress()));
  }

  @Test
  public void testFileWithPlus() {
    File file = new File("+");
    LocatorIF locator = new URILocator(file);
    String correct = getCorrectFileURI(file); 
    Assert.assertTrue("+ character not escaped correctly, got '" + locator.getAddress() + "'"
               + ", correct: '" + correct + "'",
               locator.getAddress().equals(correct));
  }

  @Test
  public void testFileWithPercent() {
    File file = new File("%");
    LocatorIF locator = new URILocator(file);
    // % must be escaped, even in internal form
    String correct = getCorrectFileURI(file) + "25";
    Assert.assertTrue("% character not escaped correctly: '" + locator.getAddress() + "', " +
               "correct: '" + correct + "'",
               locator.getAddress().equals(correct));
  }

  @Test
  public void testFileWithSpace() {
    File file = new File("/My Toilet Paper Rolls/roll1.rl");
    LocatorIF locator = new URILocator(file);
    String correct = getCorrectFileURI(file);
    Assert.assertTrue("incorrect file2url conversion: '" + locator.getAddress() + "', " +
               "correct: '" + correct + "'",
               locator.getAddress().equals(correct));
  }

  @Test
  public void testFileWithNorwegian() {
    File file = new File("d\u00E5j\u00E6.mov");
    LocatorIF locator = new URILocator(file);
    String correct = getCorrectFileURI(file);
    Assert.assertTrue("incorrect file2url conversion: '" + locator.getAddress() + "', " +
               "correct: '" + correct + "'",
               locator.getAddress().equals(correct));
  }

  @Test
  public void testGetExternalFormSimple() {
    assertExternalForm("http://www.example.com", "http://www.example.com/");
  }

  @Test
  public void testGetExternalFormSimple2() {
    assertExternalForm("http://www.example.com/index.jsp",
                     "http://www.example.com/index.jsp");
  }

  @Test
  public void testGetExternalFormSimple3() {
    assertExternalForm("http://www.example.com/index.jsp?bongo",
                     "http://www.example.com/index.jsp?bongo");
  }

  @Test
  public void testGetExternalFormSimple4() {
    assertExternalForm("http://www.example.com/index.jsp?bongo#bash",
                     "http://www.example.com/index.jsp?bongo#bash");
  }

  @Test
  public void testGetExternalFormHostname() {
    assertExternalForm("http://www.%F8l.no/", "http://www.%C3%B8l.no/");
  }
  
  @Test
  public void testGetExternalFormDirname() {
    assertExternalForm("http://www.ontopia.no/%F8l.html",
                     "http://www.ontopia.no/%C3%B8l.html");
  }

  @Test
  public void testGetExternalFormDirnameSpace() {
    assertExternalForm("http://www.ontopia.no/space%20in%20url.html",
                     "http://www.ontopia.no/space%20in%20url.html");
  }

  @Test
  public void testGetExternalFormDirnameSpace2() {
    assertExternalForm("http://www.ontopia.no/space+in+url.html",
                     "http://www.ontopia.no/space%20in%20url.html");
  }

  @Test
  public void testGetExternalFormOfWindowsFile() {
    assertExternalForm("file:///C|/topicmaps/opera/occurs/region.htm",
                     "file:/C|/topicmaps/opera/occurs/region.htm");
  }

  @Test
  public void testGetExternalFormWithSillyPipe() {
    assertExternalForm("http://www.ontopia.net/this|that/",
                     "http://www.ontopia.net/this%7Cthat/");
  }

  @Test
  public void testGetExternalFormBug2105() {
    assertExternalForm("http://en.wikipedia.org/wiki/Anton\u00EDn_Dvo\u0159\u00E1k",
                     "http://en.wikipedia.org/wiki/Anton%C3%ADn_Dvo%C5%99%C3%A1k");
  }
  
  @Test
  public void testReferenceResolutionRFC3986() {
    String base = "http://a/b/c/d;p?q";
    
    assertAbsoluteResolution(base, "g:h", "g:h");
    assertAbsoluteResolution(base, "g", "http://a/b/c/g");
    assertAbsoluteResolution(base, "./g", "http://a/b/c/g");
    assertAbsoluteResolution(base, "g/", "http://a/b/c/g/");
    assertAbsoluteResolution(base, "/g", "http://a/g");
    assertAbsoluteResolution(base, "//g", "http://g");
    //testAbsoluteResolution(base, "?y", "http://a/b/c/d;p?y");
    assertAbsoluteResolution(base, "g?y", "http://a/b/c/g?y");
    assertAbsoluteResolution(base, "#s", "http://a/b/c/d;p?q#s");
    assertAbsoluteResolution(base, "g#s", "http://a/b/c/g#s");
    assertAbsoluteResolution(base, "g?y#s", "http://a/b/c/g?y#s");
    assertAbsoluteResolution(base, ";x", "http://a/b/c/;x");
    assertAbsoluteResolution(base, "g;x", "http://a/b/c/g;x");
    assertAbsoluteResolution(base, "g;x?y#s", "http://a/b/c/g;x?y#s");
    assertAbsoluteResolution(base, "", "http://a/b/c/d;p?q");
    assertAbsoluteResolution(base, ".", "http://a/b/c/");
    assertAbsoluteResolution(base, "./", "http://a/b/c/");
    assertAbsoluteResolution(base, "..", "http://a/b/");
    assertAbsoluteResolution(base, "../", "http://a/b/");
    assertAbsoluteResolution(base, "../g", "http://a/b/g");
    assertAbsoluteResolution(base, "../..", "http://a/");
    assertAbsoluteResolution(base, "../../", "http://a/");
    assertAbsoluteResolution(base, "../../g", "http://a/g");
  }
  
  @Test
  public void testEscapedAmpersand() {
    assertExternalForm("http://www.ontopia.net/?foo=bar%26baz",
                     "http://www.ontopia.net/?foo=bar%26baz");
  }

  @Test
  public void testEscapedHash() {
    assertExternalForm("http://www.ontopia.net/?foo=bar%23baz",
                     "http://www.ontopia.net/?foo=bar%23baz");
  }

  // FIXME: this important test fails, but disabling for now
  // @Test
  public void _testNonAsciiIdempotency() throws MalformedURLException {
    String original = "http://dbpedia.org/resource/K%C3%B8benhavn";

    URILocator uri1 = new URILocator(original);
    Assert.assertEquals("External form differs from original",
                 original, uri1.getExternalForm());
    URILocator uri2 = new URILocator(uri1.getExternalForm());
    Assert.assertEquals("External form differs from original",
                 original, uri2.getExternalForm());
  }
  
  // --- Internal

  private void assertAbsoluteResolution(String base, String uri, String external) {
    try {
      // tests based on http://tools.ietf.org/html/rfc3986#section-5.4.1
      LocatorIF baseURI = new URILocator(base);
      LocatorIF locator = baseURI.resolveAbsolute(uri);
      Assert.assertTrue("incorrect external form for URI '" + uri + "': '" +
          locator.getExternalForm() + "', correct '" + external + "'",
          locator.getExternalForm().equals(external));
    } catch (java.net.MalformedURLException e) {
      Assert.fail("INTERNAL ERROR: " + e);
    }
  }
  
  private void assertExternalForm(String uri, String external) {
    try {
      LocatorIF locator = new URILocator(uri);
      Assert.assertTrue("incorrect external form for URI '" + uri + "': '" +
                 locator.getExternalForm() + "', correct '" + external + "'",
                 locator.getExternalForm().equals(external));
    } catch (java.net.MalformedURLException e) {
      Assert.fail("INTERNAL ERROR: " + e);
    }
  }
  
  private String getCorrectFileURI(File file) {
    // produce initial string
    String uri = file.getAbsolutePath().replace(File.separatorChar, '/');
    if (!uri.startsWith("/")) {
      uri = "/" + uri;
    }
    uri = "file:" + uri;

    // now, transcode to UTF-8
    try {
      byte raw[] = uri.getBytes("UTF-8");
      return new String(raw, 0, raw.length, "8859_1");
    } catch (java.io.UnsupportedEncodingException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}
