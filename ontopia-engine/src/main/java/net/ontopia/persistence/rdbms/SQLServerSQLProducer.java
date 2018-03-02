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

/** 
 * INTERNAL: Class that generates DDL statements for the sqlserver
 * database platform.
 */

public class SQLServerSQLProducer extends GenericSQLProducer {

  public SQLServerSQLProducer(Project project) {
    super(project);
  }
  
  public SQLServerSQLProducer(Project project, String[] platforms) {
    super(project, platforms);
  }

  // -- flags

  @Override
  protected boolean supportsForeignKeys() {
    return false;
  }

  @Override
  protected boolean supportsNullInColumnDefinition() {
    return true;
  }
  
}
