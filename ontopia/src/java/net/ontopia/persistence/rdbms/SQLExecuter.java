// $Id: SQLExecuter.java,v 1.4 2006/07/06 09:50:53 grove Exp $

package net.ontopia.persistence.rdbms;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import net.ontopia.persistence.proxy.DefaultConnectionFactory;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;

/** 
 * INTERNAL: Command line tool for executing SQL statements in a
 * database referenced by a database properties file.
 */

public class SQLExecuter {
  
  public static void main(String[] argv) throws Exception {

    // Initialize logging
    CmdlineUtils.initializeLogging();
    
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("SQLExecuter", argv);
    
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
    String sql = args[1];

    Properties props = new Properties();
    props.load(new FileInputStream(dbprops));
    DefaultConnectionFactory cfactory = new DefaultConnectionFactory(props, true);
    Connection conn = cfactory.requestConnection();
    
    // Execute statement
    Statement stm = null;
    try {
      stm = conn.createStatement();
      ResultSet rs = stm.executeQuery(sql);
      int cols = rs.getMetaData().getColumnCount();
      
      while (rs.next()) {
        for (int i=1; i <= cols; i++) {
          System.out.print(rs.getString(i));
          if (i != cols) System.out.print(" | ");
        }
        System.out.println();
      }
      rs.close();
      
    } finally {
      if (stm != null) stm.close();
      if (conn != null) conn.close();
    }
  }
  
  protected static void usage() {
    System.out.println("java SQLExecuter [options] <dbprops> <statement>");
    System.out.println("");
    System.out.println("  Tool for executing queries in a database.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>: filename of database properties file");
    System.out.println("  <statement>:  the SQL statement to execute");
  }
  
}

