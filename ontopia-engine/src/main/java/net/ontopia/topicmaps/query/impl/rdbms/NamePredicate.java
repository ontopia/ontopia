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

import java.util.List;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: The implementation of the 'name(topic, name-string)' predicate.
 */
public class NamePredicate
  extends net.ontopia.topicmaps.query.impl.basic.NamePredicate
  implements JDOPredicateIF {

  protected TopicMapIF topicmap;

  public NamePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }

  // --- JDOPredicateIF implementation

  @Override
  public boolean isRecursive() {
    return false;
  }

  @Override
  public void prescan(QueryBuilder builder, List arguments) {
    // no-op
  }

  @Override
  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {

    // TODO: This predicate is not yet supported natively. Delegate to
    // basic predicate.
    return false;
  }
  
}
