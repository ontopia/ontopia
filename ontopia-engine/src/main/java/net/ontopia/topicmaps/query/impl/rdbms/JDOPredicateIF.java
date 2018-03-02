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

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;

/**
 * INTERNAL: Represents a predicate in the rdbms implementation.
 */
public interface JDOPredicateIF extends BasicPredicateIF {

  /**
   * INTERNAL:
   */
  boolean isRecursive();

  /**
   * INTERNAL: This method will be called before building the
   * query. It is used mainly for analyzing the predicate.
   */
  void prescan(QueryBuilder builder, List arguments);
  
  /**
   * INTERNAL: Registers JDOExpressionsIF for this predicate with the
   * query builder.
   *
   * @return true if predicate was mapped to JDO expression; false if
   * the predicate could not be mapped to an JDO expression.
   */
  boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException;
  
}
