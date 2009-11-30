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
package net.ontopia.topicmaps.query.toma.impl.utils;

import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;

/**
 * PUBLIC: Listener interface for query tracing.
 */
public interface QueryTraceListenerIF {
  /**
   * Called at the beginning of a query.
   */
  public void startQuery();

  /**
   * Called at the end of a query.
   */
  public void endQuery();

  /**
   * Called when entering an expression.
   */
  public void enter(ExpressionIF expr);

  /**
   * Called when leaving an expression.
   * @param result the result of the evaluated expression.
   */
  public void leave(ResultSet result);

  /**
   * Called when starting to sort the query.
   */
  public void enterOrderBy();

  /**
   * Called when finished with sorting.
   */
  public void leaveOrderBy();

  /**
   * Called when entering a select statement.
   * @param result the {@link ResultSet} at the beginning.
   */
  public void enterSelect(ResultSet result);

  /**
   * Called when leaving a select statement.
   * @param result the {@link ResultSet} at the end.
   */
  public void leaveSelect(ResultSet result);

  /**
   * An arbitrary trace message.
   * @param message the message.
   */
  public void trace(String message);
}
