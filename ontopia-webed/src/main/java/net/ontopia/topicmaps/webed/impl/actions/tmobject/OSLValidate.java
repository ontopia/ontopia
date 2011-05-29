
package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.schema.core.SchemaValidatorIF;
import net.ontopia.topicmaps.schema.core.SchemaViolationException;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchema;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.core.OSLSchemaAwareIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for deleting a topic map object.
 * @since 3.0
 */
public class OSLValidate implements OSLSchemaAwareIF {
  private OSLSchema schema;
  
  public OSLSchema getSchema() {
    return schema;
  }

  public void setSchema(OSLSchema schema) {
    this.schema = schema;
  }
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    // validate parameters
    ActionSignature paramsType = ActionSignature.getSignature("mta");
    paramsType.validateArguments(params, this);

    // check for schema
    if (schema == null)
      throw new ActionRuntimeException("No schema found", true);

    // validate objects
    SchemaValidatorIF validator = schema.getValidator();
    Iterator it = params.getCollection(0).iterator();
    while (it.hasNext()) {
      Object object = it.next();

      try {
        if (object instanceof TopicMapIF)
          validator.validate((TopicMapIF) object);
        else if (object instanceof TopicIF)
          validator.validate((TopicIF) object);
        else if (object instanceof AssociationIF)
          validator.validate((AssociationIF) object);
        else
          throw new ActionRuntimeException("INTERNAL ERROR: " + object, true);
      } catch (SchemaViolationException e) {
        throw new ActionRuntimeException("Invalid data", e, true);
      }
    }
  }
}
