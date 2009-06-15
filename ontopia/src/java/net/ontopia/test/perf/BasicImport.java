// $Id: BasicImport.java,v 1.6 2008/01/10 14:39:52 geir.gronmo Exp $

package net.ontopia.test.perf;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.utils.*;

public class BasicImport {

  public static void main(String[] argv) throws Exception {    
    System.out.println("Starting...");

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("BasicImport", argv);
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

    String filename = args[0];
    TopicMapImporterIF importer = ImportExportUtils.getImporter(filename);

    TopicMapStoreIF store = new InMemoryTopicMapStore();
    TopicMapIF tm = store.getTopicMap();
    
    System.out.println("Importing " + filename);
    long start = System.currentTimeMillis();      
    importer.importInto(tm);
    store.commit();
    long end = System.currentTimeMillis();
    
    System.out.println("Done. (topic map id " + tm.getObjectId() + ") " + (end - start) + " ms.");
  }
  
}





