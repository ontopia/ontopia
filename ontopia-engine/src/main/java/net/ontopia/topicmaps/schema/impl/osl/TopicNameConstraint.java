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

import java.util.Collection;
import java.util.ArrayList;

/**
 * INTERNAL: Represents a constraint on a base name within a topic class
 * definition.
 */
public class TopicNameConstraint extends AbstractScopedCardinalityConstraint {
  protected TopicConstraintCollection parent;
  protected Collection variants;
  
  /**
   * INTERNAL: Creates a base name constraint.
   */
  public TopicNameConstraint(TopicConstraintCollection parent) {
    this.parent = parent;
    this.variants = new ArrayList();
  }

  /**
   * INTERNAL: Returns the constraints on the variants of this base name.
   * @return A collection of VariantConstraint objects.
   */
  public Collection getVariantConstraints() {
    return variants;
  }

  /**
   * INTERNAL: Removes the variant constraint from this base name. If
   * the variant constraint is not already registered with this base
   * name constraint the call is ignored.
   */
  public void removeVariantConstraint(VariantConstraint variant) {
    variants.remove(variant);
  }

  /**
   * INTERNAL: Adds the variant constraint to this base name. If the
   * variant constraint is already registered with this base name
   * constraint the call is ignored.
   */
  public void addVariantConstraint(VariantConstraint variant) {
    variants.add(variant);
  }
  
}
