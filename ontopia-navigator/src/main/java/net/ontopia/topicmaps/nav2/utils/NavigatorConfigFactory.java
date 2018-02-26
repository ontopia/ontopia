/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.Slf4jSaxErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * INTERNAL: Provide easy access for reading in an action
 * configuration file and generating an action registry object from
 * it.
 */
public class NavigatorConfigFactory {

  // initialization of log facility
  private static final Logger log = LoggerFactory
    .getLogger(NavigatorConfigFactory.class.getName());

  public static NavigatorConfigurationIF getConfiguration(InputStream stream)
    throws SAXException, IOException {
    return getConfiguration(new InputSource(stream));
  }
  
  public static NavigatorConfigurationIF getConfiguration(File specfile)
    throws SAXException, IOException {
    return getConfiguration(new InputSource(URIUtils.toURL(specfile).toExternalForm()));
  }
  
  private static NavigatorConfigurationIF getConfiguration(InputSource src)
    throws SAXException, IOException {
    
    XMLReader parser = DefaultXMLReaderFactory.createXMLReader();
    try {
      parser.setFeature("http://xml.org/sax/features/string-interning", true);
    } catch (SAXException e) {
      throw new OntopiaRuntimeException("Parser doesn't support string-interning; " +
                                        "parser is: " + parser.getClass().getName());
    }
    try {
      parser.setFeature("http://xml.org/sax/features/namespaces", false);
    } catch (SAXException e) {
      throw new OntopiaRuntimeException("Parser won't parse without namespaces; " +
                                        "parser is: " + parser.getClass().getName());
    }
    
    NavigatorConfigurationContentHandler handler =
      new NavigatorConfigurationContentHandler();
    parser.setContentHandler(handler);
    parser.setErrorHandler(new Slf4jSaxErrorHandler(log));

    parser.parse(src);

    return handler.getNavigatorConfiguration();
  }
  
}
