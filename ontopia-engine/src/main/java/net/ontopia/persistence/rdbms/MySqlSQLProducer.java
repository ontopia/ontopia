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
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/** 
 * INTERNAL: Class that generates DDL statements for the mysql
 * database platform.
 */

public class MySqlSQLProducer extends GenericSQLProducer {
    
  //! protected String[] platforms = new String[] { "mysql", "generic" };
                                               
  public MySqlSQLProducer(Project project) {
    super(project);
  }
                                               
  public MySqlSQLProducer(Project project, String[] platforms) {
    super(project, platforms);
  }
  
  @Override
  protected List<String> createStatement(Table table, List<String> statements) throws IOException {
    StringBuilder sb = new StringBuilder();
    String[] pkeys = table.getPrimaryKeys();
    // Create table
    sb.append("create table ").append(table.getName()).append(" (\n");
    Iterator<Column> iter = table.getColumns().iterator();
    while (iter.hasNext()) {
      Column col = iter.next();
      DataType type = project.getDataTypeByName(col.getType(), platforms);
      sb.append("  ").append(col.getName()).append("  ");
      if (type.getType().equals("varchar") && Integer.parseInt(type.getSize()) > 255) {
        sb.append("TEXT");
      } else {        
        sb.append(type.getType())
            .append((type.isVariable() ? "(" + type.getSize() + ")" : ""));
      }
      sb.append((!col.isNullable() ? " not null" : ""));
      if (pkeys != null || iter.hasNext()) {
        sb.append(',');
      }
      sb.append('\n');
      
    }
    // Primary keys
    if (pkeys != null) {
      sb.append("  constraint " + table.getName() +  "_pkey primary key (")
          .append(StringUtils.join(pkeys, ", "))
          .append(')')
          .append('\n');
    }
    
    statements.add(sb.append(") TYPE = InnoDB\n").toString());
    return statements;
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to create indexes for the
   * specified table.
   */
  @Override
  protected List<String> createIndexes(Table table, List<String> statements) throws IOException {
    List<Index> indexes = table.getIndexes();
    for (int i=0; i < indexes.size(); i++) {
      Index index = indexes.get(i);
      StringBuilder sb = new StringBuilder()
          .append("create index ")
          .append(index.getName())
          .append(" on ")
          .append(table.getName())
          .append('(');      
      String[] cols = index.getColumns();
      for (int x=0; x < cols.length; x++) {
        if (x > 0) {
          sb.append(", ");
        }
        sb.append(cols[x]);
        Column col = table.getColumnByName(cols[x]);
        if ("blob".equalsIgnoreCase(col.getType()) || "clob".equalsIgnoreCase(col.getType()) || "text".equalsIgnoreCase(col.getType())) {
          sb.append("(255)");
        }
      }
      sb.append(')');
      statements.add(sb.toString());
    }
    return statements;
  }
  
}
