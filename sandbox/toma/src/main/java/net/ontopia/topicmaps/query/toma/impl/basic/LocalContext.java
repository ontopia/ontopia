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

import java.util.HashMap;
import java.util.List;

import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: The current context when evaluating a query. This class contains a
 * reference map, which maps variables to {@link ResultSet}'s that bound this
 * variable.
 */
public class LocalContext implements Cloneable {

  private TopicMapIF topicmap;
  private HashMap<String, ResultSet> resultsets;

  public LocalContext(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    this.resultsets = new HashMap<String, ResultSet>();
  }

  /**
   * Returns the {@link TopicMapIF} that is used in this query.
   * 
   * @return the topic map of the query.
   */
  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  /**
   * Adds a new {@link ResultSet} to this {@link LocalContext}.
   * All bound variables of the {@link ResultSet} will be extracted,
   * and their reference will be updated.
   * 
   * @param resultset the {@link ResultSet} to be added.
   */
  public void addResultSet(ResultSet resultset) {
    List<String> variables = resultset.getBoundVariables();
    for (String var : variables) {
      resultsets.put(var, resultset);
    }
  }

  /**
   * Returns the {@link ResultSet} that contains the specified variable; if
   * there is no {@link ResultSet} that is bound by this variable, null will be
   * returned.
   * 
   * @param boundVariable the variable to look for.
   * @return the {@link ResultSet} containing this variable, or null.
   */
  public ResultSet getResultSet(String boundVariable) {
    return resultsets.get(boundVariable.toUpperCase());
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Object clone() throws CloneNotSupportedException {
    LocalContext c = new LocalContext(this.topicmap);
    c.resultsets = (HashMap<String, ResultSet>) this.resultsets.clone();
    return c;
  }
}
