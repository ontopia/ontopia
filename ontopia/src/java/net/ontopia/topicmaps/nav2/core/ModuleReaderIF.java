// $Id: ModuleReaderIF.java,v 1.8 2002/09/11 14:18:35 niko Exp $

package net.ontopia.topicmaps.nav2.core;

import java.util.Map;
import java.io.InputStream;
import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * INTERNAL: Class that reads a module from an input stream and builds
 * for each function a JSPTree from it.
 */
public interface ModuleReaderIF {

  /**
   * Method that reads a XML input stream of a module specification
   * and creates a map with functions.
   *
   * @return A Map containing a String with the function name and as
   *         the associated value the FunctionIF object.
   */
  public Map read(InputStream stream) throws IOException, SAXException;

}
