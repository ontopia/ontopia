// $Id: RDBMSImportExport.java,v 1.12 2008/01/11 13:29:32 geir.gronmo Exp $

package net.ontopia.test.perf;

import java.io.File;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.impl.rdbms.*;
  
public class RDBMSImportExport {

  public static void main(String[] argv) throws Exception {    
    System.out.println("Starting...");

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSImportExport", argv);
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
    RDBMSTopicMapStore store1 = new RDBMSTopicMapStore(propfile);

    TopicMapIF tm1 = store1.getTopicMap();
    String id1 = tm1.getObjectId();
    String impfile = args[1];
    String expfile = args[2];

    System.out.println("Importing " + impfile + " (id: " + id1 + ")");

    TopicMapImporterIF importer = ImportExportUtils.getImporter(impfile);
    
    long start1 = System.currentTimeMillis();      
    importer.importInto(tm1);

    //! XTMTopicMapWriter writer2 = new XTMTopicMapWriter(expfile + "2");
    //! writer2.write(tm1);
    
    store1.commit();
    long end1 = System.currentTimeMillis();
      
    System.out.println("Done. " + (end1 - start1) + " ms.");
    
    store1.close();

    // System.out.println("Preparing export " + propfile + ":" + Long.parseLong(id1.substring(1)));
    RDBMSTopicMapStore store2 = new RDBMSTopicMapStore(propfile, Long.parseLong(id1.substring(1)));
    TopicMapIF tm2 = store2.getTopicMap();
    String id2 = tm2.getObjectId();
    
    // XTMTopicMapWriter writer = new XTMTopicMapWriter(expfile);
    TopicMapWriterIF writer = ImportExportUtils.getWriter(expfile);

    System.out.println("Exporting " + id2 + " to " + expfile + ".");
    long start2 = System.currentTimeMillis();      
    writer.write(tm2);
    long end2 = System.currentTimeMillis();
      
    System.out.println("Done. " + (end2 - start2) + " ms.");
    
    // Close store (and database connection)    
    store2.close();
  }

  private static void usage() {
    System.out.println("java net.ontopia.test.perf.RDBMSImportExport [options] <dbprops> <impfile> <expfile>");
    System.out.println("");
    System.out.println("  Imports XTM documents into a database and then exports it.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("  <impfile>:   the file to import");
    System.out.println("  <expfile>:   the filename of the exported file");
    System.out.println("");
  }
  
}





