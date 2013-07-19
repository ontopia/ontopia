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

import net.ontopia.topicmaps.core.*;

/**
 * PUBLIC: Schema validators can validate topic map objects against a
 * schema. Violations of the schema are reported to a separate error
 * handler object.
 */
public interface SchemaValidatorIF {

  /**
   * PUBLIC: Validates a topic against the schema.
   */
  public void validate(TopicIF topic)
    throws SchemaViolationException;

  /**
   * PUBLIC: Validates a topic map against the schema. The
   * startValidation and endValidation methods of the
   * ValidationHandlerIF interface are called before and after
   * validation.
   */
  public void validate(TopicMapIF topicmap)
    throws SchemaViolationException;

  /**
   * PUBLIC: Validates an association against the schema.
   */
  public void validate(AssociationIF association)
    throws SchemaViolationException;

  /**
   * PUBLIC: Sets the validation handler that violations of the
   * schema will be reported to.
   */
  public void setValidationHandler(ValidationHandlerIF handler);

  /**
   * PUBLIC: Returns the validation handler that violations are
   * currently reported to.
   */
  public ValidationHandlerIF getValidationHandler();
  
}





