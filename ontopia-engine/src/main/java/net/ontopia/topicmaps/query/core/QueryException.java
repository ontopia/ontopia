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

package net.ontopia.topicmaps.query.core;

import net.ontopia.utils.OntopiaException;

/**
 * PUBLIC: Common super-exception for all exceptions thrown by the
 * query engine.
 */
public class QueryException extends OntopiaException {
  public QueryException(String msg) {
    super(msg);
  }
  public QueryException(Throwable cause) {
    super(cause);
  }
  public QueryException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
