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

import java.io.*;
import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.xml.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: A topic map validator that is capable of reading the
 * interchange syntax defined by XTM 1.0 (TopicMaps.org) or XTM 2.0.</p>
 */
public class XTMValidator {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(XTMValidator.class.getName());

  public static void main(String [] argv) {

    // Initialize logging
    CmdlineUtils.initializeLogging();
    
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("XTMValidator", argv);

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
    if (args.length == 0) {
      System.err.println("Error: Please specify one or more XTM files.");
      usage();
      System.exit(1);
    }

    // Perform XTM validation
    try {
      for (int i=0; i < args.length; i++) {
        log.debug("Setting up validator.");

        // Validate or transform <url> into a URL
        LocatorIF url = URIUtils.getURI(args[i]);
        
        XTMTopicMapReader validator = new XTMTopicMapReader(url);
        log.info("Validation begins.");
        validator.read();
        log.info("Validation ends.");
      }
    }
    catch (java.io.IOException e) {
      System.err.println(e);
      System.exit(3);
    }
    
  }
  
  protected static void usage() {
    System.out.println("java net.ontopia.topicmaps.xml.XTMValidator [options] <urls>");
    System.out.println("");
    System.out.println("  Validates an XTM document.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <urls>: urls or filenames of XTM topic maps to be validated");
  } 
  
}
