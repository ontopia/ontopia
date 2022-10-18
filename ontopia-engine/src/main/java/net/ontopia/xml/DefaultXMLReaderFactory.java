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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * INTERNAL: An XML reader factory that uses a JaxpXMLReaderFactory
 * unless the 'org.xml.sax.driver' system property is set, in that
 * case SaxXMLReaderFactory is used.<p>
 *
 * If the 'org.xml.sax.driver' system property cannot be used for some
 * reason, the 'net.ontopia.xml.sax.driver' system property can, if
 * specified, be used instead to override the default SAX property.<p>
 *
 * The created parser is namespace-aware and non-validating by
 * default.<p>
 */

public class DefaultXMLReaderFactory {

  private static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();

  static {
    FACTORY.setNamespaceAware(true);
    FACTORY.setValidating(false);
  }

  public static XMLReader createXMLReader() throws SAXException {
    try {
      return FACTORY.newSAXParser().getXMLReader();
    } catch (ParserConfigurationException e) {
      throw new SAXException(e);
    }
  }
}
