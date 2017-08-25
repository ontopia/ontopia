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

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'topic-name' predicate.
 */
public class TopicNamePredicate
  extends net.ontopia.topicmaps.query.impl.basic.TopicNamePredicate
  implements JDOPredicateIF {

  public TopicNamePredicate(TopicMapIF topicmap) {
    super(topicmap);
  }

  // --- JDOPredicateIF implementation

  public boolean isRecursive() {
    return false;
  }

  public void prescan(QueryBuilder builder, List arguments) {
    // no-op
  }

  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {
    
    // Interpret arguments
    Object[] args = arguments.toArray();
    
    // TOLOG: topic-name(TOPIC, NAME)
    JDOValueIF jv_topic = builder.createJDOValue(args[0]);
    JDOValueIF jv_name = builder.createJDOValue(args[1]);
    
    // JDOQL: N.topic = T
    expressions.add(new JDOEquals(new JDOField(jv_name, "topic"), jv_topic));
    //! expressions.add(new JDOContains(new JDOField(jv_topic, "basenames"), jv_name));

    expressions.add(new JDOEquals(new JDOField(jv_name, "topicmap"),
                                  new JDOObject(topicmap)));
    
    return true;
  }
  
}
