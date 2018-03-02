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

import net.ontopia.topicmaps.schema.core.CardinalityConstraintIF;

/**
 * INTERNAL: Common base class for constraints which have cardinality
 * facets.
 */
public abstract class AbstractCardinalityConstraint
                                        implements CardinalityConstraintIF {
  protected int minimum;
  protected int maximum;
  
  public AbstractCardinalityConstraint() {
    this.minimum = 0;
    this.maximum = CardinalityConstraintIF.INFINITY;
  }

  @Override
  public int getMinimum() {
    return minimum;
  }

  @Override
  public int getMaximum() {
    return maximum;
  }

  @Override
  public void setMinimum(int minimum) {
    if (minimum < 0)
      throw new IllegalArgumentException("Cannot set minimum to negative value");
    this.minimum = minimum;
  }

  @Override
  public void setMaximum(int maximum) {
    if (maximum != CardinalityConstraintIF.INFINITY && maximum < 0)
      throw new IllegalArgumentException("Cannot set maximum to negative value");
    this.maximum = maximum;
  }
  
}
