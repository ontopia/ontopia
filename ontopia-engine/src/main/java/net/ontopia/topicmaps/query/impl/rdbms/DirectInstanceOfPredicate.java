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

import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOContains;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'direct-instance-of' predicate.
 */
public class DirectInstanceOfPredicate
  extends net.ontopia.topicmaps.query.impl.basic.DirectInstanceOfPredicate
  implements JDOPredicateIF {

  public DirectInstanceOfPredicate(TopicMapIF topicmap) {
    super(topicmap);
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

    // Interpret arguments
    Object[] args = arguments.toArray();

    // TOLOG: direct-instance-of ( INSTANCE, CLASS )
    if (args[0] instanceof TopicIF && args[1] instanceof TopicIF) {

      // Do direct predicate evaluation
      if (((TopicIF)args[0]).getTypes().contains(args[1])) {
        expressions.add(JDOBoolean.TRUE);
      } else {
        expressions.add(JDOBoolean.FALSE);
      }

    } else {                  
      JDOValueIF jv_instance = builder.createJDOValue(args[0]);
      JDOValueIF jv_class = builder.createJDOValue(args[1]);
        
      // JDOQL: INSTANCE.types.contains(CLASS)
      expressions.add(new JDOContains(new JDOField(jv_instance, "types"), jv_class));
        
      // JDOQL: CLASS.topicmap = TOPICMAP
      expressions.add(new JDOEquals(new JDOField(jv_instance, "topicmap"),
                                    new JDOObject(topicmap)));
    }
    
    return true;
  }

}





