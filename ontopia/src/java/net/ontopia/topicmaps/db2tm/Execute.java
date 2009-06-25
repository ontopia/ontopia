
// $Id: Execute.java,v 1.31 2007/01/31 09:34:20 grove Exp $

package net.ontopia.topicmaps.db2tm;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Command-line driver for DB2TM.
 */
public class Execute {
  
  // --- define a logging category.
  static Logger log = LoggerFactory.getLogger(Execute.class.getName());

  Execute() {
  }
  
  public static void main(String[] argv) throws Exception {

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("Execute", argv);
    CmdlineUtils.registerLoggingOptions(options);
    OptionsListener ohandler = new OptionsListener();
    options.addLong(ohandler, "tm", 't', true);
    options.addLong(ohandler, "baseuri", 'b', true);
    options.addLong(ohandler, "out", 'o', true);
    options.addLong(ohandler, "relations", 'r', true);
    options.addLong(ohandler, "force-rescan", 'f', true);
      
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

    // Arguments
    String operation = args[0];
    String cfgfile = args[1];

    if (!"add".equals(operation) &&
        !"sync".equals(operation) &&
        !"remove".equals(operation)) {
      usage();
      System.err.println("Operation '" + operation + "' not supported.");
      System.exit(3);
    }

    try {
      // Read mapping file
      log.debug("Reading relation mapping file " + cfgfile);
      RelationMapping mapping = RelationMapping.read(new File(cfgfile));
      
      // open topic map
      String tmurl = ohandler.tm;    
      log.debug("Opening topic map " + tmurl);
      TopicMapIF topicmap;
      if (tmurl == null || tmurl.equals("tm:in-memory:new"))
        topicmap = new InMemoryTopicMapStore().getTopicMap();
      else if (tmurl.equals("tm:rdbms:new"))
        topicmap = new RDBMSTopicMapStore().getTopicMap();      
      else {
        TopicMapReaderIF reader = ImportExportUtils.getReader(tmurl);
        topicmap = reader.read();
      }
      TopicMapStoreIF store = topicmap.getStore();

      // base locator
      String outfile = ohandler.out;
      LocatorIF baseloc = (outfile == null ? store.getBaseAddress() : URIUtils.getFileURI(new File(outfile)));
      if (baseloc == null && tmurl != null)
        baseloc = (ohandler.baseuri == null ? URIUtils.getURI(tmurl) : URIUtils.getURI(ohandler.baseuri));

      // figure out which relations to actually process
      Collection relations = null;
      if (ohandler.relations != null) {
        String[] relnames = StringUtils.split(ohandler.relations, ",");
        if (relnames.length > 0) {
          relations = new HashSet(relnames.length);
          CollectionUtils.addAll(relations, relnames);
        }
      }
      
      try {
        // Process data sources in mapping
        if ("add".equals(operation))
          Processor.addRelations(mapping, relations, topicmap, baseloc);
        else if ("sync".equals(operation)) {
          if (ohandler.forceRescan != null && Boolean.valueOf(ohandler.forceRescan).booleanValue())
            Processor.synchronizeRelations(mapping, relations, topicmap, baseloc, true);
          else
            Processor.synchronizeRelations(mapping, relations, topicmap, baseloc, false);            
        } else if ("remove".equals(operation))
          Processor.removeRelations(mapping, relations, topicmap, baseloc);
        else
          throw new UnsupportedOperationException("Unsupported operation: " + operation);

        // export topicmap
        if (outfile != null) {
          log.debug("Exporting topic map to " + outfile);
          TopicMapWriterIF writer = ImportExportUtils.getWriter(outfile);
          writer.write(topicmap);
        }
        
        // commit transaction
        store.commit();
        log.debug("Transaction committed.");
      } catch (Exception t) {
        log.error("Error occurred while running operation '" + operation + "'", t);
        // abort transaction
        store.abort();
        log.debug("Transaction aborted.");
        throw t;
      } finally {
        if (store.isOpen()) store.close();
      }
      
    } catch (Exception e) {
      Throwable cause = ObjectUtils.getRealCause(e);
      if (cause instanceof DB2TMException)
        System.err.println("Error: " + e.getMessage());
      else
        throw e;
    }
  }
  
  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.db2tm.Execute [options] (add|sync|remove) <configfile>");
    System.out.println("");
    System.out.println("  Runs a DB2TM process.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --tm=<tmurl>: the topic map to update");
    System.out.println("    --baseuri=<uri>: the topic map base uri");
    System.out.println("    --out=<outfile>: the resulting topic map file (optional)");
    System.out.println("    --relations=<r1,...,rN>: list of the relations to actually process");
    System.out.println("    --force-rescan=<true|false>: forces rescan instead of changelog on relations");
    System.out.println("");
    System.out.println("  (add|sync|remove): operation to perform");
    System.out.println("  <cfgfile>: the configuration file to use");
    System.out.println("");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    String tm;
    String baseuri;
    String out;
    String relations;
    String forceRescan;
    public void processOption(char option, String value) {
      if (option == 't') tm = value;
      else if (option == 'b') baseuri = value;
      else if (option == 'o') out = value;
      else if (option == 'r') relations = value;
      else if (option == 'f') forceRescan = value;
    }
  }

}
