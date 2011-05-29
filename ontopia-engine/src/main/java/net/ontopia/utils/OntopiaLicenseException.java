
package net.ontopia.utils;

import java.io.*;

/**
 * INTERNAL: An exception class that is thrown when licence related
 * issues occur.</p>
 *
 * Extends OntopiaRuntimeException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 */

public final class OntopiaLicenseException extends OntopiaRuntimeException {

  public OntopiaLicenseException(String message) {
    super(message);
  }

  public Throwable getException() {
    return null; // hidden
  }
  
  public void printStackTrace() {
    System.err.println(toString() + " [no traceback]"); // hidden
  }
  
  public void printStackTrace(PrintStream s) {
    s.println(toString() + " [no traceback]"); // hidden
  }
  
  public void printStackTrace(PrintWriter s) {
    s.println(toString() + " [no traceback]"); // hidden
  }
  
}




