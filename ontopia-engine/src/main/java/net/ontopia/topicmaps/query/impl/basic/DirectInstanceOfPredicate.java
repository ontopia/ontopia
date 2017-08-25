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

import java.util.Collection;
import java.util.Collections;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Implements the 'direct-instance-of' predicate using the indexes.
 */
public class DirectInstanceOfPredicate extends AbstractInstanceOfPredicate {
 
  public DirectInstanceOfPredicate(TopicMapIF topicmap) {
    super(topicmap);
  }
  
  public String getName() {
    return "direct-instance-of";
  }

  // --- Data interface implementation

  protected void start() {
    // no-op
  }
  
  protected Collection getClasses(TopicIF instance) {
    return instance.getTypes();
  }

  protected Collection getInstances(TopicIF klass) {
    return index.getTopics(klass);
  }

  protected Collection getTypes() {
    return index.getTopicTypes();
  }

  protected Collection getSupertypes(TopicIF type) {
    return Collections.singleton(type);
  }  
}
