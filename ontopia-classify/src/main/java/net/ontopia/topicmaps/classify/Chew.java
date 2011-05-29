
package net.ontopia.topicmaps.classify;

import java.io.*;
import java.util.*;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;

/**
 * PUBLIC: Command-line tool for extracting keywords from a document.
 */
public class Chew {

  public static void main(String[] argv) throws Exception {

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("Chew", argv);
    CmdlineUtils.registerLoggingOptions(options);
    OptionsListener ohandler = new OptionsListener();
    options.addLong(ohandler, "terms", 't', true);
      
    // Parse command line options
    try {
      options.parse();
    } catch (CmdlineOptions.OptionsException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);      
    }

    // Get command line arguments
    String[] args = options.getArguments();
    if (args.length == 0 || args.length > 2) {
      usage();
      System.exit(3);
    }

    String infile = (args.length == 1 ? args[0] : args[1]);
    
    // load the topic maps
    TopicMapIF topicmap = (args.length == 2 ? ImportExportUtils.getReader(args[0]).read() : null);

    // rank and dump
    TermDatabase tdb = SimpleClassifier.classify(infile, topicmap);
    tdb.dump(ohandler.terms);
  }

  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.classify.Chew [options] <topicmapuri> <inputfile>");
    System.out.println("");
    System.out.println("  Performs auto-classification of a document against a topic map.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --terms=<number> : number of terms to output (default: 30)");
    System.out.println("");
    System.out.println("  <topicmapuri>:   the topic map to classify against (optional)");
    System.out.println("  <inputfile>:   the document to classify");
    System.out.println("");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    int terms = 30;
    public void processOption(char option, String value) {
      if (option == 't') terms = Integer.valueOf(value).intValue();
    }
  }
  
}
