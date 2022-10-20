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
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.Variable;

/**
 * INTERNAL: Implements the '=' predicate.
 */
public class EqualsPredicate
  extends net.ontopia.topicmaps.query.impl.basic.EqualsPredicate
  implements JDOPredicateIF {

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

    // Cannot execute if arguments are variables of type java.lang.Object
    if (args[0] instanceof Variable) {
      Class vt1 = builder.getVariableType(((Variable)args[0]).getName());
      if (Object.class.equals(vt1)) {
        return false;
      }
    }    
    if (args[1] instanceof Variable) {
      Class vt2 = builder.getVariableType(((Variable)args[1]).getName());
      if (Object.class.equals(vt2)) {
        return false;
      }
    }
      
    // TOLOG: $LEFT == $RIGHT
    JDOValueIF jv_left = builder.createJDOValue(args[0]); 
    JDOValueIF jv_right = builder.createJDOValue(args[1]); 

    // JDOQL: LEFT == RIGHT
    expressions.add(new JDOEquals(jv_left, jv_right));
    
    return true;
  }

  
}





