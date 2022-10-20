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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import org.apache.commons.lang3.StringUtils;

/** 
 * INTERNAL: Command line tool for exporting comma- or semicolon
 * separated files into a database.
 */

public class CSVExport {

  protected Connection conn;
  protected String separator = ";";

  public CSVExport(Connection conn) {
    this.conn = conn;    
  }

  public void setSeparator(String separator) {
    this.separator = separator;
  }

  protected String escape(String value) {
    if (value == null) {
      return "";
    } else {
      return value;
    }
  }

  public void exportCSV(Writer writer, String table, String[] columns) throws SQLException, IOException {
    Statement stm = conn.createStatement();
    ResultSet rs = stm.executeQuery("select " + StringUtils.join(columns, ", ") + " from " + table);
    try {
      while (rs.next()) {
        for (int i = 1; i <= columns.length; i++) {
          if (i > 1) {
            writer.write(separator);
          }
          writer.write('"');
          String value = rs.getString(i);
          if (value != null) {
            writer.write(StringUtils.replace(value, "\"", "\\\""));
          }
          writer.write('"');
        }
        writer.write('\n');
      }
    } finally {
      rs.close();
      stm.close();
    }
    writer.flush();
  }

  public static void main(String[] argv) throws Exception {

    // Initialize logging
    CmdlineUtils.initializeLogging();
    
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("CSVExport", argv);
    OptionsListener ohandler = new OptionsListener();

    // Register local options
    options.addLong(ohandler, "separator", 's', true);
    
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
    if (args.length < 3) {
      System.err.println("Error: wrong number of arguments.");
      usage();
      System.exit(1);
    }

    String dbprops = args[0];
    String table = args[1];
    String[] columns = StringUtils.split(args[2], ",");

    // Load property file
    Properties props = new Properties();
    props.load(new FileInputStream(dbprops));
    
    // Create database connection
    DefaultConnectionFactory cfactory = new DefaultConnectionFactory(props, true);
    Connection conn = cfactory.requestConnection();

    CSVExport ce = new CSVExport(conn);
    ce.setSeparator(ohandler.separator);
    //! ce.setUseQuotes(ohandler.usequotes);
    
    ce.exportCSV(new OutputStreamWriter(System.out), table, columns);
  }
  
  protected static void usage() {
    System.out.println("java net.ontopia.persistence.rdbms.CSVExport [options] <dbprops> <csvfile> <table> <columns>");
    System.out.println("");
    System.out.println("  Tool for exporting tables into CSV files.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --separator=<sep>: specifies the columns separator (default: ';')");
    System.out.println("");
    System.out.println("  <dbprops>: filename of database properties file");
    System.out.println("  <table>:  name of table to export");
    System.out.println("  <columns>:  ordered list of columns to export (comma-separated)");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    private String separator = ";";
    @Override
    public void processOption(char option, String value) throws CmdlineOptions.OptionsException {
      if (option == 's') {
        separator = value;
      }
    }
  }
  
}

