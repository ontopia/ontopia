// $Id: OntopiaUnsupportedException.java,v 1.7 2005/07/12 09:49:21 grove Exp $

package net.ontopia.utils;


/**
 * INTERNAL: Thrown to indicate that the requested operation is not
 * supported.</p>
 *
 * Extends OntopiaRuntimeException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 */

public class OntopiaUnsupportedException extends OntopiaRuntimeException {


  public OntopiaUnsupportedException(Throwable e) {
    super(e);
  }

  public OntopiaUnsupportedException(String message) {
    super(message);
  }

  public OntopiaUnsupportedException(String message, Throwable cause) {
    super(message, cause);
  }
  
}




