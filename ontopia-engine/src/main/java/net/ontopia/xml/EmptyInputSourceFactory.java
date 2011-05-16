
// $Id: EmptyInputSourceFactory.java,v 1.5 2004/11/18 13:14:02 grove Exp $

package net.ontopia.xml;

import java.io.StringReader;
import org.xml.sax.*;

/**
 * INTERNAL: An InputSource factory that creates input sources with no
 * content.
 */
public class EmptyInputSourceFactory implements InputSourceFactoryIF {

  public InputSource createInputSource() {
    return new InputSource(new StringReader(""));
  }
    
}
