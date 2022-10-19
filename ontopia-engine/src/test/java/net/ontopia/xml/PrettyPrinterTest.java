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

package net.ontopia.xml;

import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class PrettyPrinterTest {
  protected static final String EMPTY_NAMESPACE = "";
  protected static final String EMPTY_LOCALNAME = "";
  private static final String NL = System.getProperty("line.separator");
    
  @Test
  public void testMinimalDocument() {
    try {
      StringWriter writer = new StringWriter();
      PrettyPrinter printer = setUpPrinter(writer);
      printer.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "doc", new AttributesImpl());
      printer.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "doc");
      printer.endDocument();

      verify(writer,
	     "<?xml version=\"1.0\" encoding=\"iso-8859-1\" standalone=\"yes\"?>" + NL
	     + "<doc>"
	     + "</doc>" + NL);
    }
    catch (SAXException e) {
      Assert.assertTrue("SAXException: " + e, false);
    }
  }

  @Test
  public void testDocumentWithAllConstructs() {
    try {
      StringWriter writer = new StringWriter();
      PrettyPrinter printer = setUpPrinter(writer);

      AttributesImpl attrs = new AttributesImpl();
      attrs.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "a", "CDATA", "v");
      printer.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "doc", attrs);
      printer.processingInstruction("pi", "data");
      String str = "A bit of character data!";
      printer.characters(str.toCharArray(), 0, str.length());
      str = "    ";
      printer.ignorableWhitespace(str.toCharArray(), 0, str.length());
      printer.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "doc");
      printer.endDocument();

      verify(writer,
	     "<?xml version=\"1.0\" encoding=\"iso-8859-1\" standalone=\"yes\"?>" + NL
	     + "<doc a=\"v\"><?pi data?>"
	     + "A bit of character data!</doc>" + NL);
    }
    catch (SAXException e) {
      Assert.assertTrue("SAXException: " + e, false);
    }
  }

  @Test
  public void testChardataEscaping() {
    try {
      StringWriter writer = new StringWriter();
      PrettyPrinter printer = setUpPrinter(writer);

      printer.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "doc", new AttributesImpl());
      String str = "A <, and a & and a >.";
      printer.characters(str.toCharArray(), 0, str.length());
      printer.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "doc");
      printer.endDocument();

      verify(writer,
	     "<?xml version=\"1.0\" encoding=\"iso-8859-1\" standalone=\"yes\"?>" + NL
	     + "<doc>A &lt;, and a &amp; and a &gt;.</doc>" + NL);
    }
    catch (SAXException e) {
      Assert.assertTrue("SAXException: " + e, false);
    }
  }

  @Test
  public void testAttributeEscaping() {
    try {
      StringWriter writer = new StringWriter();
      PrettyPrinter printer = setUpPrinter(writer);

      AttributesImpl attrs = new AttributesImpl();
      attrs.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "a", "CDATA", "\"<&");
      printer.startElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "doc", attrs);
      printer.endElement(EMPTY_NAMESPACE, EMPTY_LOCALNAME, "doc");
      printer.endDocument();

      verify(writer,
	     "<?xml version=\"1.0\" encoding=\"iso-8859-1\" standalone=\"yes\"?>" + NL
	     + "<doc a=\"&quot;&lt;&amp;\"></doc>" + NL);
    }
    catch (SAXException e) {
      Assert.assertTrue("SAXException: " + e, false);
    }
  }

  // --- Internal methods

  private PrettyPrinter setUpPrinter(StringWriter writer)
    throws SAXException {
    PrettyPrinter pp = new PrettyPrinter(writer, "iso-8859-1");
    pp.startDocument();
    return pp;
  }

  private void verify(StringWriter out, String expected) {
    String result = out.toString();

    int elen = expected.length();
    int rlen = result.length();

    int ix;
    for (ix = 0; ix < rlen && ix < elen && 
           result.charAt(ix) == expected.charAt(ix); ix++)
      ;

    if (ix < rlen && rlen > elen)
      Assert.fail("Result longer than expected; expected: " + elen + "; " +
           "result: " + rlen + "; rest: " + getRest(result, expected));
    else if (ix < elen && rlen < elen)
      Assert.fail("Result shorter than expected; expected: " + elen + "; " +
           "result: " + rlen + "; rest: " + getRest(expected, result));
    else if (ix < rlen && rlen == elen)
      Assert.fail("Result differs from expected in position " + ix + "; " +
           "result: " + result.charAt(ix) + " (" +
           encode(result.charAt(ix)) + "; " +
           "expected: " + expected.charAt(ix) + " (" +
           encode(expected.charAt(ix)));
  }

  // assumes s1.length() > s2.length()
  private String getRest(String s1, String s2) {
    StringBuilder buf = new StringBuilder();
    for (int ix = s2.length(); ix < s1.length(); ix++) 
      buf.append(encode(s1.charAt(ix)) + " ");
    return buf.toString();
  }

  private String encode(char ch) {
    return ("U+" +
            encodeHexDigit((ch & 0xF000) >> 12) +
            encodeHexDigit((ch & 0x0F00) >> 8) +
            encodeHexDigit((ch & 0x00F0) >> 4) +
            encodeHexDigit(ch & 0x000F));
  }
  
  private char encodeHexDigit(int value) {
    if (value <= 9)
      return (char) ('0' + value);
    else
      return (char) ('A' + (value - 10));
  }
}
