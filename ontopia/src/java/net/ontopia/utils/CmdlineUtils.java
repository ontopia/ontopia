
// $Id: CmdlineUtils.java,v 1.30 2006/02/15 15:16:04 larsga Exp $

package net.ontopia.utils;

import java.io.File;
import java.util.*;
import java.net.*;

import net.ontopia.infoset.core.LocatorIF;

import org.apache.log4j.*;

/**
 * INTERNAL: Class that contains useful stuff for command line utilities.
 */
public class CmdlineUtils {

  // Define a logging category.
  static Logger log = Logger.getLogger(CmdlineUtils.class.getName());

  public static void initializeLogging() {
    String propfile = null;
    try {
      propfile = System.getProperty("log4j.configuration");
    } catch (SecurityException e) {
      log.warn(e.toString());      
    }
      
    if (propfile == null) {
      // Reset and initialize logging configuration
      BasicConfigurator.resetConfiguration();
      BasicConfigurator.configure();
      try {
        String priority = System.getProperty("net.ontopia.utils.CmdlineUtils.priority");
        setLoggingPriority((priority == null ? "INFO" : priority));
      } catch (SecurityException e) {
        setLoggingPriority("INFO");
      }
    } else {
      // Use the specified log4j property file
      PropertyConfigurator.configure(propfile);
    }
  }

  public static void setLoggingPriority(String priority) {
    if (priority == null) priority = "INFO";
    
    if (priority.equals("ALL"))
      LogManager.getLoggerRepository().setThreshold((Level) Level.ALL);
    else if (priority.equals("DEBUG")) {
      LogManager.getLoggerRepository().setThreshold((Level) Level.DEBUG);
      //Category.getDefaultHierarchy().enableAll();
    } else if (priority.equals("INFO")) {
      //Category.getDefaultHierarchy().disable(Priority.DEBUG);
      LogManager.getLoggerRepository().setThreshold((Level) Level.INFO);
    } else if (priority.equals("WARN")) {
      //Category.getDefaultHierarchy().disable(Priority.INFO);
      LogManager.getLoggerRepository().setThreshold((Level) Level.WARN);
    } else if (priority.equals("ERROR")) {
      //Category.getDefaultHierarchy().disable(Priority.WARN);
      LogManager.getLoggerRepository().setThreshold((Level) Level.ERROR);
    } else if (priority.equals("FATAL")) {
      //Category.getDefaultHierarchy().disable(Priority.ERROR);
      LogManager.getLoggerRepository().setThreshold((Level) Level.FATAL);
    } else if (priority.equals("NONE")) {
      //Category.getDefaultHierarchy().disableAll();
      LogManager.getLoggerRepository().setThreshold((Level) Level.OFF);
    } else {
      // Default is INFO
      //Category.getDefaultHierarchy().disable(Priority.INFO);     
      LogManager.getLoggerRepository().setThreshold((Level) Level.INFO);
    }
  }
  
  public static void registerLoggingOptions(CmdlineOptions options) {    
    // Configure option listeners
    LoggingOptionsListener listener = new LoggingOptionsListener();    
    options.addLong(listener, "logargs", '9', true);
    options.addLong(listener, "loglevel", '8', true);
    //options.addLong(listener, "logfile", '7', true);
  }

  static class LoggingOptionsListener implements CmdlineOptions.ListenerIF {
    
    public void processOption(char option, String value) throws CmdlineOptions.OptionsException {

      switch (option)
        {
        case '9':
          PropertyConfigurator.configure(value);
          break;
        case '8':
          if (value == null) break;
          if (value.equals("DEBUG"))
            LogManager.getLoggerRepository().setThreshold((Level) Level.DEBUG);
          else if (value.equals("INFO"))
            LogManager.getLoggerRepository().setThreshold((Level) Level.INFO);
          else if (value.equals("WARN"))
            LogManager.getLoggerRepository().setThreshold((Level) Level.WARN);
          else if (value.equals("ERROR"))
            LogManager.getLoggerRepository().setThreshold((Level) Level.ERROR);
          else if (value.equals("FATAL"))
            LogManager.getLoggerRepository().setThreshold((Level) Level.FATAL);
          else if (value.equals("NONE"))
            LogManager.getLoggerRepository().setThreshold((Level) Level.OFF);
          else if (value.equals("ALL"))
            LogManager.getLoggerRepository().setThreshold((Level) Level.ALL);
          break;
        }
    }
  }

  public static void printLoggingOptionsUsage(java.io.PrintStream stream) {
    stream.println("    --logargs=<propfile>  : log4j properties config file");
    stream.println("    --loglevel=[DEBUG|INFO|WARN|ERROR|FATAL|NONE]  : the log level to use (verbosity threshold)");
  }
  
}
