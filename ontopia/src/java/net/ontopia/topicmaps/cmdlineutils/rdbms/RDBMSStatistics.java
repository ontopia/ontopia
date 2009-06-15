
// $Id: RDBMSStatistics.java,v 1.3 2008/01/10 14:39:52 geir.gronmo Exp $

package net.ontopia.topicmaps.cmdlineutils.rdbms;

import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.impl.rdbms.*;

/**
 * PUBLIC: Command line utility for generating statistics about the topic map
 *
 * <p>Run the class with no arguments to see how to use it.
 */ 
public class RDBMSStatistics {

  public static void main(String[] argv) throws Exception {    
    //System.out.println("Starting...");

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSStatistics", argv);
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

    if (args.length != 2) {
      usage();
      System.exit(3);
    }
    
    String propfile = args[0];
    long topicMapId = ImportExportUtils.getTopicMapId(args[1]);
    RDBMSTopicMapStore store = new RDBMSTopicMapStore(propfile, topicMapId);
    TopicMapIF tm = store.getTopicMap();
    String id = tm.getObjectId();
    try {
      Map stats = Stats.getStatistics(store.getTopicMap());
      stats = new TreeMap(stats);

      System.out.println("Topic Map statistics for " + id);

      Iterator i = stats.keySet().iterator();
      while (i.hasNext()) { 
        Object key = i.next();
        Object value = stats.get(key);
        System.out.println(key + "=" + value);
      }

    } finally {
      store.close();
    }

  }

  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSStatistics [options] <dbprops> <tmid>");
    System.out.println("");
    System.out.println("  Produces some statistics about a topic map store in a RDBMS.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("  <tmid>:      the topic map id");
    System.out.println("");
  }
  
}
