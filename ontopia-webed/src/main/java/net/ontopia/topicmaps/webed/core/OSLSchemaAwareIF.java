/*
 * #!
 * Ontopia Webed
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
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
