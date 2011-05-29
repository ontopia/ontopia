
package net.ontopia.topicmaps.cmdlineutils.rdbms;

import java.io.File;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.impl.rdbms.*;

/**
 * PUBLIC: Command line utility for deleting topic maps in a
 * relational database system.
 *
 * <p>Run the class with no arguments to see how to use it.
 */
public class RDBMSDelete {
  
  public static void main(String[] argv) throws Exception {    

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSDelete", argv);
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
      usage();
      System.exit(3);
    }
    String propfile = args[0];
    long topicMapId = ImportExportUtils.getTopicMapId(args[1]);

    RDBMSTopicMapStore store = new RDBMSTopicMapStore(propfile, topicMapId);      

    System.out.println("Deleting topic map with id " + topicMapId);
    store.delete(true);
  }

  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSDelete [options] <dbprops> <tmid>");
    System.out.println("");
    System.out.println("  Deletes a topic map from a database.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("  <tmid>:      the id of the topic map to delete");
    System.out.println("");
  }
  
}
