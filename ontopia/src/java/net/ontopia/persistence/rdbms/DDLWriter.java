// $Id: DDLWriter.java,v 1.13 2006/12/18 12:27:23 grove Exp $

package net.ontopia.persistence.rdbms;

import java.io.FileWriter;
import java.io.Writer;

import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.StringUtils;

/** 
 * INTERNAL: Command line tool for producing DDL files for creating
 * and dropping database schemas.
 */

public class DDLWriter {
  
  public static void main(String[] argv) throws Exception {

    // Initialize logging
    CmdlineUtils.initializeLogging();
    
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("DDLWriter", argv);
    
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
      System.err.println("Error: need exactly two files as arguments.");
      usage();
      System.exit(1);
    }

    String schema = args[0];
    String dbtype = args[1];
    String[] platforms = StringUtils.split(args[2], ",");
    String createfile = args[3];
    String dropfile = args[4];
    
    Project project = DatabaseProjectReader.loadProject(schema);
    
    GenericSQLProducer producer = null;
    if (dbtype.equals("postgresql"))
      producer = new PostgreSQLProducer(project, platforms);
    else if (dbtype.equals("oracle"))
      producer = new OracleSQLProducer(project, platforms);
    else if (dbtype.equals("sqlserver"))
      producer = new SQLServerSQLProducer(project, platforms);
    else if (dbtype.equals("mysql"))
      producer = new MySqlSQLProducer(project, platforms);
    else if (dbtype.equals("db2"))
      producer = new DB2SQLProducer(project, platforms);
    else if (dbtype.equals("firebird"))
      producer = new FirebirdSQLProducer(project, platforms);
    else
      producer = new GenericSQLProducer(project, platforms);
      
    // Generate create file
    Writer cwriter = new FileWriter(createfile);
    producer.writeCreate(cwriter);
    cwriter.close();

    // Generate create file
    Writer dwriter = new FileWriter(dropfile);
    producer.writeDrop(dwriter);
    dwriter.close();
  }
  
  protected static void usage() {
    System.out.println("java DDLWriter [options] <schema> <dbtype> <platforms> <createfile> <dropfile>");
    System.out.println("");
    System.out.println("  Creates DDL files for creating and dropping database schemas.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <schema>: schema description file");
    System.out.println("  <dbtype>: the database type");
    System.out.println("  <platforms>: the database platforms (comma separated, no whitespace)");
    System.out.println("  <createfile>: filename of create file");
    System.out.println("  <dropfile>:   filename of drop file");
  }
  
}

