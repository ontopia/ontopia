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

import java.util.Collection;
import java.util.List;

/**
 * INTERNAL: Common superclass for or clauses and other kinds of clauses.
 */
public abstract class AbstractClause {

  /**
   * INTERNAL: Returns all the variables bound by this clause when it
   * is satisfied.
   */
  public abstract Collection getAllVariables();

  /**
   * INTERNAL: Returns all the literals used by this clause as
   * parameters. (Literals in the second half of pair arguments are
   * ignored.)
   */
  public abstract Collection getAllLiterals();

  /**
   * INTERNAL: Returns the arguments of this clause. For OrClause this
   * is the list of all arguments to all the subclauses in the OrClause.
   * Likewise for NotClause.
   */
  public abstract List getArguments();
  
}
