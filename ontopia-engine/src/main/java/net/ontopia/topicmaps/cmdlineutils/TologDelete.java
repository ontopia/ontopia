
package net.ontopia.topicmaps.cmdlineutils;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Command-line that deletes topic map objects returned by
 * one or more tolog queries. Each tolog query must project exactly
 * one column.
 *
 * @since 3.2.2
 */
public class TologDelete {
  
  // --- define a logging category.
  static Logger log = LoggerFactory.getLogger(TologDelete.class.getName());

  TologDelete() {
  }
  
  public static void main(String[] argv) throws Throwable {

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("TologDelete", argv);
    CmdlineUtils.registerLoggingOptions(options);
    OptionsListener ohandler = new OptionsListener();
    options.addLong(ohandler, "tm", 't', true);
    options.addLong(ohandler, "out", 'o', true);
    options.addLong(ohandler, "simulate", 's', true);
      
    // Parse command line options
    try {
      options.parse();
    } catch (CmdlineOptions.OptionsException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);      
    }

    // Get command line arguments
    String[] args = options.getArguments();

    if (args.length < 1) {
      usage();
      System.exit(3);
    }

    try {
      // open topic map
      String tmurl = ohandler.tm;
      if (tmurl == null)
        throw new OntopiaRuntimeException("--tm option must be specified");

      boolean simulate = false;
      if (ohandler.simulate != null)
        simulate = Boolean.valueOf(ohandler.simulate).booleanValue();

      System.out.println("Simulate: " + simulate + ":" + ohandler.simulate);
      log.debug("Loading topic map '" + tmurl + "'");
      TopicMapReaderIF reader = ImportExportUtils.getReader(tmurl);
      TopicMapStoreIF store = reader.read().getStore();
      
      try {
        // parse queries before attempting to modify topic map
        QueryProcessorIF qp = QueryUtils.getQueryProcessor(store.getTopicMap());
        ParsedQueryIF[] pqs = new ParsedQueryIF[args.length];        
        for (int i=0; i < args.length; i++) {
          System.out.println("Query: " + i + " " + args[i]);
          pqs[i] = qp.parse(args[i]);
        }

        // for each query delete topic map objects
        for (int i=0; i < pqs.length; i++) {
          QueryResultIF result = pqs[i].execute();
          try {
            // complain if multiple columns
            if (result.getWidth() != 1)
              throw new OntopiaRuntimeException("Query must project exactly one column: " + args[i]);
            
            while (result.next()) {
              TMObjectIF o = (TMObjectIF)result.getValue(0);
              System.out.println("Removing: " + o);
              log.debug("Removing: " + o);
              if (!simulate)
                o.remove();
            }
          } finally {
            result.close();
          }
        }

        // export topicmap
        if (ohandler.out != null) {
          String outfile = ohandler.out;
          log.debug("Exporting topic map to " + outfile);
          TopicMapWriterIF writer = ImportExportUtils.getWriter(outfile);
          writer.write(store.getTopicMap());
        }
        
        // commit transaction
        store.commit();
        log.debug("Transaction committed.");
      } catch (Exception t) {
        log.error("Error occurred", t);
        // abort transaction
        store.abort();
        log.debug("Transaction aborted.");
        throw t;
      } finally {
        if (store.isOpen()) store.close();
      }
      
    } catch (Exception e) {
      throw ObjectUtils.getRealCause(e);
    }
  }
  
  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.TologDelete [options] <query>*");
    System.out.println("");
    System.out.println("  Deletes topic map objects returned by one or more tolog queries.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --tm=<tmurl>: the topic map to update");
    System.out.println("    --out=<outfile>: the resulting topic map file (optional)");
    System.out.println("    --simulate=true|false: if false queries will be executed, but no changes made");
    System.out.println("");
    System.out.println("  <query>: the tolog query to find objects to delete");
    System.out.println("");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    String tm;
    String out;
    String simulate;
    public void processOption(char option, String value) {
      if (option == 't') tm = value;
      else if (option == 'o') out = value;
      else if (option == 's') simulate = value;
    }
  }

}
