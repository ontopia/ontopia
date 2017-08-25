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
 * INTERNAL: Implements the 'base-locator(address)' predicate.
 */
public class BaseLocatorPredicate
  extends net.ontopia.topicmaps.query.impl.basic.BaseLocatorPredicate
  implements JDOPredicateIF {

  public BaseLocatorPredicate(TopicMapIF topicmap) {
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

    return false;

    //! // Interpret arguments
    //! Object[] args = arguments.toArray();
    //! 
    //! LocatorIF loc = topicmap.getStore().getBaseAddress();
    //! String baseloc = (loc == null ? null : loc.getAddress());
    //! 
    //! JDOValueIF jv_address = builder.createJDOValue(args[0]);
    //! JDOValueIF jv_baseloc = builder.createJDOValue(baseloc);
    //! 
    //! // JDOQL: A = tm.getStore().getBaseAddress()
    //! expressions.add(new JDOEquals(jv_address, jv_baseloc));
    //! 
    //! return true;
  }
  
}
