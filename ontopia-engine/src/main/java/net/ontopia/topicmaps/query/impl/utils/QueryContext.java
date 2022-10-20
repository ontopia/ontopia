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

import java.util.Map;

import net.ontopia.topicmaps.query.parser.ParsedRule;
import net.ontopia.topicmaps.query.parser.TologQuery;
import net.ontopia.topicmaps.query.parser.TologOptions;

/**
 * INTERNAL: Used during traversal of queries to represent the context
 * at any given point in the query.
 */
public class QueryContext {
  private int nesting_level;
  private ParsedRule rule;  // not set outside rules
  private TologQuery query;

  // note that query may be null (in DeclContext, for example)
  public QueryContext(TologQuery query, ParsedRule rule) {
    this.query = query;
    this.rule = rule;
  }
  
  public QueryContext(TologQuery query) {
    this.query = query;
  }

  /**
   * Returns value of boolean option.
   */
  public boolean getBooleanOption(String name) {
    TologOptions options;
    if (query != null) {
      options = query.getOptions();
    } else {
      options = rule.getOptions();
    }

    return options.getBooleanValue(name);
  }

  /**
   * Returns the name of the rule we are traversing. If we are
   * traversing a query (that is, not a rule at all) null is returned.
   */
  public String getRuleName() {
    if (rule == null) {
      return null;
    } else {
      return rule.getName();
    }
  }
  
  /**
   * Returns the clause list nesting level we are at. The top level of
   * a query or rule is 1.
   */
  public int getNestingLevel() {
    return nesting_level;
  }

  public Map getVariableTypes() {
    if (rule != null) {
      return rule.getVariableTypes();
    } else {
      return query.getVariableTypes();
    }
  }

  public Map getParameterTypes() {
    if (rule != null) {
      return rule.getParameterTypes();
    } else {
      return query.getParameterTypes();
    }
  }  
  
  public void enterClauseList() {
    nesting_level++;
  }

  public void leaveClauseList() {
    nesting_level--;
  }
  
}
