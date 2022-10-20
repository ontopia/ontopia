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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * INTERNAL: Command line tool for importing comma- or semicolon
 * separated files into a database.
 */

public class CSVImport {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(CSVImport.class.getName());

  protected Project project;
  protected Connection conn;

  protected String table;
  protected String[] columns;
  protected String separator = ",";
  protected boolean cleartable = true;
  protected boolean stripquotes = true;
  protected boolean ignorecolumns = true;
  protected int ignorelines = 0;
  
  public CSVImport(Project project, Connection conn) {
    this.project = project;
    this.conn = conn;    
  }

  public Project getProject() {
    return project;
  }
     
  public void setTable(String table) {
    this.table = table;
  }
  public void setColumns(String[] columns) {
    this.columns = columns;
  }
  public void setSeparator(String separator) {
    this.separator = separator;
  }
  public void setClearTable(boolean cleartable) {
    this.cleartable = cleartable;
  }
  public void setStripQuotes(boolean stripquotes) {
    this.stripquotes = stripquotes;
  }
  public void setIgnoreColumns(boolean ignorecolumns) {
    this.ignorecolumns = ignorecolumns;
  }
  public void setIgnoreLines(int ignorelines) {
    this.ignorelines = ignorelines;
  }
  
  public static void main(String[] argv) throws Exception {

    // Initialize logging
    CmdlineUtils.initializeLogging();
    
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("CSVImport", argv);
    OptionsListener ohandler = new OptionsListener();

    // Register local options
    options.addLong(ohandler, "separator", 's', true);
    options.addLong(ohandler, "stripquotes", 'q');
    options.addLong(ohandler, "ignorelines", 'i', true);
    
    // Register logging options
    CmdlineUtils.registerLoggingOptions(options);
      
    // Parse command line options
    try {
      options.parse();
    } catch (CmdlineOptions.OptionsException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);      
    }

    // Get command line arguments
    String[] args = options.getArguments();    
    if (args.length < 4) {
      System.err.println("Error: wrong number of arguments.");
      usage();
      System.exit(1);
    }

    String schema = args[0];
    String dbprops = args[1];
    String csvfile = args[2];
    String table = args[3];
    String[] columns = StringUtils.split(args[4], ",");

    // Load property file
    Properties props = new Properties();
    props.load(new FileInputStream(dbprops));
    
    // Create database connection
    DefaultConnectionFactory cfactory = new DefaultConnectionFactory(props, false);
    Connection conn = cfactory.requestConnection();

    CSVImport ci = new CSVImport(DatabaseProjectReader.loadProject(schema), conn);
    ci.setTable(table);
    ci.setColumns(columns);
    ci.setSeparator(ohandler.separator);
    ci.setClearTable(true);
    ci.setStripQuotes(ohandler.stripquotes);
    ci.setIgnoreColumns(true);
    ci.setIgnoreLines(ohandler.ignorelines);
    
    ci.importCSV(new FileInputStream(csvfile));
  }

  public void importCSV(InputStream csvfile) throws Exception {
    // Execute statements
    try {

      String[] qmarks = new String[columns.length];
      for (int i=0; i < qmarks.length; i++) {
        qmarks[i] = "?";
      }

      if (cleartable) {
        String delsql = "delete from " + table;
        Statement delstm = conn.createStatement();
        delstm.executeUpdate(delsql);
        //! conn.commit();
      }
      
      String sql = "insert into " + table + " (" + StringUtils.join(columns, ", ")
        + ") values (" + StringUtils.join(qmarks, ", ") + ")";
      log.debug("SQL: " + sql);
      PreparedStatement stm = conn.prepareStatement(sql);

      int datatypes[] = new int[columns.length];      
      for (int i=0; i < columns.length; i++) {
        Table tbl = project.getTableByName(table);
        if (tbl == null) {
          throw new OntopiaRuntimeException("Unknown table: " + table);
        }
        Column col = tbl.getColumnByName(columns[i]);
        if (col == null) {
          throw new OntopiaRuntimeException("Unknown table column: " + columns[i]);
        }
        if (col.getType() == null) {
          throw new OntopiaRuntimeException("Column type is null: " + col.getType());
        }
        DataType datatype = project.getDataTypeByName(col.getType(), "generic");
        if (datatype == null) {
          throw new OntopiaRuntimeException("Unknown column type: " + col.getType());
        }
        String dtype = datatype.getType();
        if ("varchar".equals(dtype)) {
          datatypes[i] = Types.VARCHAR;
        } else if ("integer".equals(dtype)) {
          datatypes[i] = Types.INTEGER;
        } else {
          throw new OntopiaRuntimeException("Unknown datatype: "+ dtype);
        }        
      }
      
      LineNumberReader reader = new LineNumberReader(new InputStreamReader(csvfile));

      // Ignore first X lines
      for (int i=0; i < ignorelines; i++) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
      }

      // Process input
      log.debug("[" + StringUtils.join(columns, ", ") + "]");
      int lineno = 0;
      while (true) {
        lineno++;
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        try {
          String[] cols = StringUtils.split(line, separator);
          if (cols.length > columns.length && !ignorecolumns) {
            log.debug("Ignoring columns: " + (columns.length+1) + "-" + cols.length + " '" + line + "'");
          }
          log.debug("CVALUES: " + (columns.length+1) + "-" + cols.length + " '" + line + "'");
          
          String dmesg = "(";
          for (int i=0; i < columns.length; i++) {
            String col = cols[i];
            // If first column character is '"' strip quotes.
            if (stripquotes) {
              int len = col.length();
              if (len > 1 &&
                  ((col.charAt(0) == '"' && col.charAt(len-1) == '"') ||
                   (col.charAt(0) == '\''&& col.charAt(len-1) == '\''))) {
                col = col.substring(1,len-1);
              }
            }
            if (col != null && col.equals("")) {
              col = null;
            }
            
            dmesg = dmesg + col;
            if (i < columns.length-1) {
              dmesg = dmesg + ", ";
            }
            stm.setObject(i+1, col, datatypes[i]);
          }
          dmesg = dmesg + ")";
          log.debug(dmesg);
          stm.execute();
        } catch (Exception e) {
          conn.rollback();
          throw new OntopiaRuntimeException("Cannot read line " + lineno + ": '" + line + "'", e);
        }
      }
      conn.commit();
    } finally {
      if (conn != null) {
        conn.close();
      }
    }
  }
  
  protected static void usage() {
    System.out.println("java net.ontopia.persistence.rdbms.CSVImport [options] <schema> <dbprops> <csvfile> <table> <columns>");
    System.out.println("");
    System.out.println("  Tool for importing semicolon separated files into tables.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --separator=<sep>: specifies the columns separator (default: ';')");
    System.out.println("    --stripquotes: if specified quotes around column values will be stripped");
    System.out.println("    --ignorelines=<count>: ignore the first <count> lines");
    System.out.println("");
    System.out.println("  <schema>: schema description file");
    System.out.println("  <dbprops>: filename of database properties file");
    System.out.println("  <csvfile>:  semicolon separated data filename");
    System.out.println("  <table>:  name of table to import into");
    System.out.println("  <columns>:  ordered list of columns to import (comma-separated)");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    private boolean stripquotes = false;
    private String separator = ";";
    private int ignorelines = 0;
    @Override
    public void processOption(char option, String value) throws CmdlineOptions.OptionsException {
      if (option == 's') {
        separator = value;
      } else if (option == 'q') {
        stripquotes = true;
      } else if (option == 'i') {
        ignorelines = Integer.parseInt(value);
      }
    }
  }
  
}

