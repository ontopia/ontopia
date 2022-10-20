/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;

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
    private int terms = 30;
    @Override
    public void processOption(char option, String value) {
      if (option == 't') {
        terms = Integer.valueOf(value).intValue();
      }
    }
  }
  
}
