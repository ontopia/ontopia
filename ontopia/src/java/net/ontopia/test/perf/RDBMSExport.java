// $Id: RDBMSExport.java,v 1.4 2008/01/10 14:39:52 geir.gronmo Exp $

package net.ontopia.test.perf;

import java.io.File;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.impl.rdbms.*;
  
public class RDBMSExport {

  public static void main(String[] argv) throws Exception {    
    System.out.println("Starting...");

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSExport", argv);
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

    if (args.length != 3) {
      usage();
      System.exit(3);
    }
    
    String propfile = args[0];
    long topicMapId = Long.parseLong(args[1]);
    RDBMSTopicMapStore store = new RDBMSTopicMapStore(propfile, topicMapId);
    TopicMapIF tm = store.getTopicMap();
    String id = tm.getObjectId();
    
    TopicMapWriterIF writer = ImportExportUtils.getWriter(args[2]);

    System.out.println("Exporting " + id + " to " + args[2] + ".");
    long start = System.currentTimeMillis();      
    writer.write(tm);
    long end = System.currentTimeMillis();
      
    System.out.println("Done. " + (end - start) + " ms.");
    
    // Close store (and database connection)    
    store.close();
  }

  private static void usage() {
    System.out.println("java net.ontopia.test.perf.RDBMSExport [options] <dbprops> <tmid> <expfile>");
    System.out.println("");
    System.out.println("  Exports topic maps from RDBMS to file.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("  <tmid>:      the topic map id");
    System.out.println("  <expfile>:   the filename of the exported file");
    System.out.println("");
  }
  
}





