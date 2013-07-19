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
