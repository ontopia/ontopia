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

package net.ontopia.topicmaps.utils.rdf;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: Thrown when an error occurs during RDF-to-topic maps
 * conversion.
 * @since 2.0
 */
public class RDFMappingException extends OntopiaRuntimeException {
  private String subject;
  private String property;
  
  public RDFMappingException(String msg) {
    super(msg);
  }

  /**
   * PUBLIC: Creates an exception that remembers the subject and the
   * property of the statement that caused the error.
   * @since 2.0.4
   */
  public RDFMappingException(String msg, String subject, String property) {
    super(msg + "\n  Subject: " + subject + "\n  Property: " + property);
    this.subject = subject;
    this.property = property;
  }

  /**
   * PUBLIC: Returns the URI of the subject of the statement that
   * caused this exception, if any, and if known.
   * @since 2.0.4
   */
  public String getSubject() {
    return subject;
  }

  /**
   * PUBLIC: Returns the URI of the property of the statement that
   * caused this exception, if any, and if known.
   * @since 2.0.4
   */
  public String getProperty() {
    return property;
  }
}
