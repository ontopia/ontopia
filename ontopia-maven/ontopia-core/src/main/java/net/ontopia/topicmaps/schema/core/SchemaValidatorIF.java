// $Id: SchemaValidatorIF.java,v 1.3 2002/05/29 13:38:43 hca Exp $

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





