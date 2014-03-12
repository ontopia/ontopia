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

package net.ontopia.topicmaps.schema.core;

import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * PUBLIC: This exception is thrown when topic maps violate their
 * schemas.
 */
public class SchemaViolationException extends Exception {
  protected TMObjectIF container;
  protected Object offender;
  protected ConstraintIF constraint;

  /**
   * Creates new exception.
   * @param message A message describing the nature of the violation.
   * @param offender The object that violated the schema.
   * @param container The container of the offending object.
   * @param constraint The schema constraint that was violated.
   */   
  public SchemaViolationException(String message,
                                  TMObjectIF container,
                                  Object offender,
                                  ConstraintIF constraint) {
    super(message);
    this.container = container;
    this.offender = offender;
    this.constraint = constraint;
  }

  /**
   * PUBLIC: Returns the owner of the offending object.
   */
  public TMObjectIF getContainer() {
    return container;
  }

  /**
   * PUBLIC: Returns the object that violated the schema. May be null.
   */
  public Object getOffender() {
    return offender;
  }

  /**
   * PUBLIC: Returns the constraint that was violated. May be null.
   */
  public ConstraintIF getConstraint() {
    return constraint;
  }
  
}





