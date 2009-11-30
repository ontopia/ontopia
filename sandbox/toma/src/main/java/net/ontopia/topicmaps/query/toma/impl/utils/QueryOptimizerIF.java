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
package net.ontopia.topicmaps.query.toma.impl.utils;

import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;

/**
 * PUBLIC: Interface for a query optimizer of TOMA queries.
 */
public interface QueryOptimizerIF {
  /**
   * Optimize an expression.
   * 
   * @param expr the expression to be optimized.
   * @return an optimized version of the expression, or the expression itself,
   *         if no optimization was possible/known.
   */
  public ExpressionIF optimize(ExpressionIF expr);
}
