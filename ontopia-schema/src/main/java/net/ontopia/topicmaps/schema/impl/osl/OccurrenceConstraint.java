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

package net.ontopia.topicmaps.schema.impl.osl;

/**
 * INTERNAL: Represents a constraint on the occurrences of a class of topics.
 */
public class OccurrenceConstraint
                            extends AbstractScopedTypedCardinalityConstraint {
  /**
   * INTERNAL: Used to indicate that occurrences can only be internal, that
   * is be represented using the &lt;resourceData> element in XTM 1.0.
   */
  public static final int RESOURCE_INTERNAL = 0;
  /**
   * INTERNAL: Used to indicate that occurrences can only be external, that
   * is be represented using the URI of the resource.
   */
  public static final int RESOURCE_EXTERNAL = 1;
  /**
   * INTERNAL: Used to indicate that occurrences can be either internal or
   * external.
   */
  public static final int RESOURCE_EITHER   = 2;
  
  protected TopicConstraintCollection parent;
  protected int internal;
  
  /**
   * INTERNAL: Creates a new occurrence constraint in the given
   * collection of topic constraints.
   */
  public OccurrenceConstraint(TopicConstraintCollection parent) {
    this.parent = parent;
    this.internal = RESOURCE_EITHER;
  }

  /**
   * INTERNAL: Used to control whether instances of occurrence type can be
   * internal, external, or both. The allowed values are those of the
   * RESOURCE_* constants.
   */
  public void setInternal(int internal) {
    this.internal = internal;
  }

  /**
   * INTERNAL: Returns a value indicating whether instances of this
   * occurrence type can be internal, external, or both. The allowed
   * values are those of the RESOURCE_* constants.
   */
  public int getInternal() {
    return internal;
  }
  
  // --- Object methods
  
}
