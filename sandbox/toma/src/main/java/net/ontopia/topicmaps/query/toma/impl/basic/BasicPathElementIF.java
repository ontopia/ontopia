/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Specialization of the PathElementIF interface for the
 * BasicQueryProcessor implementation.
 */
public interface BasicPathElementIF {

  /**
   * Evaluate an path element based on the current context and the input value.
   * The result is a collection of values, as one input can generate multiple
   * outputs (e.g. names of a topic).
   * 
   * @param context the current processing context.
   * @param input the input value to be evaluated.
   * @return a Collection of results.
   * @throws InvalidQueryException if the path element could not be evaluated
   *           because of syntactic or semantic error in the query definition.
   */
  public Collection<?> evaluate(LocalContext context, Object input)
      throws InvalidQueryException;

  /**
   * Initialize the resultset layout for the current context.
   * 
   * @param context the current context.
   */
  public void initResultSet(LocalContext context);
  
  /**
   * Get the number of columns this path element will generate in the
   * evaluation.
   * 
   * @return the number of result columns.
   */
  public int getResultSize();
  
  /**
   * Get the names of the result columns this path element will generate.
   *
   * @return an array containing the names of the result columns.
   */
  public String[] getColumnNames();
}
