/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.xml;

import org.slf4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * INTERNAL: SAX2 error handler implementation that uses slf4j to log
 * warnings, errors and fatal errors.</p>
 *
 * Note: You can set the default for ignoring namespace related error
 * messages by setting the boolean system property
 * 'net.ontopia.xml.Slf4jSaxErrorHandler.ignoreNamespaceErrors'. The
 * default can later be overriden by calling the
 * setIgnoreNamespaceError(boolean) method. The system property only
 * has effect when the instance is being creates.<p>
 */
public class Slf4jSaxErrorHandler implements ErrorHandler {
  
  protected Logger log;
  protected boolean ignoreNamespaceErrors;
  
  public Slf4jSaxErrorHandler(Logger log) {
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
      this.ignoreNamespaceErrors = Boolean.parseBoolean(propval);
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
  
  @Override
  public void warning(SAXParseException e) {
    log.warn(e.getMessage() + " " + getExceptionLocationInfo(e));
    // throw e;
  }
  
  @Override
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

  @Override
  public void fatalError(SAXParseException e) throws SAXParseException {
    // for Xerces-2
    if (!(   e.getMessage().startsWith("The prefix ")
          && e.getMessage().endsWith(" is not bound.")
          && ignoreNamespaceErrors) ) {
      log.error("FATAL: " + e.getMessage() + " " + getExceptionLocationInfo(e));
    }
    // throw e;
  }
  
}
