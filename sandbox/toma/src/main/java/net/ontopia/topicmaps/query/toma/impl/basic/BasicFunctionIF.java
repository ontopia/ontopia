/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Derived interface for functions that are being evaluated
 * by the {@link BasicQueryProcessor}.
 */
public interface BasicFunctionIF extends BasicExpressionIF {
  
  /**
   * Perform evaluation of the function on a specific input value.
   * @param obj the input value.
   * @return the result of the execution.
   */
  public String evaluate(Object obj) throws InvalidQueryException;

  /**
   * Aggregates a collection of values together, based on the definition of the
   * actual function.
   * 
   * @param values the values to be aggregated.
   * @return the aggregated value.
   * @throws InvalidQueryException if the function is not capable to aggregate
   *           values.
   */
  public Object aggregate(Collection<?> values) throws InvalidQueryException;
}
