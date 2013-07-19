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

/**
 * PUBLIC: Abstract SearcherIF convenience superclass used to get the
 * default implementation of the four set methods. Subclassing this
 * class ensures better forward compatibility.<p>
 */
public abstract class AbstractSearcher implements SearcherIF {

  protected String moduleURI;
  protected String predicateName;
  protected TopicMapIF topicmap;
  protected Map parameters;

  // -- default setter implementations
  
  public void setModuleURI(String moduleURI) {
    this.moduleURI = moduleURI;
  }
  
  public void setPredicateName(String predicateName) {
    this.predicateName = predicateName;
  }
  
  public void setTopicMap(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }

  public void setParameters(Map parameters) {
    this.parameters = parameters;
  }
  
}
