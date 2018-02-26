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

import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.PredicateClause;
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * INTERNAL: Implemented by classes that know how to optimize tolog queries.
 */
public interface QueryOptimizerIF {

  void optimize(TologQuery query, QueryContext context)
    throws InvalidQueryException;
  
  PredicateClause optimize(PredicateClause clause, QueryContext context)
    throws InvalidQueryException;

  List optimize(List clauses, QueryContext context)
    throws InvalidQueryException;
  
}
