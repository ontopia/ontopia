
// $Id: NavigatorConfigFactory.java,v 1.10 2004/11/24 12:05:06 larsga Exp $

package net.ontopia.topicmaps.nav2.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.ontopia.xml.ConfiguredXMLReaderFactory;
import net.ontopia.xml.Log4jSaxErrorHandler;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorConfigurationContentHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.log4j.Logger;

/**
 * INTERNAL: Provide easy access for reading in an action
 * configuration file and generating an action registry object from
 * it.
 */
public class NavigatorConfigFactory {

  // initialization of log facility
  private static Logger log = Logger
    .getLogger(NavigatorConfigFactory.class.getName());

  public static NavigatorConfigurationIF getConfiguration(InputStream stream)
    throws SAXException, IOException {
    return getConfiguration(new InputSource(stream));
  }
  
  public static NavigatorConfigurationIF getConfiguration(File specfile)
    throws SAXException, IOException {
    return getConfiguration(new InputSource(specfile.toURL().toExternalForm()));
  }
  
  private static NavigatorConfigurationIF getConfiguration(InputSource src)
    throws SAXException, IOException {
    
    ConfiguredXMLReaderFactory cxrfactory = new ConfiguredXMLReaderFactory();
    XMLReader parser = cxrfactory.createXMLReader();
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
    parser.setErrorHandler(new Log4jSaxErrorHandler(log));

    parser.parse(src);

    return handler.getNavigatorConfiguration();
  }
  
}
