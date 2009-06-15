
// $Id: Log4jSaxErrorHandler.java,v 1.15 2003/03/16 14:22:33 larsga Exp $

package net.ontopia.xml;

import org.xml.sax.*;
import org.apache.log4j.*;
import net.ontopia.utils.PropertyUtils;

/**
 * INTERNAL: SAX2 error handler implementation that uses log4j to log
 * warnings, errors and fatal errors.</p>
 *
 * Note: You can set the default for ignoring namespace related error
 * messages by setting the boolean system property
 * 'net.ontopia.xml.Log4jSaxErrorHandler.ignoreNamespaceErrors'. The
 * default can later be overriden by calling the
 * setIgnoreNamespaceError(boolean) method. The system property only
 * has effect when the instance is being creates.<p>
 */
public class Log4jSaxErrorHandler implements ErrorHandler {
  
  protected Logger log;
  protected boolean ignoreNamespaceErrors;
  
  public Log4jSaxErrorHandler(Logger log) {
    this.log = log;
    this.ignoreNamespaceErrors = false;

    // Check to see if system property overrides the property default
    String propval = null;
    try {
      propval = System.getProperty(getClass().getName() + ".ignoreNamespaceErrors");
    } catch (SecurityException e) {
      log.warn(e.toString());      
    }
    if (propval != null)
      this.ignoreNamespaceErrors = PropertyUtils.isTrue(propval);
  }

  /**
   * INTERNAL: Sets error logging strategy when namespace is not
   * declared.
   */
  public void setIgnoreNamespaceError(boolean ignore) {
    this.ignoreNamespaceErrors = ignore;
  }
  
  protected String getExceptionLocationInfo(SAXParseException e) {
    return "(resource '" + e.getSystemId() + "' line " + e.getLineNumber() +
      " col " + e.getColumnNumber() + ")";
  }
  
  public void warning(SAXParseException e) {
    log.warn(e.getMessage() + " " + getExceptionLocationInfo(e));
    // throw e;
  }
  
  public void error(SAXParseException e) {
    // this is a little bit fragile, but ...
    if (!((   e.getMessage().startsWith("Undeclared prefix")
           || e.getMessage().startsWith("undeclared element prefix in: ")
           || e.getMessage().startsWith("org.apache.crimson.parser/P-084 "))
          && ignoreNamespaceErrors) ) {
      log.error(e.getMessage() + " " + getExceptionLocationInfo(e));
    }
    // throw e;
  }

  public void fatalError(SAXParseException e) throws SAXParseException {
    // for Xerces-2
    if (!(   e.getMessage().startsWith("The prefix ")
          && e.getMessage().endsWith(" is not bound.")
          && ignoreNamespaceErrors) ) {
      log.fatal(e.getMessage() + " " + getExceptionLocationInfo(e));
    }
    // throw e;
  }
  
}
