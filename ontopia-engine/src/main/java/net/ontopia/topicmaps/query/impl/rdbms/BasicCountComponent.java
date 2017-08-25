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
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * INTERNAL: Query component that is used to perform the <i>count</i>
 * operation for QueryMatches instances. The implementation returns
 * the result of a call to impl.basic.QueryProcessor.count(TologQuery,
 * QueryMatches).
 */

public class BasicCountComponent implements QueryComponentIF {

  protected TologQuery query;
  protected net.ontopia.topicmaps.query.impl.basic.QueryProcessor qproc;
  
  public BasicCountComponent(TologQuery query,
                             net.ontopia.topicmaps.query.impl.basic.QueryProcessor qproc) {
    this.query = query;
    this.qproc = qproc;
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Map arguments)
    throws InvalidQueryException {
    return qproc.count(query, matches);
  }
  
}
