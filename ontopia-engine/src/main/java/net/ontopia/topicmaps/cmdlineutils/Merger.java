/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.cmdlineutils;

import java.io.File;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;

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

      if (ohandler.suppress) {
        DuplicateSuppressionUtils.removeDuplicates(merged);
      }
      
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
      if (encoding != null) {
        new XTMTopicMapWriter(new File(outfile), encoding).write(tm);
      } else {
        new XTMTopicMapWriter(new File(outfile)).write(tm);
      }
    } else {
      ImportExportUtils.getWriter(new File(outfile), encoding).write(tm);
    }
  }

  // --- Listener class
  
  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    private boolean xtm = false;
    private boolean enc = false;
    private boolean suppress = false;
    
    @Override
    public void processOption(char option, String value)
      throws CmdlineOptions.OptionsException {
      if (option == 'x') {
        xtm = true;
      }
      if (option == 'e') {
        enc = true;
      }
      if (option == 's') {
        suppress = Boolean.valueOf(value).booleanValue();
      }
    }
  }
  

}
