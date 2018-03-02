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

package net.ontopia.persistence.rdbms;

import java.io.IOException;
import java.util.List;

/** 
 * INTERNAL: DDL statement generator for the PostgreSQL database
 * platform.
 */
public class PostgreSQLProducer extends GenericSQLProducer {
                                               
  public PostgreSQLProducer(Project project) {
    super(project);
  }
  
  public PostgreSQLProducer(Project project, String[] platforms) {
    super(project, platforms);
  }

  @Override
  protected List<String> dropStatement(Table table, List<String> statements) throws IOException {
    statements.add(new StringBuilder()
        .append("drop table ")
        .append(table.getName())
        .append(" cascade")
        .toString());
    return statements;
  }
  
  // -- flags
  
  @Override
  protected boolean supportsForeignKeys() {
    return true;
  }
  
}
