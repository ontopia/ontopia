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
 * INTERNAL: Class that generates DDL statements for the IBM db2
 * universal database platform.
 */

public class DB2SQLProducer extends GenericSQLProducer {
                                               
  public DB2SQLProducer(Project project) {
    super(project);
  }
                                               
  public DB2SQLProducer(Project project, String[] platforms) {
    super(project, platforms);
  }

  protected boolean supportsNullInColumnDefinition() {
    return false;
  }

  /* Limits: constraint and index names limited to 18 bytes */

  protected String getPrimaryKeyName(Table table) {
    String sname = table.getShortName();
    return (sname != null ? sname : table.getName()) + "_pkey";
  }

  protected String getIndexName(Index index) {
    String sname = index.getShortName();
    return (sname != null ? sname : index.getName());
  }
  
}
