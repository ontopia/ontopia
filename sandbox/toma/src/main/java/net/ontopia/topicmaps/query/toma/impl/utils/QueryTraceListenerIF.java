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
 * INTERNAL: Used for testing and timing of queries.
 */
public interface QueryTraceListenerIF {
  public void startQuery();

  public void endQuery();

  public void enter(ExpressionIF expr);

  public void leave(ResultSet result);

  public void enterOrderBy();

  public void leaveOrderBy();

  public void enterSelect(ResultSet result);

  public void leaveSelect(ResultSet result);

  public void trace(String message);
}
