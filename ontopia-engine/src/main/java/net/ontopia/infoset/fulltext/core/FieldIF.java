/*
 * #!
 * Ontopia Engine
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

package net.ontopia.infoset.fulltext.core;

import java.io.Reader;

/**
 * INTERNAL: Represents a name value pair that can be attached to a
 * document.<p>
 */

public interface FieldIF {

  /**
   * INTERNAL: Returns the name of the field.
   */
  String getName();

  /**
   * INTERNAL: Returns the String value of the field. Note that null is
   * returned if the field has a reader set.
   */
  String getValue();

  /**
   * INTERNAL: Returns the Reader value of the field. Note that null is
   * returned if the field has a value set.
   */
  Reader getReader();

  /**
   * INTERNAL: Returns true if the field is to be stored in the index
   * for return with search hits.
   */
  boolean isStored();

  /**
   * INTERNAL: Returns true if the field is to be indexed, so that it
   * may be searched on.
   */
  boolean isIndexed();

  /**
   * INTERNAL: Returns true if the field is to be tokenized prior to
   * indexing.
   */
  boolean isTokenized();
  
}





