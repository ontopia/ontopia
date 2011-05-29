
package net.ontopia.topicmaps.schema.core;

/**
 * PUBLIC: Schema writers can write object structures representing
 * schemas out to an implicitly specified location in some schema
 * language syntax.
 */
public interface SchemaWriterIF {

  /**
   * PUBLIC: Writes the schema.
   * @exception java.io.IOException Thrown if there are problems writing
   *                                to the specified location.
   */
  public void write(SchemaIF schema) throws java.io.IOException;
  
}





