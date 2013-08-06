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

import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;

/**
 * PUBLIC: Command line utility for deleting topic maps in a
 * relational database system.
 *
 * <p>Run the class with no arguments to see how to use it.
 */
public class RDBMSDelete {
  
  public static void main(String[] argv) throws Exception {    

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSDelete", argv);
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

    if (args.length < 2) {
      usage();
      System.exit(3);
    }
    String propfile = args[0];
    long topicMapId = ImportExportUtils.getTopicMapId(args[1]);

    RDBMSTopicMapStore store = new RDBMSTopicMapStore(propfile, topicMapId);      

    System.out.println("Deleting topic map with id " + topicMapId);
    store.delete(true);
  }

  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSDelete [options] <dbprops> <tmid>");
    System.out.println("");
    System.out.println("  Deletes a topic map from a database.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("  <tmid>:      the id of the topic map to delete");
    System.out.println("");
  }
  
}
