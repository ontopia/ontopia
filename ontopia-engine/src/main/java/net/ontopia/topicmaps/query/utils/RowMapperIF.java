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

package net.ontopia.topicmaps.query.utils;

import net.ontopia.topicmaps.query.core.QueryResultIF;

/**
 * EXPERIMENTAL: Interface to be implemented by row mappers used by
 * the queryForList method on QueryWrapper. 
 * @since 3.4.4
 */
public interface RowMapperIF<T> {

  /**
   * EXPERIMENTAL: This method is called once for each row in query
   * results, and the returned object is added to the list returned by
   * queryForList.
   * @param result the query result object
   * @param rowno the number of this row in the current query result
   * (in zero-based counting)
   * @return an object to put into the query result list
   */
  public T mapRow(QueryResultIF result, int rowno);
  
}
