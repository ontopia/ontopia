
// $Id: RDBMSImport.java,v 1.13 2008/01/10 14:39:52 geir.gronmo Exp $

package net.ontopia.topicmaps.cmdlineutils.rdbms;

import java.io.File;
import java.util.Properties;

import net.ontopia.persistence.jdbcspy.SpyDriver;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.impl.rdbms.*;

/**
 * PUBLIC: Command line utility for importing topic map files into a
 * relational database system. Topic map files can either be imported
 * into an existing topic map or imported as separated topic maps in
 * the database.
 *
 * <p>Run the class with no arguments to see how to use it.
 */
public class RDBMSImport {
  
  public static void main(String[] argv) throws Exception {    

    // initialize logging
    CmdlineUtils.initializeLogging();
      
    // register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSImport", argv);
    OptionsListener ohandler = new OptionsListener();
    CmdlineUtils.registerLoggingOptions(options);
    options.addLong(ohandler, "tmid", 'i', true);
    options.addLong(ohandler, "title", 't', true);
    options.addLong(ohandler, "comments", 'c', true);
    options.addLong(ohandler, "validate", 'v', true);
    options.addLong(ohandler, "suppress", 's', true);
    options.addLong(ohandler, "loadExternal", 'e', true);
    options.addLong(ohandler, "validate", 'v', true);
    options.addLong(ohandler, "jdbcspy", 'j', true);
      
    // parse command line options
    try {
      options.parse();
    } catch (CmdlineOptions.OptionsException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);      
    }

    // get command line arguments
    String[] args = options.getArguments();

    if (args.length < 2) {
      usage();
      System.exit(3);
    }
    
    // load properties file
    Properties props = PropertyUtils.loadProperties(new File(args[0]));

    // override shared cache property
    props.put("net.ontopia.topicmaps.impl.rdbms.Cache.shared", "false");
    
    RDBMSTopicMapStore store = new RDBMSTopicMapStore(props, ohandler.topicMapId);      
    TopicMapIF tm = store.getTopicMap();

    // set topic map title
    if (ohandler.topicMapTitle != null)
      ((TopicMap)tm).setTitle(ohandler.topicMapTitle);

    // set topic map comments
    if (ohandler.topicMapComments != null)
      ((TopicMap)tm).setComments(ohandler.topicMapComments);
    
    for (int i=1; i < args.length; i++) {
      
      String filename = args[i];
      TopicMapImporterIF importer = ImportExportUtils.getImporter(filename);
      
      // disable XTM validation
      if (importer instanceof XTMTopicMapReader && !ohandler.validate)
        ((XTMTopicMapReader)importer).setValidation(false);

      // disable following external topicRefs
      if (importer instanceof XTMTopicMapReader && !ohandler.loadExternal)
        ((XTMTopicMapReader)importer).setFollowTopicRefs(false);
      
      System.out.println("Importing " + filename + " into " + tm.getObjectId());
      long start = System.currentTimeMillis();
      
      importer.importInto(tm);

      if (ohandler.suppress)
        DuplicateSuppressionUtils.removeDuplicates(tm);
      
      store.commit();
      
      long end = System.currentTimeMillis();      
      System.out.println("Done. " + (end - start) + " ms.");

    }

    if (ohandler.jdbcspyFile != null)
      SpyDriver.writeReport(ohandler.jdbcspyFile);

    // close store (and database connection)    
    store.close();
  }

  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSImport [options] <dbprops> <tmfile1> [<tmfile2>] ...");
    System.out.println("");
    System.out.println("  Imports topic map files into a topic map in a database.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --tmid=<topic map id> : existing TM to import into (creates new TM by default)");
    System.out.println("    --title=<topic map title> : persistent name of topic map");
    System.out.println("    --comments=<topic map comments> : persistent comments about topic map");
    System.out.println("    --validate=true|false : if true topic map document will be validated (default: true)");
    System.out.println("    --suppress=true|false: suppress duplicate characteristics (default: false)");
    System.out.println("    --loadExternal=true|false : if true external topic references will be resolved (default: true)");
    System.out.println("    --jdbcspy=<filename> : write jdbcspy report to the given file");
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("  <tmfile#>:   the topic map files to import");
    System.out.println("");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    long topicMapId = -1;
    boolean validate = true;
    boolean suppress = false;
    boolean loadExternal = true;
    String jdbcspyFile;
    String topicMapTitle;
    String topicMapComments;
    public void processOption(char option, String value) {
      if (option == 'i') topicMapId = ImportExportUtils.getTopicMapId(value);
      if (option == 'v') validate = Boolean.valueOf(value).booleanValue();
      if (option == 'e') loadExternal = Boolean.valueOf(value).booleanValue();
      if (option == 's') suppress = Boolean.valueOf(value).booleanValue();
      if (option == 'j') jdbcspyFile = value;
      if (option == 't') topicMapTitle = value;
      if (option == 'c') topicMapComments = value;
    }
  }
  
}
