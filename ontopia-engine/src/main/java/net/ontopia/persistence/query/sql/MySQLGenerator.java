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

import java.util.Map;

/**
 * INTERNAL: MySQL SQL statement generator.
 */

public class MySQLGenerator extends GenericSQLGenerator {

  MySQLGenerator(Map properties) {
    super(properties);
  }

  // TODO: string = operations are case-insensitive. they should not
  // be. cast expression to 'binary L = R'.

  @Override
  public void fromSubSelectAlias(StringBuilder sql, BuildInfo info) {
    // sub-SELECT in FROM must have an alias.
    // For example, FROM (SELECT ...) [AS] foo
    sql.append(" as FOOBAR");
  }

  @Override
  protected StringBuilder createOffsetLimitClause(int offset, int limit, BuildInfo info) {    
    // NOTE: offset supported in versions > 4.0

    // LIMIT x OFFSET y clause
    if (limit > 0 && offset > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append(" limit ").append(offset).append(", ").append(limit);
      return sb;
    } else if (limit > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append(" limit ").append(limit);
      return sb;
    // else if (offset > 0) // NOTE: does not work with MySQL
    //   sql_order_by.append(" limit ").append(limit);
    } else {
      return null;
    }
  }
  
}
