
// $Id: JSPPageReader.java,v 1.20 2005/09/08 10:00:53 ian Exp $

package net.ontopia.utils.ontojsp;

import java.io.File;
import java.io.IOException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
import net.ontopia.xml.ConfiguredXMLReaderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * INTERNAL: Class that reads a jsp file and builds a JSPTree from it.
 */
public class JSPPageReader {

  // initialization of logging facility
  private static Logger logger =
    LoggerFactory.getLogger(JSPPageReader.class.getName());

  protected File source;

  /**
   * Constructor that accepts a filename as argument.
   */
  public JSPPageReader(File source) {
    this.source = source;
  }

  /**
   * Creates an XMLReader object.
   */
  public XMLReader createXMLReader() {
    ConfiguredXMLReaderFactory cxrfactory = new ConfiguredXMLReaderFactory();
    try {
      // Set parser features
      cxrfactory.setFeature("http://xml.org/sax/features/namespaces", false);
      // cxrfactory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      cxrfactory.setFeature("http://xml.org/sax/features/validation", false);
      // Create new parser object
      return cxrfactory.createXMLReader();
    } catch (SAXException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * Reads a jsp file and creates a tree of JSPTreeNodeIF objects.
   *
   * @return net.ontopia.utils.ontojsp.JSPTreeNodeIF
   */
  public JSPTreeNodeIF read()
    throws IOException, SAXException {

    String filename = URIUtils.toURL(source).toString();
    JSPContentHandler handler = new JSPContentHandler();
    XMLReader parser = createXMLReader();
    handler.register(parser);
    logger.debug("Read in JSP from " + filename);
    parser.parse(filename);
    return handler.getRootNode();
  }
  
}
