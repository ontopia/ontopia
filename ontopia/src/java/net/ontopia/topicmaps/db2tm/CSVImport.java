// $Id: CSVImport.java,v 1.1 2006/08/27 14:37:56 grove Exp $

package net.ontopia.topicmaps.db2tm;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.*;

import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

/** 
 * INTERNAL: Command line tool for importing comma- or semicolon
 * separated files into a database.
 */

public class CSVImport {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(CSVImport.class.getName());
  
  protected Connection conn;

  protected String table;
  protected String[] columns;
  protected char quoteCharacter = '"';
  protected char separator = ',';
  protected boolean cleartable = true;
  protected boolean stripquotes = true;
  protected boolean ignorecolumns = true;
  protected int ignorelines = 0;

  CSVImport(Connection conn) {
    this.conn = conn;
  }
     
  public void setTable(String table) {
    this.table = table;
  }
  public void setColumns(String[] columns) {
    this.columns = columns;
  }
  public void setSeparator(char separator) {
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
  
  public void importCSV(InputStream csvfile) throws Exception {
    // Execute statements
    try {
      System.out.println("TABLE: " + table);

      if (cleartable) {
        String delsql = "delete from " + table;
        System.out.println("DELETE:" + delsql);
        log.debug("DELETE: " + delsql);
        Statement delstm = conn.createStatement();
        delstm.executeUpdate(delsql);
        //! conn.commit();
      }

      // get hold of column metadata
      List colnames = new ArrayList();
      List coltypes_ = new ArrayList();
      ResultSet rs = conn.getMetaData().getColumns(null, null, table, null);
      try {
        while(rs.next()) {
          String colname = rs.getString(4);
          int coltype = rs.getInt(5);
          colnames.add(colname);
          coltypes_.add(new Integer(coltype));
        }
      } finally {
        rs.close();
      }
      int[] coltypes = new int[coltypes_.size()];
      for (int i=0; i < coltypes.length; i++) {
        coltypes[i] = ((Integer)coltypes_.get(i)).intValue();
      }

      String[] qmarks = new String[coltypes.length];
      for (int i=0; i < qmarks.length; i++) {
        qmarks[i] = "?";
      }
      
      String sql = "insert into " + table + " (" + StringUtils.join(colnames, ", ")
        + ") values (" + StringUtils.join(qmarks, ", ") + ")";
      log.debug("INSERT: " + sql);
      System.out.println("INSERT:" + sql);
      PreparedStatement stm = conn.prepareStatement(sql);
      
      LineNumberReader reader = new LineNumberReader(new InputStreamReader(csvfile));
      CSVReader csvreader = new CSVReader(reader, separator, quoteCharacter);

      // Ignore first X lines
      for (int i=0; i < ignorelines; i++) {
        String[] tuple = csvreader.readNext();
        if (tuple == null) break;
      }

      // HACK: override date+datetime formats
      JDBCUtils.df_date = new SimpleDateFormat("dd.MM.yyyy");
      JDBCUtils.df_datetime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
      
      // Process input
      log.debug("[" + StringUtils.join(colnames, ", ") + "]");
      int lineno = 0;
      String [] tuple = null;
      try {
        while ((tuple = csvreader.readNext()) != null) {
          if (tuple == null) break;
          
          for (int i=0; i < tuple.length; i++) {
            System.out.println("V:" + (i+1) + " " + colnames.get(i) + ":" + coltypes[i] + " " + tuple[i].length() + "'" + tuple[i] + "'");
            JDBCUtils.setObject(stm, i+1, tuple[i], coltypes[i]);
          }
          stm.execute();
        }
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Problemer med å laste inn linje " + reader.getLineNumber() + ": " + Arrays.asList(tuple), e);
      }
      conn.commit();
        
    } catch (Exception e) {
      //conn.rollback();
      throw e;
    } finally {
      //if (conn != null) conn.close();
    }
  }
  
  protected static void usage() {
    System.out.println("java net.ontopia.topicmaps.db2tm.CSVImport [options] <dbprops> <csvfile> <table> <columns>");
    System.out.println("");
    System.out.println("  Tool for importing semicolon separated files into tables.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --separator=<sep>: specifies the columns separator (default: ';')");
    System.out.println("    --stripquotes: if specified quotes around column values will be stripped");
    System.out.println("    --ignorelines=<count>: ignore the first <count> lines");
    System.out.println("");
    System.out.println("  <dbprops>: filename of database properties file");
    System.out.println("  <csvfile>:  semicolon separated data filename");
    System.out.println("  <table>:  name of table to import into");
    System.out.println("  <columns>:  ordered list of columns to import (comma-separated)");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    boolean stripquotes = false;
    char separator = ',';
    int ignorelines = 0;
    public void processOption(char option, String value) throws CmdlineOptions.OptionsException {
      if (option == 's')
        separator = value.charAt(0);
      else if (option == 'q')
        stripquotes = true;
      else if (option == 'i')
        ignorelines = Integer.parseInt(value);
    }
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
    if (args.length < 2) {
      System.err.println("Error: wrong number of arguments.");
      usage();
      System.exit(1);
    }

    String dbprops = args[0];
    String csvfile = args[1];
    String table = (args.length >= 3 ? args[2] : null);
    if (table == null) {
      if (csvfile.endsWith(".csv"))
        table = csvfile.substring(0, csvfile.length() - 4);
      else
        table = csvfile;
    }    
    String[] columns = (args.length >= 4 ? StringUtils.split(args[3], ",") : null);
    
    // Load property file
    Properties props = new Properties();
    props.load(new FileInputStream(dbprops));
    
    // Create database connection
    DefaultConnectionFactory cfactory = new DefaultConnectionFactory(props, false);
    Connection conn = cfactory.requestConnection();

    CSVImport ci = new CSVImport(conn);
    ci.setTable(table);
    ci.setColumns(columns);
    ci.setSeparator(ohandler.separator);
    ci.setClearTable(true);
    ci.setStripQuotes(ohandler.stripquotes);
    ci.setIgnoreColumns(true);
    ci.setIgnoreLines(ohandler.ignorelines);
    
    ci.importCSV(new FileInputStream(csvfile));
  }
  
}

