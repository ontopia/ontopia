
// $Id: Merger.java,v 1.22 2008/05/28 10:30:49 geir.gronmo Exp $

package net.ontopia.topicmaps.cmdlineutils;

import java.io.File;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.MergeUtils;

/**
 * PUBLIC: Merges two topic map documents.</p>
 */

public class Merger {

  private static String encoding;

  public static void main(String [] argv) {

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("Merger", argv);
    OptionsListener ohandler = new OptionsListener();
      
    // Register local options
    options.addLong(ohandler, "xtm", 'x', true);
    options.addLong(ohandler, "enc", 'e', true);
    options.addLong(ohandler, "suppress", 's', true);
      
    // Register logging options
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
    if (args.length < 3) {
      System.err.println("Error: need at least three XTM files as arguments.");
      usage();
      System.exit(1);
    }

    if (ohandler.enc) {
      // Get the encoding
      encoding = args[args.length - 1];
      // Strip away the encoding from the argument array 
      String[] tmp = new String[args.length - 1];
      for (int i = 0; i < args.length - 1; i++) {
        tmp[i] = args[i];
      }
      args = tmp;
    }

    try {
      
      TopicMapIF merged = merge(args[0], args[1]);
      for (int ix = 2; ix < args.length - 1; ix++) {
        merged = merge(merged, args[ix]);
      }

      if (ohandler.suppress)
        DuplicateSuppressionUtils.removeDuplicates(merged);
      
      export(merged, args[args.length - 1], ohandler.xtm);
    }
    catch (java.net.MalformedURLException e) {
      e.printStackTrace(System.err);
    }
    catch (ConstraintViolationException e) {
      System.err.println("There was a conflict when merging the two topic maps.");
      e.printStackTrace(System.err);
      System.err.println("Merging aborted.");
      System.exit(2);
    }
    catch (java.io.IOException e) {
      System.err.println(e);
      System.exit(3);
    }
  }
  
  protected static void usage() {
    System.out.println("java Merger [options] <stm> <stm> [<stm> ...] <ttm> <enc>");
    System.out.println("");
    System.out.println("  Merges two topic maps and outputs a third.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --xtm: write the output in XTM 1.0 attribute syntax");
    System.out.println("    --enc: specify if there is an optional encoding argument");
    System.out.println("    --suppress=true|false: suppress duplicate characteristics (default: false)");
    System.out.println("");
    System.out.println("    <stm>: filename or url of source topic map");
    System.out.println("    <ttm>: output topic map filename");
    System.out.println("    <enc>: output encoding");
  }

  protected static TopicMapIF merge(String stm1, String stm2)
    throws ConstraintViolationException, java.io.IOException {
    TopicMapIF source1 = ImportExportUtils.getReader(stm1).read();
    TopicMapIF source2 = ImportExportUtils.getReader(stm2).read();
    MergeUtils.mergeInto(source1, source2);
    return source1;
  }

  protected static TopicMapIF merge(TopicMapIF target, String source) 
    throws ConstraintViolationException, java.io.IOException {
    TopicMapIF sourcetm = ImportExportUtils.getReader(source).read();
    MergeUtils.mergeInto(target, sourcetm);
    return target;
  }

  protected static void export(TopicMapIF tm, String outfile, boolean xtm)
    throws java.io.IOException {
    if (xtm) {
      if (encoding != null)
        new XTMTopicMapWriter(new File(outfile), encoding).write(tm);
      else
        new XTMTopicMapWriter(outfile).write(tm);
    } else
      ImportExportUtils.getWriter(outfile, encoding).write(tm);
  }

  // --- Listener class
  
  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    boolean xtm = false;
    boolean enc = false;
    boolean suppress = false;
    
    public void processOption(char option, String value)
      throws CmdlineOptions.OptionsException {
      if (option == 'x') xtm = true;
      if (option == 'e') enc = true;
      if (option == 's') suppress = Boolean.valueOf(value).booleanValue();
    }
  }
  

}
