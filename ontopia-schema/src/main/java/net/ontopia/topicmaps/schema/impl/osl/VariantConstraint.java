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

package net.ontopia.topicmaps.schema.impl.osl;

/**
 * INTERNAL: Represents a constraint on the allowed variant names of
 * a base name.
 */
public class VariantConstraint extends AbstractScopedCardinalityConstraint {
  protected TopicNameConstraint parent;
  
  /**
   * INTERNAL: Creates a new variant name constraint belonging to the
   * given base name constraint.
   */
  public VariantConstraint(TopicNameConstraint parent) {
    this.parent = parent;
  }

  /**
   * INTERNAL: Returns the base name constraint that is the parent of
   * this constraint.
   */
  public TopicNameConstraint getParent() {
    return parent;
  }
  
}






