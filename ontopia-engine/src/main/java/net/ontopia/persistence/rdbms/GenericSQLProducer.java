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
import net.ontopia.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * INTERNAL: Class that generates DDL statements for the generic
 * database platform.
 */

public class GenericSQLProducer {
  
  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(GenericSQLProducer.class.getName());
    
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
    Iterator iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = (Table)iter.next();
      outputStatements(createStatement(table, new ArrayList()), writer);
      writer.write("\n");
    }
    
    // Primary keys
    iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = (Table)iter.next();
      outputStatements(addPrimaryKeys(table, new ArrayList()), writer);
    }

    // Foreign keys
    if (supportsForeignKeys()) {
      iter = project.getTables().iterator();
      boolean fkeys = iter.hasNext();
      if (fkeys) writer.write("\n/* \n");

      while (iter.hasNext()) {
        Table table = (Table)iter.next();
        int counter = 0;
        Iterator iter2 = table.getColumns().iterator();
        while (iter2.hasNext()) {
          Column col = (Column)iter2.next();
          if (col.getReferencedTable() != null) {
            counter++;
            String keyname = "FK_" + table.getName() + "_" + counter;
            outputStatements(addForeignKey(table, col, keyname, new ArrayList()), writer);
          }
        }
      }
      if (fkeys) writer.write("*/\n");
    }

    // Indexes
    iter = project.getTables().iterator();
    if (iter.hasNext()) writer.write("\n");    
    while (iter.hasNext()) {
      Table table = (Table)iter.next();
      if (table.getIndexes() != null)
        outputStatements(createIndexes(table, new ArrayList()), writer);
    }

    // Actions
    List actions = (List)project.getCreateActions(platforms);
    if (!actions.isEmpty()) {
      writer.write("\n");
      outputStatements(actions, writer);
      writer.write("\n");
    }
  }

  public void executeCreate(Connection conn) throws IOException, SQLException {

    List statements = new ArrayList();
    
    // Create table
    Iterator iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = (Table)iter.next();
      createStatement(table, statements);
    }
    
    // Primary keys
    iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = (Table)iter.next();
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
      Table table = (Table)iter.next();
      if (table.getIndexes() != null)
        createIndexes(table, statements);
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
    List actions = (List)project.getDropActions(platforms);
    if (!actions.isEmpty()) {
      writer.write("\n");
      outputStatements(actions, writer);
      writer.write("\n");
    }

    Iterator iter;
    // Foreign keys
    if (supportsForeignKeys()) {
      iter = project.getTables().iterator();
      boolean fkeys = iter.hasNext();
      if (fkeys) writer.write("\n/* \n");

      while (iter.hasNext()) {
        Table table = (Table)iter.next();
        int counter = 0;
        Iterator iter2 = table.getColumns().iterator();
        while (iter2.hasNext()) {
          Column col = (Column)iter2.next();
          if (col.getReferencedTable() != null) {
            counter++;
            String keyname = "FK_" + table.getName() + "_" + counter;
            outputStatements(dropConstraint(table, col, keyname, new ArrayList()), writer);
          }
        }
      }
      if (fkeys) writer.write("*/\n");
    }

    // drop table
    iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = (Table)iter.next();
      outputStatements(dropStatement(table, new ArrayList()), writer);
    }

  }
  
  public void executeDrop(Connection conn) throws IOException, SQLException {

    List statements = new ArrayList();

    // Actions
    statements.addAll(project.getDropActions(platforms));
    
    // drop table
    Iterator iter = project.getTables().iterator();
    while (iter.hasNext()) {
      Table table = (Table)iter.next();
      dropStatement(table, statements);
    }

    // Execute the statements
    executeStatements(statements, conn);    
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to create the specified table.
   */
  protected List createStatement(Table table, List statements) throws IOException {
    String[] pkeys = table.getPrimaryKeys();
    // Create table
    StringBuffer sb = new StringBuffer();
    sb.append("create table ");
    sb.append(table.getName());
    sb.append(" (\n");
    Iterator iter = table.getColumns().iterator();
    while (iter.hasNext()) {
      Column col = (Column)iter.next();
      DataType type = project.getDataTypeByName(col.getType(), platforms);
      if (type == null)
        throw new OntopiaRuntimeException("Unknown datatype: '" + col.getType() + "'");
      sb.append("  ");
      sb.append(col.getName());      
      sb.append("  ");
      sb.append(type.getType());
      sb.append((type.isVariable() ? "(" + type.getSize() + ")" : ""));
      sb.append((!col.isNullable() ? " not null" : (supportsNullInColumnDefinition() ? " null" : "")));
      if (pkeys != null || iter.hasNext())
        sb.append(",");
      sb.append("\n");
      
    }
    // Primary keys
    if (pkeys != null) {
      sb.append("  constraint " + getPrimaryKeyName(table) + " primary key (");
      sb.append(StringUtils.join(pkeys, ", "));
      sb.append(")\n");
    }    
    sb.append(")");
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
  protected List dropStatement(Table table, List statements) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("drop table ");
    sb.append(table.getName());
    statements.add(sb.toString());
    return statements;
  }

  /**
   * INTERNAL: Generate the DDL statement(s) to add primary keys for
   * the specified table. This method should only be implemented if
   * primary keys need to be created by a separate statement.
   */
  protected List addPrimaryKeys(Table table, List statements) throws IOException {
    // Ignore, since we do this in the createStatement method.
    return statements;
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to add foreigns keys for
   * the specified column. This method should only be implemented if
   * foreign keys need to be created by a separate statement.
   */
  protected List addForeignKey(Table table, Column col, String keyname, List statements) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("alter table ");
    sb.append(table.getName());
    sb.append(" add constraint ");
    sb.append(keyname);
    sb.append(" foreign key (");
    sb.append(col.getName());
    sb.append(") references ");
    sb.append(col.getReferencedTable());
    sb.append(" (");
    sb.append(col.getReferencedColumn());
    sb.append(") deferrable initially deferred");
    statements.add(sb.toString());
    return statements;
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to drop foreigns keys for
   * the specified column. This method should only be implemented if
   * foreign keys need to be created by a separate statement.
   */
  protected List dropConstraint(Table table, Column col, String keyname, List statements) throws IOException {
    StringBuffer sb = new StringBuffer();
    sb.append("alter table ");
    sb.append(table.getName());
    sb.append(" drop constraint ");
    sb.append(keyname);
    statements.add(sb.toString());
    return statements;
  }
  
  /**
   * INTERNAL: Generate the DDL statement(s) to create indexes for the
   * specified table.
   */
  protected List createIndexes(Table table, List statements) throws IOException {
    List indexes = table.getIndexes();
    for (int i=0; i < indexes.size(); i++) {
      Index index = (Index)indexes.get(i);
      StringBuffer sb = new StringBuffer();
      sb.append("create index ");
      sb.append(getIndexName(index));
      sb.append(" on ");
      sb.append(table.getName());
      sb.append("(");
      sb.append(StringUtils.join(index.getColumns(), ", "));
      sb.append(")");
      statements.add(sb.toString());
    }
    return statements;
  }
  
  // -- utility methods

  protected void outputStatements(List statements, Writer writer) throws IOException {
    Iterator iter = statements.iterator();
    while (iter.hasNext()) {
      writer.write((String)iter.next());
      writer.write(";\n");
    }
  }

  protected void executeStatements(List statements, Connection conn) throws IOException, SQLException {
    // Execute the statements
    Iterator iter = statements.iterator();
    while (iter.hasNext()) {
      Statement stm = conn.createStatement();
      String sql = (String)iter.next();
      log.info("Executing sql statements generated by " + getClass().getName());
      if (log.isDebugEnabled())
        log.debug("Executing: " + sql);
      stm.executeUpdate(sql);
    }
  }

  // -- flags

  protected boolean supportsForeignKeys() {
    return false;
  }

}
