// $Id: SchemaReaderIF.java,v 1.5 2002/05/29 13:38:43 hca Exp $

package net.ontopia.topicmaps.schema.core;

/**
 * PUBLIC: Schema readers can read instances of a topic map schema
 * from some implicitly specified source and return object structures
 * representing the schema.
 */
public interface SchemaReaderIF {

  /**
   * PUBLIC: Reads the schema from the data source and returns the
   * object structure.
   * @exception java.io.IOException Thrown if there are problems with
   *                                the data source while reading the schema.
   * @exception org.xml.sax.SAXException Thrown if the schema is not
   *                                     well-formed XML.
   * @exception SchemaSyntaxException Thrown if the schema violates the
   *                                  schema language syntax.
   */
  public SchemaIF read()
    throws java.io.IOException, SchemaSyntaxException;
  
}





