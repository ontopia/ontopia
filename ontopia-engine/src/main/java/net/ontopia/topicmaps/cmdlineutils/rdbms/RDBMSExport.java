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

package net.ontopia.topicmaps.cmdlineutils.rdbms;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;

/**
 * PUBLIC: Command line utility for exporting topic maps from a
 * relational database system to files.
 *
 * <p>Run the class with no arguments to see how to use it.
 */ 
public class RDBMSExport {

  public static void main(String[] argv) throws Exception {    
    //System.out.println("Starting...");

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSExport", argv);
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
    long topicMapId = ImportExportUtils.getTopicMapId(args[1]);
    RDBMSTopicMapStore store = new RDBMSTopicMapStore(propfile, topicMapId);
    TopicMapIF tm = store.getTopicMap();
    String id = tm.getObjectId();
    
    TopicMapWriterIF writer = ImportExportUtils.getWriter(args[2]);

    System.out.println("Exporting " + id + " to " + args[2] + ".");
    long start = System.currentTimeMillis();      
    writer.write(tm);
    long end = System.currentTimeMillis();
      
    System.out.println("Done. " + (end - start) + " ms.");
    
    // Close store (and database connection)    
    store.close();
  }

  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSExport [options] <dbprops> <tmid> <expfile>");
    System.out.println("");
    System.out.println("  Exports topic maps from RDBMS to file.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("  <tmid>:      the topic map id");
    System.out.println("  <expfile>:   the filename of the exported file");
    System.out.println("");
  }
  
}
