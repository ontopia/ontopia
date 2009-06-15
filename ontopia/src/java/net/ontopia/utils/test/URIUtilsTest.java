
// $Id: URIUtilsTest.java,v 1.3 2005/06/19 13:03:28 larsga Exp $

package net.ontopia.utils.test;

import net.ontopia.test.*;
import net.ontopia.utils.*;

public class URIUtilsTest extends AbstractOntopiaTestCase {
  
  public URIUtilsTest(String name) {
    super(name);
  }

  // --- test cases for urlEncode
  
  public void testEncodeEmpty() throws java.io.IOException {
    assertEquals("incorrect encoding of empty string",
                 URIUtils.urlEncode("", "utf-8"), "");
  }
  
  public void testEncodeSimpleNormal() throws java.io.IOException {
    assertEquals("incorrect encoding of simple string",
                 URIUtils.urlEncode("abcde", "utf-8"), "abcde");
  }
  
  public void testEncodeSimpleNormal2() throws java.io.IOException {
    assertEquals("incorrect encoding of simple string",
                 URIUtils.urlEncode("ab~de", "utf-8"), "ab~de");
  }

  public void testEncodeNonAscii() throws java.io.IOException {
    assertEquals("incorrect encoding of non-ASCII string",
                 URIUtils.urlEncode("r\u00F8r", "utf-8"), "r%C3%B8r");
  }

  public void testEncodeNonAscii2() throws java.io.IOException {
    assertEquals("incorrect encoding of non-ASCII string",
                 URIUtils.urlEncode("r\u00F8r", "iso-8859-1"), "r%F8r");
  }

  public void testEncodeReserved() throws java.io.IOException {
    assertEquals("incorrect encoding of reserved string",
                 URIUtils.urlEncode("hei hei", "utf-8"), "hei+hei");
  }

  public void testEncodeNullCharset() throws java.io.IOException {
    assertEquals("couldn't handle null charset",
                 URIUtils.urlEncode("hei hei", null), "hei+hei");
  }
}
