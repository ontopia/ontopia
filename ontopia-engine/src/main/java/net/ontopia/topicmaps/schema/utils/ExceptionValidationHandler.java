
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

  public void violation(String message, TMObjectIF container, Object offender,
                        ConstraintIF constraint)
    throws SchemaViolationException {
    
    throw new SchemaViolationException(message, container, offender,
                                       constraint);
  }

  public void startValidation() {
  }
    
  public void endValidation() {
  }
}





