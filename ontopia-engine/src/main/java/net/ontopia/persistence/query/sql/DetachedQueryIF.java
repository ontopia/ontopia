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

package net.ontopia.persistence.query.sql;

import java.sql.Connection;
import java.util.Map;
  
/**
 * INTERNAL: Interface for representing shared queries.
 */

public interface DetachedQueryIF {

  /**
   * INTERNAL: Executes the query without any parameters. The query
   * result is returned. The actual type of the query result is
   * specific to the query implementation.
   */
  public Object executeQuery(Connection conn) throws Exception;

  /**
   * INTERNAL: Executes the query with the given parameters. The query
   * result is returned. The actual type of the query result is
   * specific to the query implementation.
   */
  public Object executeQuery(Connection conn, Object[] params) throws Exception;

  /**
   * INTERNAL: Executes the query with the given named parameters. The
   * query result is returned. The actual type of the query result is
   * specific to the query implementation.
   */
  public Object executeQuery(Connection conn, Map params) throws Exception;
  
}






