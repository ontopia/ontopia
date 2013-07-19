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

import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.parser.AbstractClause;
import net.ontopia.topicmaps.query.parser.OrClause;

/**
 * INTERNAL: Used for testing and timing of queries.
 */
public interface QueryTraceListenerIF {

  public void startQuery();

  public void endQuery();
  
  public void enter(BasicPredicateIF predicate, AbstractClause clause, 
                    QueryMatches input);

  public void enter(OrClause clause, QueryMatches input);

  public void enter(List branch);
  
  public void leave(QueryMatches result);

  public void leave(List branch);

  public void enterOrderBy();

  public void leaveOrderBy();
  
  public void enterSelect(QueryMatches result);

  public void leaveSelect(QueryMatches result);

  public void trace(String message);
  
}
