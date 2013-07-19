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

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Set;
import net.ontopia.topicmaps.query.parser.AbstractClause;

public abstract class CostEstimator {
  /**
   * INTERNAL: Computes the cost of evaluating the given clause
   * in the given context of variable bindings. The cost largely
   * depends on the number of unbound variables in the clause.
   * @param context A set of bound variables.
   * @param clause The clause whose cost we want to compute.
   * @param literalvars Contains the variables representing literals.
   *                    Only an issue in rules.
   * @param rulename The name of the current rule (so we can delay
   *                 recursive evaluation).
   */
  public abstract int computeCost(Set context, AbstractClause clause,
                                  Set literalvars, String rulename);
}
