
package net.ontopia.topicmaps.schema.core;

import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * PUBLIC: Schema validator objects report errors through this interface.
 */
public interface ValidationHandlerIF {

  /**
   * PUBLIC: Called before the validation of an entire topic map
   * begins.  When single topics or associations are validated this
   * method is not called.
   */   
  public void startValidation();

  /**
   * PUBLIC: Called when violations of the schema are discovered.
   * @param message A message describing the nature of the violation.
   * @param offender The object that violated the schema.
   * @param container The container of the offending object.
   * @param constraint The constraint that was violated.
   * @exception SchemaViolationException Implementations may throw this
   *            exception if they wish to halt validation.
   */
  public void violation(String message, TMObjectIF container, Object offender,
                        ConstraintIF constraint)
    throws SchemaViolationException;

  /**
   * PUBLIC: Called after the validation of an entire topic map. When
   * single topics or associations are validated this method is not
   * called.
   */
  public void endValidation();
  
}
