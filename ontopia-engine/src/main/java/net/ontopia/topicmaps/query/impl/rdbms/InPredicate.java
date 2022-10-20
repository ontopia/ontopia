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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ontopia.persistence.query.jdo.JDOCollection;
import net.ontopia.persistence.query.jdo.JDOContains;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: The implementation of the 'in(var, e1, ..., eN)' predicate.
 */
public class InPredicate
  extends net.ontopia.topicmaps.query.impl.basic.InPredicate
  implements JDOPredicateIF {

  protected TopicMapIF topicmap;

  public InPredicate(TopicMapIF topicmap) {
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
    
    // Interpret arguments
    Object[] args = arguments.toArray();

    // FIXME: Support other value types in all argument positions.
    
    JDOValueIF jv_var = builder.createJDOValue(args[0]);
    
    Collection values = new ArrayList(args.length - 1);
    for (int i=1; i < args.length; i++) {
      values.add(args[i]);
    }

    Class vartype = builder.getArgumentType(args[0]);
    if (vartype == null) {
      throw new InvalidQueryException("Argument " + args[0] + " of unknown type.");
    }

    // TODO: We should really support primitive types here

    if (TMObjectIF.class.isAssignableFrom(vartype)) {    
      // JDOQL: VALUES.containsAll(V)
      expressions.add(new JDOContains(new JDOCollection(values, vartype), jv_var));
      
      // JDOQL: V.topicmap = TOPICMAP
      expressions.add(new JDOEquals(new JDOField(jv_var, "topicmap"),
                                    new JDOObject(topicmap)));
      return true;
    }
    return false;
  }
  
}
