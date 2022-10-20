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

package net.ontopia.topicmaps.query.impl.basic;

import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.parser.TologOptions;
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * INTERNAL: Object used to hold the global query execution context;
 * that is, the context beginning with the start of the execution of a
 * query and ending with the completion of its execution. Different
 * queries, and different executions of the same query, have different
 * contexts.
 */
public class QueryContext {
  private TopicMapIF topicmap;
  private TologQuery query; // null inside rule predicates, since variable type
                            // information is entirely different there
  private Map arguments;    // parameter values
  private TologOptions options;
  
  public QueryContext(TopicMapIF topicmap, TologQuery query, Map arguments,
                      TologOptions options) {
    this.topicmap = topicmap;
    this.query = query;
    this.arguments = arguments;
    this.options = options;
  }

  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  public Object[] getVariableTypes(String varname) {
    if (query == null) {
      return null;
    } else {
      return (Object[]) query.getVariableTypes().get(varname);
    }
  }

  public Object getParameterValue(String paramname) {
    return arguments.get(paramname);
  }

  public Map getParameters() {
    return arguments;
  }

  public TologOptions getTologOptions() {
    return options;
  }

  public TologQuery getQuery() {
    return query;
  }
}
