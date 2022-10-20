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
import java.io.IOException;
import java.net.MalformedURLException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;

/**
 * PUBLIC: Reads a topic map and writes it out in ISO CXTM.
 */
public class Canonicalizer {
  
  public static void main(String [] argv) {
    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("Canonicalizer", argv);
    OptionsListener ohandler = new OptionsListener();

    // Register local options
    options.addLong(ohandler, "readall", 'a', false);
      
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
    
    if (args.length != 2) {
      usage();
      System.exit(1);
    }

    try {

      // Canonicalize document
      canonicalize(args[0], args[1], ohandler.readall);
    }
    catch (MalformedURLException e) {
      System.err.println(e);
      System.exit(2);
    }
    catch (IOException e) {
      System.err.println(e);
      System.exit(2);
    }
  }

  protected static void usage() {
    System.out.println("");
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.Canonicalizer [options] <in> <out>");
    System.out.println("");
    System.out.println("  Reads a topic map and writes it out in canonical form");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --readall: reads all topic maps  in the document");
    System.out.println("");
    System.out.println("  <in>:  file name or url of source topic map");
    System.out.println("  <out>: file to write canonical version to");
  }

  protected static void canonicalize(String stm, String ctm, boolean readall) 
    throws IOException, MalformedURLException {
    TopicMapIF source;
    
    try {
      if (readall) {
        TopicMapReaderIF importer = ImportExportUtils.getReader(stm);          
        source = new InMemoryTopicMapStore().getTopicMap();
        importer.importInto(source);
      } else {
        TopicMapReaderIF reader = ImportExportUtils.getReader(stm);          
        source = reader.read();
      }

      DuplicateSuppressionUtils.removeDuplicates(source);      
    }
    catch (InvalidTopicMapException e) {
      System.err.println("ERROR reading file: " + e.getMessage());
      return;
    }

    new CanonicalXTMWriter(new File(ctm)).write(source);
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    private boolean readall = false;
    @Override
    public void processOption(char option, String value) throws CmdlineOptions.OptionsException {
      if (option == 'a') {
        readall = true;
      }
    }
  }
  
}






