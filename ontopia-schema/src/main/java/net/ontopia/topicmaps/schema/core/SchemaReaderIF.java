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





