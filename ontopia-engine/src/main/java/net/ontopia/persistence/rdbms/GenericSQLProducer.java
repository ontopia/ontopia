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
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * INTERNAL: Class that generates DDL statements for the generic
 * database platform.
 */

public class GenericSQLProducer {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(GenericSQLProducer.class.getName());
    
  protected Project project;
  protected String[] platforms = new String[] { "generic" };
                                               
  public GenericSQLProducer(Project project) {
    this.project = project;
  }
                                               
  public GenericSQLProducer(Project project, String[] platforms) {
    this.project = project;
    this.platforms = platforms;
  }

  /**
   * INTERNAL: Create the DDL statement(s) to create the database schema.
   */
  public void writeCreate(Writer writer) throws IOException {

    // Create table
    Iterator<Table> iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = iter.next();
      outputStatements(createStatement(table, new ArrayList<String>()), writer);
      writer.write("\n");
    }
    
    // Primary keys
    iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = iter.next();
      outputStatements(addPrimaryKeys(table, new ArrayList<String>()), writer);
    }

    // Foreign keys
    if (supportsForeignKeys()) {
      iter = project.getTables().iterator();
      boolean fkeys = iter.hasNext();
      if (fkeys) {
        writer.write("\n/* \n");
      }

      while (iter.hasNext()) {
        Table table = iter.next();
        int counter = 0;
        Iterator<Column> iter2 = table.getColumns().iterator();
        while (iter2.hasNext()) {
          Column col = iter2.next();
          if (col.getReferencedTable() != null) {
            counter++;
            String keyname = "FK_" + table.getName() + "_" + counter;
            outputStatements(addForeignKey(table, col, keyname, new ArrayList<String>()), writer);
          }
        }
      }
      if (fkeys) {
        writer.write("*/\n");
      }
    }

    // Indexes
    iter = project.getTables().iterator();
    if (iter.hasNext()) {
      writer.write("\n");
    }    
    while (iter.hasNext()) {
      Table table = iter.next();
      if (table.getIndexes() != null) {
        outputStatements(createIndexes(table, new ArrayList<String>()), writer);
      }
    }

    // Actions
    List<String> actions = project.getCreateActions(platforms);
    if (!actions.isEmpty()) {
      writer.write("\n");
      outputStatements(actions, writer);
      writer.write("\n");
    }
  }

  public void executeCreate(Connection conn) throws IOException, SQLException {

    List<String> statements = new ArrayList<String>();
    
    // Create table
    Iterator<Table> iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = iter.next();
      createStatement(table, statements);
    }
    
    // Primary keys
    iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = iter.next();
      addPrimaryKeys(table, statements);
    }

    //! // Foreign keys
    //! if (supportsForeignKeys()) {
    //!   iter = project.getTables().iterator();
    //!   boolean fkeys = iter.hasNext();
    //!   //! if (fkeys) writer.write("\n/* \n");
    //! 
    //!   while (iter.hasNext()) {
    //!     Table table = (Table)iter.next();
    //!     int counter = 0;
    //!     Iterator iter2 = table.getColumns().iterator();
    //!     while (iter2.hasNext()) {
    //!       Column col = (Column)iter2.next();
    //!       if (col.getReferencedTable() != null) {
    //!         counter++;
    //!         String keyname = "FK_" + table.getName() + "_" + counter;
    //!         outputStatements(addForeignKey(table, col, keyname, new ArrayList()), writer);
    //!       }
    //!     }
    //!   }
    //!   //! if (fkeys) writer.write("\n*/\n");
    //! }

    // Indexes
    iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = iter.next();
      if (table.getIndexes() != null) {
        createIndexes(table, statements);
      }
    }

    // Actions
    statements.addAll(project.getCreateActions(platforms));

    // Execute the statements
    executeStatements(statements, conn);
  }

  /**
   * INTERNAL: Create the DDL statement(s) to drop the database schema.
   */
  public void writeDrop(Writer writer) throws IOException {

    // Actions
    List<String> actions = project.getDropActions(platforms);
    if (!actions.isEmpty()) {
      writer.write("\n");
      outputStatements(actions, writer);
      writer.write("\n");
    }

    Iterator<Table> iter;
    // Foreign keys
    if (supportsForeignKeys()) {
      iter = project.getTables().iterator();
      boolean fkeys = iter.hasNext();
      if (fkeys) {
        writer.write("\n/* \n");
      }

      while (iter.hasNext()) {
        Table table = iter.next();
        int counter = 0;
        Iterator<Column> iter2 = table.getColumns().iterator();
        while (iter2.hasNext()) {
          Column col = iter2.next();
          if (col.getReferencedTable() != null) {
            counter++;
            String keyname = "FK_" + table.getName() + "_" + counter;
            outputStatements(dropConstraint(table, col, keyname, new ArrayList<String>()), writer);
          }
        }
      }
      if (fkeys) {
        writer.write("*/\n");
      }
    }

    // drop table
    iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = iter.next();
      outputStatements(dropStatement(table, new ArrayList<String>()), writer);
    }

  }
  
  public void executeDrop(Connection conn) throws IOException, SQLException {

    List<String> statements = new ArrayList<String>();

    // Actions
    statements.addAll(project.getDropActions(platforms));
    
    // drop table
    Iterator<Table> iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = iter.next();
      dropStatement(table, statements);
    }

    // Execute the statements
    executeStatements(statements, conn);    
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to create the specified table.
   */
  protected List<String> createStatement(Table table, List<String> statements) throws IOException {
    String[] pkeys = table.getPrimaryKeys();
    // Create table
    StringBuilder sb = new StringBuilder("create table ")
        .append(table.getName())
        .append(" (\n");
    Iterator<Column> iter = table.getColumns().iterator();
    while (iter.hasNext()) {
      Column col = iter.next();
      DataType type = project.getDataTypeByName(col.getType(), platforms);
      if (type == null) {
        throw new OntopiaRuntimeException("Unknown datatype: '" + col.getType() + "'");
      }
      sb.append("  ")
          .append(col.getName())
          .append("  ")
          .append(type.getType())
          .append((type.isVariable() ? "(" + type.getSize() + ")" : ""))
          .append((!col.isNullable() ? " not null" : (supportsNullInColumnDefinition() ? " null" : "")));
      if (pkeys != null || iter.hasNext()) {
        sb.append(',');
      }
      sb.append('\n');
      
    }
    // Primary keys
    if (pkeys != null) {
      sb.append("  constraint " + getPrimaryKeyName(table) + " primary key (");
      sb.append(StringUtils.join(pkeys, ", "));
      sb.append(")\n");
    }    
    sb.append(')');
    statements.add(sb.toString());    
    return statements;
  }

  protected String getPrimaryKeyName(Table table) {
    return table.getName() + "_pkey";
  }

  protected String getIndexName(Index index) {
    return index.getName();
  }
  
  protected boolean supportsNullInColumnDefinition() {
    return true;
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to drop the specified table.
   */
  protected List<String> dropStatement(Table table, List<String> statements) throws IOException {
    statements.add("drop table " + table.getName());
    return statements;
  }

  /**
   * INTERNAL: Generate the DDL statement(s) to add primary keys for
   * the specified table. This method should only be implemented if
   * primary keys need to be created by a separate statement.
   */
  protected List<String> addPrimaryKeys(Table table, List<String> statements) throws IOException {
    // Ignore, since we do this in the createStatement method.
    return statements;
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to add foreigns keys for
   * the specified column. This method should only be implemented if
   * foreign keys need to be created by a separate statement.
   */
  protected List<String> addForeignKey(Table table, Column col, String keyname, List<String> statements) throws IOException {
    statements.add(new StringBuilder("alter table ")
        .append(table.getName())
        .append(" add constraint ")
        .append(keyname)
        .append(" foreign key (")
        .append(col.getName())
        .append(") references ")
        .append(col.getReferencedTable())
        .append(" (")
        .append(col.getReferencedColumn())
        .append(") deferrable initially deferred")
        .toString());
    return statements;
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to drop foreigns keys for
   * the specified column. This method should only be implemented if
   * foreign keys need to be created by a separate statement.
   */
  protected List<String> dropConstraint(Table table, Column col, String keyname, List<String> statements) throws IOException {
    statements.add(new StringBuilder("alter table ")
        .append(table.getName())
        .append(" drop constraint ")
        .append(keyname)
        .toString());
    return statements;
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to create indexes for the
   * specified table.
   */
  protected List<String> createIndexes(Table table, List<String> statements) throws IOException {
    List<Index> indexes = table.getIndexes();
    for (int i=0; i < indexes.size(); i++) {
      Index index = indexes.get(i);
      statements.add(new StringBuilder()
          .append("create index ")
          .append(getIndexName(index))
          .append(" on ")
          .append(table.getName())
          .append('(')
          .append(StringUtils.join(index.getColumns(), ", "))
          .append(')')
          .toString());
    }
    return statements;
  }
  
  // -- utility methods

  protected void outputStatements(List<String> statements, Writer writer) throws IOException {
    Iterator<String> iter = statements.iterator();
    while (iter.hasNext()) {
      writer.write(iter.next());
      writer.write(";\n");
    }
  }

  protected void executeStatements(List<String> statements, Connection conn) throws IOException, SQLException {
    // Execute the statements
    Iterator<String> iter = statements.iterator();
    while (iter.hasNext()) {
      Statement stm = conn.createStatement();
      String sql = iter.next();
      log.info("Executing sql statements generated by " + getClass().getName());
      if (log.isDebugEnabled()) {
        log.debug("Executing: " + sql);
      }
      stm.executeUpdate(sql);
    }
  }

  // -- flags

  protected boolean supportsForeignKeys() {
    return false;
  }

}
