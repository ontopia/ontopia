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

package net.ontopia.topicmaps.query.spi;

import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Abstract predicate class that works as a common
 * superclass for the real predicate classes. Don't subclass this one
 * directly; instead, subclass one of its subclasses, FilterPredicate
 * or ProcessPredicated, depending on what kind of predicate you
 * want.
 */
public abstract class JavaPredicate implements BasicPredicateIF {
  private String moduleURI;
  private String predicateName;
  private TopicMapIF topicmap;
  private Map parameters;
  
  @Override
  public String getName() {
    return predicateName;
  }

  @Override
  public String getSignature() {
    return ".?!+";
  }

  @Override
  public int getCost(boolean[] boundparams) {
    for (int i=0; i < boundparams.length; i++) {
      if (!boundparams[i]) {
        return PredicateDrivenCostEstimator.INFINITE_RESULT;
      }
    }

    return PredicateDrivenCostEstimator.FILTER_RESULT;
  }
  
  // -- default setter implementations

  public String getModuleURI() {
    return moduleURI;
  }
  
  public void setModuleURI(String moduleURI) {
    this.moduleURI = moduleURI;
  }

  public String getPredicateName() {
    return predicateName;
  }
  
  public void setPredicateName(String predicateName) {
    this.predicateName = predicateName;
  }

  public TopicMapIF getTopicMap() {
    return topicmap;
  }
  
  public void setTopicMap(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }

  public Map getParameters() {
    return parameters;
  }
  
  public void setParameters(Map parameters) {
    this.parameters = parameters;
  }

  /**
   * INTERNAL: Internal machinery.
   */
  @Override
  public abstract QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException;
  
}
