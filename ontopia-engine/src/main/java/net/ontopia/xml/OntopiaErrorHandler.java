
// $Id: OntopiaErrorHandler.java,v 1.4 2003/03/16 14:22:33 larsga Exp $

package net.ontopia.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <b><code>OntopiaErrorHandler</code></b> implements the SAX
 * <code>ErrorHandler</code> interface and defines callback
 * behavior for the SAX callbacks associated with an XML
 * document's errors.
 */
public class OntopiaErrorHandler implements ErrorHandler {
 
  /**
   * This will report a warning that has occurred; this indicates
   * that while no XML rules were broken, something appears
   * to be incorrect or missing.
   *
   * @param exception <code>SAXParseException</code> that occurred.
   * @throws <code>SAXException</code> when things go wrong
   */
  public void warning(SAXParseException exception)
    throws SAXException {
 
    System.out.println("**Parsing Warning**\n" +
		       " Line: " +
		       exception.getLineNumber() + "\n" +
		       " URI: " +
		       exception.getSystemId() + "\n" +
		       " Message: " +
		       exception.getMessage());
    throw new SAXException("Warning encountered");
  }
 
  /**
   * This will report an error that has occurred; this indicates
   * that a rule was broken, typically in validation, but that
   * parsing can reasonably continue.
   *
   * @param exception <code>SAXParseException</code> that occurred.
   * @throws <code>SAXException</code> when things go wrong
   */
  public void error(SAXParseException exception)
    throws SAXException {
 
    System.out.println("**Parsing Error**\n" +
		       " Line: " +
		       exception.getLineNumber() + "\n" +
		       " URI: " +
		       exception.getSystemId() + "\n" +
		       " Message: " +
		       exception.getMessage());
    throw new SAXException("Error encountered");
  }
 
  /**
   * This will report a fatal error that has occurred; this indicates
   * that a rule has been broken that makes continued parsing either
   * impossible or an almost certain waste of time.
   *
   * @param exception <code>SAXParseException</code> that occurred.
   * @throws <code>SAXException</code> when things go wrong
   */
  public void fatalError(SAXParseException exception)
    throws SAXException {
 
    System.out.println("**Parsing Fatal Error**\n" +
		       " Line: " +
		       exception.getLineNumber() + "\n" +
		       " URI: " +
		       exception.getSystemId() + "\n" +
		       " Message: " +
		       exception.getMessage());
    throw new SAXException("Fatal Error encountered");
  }
  
}                                                                    
