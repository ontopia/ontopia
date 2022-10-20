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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.ontopia.topicmaps.nav2.core.ModuleReaderIF;
import net.ontopia.utils.EncryptedInputStream;
import net.ontopia.xml.DefaultXMLReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * INTERNAL: Default implementation of the interface ModuleReaderIF
 */
public class ModuleReader implements ModuleReaderIF {

  // initialization of logging facility
  private static Logger logger =
    LoggerFactory.getLogger(ModuleReader.class.getName());
  
  protected boolean encrypted;
  protected XMLReader parser;
  
  /**
   * INTERNAL: Constructor that accepts whether the input is encrypted
   * or plain.
   */
  public ModuleReader(boolean encrypted) {
    this.encrypted = encrypted;
  }

  // Implementation of ModuleReaderIF
  
  @Override
  public Map read(InputStream source) throws IOException, SAXException {
    logger.debug("Start to read in module.");
    parser = getXMLParser();
    ModuleContentHandler handler = new ModuleContentHandler();
    handler.register(parser);
    InputSource inpsrc = new InputSource();
    if (encrypted) {
      inpsrc.setByteStream(new EncryptedInputStream(source));
    } else {
      inpsrc.setByteStream(source);
    }
    
    try {
      parser.parse(inpsrc);
    } catch (SAXParseException e) {
      throw new SAXException(e.getLineNumber() + ":" +
                             e.getColumnNumber() + ": " +
                             e.getMessage());
    }

    return handler.getFunctions();
  }

  // ------------------------------------------------------------
  // internal helper method(s)
  // ------------------------------------------------------------
  
  protected XMLReader getXMLParser() throws SAXException {
    if (parser == null) {
      parser = DefaultXMLReaderFactory.createXMLReader();
      parser.setFeature("http://xml.org/sax/features/string-interning", true);
      parser.setFeature("http://xml.org/sax/features/namespaces", false);
      logger.info("using parser: " + parser);
    }
    return parser;
  }
  
}
