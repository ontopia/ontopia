
// $Id: RDBMSImport.java,v 1.18 2008/01/10 14:39:52 geir.gronmo Exp $

package net.ontopia.test.perf;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.impl.rdbms.*;
  
public class RDBMSImport {

  public static TopicMapIF topicmap;
  
  public static void main(String[] argv) throws Exception {    
    System.out.println("Starting...");

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSImport", argv);
    OptionsListener ohandler = new OptionsListener();
    CmdlineUtils.registerLoggingOptions(options);
    options.addLong(ohandler, "tmid", 't', true);
      
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

    RDBMSTopicMapStore store = new RDBMSTopicMapStore(args[0], ohandler.getTopicMapId());      
    TopicMapIF tm = store.getTopicMap();
    topicmap = tm; // for profiling purposes
    
    for (int i=1; i < args.length; i++) {
      
      String filename = args[i];
      TopicMapImporterIF importer = ImportExportUtils.getImporter(filename);

      System.out.println("Importing " + filename);
      long start = System.currentTimeMillis();
      
      importer.importInto(tm);
      store.commit();
      
      long end = System.currentTimeMillis();      
      System.out.println("Done. (topic map id " + tm.getObjectId() + ") " + (end - start) + " ms.");
    }

    // Close store (and database connection)    
    store.close();
  }

  private static void usage() {
    System.out.println("java net.ontopia.test.perf.RDBMSImport [options] <dbprops> <tmfile1> [<tmfile2>] ...");
    System.out.println("");
    System.out.println("  Imports topic map files into a database.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --tmid=<topic map id> : existing TM to import into");
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("  <tmfile#>:   the topic map files to import");
    System.out.println("");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    long topicMapId = -1;
    
    public void processOption(char option, String value) {
      if (option == 't') topicMapId = Long.parseLong(value);
    }

    public long getTopicMapId() {
      return topicMapId;
    }
  }
  
}
