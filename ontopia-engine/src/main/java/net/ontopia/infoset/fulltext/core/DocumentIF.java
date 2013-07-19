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

import java.util.Collection;

/**
 * INTERNAL: Represents an indexable unit of information. A document
 * contains named fields which can have values of the types String or
 * Reader.<p>
 */

public interface DocumentIF {
  
  /**
   * INTERNAL: Returns the field with the specified name.
   */
  public FieldIF getField(String name);
  
  /**
   * INTERNAL: Returns all the fields of this document.
   *
   * @return A collection of FieldIF objects.
   */
  public Collection<FieldIF> getFields();

  /**
   * INTERNAL: Adds the given field to the document.
   */
  public void addField(FieldIF field);

  /**
   * INTERNAL: Removes the given field from the document.
   */
  public void removeField(FieldIF field);
  
}
