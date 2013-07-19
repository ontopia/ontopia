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

package net.ontopia.utils;

import junit.framework.TestCase;

public class URIUtilsTest extends TestCase {
  
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
