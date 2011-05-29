
package net.ontopia.topicmaps.webed.impl.actions.topicmap;

import net.ontopia.topicmaps.schema.impl.osl.OSLSchema;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.OSLSchemaAwareIF;

/**
 * PUBLIC: Base action class which is used by actions on the topic
 * map level (for example creation of new topic or association objects).
 */
public abstract class AbstractTopicMapAction 
  implements OSLSchemaAwareIF, ActionIF {
  
  /**
   * The schema object which may be available (optionally).
   * TODO: Separate this attribute.
   */
  protected OSLSchema schema;
  
  // ------------------------------------------------------------
  // implementation of interface ActionSchemaIF
  // ------------------------------------------------------------
    
  public OSLSchema getSchema() {
    return schema;
  }

  public void setSchema(OSLSchema schema) {
    this.schema = schema;
  }

}
