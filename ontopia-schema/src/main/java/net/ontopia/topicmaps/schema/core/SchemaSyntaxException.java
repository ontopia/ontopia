/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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
