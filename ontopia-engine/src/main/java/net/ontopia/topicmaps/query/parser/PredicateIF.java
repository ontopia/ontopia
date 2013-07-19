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

package net.ontopia.topicmaps.query.parser;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Represents a predicate.
 */
public interface PredicateIF {

  /**
   * INTERNAL: Returns the name of the predicate. This will usually be
   * the name of the predicate within its module ('instance-of', '/=',
   * 'starts-with'), but some virtual predicates cannot be reached
   * from the syntax and may return names that will not parse.
   */
  public String getName();

  /**
   * INTERNAL: Returns a string representing the signature of the
   * predicate. The string consists of a whitespace-separated list of
   * arguments, where each argument is made up of tokens representing
   * the type or cardinality of that argument.</p>
   *
   * <p>The <tt>instance-of</tt> predicate would have a signature of
   * "t t", while <tt>/=</tt> would have ". .", and <tt>in</tt> would
   * have ". .+". For full details, see
   * topicmaps.impl.utils.ArgumentValidator.
   */
  public String getSignature() throws InvalidQueryException;

  /**
   * INTERNAL.
   */
  public int getCost(boolean[] boundparams);
  
}
