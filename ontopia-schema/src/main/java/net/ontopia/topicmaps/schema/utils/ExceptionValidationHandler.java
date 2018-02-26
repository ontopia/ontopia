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

package net.ontopia.topicmaps.schema.utils;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.schema.core.SchemaViolationException;
import net.ontopia.topicmaps.schema.core.ValidationHandlerIF;
import net.ontopia.topicmaps.schema.core.ConstraintIF;

/**
 * PUBLIC: Validation handler implementation which throws an exception
 * on every schema violation.
 */
public class ExceptionValidationHandler implements ValidationHandlerIF {

  @Override
  public void violation(String message, TMObjectIF container, Object offender,
                        ConstraintIF constraint)
    throws SchemaViolationException {
    
    throw new SchemaViolationException(message, container, offender,
                                       constraint);
  }

  @Override
  public void startValidation() {
    // no-op
  }
    
  @Override
  public void endValidation() {
    // no-op
  }
}





