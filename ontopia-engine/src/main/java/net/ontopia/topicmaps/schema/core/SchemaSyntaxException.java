
package net.ontopia.topicmaps.schema.core;

import org.xml.sax.Locator;

/**
 * PUBLIC: This exception is thrown when a topic map schema violates
 * the syntax of the schema language it is written in.
 */
public class SchemaSyntaxException extends Exception {
  protected Locator errorloc;

  /**
   * Creates new exception.
   * @param errorloc The location in the XML document where the error
   *                 occurred.
   */   
  public SchemaSyntaxException(String message, Locator errorloc) {
    super(message);
    this.errorloc = errorloc;
  }

  /**
   * PUBLIC: Returns the location of the error.
   */
  public Locator getErrorLocation() {
    return errorloc;
  }
  
}
