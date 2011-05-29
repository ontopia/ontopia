
package net.ontopia.topicmaps.webed.core;

import net.ontopia.topicmaps.schema.impl.osl.OSLSchema;

/**
 * INTERNAL: The interface is implemented by actions which use schema
 * information to perform their tasks. If an action implements this
 * interface the web editor framework will pass the schema to it, if
 * there is one.
 */
public interface OSLSchemaAwareIF extends ActionIF {

  /**
   * INTERNAL: Gets schema object. 
   */
  public OSLSchema getSchema();

  /**
   * INTERNAL: Sets the schema object.
   */
  public void setSchema(OSLSchema schema);

}
