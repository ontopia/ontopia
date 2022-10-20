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
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.topicmaps.impl.rdbms.Occurrence;

/**
 * INTERNAL: Implements dynamic occurrence predicates.
 */
public class DynamicOccurrencePredicate
  extends net.ontopia.topicmaps.query.impl.basic.DynamicOccurrencePredicate
  implements JDOPredicateIF {

  public DynamicOccurrencePredicate(TopicMapIF topicmap, LocatorIF base, TopicIF type) {
    super(topicmap, base, type);
  }

  // --- JDOPredicateIF implementation

  @Override
  public boolean isRecursive() {
    return false;
  }

  @Override
  public void prescan(QueryBuilder builder, List arguments) {
    // variable as second argument is an unsupported variabel
    if (arguments.get(1) instanceof Variable) {
      builder.addUnsupportedVariable((Variable)arguments.get(1));
    }
  }

  @Override
  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {

    // Interpret arguments
    Object[] args = arguments.toArray();
    
    // TOLOG: occtype(topic, value)
    JDOValueIF jv_occurs = builder.createJDOVariable("O", Occurrence.class);
      
    JDOValueIF jv_otype = builder.createJDOValue(type);
    JDOValueIF jv_topic = builder.createJDOValue(args[0]);
    JDOValueIF jv_value = builder.createJDOValue(args[1]);
      
    // JDOQL: O.type = OT
    expressions.add(new JDOEquals(new JDOField(jv_occurs, "type"), jv_otype));
      
    // JDOQL: O.topic = T
    expressions.add(new JDOEquals(new JDOField(jv_occurs, "topic"), jv_topic));
      
    // JDOQL: (O.value = V)
    
    expressions.add(new JDOEquals(new JDOField(jv_occurs, "value"), jv_value));
    
    // JDOQL: O.topicmap = TOPICMAP
    expressions.add(new JDOEquals(new JDOField(jv_occurs, "topicmap"),
                                  new JDOObject(topicmap)));    
    return true;
  }

  
}
