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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class DefaultXMLReaderFactory implements XMLReaderFactoryIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(DefaultXMLReaderFactory.class.getName());
    
  public XMLReader createXMLReader() throws SAXException {
    try {
      // If SAX driver property is set use the SAX factory      
      String factoryname = System.getProperty("org.xml.sax.driver");
      String factoryname_override = System.getProperty("net.ontopia.xml.sax.driver");

      // If override property is specified use that.
      if (factoryname_override != null)
        factoryname = factoryname_override;
      
      if (factoryname != null) {
        if (factoryname.startsWith("weblogic."))
          // In case of WebLogic we want to enforce using the Crimson
          // parser (see bug #590). WebLogic overrides the sax driver
          // property, so there is no way to override it.
          return new SaxXMLReaderFactory("org.apache.crimson.parser.XMLReaderImpl").createXMLReader();
        else
          return new SaxXMLReaderFactory(factoryname).createXMLReader();
      }
    } catch (SecurityException e) {
      log.warn(e.toString());      
    }

    // Otherwise use the JAXP factory
    return new JaxpXMLReaderFactory().createXMLReader();
  }
    
}
