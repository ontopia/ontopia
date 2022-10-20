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
import java.util.Objects;
import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'reifies(reifier, reified)' predicate.
 */
public class ReifiesPredicate
  extends net.ontopia.topicmaps.query.impl.basic.ReifiesPredicate
  implements JDOPredicateIF {

  public ReifiesPredicate(TopicMapIF topicmap) {
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
    
    // TOLOG: reifies(REIFIER, REIFIES)
    JDOValueIF jv_reifier = builder.createJDOValue(args[0]);

    if (args[0] instanceof TopicIF && args[1] instanceof TMObjectIF) {
            
      // Do direct predicate evaluation
			ReifiableIF reified = ((TopicIF)args[0]).getReified();
      if (Objects.equals(reified, args[1])) {
        expressions.add(JDOBoolean.TRUE);
      } else {
        expressions.add(JDOBoolean.FALSE);
      }

    } else {

      if (builder.isArgumentOfType(args[1], ReifiableIF.class)) {
				JDOValueIF jv_reified = builder.createJDOValue(args[1]);          
      
				expressions.add(new JDOEquals(new JDOField(jv_reified, "reifier"), jv_reifier));

				// JDOQL: REIFIER.topicmap = TOPICMAP
				expressions.add(new JDOEquals(new JDOField(jv_reifier, "topicmap"),
																			new JDOObject(topicmap)));
			} else {
				return false;
			}
    }
    
    return true;
  }
  
}
