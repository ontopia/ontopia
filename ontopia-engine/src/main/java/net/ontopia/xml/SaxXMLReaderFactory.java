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

import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * INTERNAL: A factory for creating SAX2 XMLReaders using the
 * SAX2 XMLReaderFactory.createXMLReader(klass) method.<p>
 *
 * The created parser is namespace-aware and non-validating by
 * default.<p>
 */

public class SaxXMLReaderFactory implements XMLReaderFactoryIF {

  String driver;
  
  public SaxXMLReaderFactory() {
    this.driver = null;
  }

  public SaxXMLReaderFactory(String driver) {
    this.driver = driver;
  }

  /**
   * INTERNAL: Returns the driver klass name if specified either
   * explicitly on this instance or through the 'org.xml.sax.driver'
   * system property.
   */
  public String getDriver() {
    if (driver != null)
      return driver;
    else
      // If local driver klass hasn't been specified, use system property.
      try {
        return System.getProperty("org.xml.sax.driver");
      } catch (SecurityException e) {
        // Ignore
        return null;
      }
  }

  public XMLReader createXMLReader() throws SAXException {
    XMLReader reader = null;
    if (driver == null)
      reader = XMLReaderFactory.createXMLReader();    
    else
      reader = XMLReaderFactory.createXMLReader(driver);
    reader.setFeature("http://xml.org/sax/features/namespaces", true);
    reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
    reader.setFeature("http://xml.org/sax/features/validation", false);
    return reader;
  }
    
}
