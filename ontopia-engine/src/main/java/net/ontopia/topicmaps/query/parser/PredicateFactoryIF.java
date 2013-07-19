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

import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Implemented by classes which can create PredicateIF objects.
 */
public interface PredicateFactoryIF {

  /**
   * INTERNAL: Looks up a built-in predicate by its name.
   */
  public PredicateIF createPredicate(String name);

  /**
   * INTERNAL: Creates a rule predicate for the parsed rule.
   */
  public PredicateIF createPredicate(ParsedRule rule);

  /**
   * INTERNAL: Creates a dynamic predicate for the given topic; either
   * an association predicate or an occurrence predicate, depending on
   * the value of the <tt>assoc</tt> parameter.
   */
  public PredicateIF createPredicate(TopicIF type, boolean assoc);

  /**
   * INTERNAL: Creates a module instance for the specified URI. If the
   * predicate factory does not recognize the URI null is returned.
   */
  public ModuleIF createModule(String uri);

  /**
   * INTERNAL: Used to find out if the given predicate name is the
   * name of a built-in predicate. Needed for error checking, see bug
   * #1082.
   */
  public boolean isBuiltInPredicate(String name);
}
