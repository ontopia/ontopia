
// $Id: XTMValidator.java,v 1.13 2008/05/29 10:54:57 geir.gronmo Exp $

package net.ontopia.topicmaps.cmdlineutils;

import java.io.*;
import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.xml.*;

import org.apache.log4j.*;

/**
 * PUBLIC: A topic map validator that is capable of reading the
 * interchange syntax defined by XTM 1.0 (TopicMaps.org) or XTM 2.0.</p>
 */
public class XTMValidator {

  // Define a logging category.
  static Logger log = Logger.getLogger(XTMValidator.class.getName());

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
