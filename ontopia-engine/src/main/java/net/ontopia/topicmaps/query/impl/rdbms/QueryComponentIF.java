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

import java.util.Map;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: Represents a tolog query component. A tolog query is
 * sometimes split into smaller component that performs operations on
 * an existing query matches (result sets).<p>
 *
 * The result of processing a component is another QueryMatches
 * instance that can be used as the input of another query components,
 * or be the result of the entire query.<p>
 */
public interface QueryComponentIF {

  /**
   * INTERNAL: Processes the specified QueryMatches instance and
   * produces a new QueryMatches instance based on information in the
   * input instance.
   */
  QueryMatches satisfy(QueryMatches matches, Map arguments)
    throws InvalidQueryException;
  
}
