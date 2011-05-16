
// $Id: ModuleReader.java,v 1.3 2003/07/28 10:24:20 larsga Exp $

package net.ontopia.topicmaps.nav2.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import net.ontopia.xml.ConfiguredXMLReaderFactory;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.EncryptedInputStream;
import net.ontopia.topicmaps.nav2.core.ModuleReaderIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  
  public Map read(InputStream source) throws IOException, SAXException {
    logger.debug("Start to read in module.");
    parser = getXMLParser();
    ModuleContentHandler handler = new ModuleContentHandler();
    handler.register(parser);
    InputSource inpsrc = new InputSource();
    if (encrypted)
      inpsrc.setByteStream(new EncryptedInputStream(source));
    else
      inpsrc.setByteStream(source);
    
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
      ConfiguredXMLReaderFactory cxrfactory = new ConfiguredXMLReaderFactory();
      parser = cxrfactory.createXMLReader();
      parser.setFeature("http://xml.org/sax/features/string-interning", true);
      parser.setFeature("http://xml.org/sax/features/namespaces", false);
      logger.info("using parser: " + parser);
    }
    return parser;
  }
  
}
